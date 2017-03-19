package vtag.vorhangkontrolle.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import vtag.vorhangkontrolle.Controller;
import vtag.vorhangkontrolle.R;

public class StandbyActivity extends AppCompatActivity {

    Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standby);
        controller = Controller.getController();
        controller.registerActivity(this);
    }

    @Override
    public void onBackPressed() {
        // this would just mess everything up...
    }

    public void handleRequest(){
        setContentView(R.layout.activity_standby_handle_request);
    }

    public void acceptRequest(View v){
        controller.acceptRequest();
    }

    public void denyRequest(View v){
        controller.denyRequest();
    }

    public void cancelRequest(View v){
        controller.cancelRequest();
    }

    public void sendRequest(View v){
        controller.sendRequest();
        setContentView(R.layout.activity_standby_sent_request);
    }
}
