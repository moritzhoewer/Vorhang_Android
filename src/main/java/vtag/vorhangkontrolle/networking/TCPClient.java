package vtag.vorhangkontrolle.networking;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Provides functionality for communicating via TCP.
 *
 * @author Moritz Höwer
 * @version 1.0 - 17.03.2017
 */
public class TCPClient {

    private static final int SERVER_PORT = 8698;
    public static final int CONNECT_TIMEOUT = 1000;
    private MessageHandler messageHandler;
    private ClientListener clientListener;
    private InetAddress serverAddress;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread receiverThread;
    private boolean connected;


    public TCPClient(InetAddress serverAddress, MessageHandler messageHandler, ClientListener clientListener) {
        this.serverAddress = serverAddress;
        this.messageHandler = messageHandler;
        this.clientListener = clientListener;
        connected = false;
    }

    public boolean connect() {
        if(connected){
            return true;
        }

        Log.d("TCPCLient", "Connecting!");
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddress, SERVER_PORT), CONNECT_TIMEOUT);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            receiverThread = new Thread(this::receiveLoop);
            connected = true;
            receiverThread.start();
            return true;
        } catch (IOException ex) {
            Log.e("TCPCLient", "Failed to connect to Server!");
            return false;
        }
    }

    public void disconnect() {
        if(!connected){
            return;
        }

        Log.d("TCPCLient", "Disconnecting!");
        clientListener.handleDisconnect();
        try {
            connected = false;
            in.close();
            out.close();
            socket.close();
            receiverThread.join();
        } catch (IOException | InterruptedException e) {
            Log.e("TCPCLient", "Exception while disconnecting!");
        } finally {
            in = null;
            out = null;
            socket = null;
            receiverThread = null;
        }
    }

    public void sendMessage(String message) {
        Log.d("TCPCLient", "Sending: " + message);
        out.println(message);
        out.flush();
    }

    private void receiveLoop() {
        while(connected){
            try{
                Log.d("TCPCLient", "Receiving...");
                String message = in.readLine();
                if(message == null){
                    break;
                } else {
                    Log.d("TCPCLient", "Received: " + message);
                    messageHandler.handleMessage(message.trim());
                }
            } catch (IOException e){
                if(connected){
                    Log.e("TCPCLient", "Exception while receiving!");
                    break;
                }
            }
        }

        if(connected) {
            // we received null, so we need to disconnect
            disconnect();
        }
    }
}
