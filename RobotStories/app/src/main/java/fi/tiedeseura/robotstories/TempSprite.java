package fi.tiedeseura.robotstories;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.List;

/**
 * Created by mikko on 5.8.15.
 */
public class TempSprite {
    private float x;
    private float y;
    private Bitmap bmp;
    private int life = 15;
    private List<TempSprite> temps;

    public TempSprite(List<TempSprite> temps, GUIView guiView, float x, float y, Bitmap bmp) {
        Log.d("asd", "tmpsprite");
        this.x = Math.min(Math.max(x - bmp.getWidth() / 2, 0), guiView.getWidth() - bmp.getWidth());
        this.y = Math.min(Math.max(y - bmp.getHeight() / 2, 0), guiView.getHeight() - bmp.getHeight());
        this.bmp = bmp;
        this.temps = temps;
    }

    public void onDraw(Canvas canvas) {
        update();
        canvas.drawBitmap(bmp, x, y, null);
    }

    private void update() {
        if (--life < 1) {
            temps.remove(this);
        }
    }
}

