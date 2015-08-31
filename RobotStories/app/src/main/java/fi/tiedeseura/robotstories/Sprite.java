package fi.tiedeseura.robotstories;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by mikko on 5.8.15.
 */
public class Sprite {
    // direction = 0 up, 1 left, 2 down, 3 right,
    // animation = 3 back, 1 left, 0 front, 2 right
    public static final int UP = 0;
    public static final int LEFT = 1;
    public static final int DOWN = 2;
    public static final int RIGHT = 3;
    int[] DIRECTION_TO_ANIMATION_MAP = { 3, 1, 0, 2 };
    public static final int BMP_ROWS = 4;
    public static final int BMP_COLUMNS = 3;
    private static final int MAX_SPEED = 15;

    StagePoint point;

    //private int xSpeed = 50;
    //private int ySpeed = 50;
    private GUIView guiView;
    private Bitmap bmp;
    private int currentFrame = 0;
    private int origWidth;
    private int origHeight;
    public static final int targetWidth = 100;
    public static final int targetHeight = 100;
    private int direction = DOWN;

    public Sprite(GUIView guiView, Bitmap bmp) {
        this.guiView = guiView;
        this.bmp = bmp;
        this.origWidth = bmp.getWidth() / BMP_COLUMNS;
        this.origHeight = bmp.getHeight() / BMP_ROWS;
        this.point = new StagePoint(guiView.getStage());
    }

    private void update() {
        currentFrame = ++currentFrame % BMP_COLUMNS;
    }

    public void setDirection(int dir) {
        this.direction = dir;
    }

    public void onDraw(Canvas canvas) {
        update();
        int srcX = currentFrame * origWidth;
        int srcY = getAnimationRow() * origHeight;
        Rect src = new Rect(srcX, srcY, srcX + origWidth, srcY + origHeight);
        Rect dst = new Rect((int)point.getScreenX()-targetWidth/2, (int)point.getScreenY()-targetHeight/2, (int)point.getScreenX() + targetWidth/2, (int)point.getScreenY() + targetHeight/2);
        canvas.drawBitmap(bmp, src, dst, null);
    }

    // direction = 0 up, 1 left, 2 down, 3 right,
    // animation = 3 back, 1 left, 0 front, 2 right
    private int getAnimationRow() {
        //double dirDouble = (Math.atan2(xSpeed, ySpeed) / (Math.PI / 2) + 2);
        //int direction = (int) Math.round(dirDouble) % BMP_ROWS;
        //return direction;
        return DIRECTION_TO_ANIMATION_MAP[direction];
    }

    public boolean isCollision(float x2, float y2) {
        return x2 > point.getScreenX()-targetWidth/2 && x2 < point.getScreenX() + targetWidth/2 && y2 > point.getScreenY()-targetHeight/2 && y2 < point.getScreenY() + targetHeight/2;
    }

    /*
    public int getScreenX() {
        return (int)this.point.getScreenX();
    }
    public int getScreenY() {
        return (int)this.point.getScreenY();
    }
    */
    public StagePoint getPosition() {
        return this.point;
    }
    public void setPosition(StagePoint p) {
        this.point = p;
    }
    public void setPosition(float center_x, float center_y) {
        this.point.setScreenX(center_x);
        this.point.setScreenY(center_y);
    }
}

