package fi.tiedeseura.robotstories;

import android.graphics.BitmapFactory;

/**
 * Created by mikko on 5.8.15.
 */
public class MenuSprite extends MarkerSprite {
    public MenuSprite(GUIView guiView, int center_x, int center_y) {
        super(guiView, center_x, center_y);
        this.targetWidth = 200;
        this.targetHeight = 200;
        this.bmp = BitmapFactory.decodeResource(guiView.getResources(), R.drawable.selected);
    }
}

