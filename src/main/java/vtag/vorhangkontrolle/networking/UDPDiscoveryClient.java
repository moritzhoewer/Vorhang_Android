package vtag.vorhangkontrolle.networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Provides functionality for finding the server on the network.
 * Utilizes UDP Broadcast packages.
 *
 * @author Moritz Höwer
 * @version 1.0 - 17.03.2017
 */
public class UDPDiscoveryClient {

    private static final String DISCOVER_REQUEST = "VORHANGKONTROLLE_REQUEST";
    private static final int RECEIVE_BUFFER_SIZE = 4096;
    private static final String DISCOVER_RESPONSE = "VORHANGKONTROLLE_RESPONSE";

    /**
     * Find the server address on the network.
     *
     * @param timeout how long to wait for a reply (in ms)
     * @return the server address or {@code null} if no response is received within th timeout
     */
    public InetAddress getServerAddress(int timeout) {
        // Find the server using UDP broadcast
        try {
            // Open a random port to send the package
            DatagramSocket sock = new DatagramSocket();
            sock.setBroadcast(true);

            byte[] sendData = DISCOVER_REQUEST.getBytes();

            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getByName("255.255.255.255"), 9001);
            sock.send(sendPacket);

            // Wait for a response
            byte[] recvBuf = new byte[RECEIVE_BUFFER_SIZE];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            sock.setSoTimeout(timeout);
            sock.receive(receivePacket);

            // Check if the message is correct
            String message = new String(receivePacket.getData()).trim();
            if (message.equals(DISCOVER_RESPONSE)) {
                // Close the port!
                sock.close();
                return receivePacket.getAddress();
            }

            // Close the port!
            sock.close();
            return null;
        } catch (IOException ex) {
            return null;
        }
    }
}
