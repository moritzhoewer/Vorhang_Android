package vtag.vorhangkontrolle.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import vtag.vorhangkontrolle.Controller;
import vtag.vorhangkontrolle.R;

public class MainActivity extends AppCompatActivity {

    private Controller controller;

    private View btnRetry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Controller.getController();
        controller.registerActivity(this);
        controller.attemptConnection();
        btnRetry = findViewById(R.id.btnConnect);
        assert btnRetry != null;
        btnRetry.setEnabled(false);
    }

    @Override
    public void onBackPressed() {
        // this would just mess everything up...
    }

    public void connectionFailed(){
        btnRetry.setEnabled(true);
    }

    public void connect(View v) {
        btnRetry.setEnabled(false);
        controller.attemptConnection();
    }
}
