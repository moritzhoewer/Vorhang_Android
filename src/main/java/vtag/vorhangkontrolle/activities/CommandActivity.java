package vtag.vorhangkontrolle.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import vtag.vorhangkontrolle.Command;
import vtag.vorhangkontrolle.CommandDisplayRule;
import vtag.vorhangkontrolle.Controller;
import vtag.vorhangkontrolle.R;

/**
 * Fullscreen activity for displaying commands.
 *
 * @author Moritz Höwer
 * @version 1.0 - 18.03.2017
 */
public class CommandActivity extends AppCompatActivity {

    private View background;
    private ImageView image;
    private TextView text;
    private Controller controller;

    public void displayCommand(Command c){
        CommandDisplayRule cdr = c.getDisplayRule();
        background.setBackgroundResource(cdr.getBackgroundColorID());
        image.setImageResource(cdr.getImageID());
        text.setText(cdr.getTextID());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command);
        background = findViewById(R.id.rlCommand);
        image = (ImageView)findViewById(R.id.imgCommandImage);
        text = (TextView)findViewById(R.id.txtCommandText);

        controller = Controller.getController();
        controller.registerActivity(this);
    }

    @Override
    public void onBackPressed() {
        // this would just mess everything up...
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            background.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
