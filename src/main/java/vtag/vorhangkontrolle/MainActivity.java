package vtag.vorhangkontrolle;

import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.net.InetAddress;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void connect(View v) {
        new Thread(() -> {
            DiscoveryClient dclient = new DiscoveryClient();
            InetAddress serverAddr = dclient.getServerAddress();

            // Get a handler that can be used to post to the main thread
            Handler mainHandler = new Handler(getApplicationContext().getMainLooper());

            mainHandler.post(() -> {
                Toast t;
                if(serverAddr != null){
                    t = Toast.makeText(getApplicationContext(), "Server IP is " + serverAddr.getHostAddress(), Toast.LENGTH_LONG);
                } else {
                    t = Toast.makeText(getApplicationContext(), "Server not found!", Toast.LENGTH_SHORT);
                }
                t.show();
            });
        }).start();
    }
}
