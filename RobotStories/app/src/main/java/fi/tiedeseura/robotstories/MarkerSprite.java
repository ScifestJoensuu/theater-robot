package fi.tiedeseura.robotstories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by mikko on 5.8.15.
 */
public class MarkerSprite {
    protected int BMP_ROWS = 1;
    protected int BMP_COLUMNS = 8;
    protected int center_x = 0;
    protected int center_y = 0;
    protected GUIView guiView;
    protected Bitmap bmp;
    protected int currentFrame = 0;
    protected int origWidth;
    protected int origHeight;
    protected int targetWidth = 100;
    protected int targetHeight = 100;

    public MarkerSprite(GUIView guiView, int x, int y) {
        this.guiView = guiView;
        this.bmp = BitmapFactory.decodeResource(guiView.getResources(), R.drawable.selected);
        this.origWidth = bmp.getWidth() / BMP_COLUMNS;
        this.origHeight = bmp.getHeight() / BMP_ROWS;
        this.targetWidth = (int)(Sprite.targetWidth*1.5);
        this.targetHeight = (int)(Sprite.targetHeight*1.5);
        setPosition(x, y);
    }

    private void update() {
        currentFrame = ++currentFrame % BMP_COLUMNS;
    }

    public void onDraw(Canvas canvas) {
        update();
        int srcX = currentFrame * origWidth;
        int srcY = getAnimationRow() * origHeight;
        Rect src = new Rect(srcX, srcY, srcX + origWidth, srcY + origHeight);
        Rect dst = new Rect(center_x-targetWidth/2, center_y-targetHeight/2, center_x + targetWidth/2, center_y + targetHeight/2);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    private int getAnimationRow() {
        return 0;
    }

    public boolean isCollision(float x2, float y2) {
        return x2 > center_x - targetWidth/2 && x2 < center_x + targetWidth/2 && y2 > center_y-targetHeight/2 && y2 < center_y + targetHeight/2;
    }

    public void setX(int x) {
        this.center_x = x;
    }

    public void setY(int y) {
        this.center_y = y;
    }

    public void setPosition(float center_x, float center_y) {
        this.center_x = (int)center_x;
        this.center_y = (int)center_y;
    }
}

