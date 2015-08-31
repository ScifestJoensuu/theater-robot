package fi.tiedeseura.robotstories;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

/**
 * Created by mikko on 7.8.15.
 */
public class Button {
    private Bitmap bmp;
    private Bitmap bmp_touched;
    private StagePoint position;
    private int width;
    private int height;
    private String text;
    private GUIView guiView;
    private int touch;
    private int id;
    private int textSize;

    public Button(GUIView guiView, String text, StagePoint position, int id) {
        this.touch = 0;
        this.text = text;
        this.width = text.length()*35; // estimate
        this.height = 50;   // estimate
        this.position = position;
        this.guiView = guiView;
        this.bmp = BitmapFactory.decodeResource(guiView.getResources(), R.drawable.button);
        this.bmp_touched = BitmapFactory.decodeResource(guiView.getResources(), R.drawable.button_pressed);
        this.id = id;
        this.textSize = 30;
    }

    public void setTextSize(int size) {
        this.textSize = size;
    }

    public boolean touched(StagePoint p) {
        boolean t = p.getScreenX() < this.position.getScreenX() + width/2 && p.getScreenX() > this.position.getScreenX() - width/2 && p.getScreenY() < this.position.getScreenY() + height/2 && p.getScreenY() > this.position.getScreenY() - height/2;
        if(t) {
            Log.d("", "Button '" + this.text + "' touched");
            this.touch = 2;
        }
        return t;
    }

    public int getID() {
        return this.id;
    }

    public void onDraw(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setARGB(255, 92, 60,40);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextSize(textSize);

        int srcX = 0;
        int srcY = 0;
        Rect tmp = new Rect();
        Rect src = new Rect(srcX, srcY, bmp.getWidth(), bmp.getHeight());

        if(this.touch > 0){
            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            //src = new Rect(srcX, srcY, bmp_touched.getWidth(), bmp_touched.getHeight());
            textPaint.setTextSize(28);
            textPaint.setARGB(255, 68, 47,34);
            touch--;
            textPaint.getTextBounds(text,0,text.length(),tmp);
            Rect dst = new Rect((int)position.getScreenX()-tmp.width(), (int)position.getScreenY()-tmp.height(), (int)position.getScreenX() + tmp.width(), (int)position.getScreenY() + tmp.height());
            canvas.drawBitmap(bmp_touched, src, dst, null);
        } else {
            textPaint.getTextBounds(text,0,text.length(),tmp);
            Rect dst = new Rect((int)position.getScreenX()-tmp.width(), (int)position.getScreenY()-tmp.height(), (int)position.getScreenX() + tmp.width(), (int)position.getScreenY() + tmp.height());
            canvas.drawBitmap(bmp, src, dst, null);
        }
        this.width = 2*tmp.width();
        this.height = 2*tmp.height();

        canvas.drawText(text, (int)position.getScreenX() - width/4 , (int)position.getScreenY() + height/4, textPaint);
    }

    public int getWidth(String text) {
        Rect tmp = new Rect();
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setARGB(255, 92, 60,40);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextSize(textSize);
        textPaint.getTextBounds(text,0,text.length(),tmp);
        return tmp.width()*2;
    }

    public static int getWidthEstimate(String text) {
        Rect tmp = new Rect();
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setARGB(255, 92, 60,40);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextSize(30);
        textPaint.getTextBounds(text,0,text.length(),tmp);
        return tmp.width()*2;
    }

    public StagePoint getPosition() {
        return this.position;
    }
}
