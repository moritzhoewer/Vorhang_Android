package vtag.vorhangkontrolle;

import android.os.Handler;
import android.os.Looper;

import java.net.InetAddress;

import vtag.vorhangkontrolle.activities.MainActivity;
import vtag.vorhangkontrolle.networking.UDPDiscoveryClient;

/**
 * Main controller (MVC Concept)
 *
 * @author Moritz Höwer
 * @version 1.0 - 17.03.2017
 */
public class MainController {

    private static final int DISCOVERY_TIMEOUT = 1000;
    private Thread connectionThread;
    private Handler mainHandler;
    private MainActivity mainActivity;

    public MainController(MainActivity mainActivity){
        this.mainActivity = mainActivity;
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void attemptConnection(){
        connectionThread = new Thread(this::connectToServer);
        connectionThread.start();
    }

    private void connectionFailed(){
        mainHandler.post(mainActivity::connectionFailed);
    }

    private void connectionSuccessfull(){
        // TODO: Switch to Menu
    }

    private void connectToServer(){
        UDPDiscoveryClient discovery = new UDPDiscoveryClient();
        InetAddress serverAddress = discovery.getServerAddress(DISCOVERY_TIMEOUT);
        if(serverAddress == null){
            connectionFailed();
        } else {
            // TODO: Connnect TCP
            connectionSuccessfull();
        }
    }

}
