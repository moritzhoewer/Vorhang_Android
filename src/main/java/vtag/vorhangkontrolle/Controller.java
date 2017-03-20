package vtag.vorhangkontrolle;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.net.InetAddress;

import vtag.vorhangkontrolle.activities.CommandActivity;
import vtag.vorhangkontrolle.activities.MainActivity;
import vtag.vorhangkontrolle.activities.StandbyActivity;
import vtag.vorhangkontrolle.networking.ClientListener;
import vtag.vorhangkontrolle.networking.MessageHandler;
import vtag.vorhangkontrolle.networking.TCPClient;
import vtag.vorhangkontrolle.networking.UDPDiscoveryClient;

/**
 * Main controller (MVC Concept)
 *
 * @author Moritz Höwer
 * @version 1.0 - 17.03.2017
 */
public class Controller implements MessageHandler, ClientListener {
    private enum State {
        UNKNOWN, DISCONNECTED, STANDBY, WAITING_FOR_SERVER, WAITING_FOR_USER, COMMAND
    }

    private State state;
    private boolean hasFocus;
    private int notificationId;
    private static final int DISCOVERY_TIMEOUT = 1000;
    private static final int VIBRATE_LONG = 500;
    private static final int VIBRATE_SHORT = 200;
    private static final int DELAY_BACK_TO_STANDBY = 1500;
    private Handler mainHandler;
    private TCPClient client;

    private MainActivity mainActivity;
    private CommandActivity commandActivity;
    private StandbyActivity standbyActivity;

    private Vibrator vibrator;

    private static Controller instance = null;

    public static Controller getController() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    private Controller() {
        mainHandler = new Handler(Looper.getMainLooper());
        state = State.UNKNOWN;
        hasFocus = false;
        notificationId = -1;
    }

    public void registerActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        vibrator = (Vibrator) (mainActivity.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE));
        state = State.DISCONNECTED;
    }

    public void registerActivity(CommandActivity commandActivity) {
        this.commandActivity = commandActivity;
        state = State.COMMAND;
    }

    public void registerActivity(StandbyActivity standbyActivity) {
        this.standbyActivity = standbyActivity;
        state = State.STANDBY;
    }

    public void gainedFocus() {
        hasFocus = true;
        if(notificationId != -1){
            NotificationManager mNotificationManager =
                    (NotificationManager) standbyActivity.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(notificationId);
            notificationId = -1;
        }
    }

    public void lostFocus() {
        hasFocus = false;
    }

    public void sendRequest() {
        new Thread(() -> client.sendMessage("REQUEST")).start();
        state = State.WAITING_FOR_SERVER;
    }

    public void cancelRequest() {
        new Thread(() -> client.sendMessage("CANCEL")).start();
        state = State.STANDBY;
        startStandbyActivity();
    }

    public void acceptRequest() {
        new Thread(() -> client.sendMessage("ACCEPT")).start();
        state = State.COMMAND;
        startCommandActivity();
    }

    public void denyRequest() {
        new Thread(() -> client.sendMessage("DENY")).start();
        state = State.STANDBY;
        startStandbyActivity();
    }

    @Override
    public void handleDisconnect() {
        startMainActivity();
        state = State.DISCONNECTED;
    }

    public void attemptConnection() {
        new Thread(this::connectToServer).start();
    }

    private void connectionFailed() {
        mainHandler.post(mainActivity::connectionFailed);
        showToast(mainActivity.getString(R.string.toast_server_not_found));
    }

    private void connectionSuccessfull() {
        startStandbyActivity();
        state = State.STANDBY;
        showToast(mainActivity.getString(R.string.toast_connected));
    }

    private void connectToServer() {
        UDPDiscoveryClient discovery = new UDPDiscoveryClient();
        InetAddress serverAddress = discovery.getServerAddress(DISCOVERY_TIMEOUT);
        if (serverAddress == null) {
            connectionFailed();
        } else {
            client = new TCPClient(serverAddress, this, this);
            if (client.connect()) {
                connectionSuccessfull();
            } else {
                client = null;
                connectionFailed();
            }
        }
    }

    private void startCommandActivity() {
        Intent intent = new Intent(mainActivity, CommandActivity.class);
        mainActivity.startActivity(intent);
    }

    private void startMainActivity() {
        Intent intent = new Intent(mainActivity, MainActivity.class);
        mainActivity.startActivity(intent);
    }

    private void startStandbyActivity() {
        Intent intent = new Intent(mainActivity, StandbyActivity.class);
        mainActivity.startActivity(intent);
    }

    @Override
    public void handleMessage(String message) {
        switch (state) {
            case COMMAND:
                if (message.startsWith("CMD")) {
                    try {
                        Command command = Command.forName(message);
                        mainHandler.post(() -> commandActivity.displayCommand(command));
                        vibrator.vibrate(VIBRATE_SHORT);
                        if (command == Command.PERFECT) {
                            try {
                                Thread.sleep(DELAY_BACK_TO_STANDBY);
                            } catch (InterruptedException e) {
                                Log.e("VorhangKontrolle", "Waiting was interrupted!");
                            }
                            startStandbyActivity();
                        }
                    } catch (IllegalArgumentException e) {
                        Log.w("VorhangKontrolle", "Received invalid Command: " + message);
                    }
                } else if (message.equals("CANCEL")) {
                    state = State.STANDBY;
                    startStandbyActivity();
                    showToast(mainActivity.getString(R.string.toast_foh_cancelled));
                }
                break;
            case STANDBY:
                if (message.equals("REQUEST")) {
                    state = State.WAITING_FOR_USER;
                    mainHandler.post(standbyActivity::handleRequest);
                    vibrator.vibrate(VIBRATE_LONG);
                    if (!hasFocus) {
                        createNotification();
                    }
                }
                break;
            case WAITING_FOR_SERVER:
                if (message.equals("ACCEPT")) {
                    startCommandActivity();
                    showToast(mainActivity.getString(R.string.toast_foh_accepted));
                    vibrator.vibrate(VIBRATE_LONG);
                } else if (message.equals("DENY")) {
                    startStandbyActivity();
                    showToast(mainActivity.getString(R.string.toast_foh_denied));
                    doubleVibrate();
                }
                break;
            case WAITING_FOR_USER:
                if (message.equals("CANCEL")) {
                    startStandbyActivity();
                    showToast(mainActivity.getString(R.string.toast_foh_cancelled));
                    doubleVibrate();
                    break;
                }
                break;
        }
    }

    private void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(standbyActivity)
                        .setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Anfrage vom FOH!")
                        .setContentText("FOH möchte den Vorhang fahren!");
        notificationId = 1;
        NotificationManager mNotificationManager =
                (NotificationManager) standbyActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notificationId, mBuilder.build());
    }

    private void doubleVibrate() {
        vibrator.vibrate(new long[]{100, 100, 100, 100}, -1);
    }

    private void showToast(String text) {
        mainHandler.post(() -> Toast.makeText(mainActivity, text, Toast.LENGTH_SHORT).show());
    }
}
