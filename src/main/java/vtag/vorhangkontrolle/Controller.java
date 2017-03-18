package vtag.vorhangkontrolle;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.util.Log;

import vtag.vorhangkontrolle.activities.CommandActivity;
import vtag.vorhangkontrolle.activities.MainActivity;
import vtag.vorhangkontrolle.networking.MessageHandler;

/**
 * Main controller (MVC Concept)
 *
 * @author Moritz Höwer
 * @version 1.0 - 17.03.2017
 */
public class Controller implements MessageHandler {

    private static final int DISCOVERY_TIMEOUT = 1000;
    public static final int VIBRATE_LONG = 500;
    public static final int VIBRATE_SHORT = 200;
    public static final int DELAY_BACK_TO_STANDBY = 1500;
    private Thread connectionThread;
    private Handler mainHandler;

    private MainActivity mainActivity;
    private CommandActivity commandActivity;

    private Vibrator vibrator;

    private static Controller instance = null;

    public static Controller getMainController() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    private Controller() {
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public void registerActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        vibrator = (Vibrator) (mainActivity.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE));
    }

    public void registerActivity(CommandActivity commandActivity) {
        this.commandActivity = commandActivity;
        new Thread(()->handleMessage("CMD_PERFECT")).start();
    }

    public void attemptConnection() {
        connectionThread = new Thread(this::connectToServer);
        connectionThread.start();
    }

    private void connectionFailed() {
        mainHandler.post(mainActivity::connectionFailed);
    }

    private void connectionSuccessfull() {
        // TODO: Switch to Standby Activity
    }

    private void connectToServer() {/*
        UDPDiscoveryClient discovery = new UDPDiscoveryClient();
        InetAddress serverAddress = discovery.getServerAddress(DISCOVERY_TIMEOUT);
        if(serverAddress == null){
            connectionFailed();
        } else {
            // TODO: Connnect TCP
            connectionSuccessfull();
        }*/
        startCommandActivity();
    }

    private void startCommandActivity() {
        Intent intent = new Intent(mainActivity, CommandActivity.class);
        mainActivity.startActivity(intent);
    }

    private void startStandbyActivity(){
        Intent intent = new Intent(mainActivity, MainActivity.class); //TODO: Standby activity!
        mainActivity.startActivity(intent);
    }

    @Override
    public void handleMessage(String message) {
        if (message.startsWith("CMD")) {
            try {
                Command command = Command.forName(message);
                mainHandler.post(() -> commandActivity.displayCommand(command));
                vibrator.vibrate(VIBRATE_SHORT);
                if(command == Command.PERFECT){
                    try{
                        Thread.sleep(DELAY_BACK_TO_STANDBY);
                    } catch (InterruptedException e){
                        Log.e("VorhangKontrolle", "Waiting was interrupted!");
                    }
                }
                startStandbyActivity();
            } catch (IllegalArgumentException e) {
                Log.w("VorhangKontrolle", "Received invalid Command: " + message);
            }
        }
    }
}
