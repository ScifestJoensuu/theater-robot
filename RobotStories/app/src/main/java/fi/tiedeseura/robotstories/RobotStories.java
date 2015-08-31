package fi.tiedeseura.robotstories;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class RobotStories extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("testi", "start");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(new GUIView(this));
    }
}
