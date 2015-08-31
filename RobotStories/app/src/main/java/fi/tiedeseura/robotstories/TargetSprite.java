package fi.tiedeseura.robotstories;

import android.graphics.BitmapFactory;

/**
 * Created by mikko on 5.8.15.
 */
public class TargetSprite extends MarkerSprite {
    public TargetSprite(GUIView guiView, int center_x, int center_y) {
        super(guiView, center_x, center_y);
        BMP_COLUMNS = 8;
        this.bmp = BitmapFactory.decodeResource(guiView.getResources(), R.drawable.target);
    }
}

