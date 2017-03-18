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

    private static final int SERVER_PORT = 9001;
    public static final int CONNECT_TIMEOUT = 1000;
    private MessageHandler messageHandler;
    private InetAddress serverAddress;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Thread receiverThread;
    private boolean connected;


    public TCPClient(InetAddress serverAddress, MessageHandler messageHandler) {
        this.serverAddress = serverAddress;
        this.messageHandler = messageHandler;
        connected = false;
    }

    public boolean connect() {
        if(connected){
            return true;
        }

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
            Log.e("VorhangKontrolle", "Failed to connect to Server!");
            return false;
        }
    }

    public void disconnect() {
        if(!connected){
            return;
        }

        try {
            connected = false;
            in.close();
            out.close();
            socket.close();
            receiverThread.join();
        } catch (IOException | InterruptedException e) {
            Log.e("VorhangKontrolle", "Exception while disconnecting!");
        } finally {
            in = null;
            out = null;
            socket = null;
            receiverThread = null;
        }
    }

    public void sendMessage(String message) {
        out.print(message);
        out.flush();
    }

    private void receiveLoop() {
        while(connected){
            try{
                String message = in.readLine();
                if(message == null){
                    break;
                } else {
                    messageHandler.handleMessage(message);
                }
            } catch (IOException e){
                if(connected){
                    Log.e("VorhangKontrolle", "Exception while receiving!");
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
