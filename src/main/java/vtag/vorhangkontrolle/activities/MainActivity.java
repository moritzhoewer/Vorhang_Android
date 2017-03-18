package vtag.vorhangkontrolle.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import vtag.vorhangkontrolle.Controller;
import vtag.vorhangkontrolle.R;

public class MainActivity extends AppCompatActivity {

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Controller.getMainController();
        controller.registerActivity(this);
    }

    public void connectionFailed(){
        Toast t = Toast.makeText(this, "No Server was found!", Toast.LENGTH_SHORT);
        t.show();
    }

    public void connect(View v) {
        // TODO: Indicate that we are connecting
        controller.attemptConnection();
    }
}
