package fi.tiedeseura.robotstories;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mikko on 7.8.15.
 */
public class Menu {
    public static final int BUTTON_POS_NORTH = 0;
    public static final int BUTTON_POS_NORTHEAST = 1;
    public static final int BUTTON_POS_EAST = 2;
    public static final int BUTTON_POS_SOUTHEAST = 3;
    public static final int BUTTON_POS_SOUTH = 4;
    public static final int BUTTON_POS_SOUTHWEST = 5;
    public static final int BUTTON_POS_WEST = 6;
    public static final int BUTTON_POS_NORTHWEST = 7;

    public static final int BUTTON_SETTINGS = 0;
    public static final int BUTTON_STAGE = 1;
    public static final int BUTTON_MODE = 2;
    public static final int BUTTON_NEXT = 3;
    public static final int BUTTON_PREVIOUS = 4;
    public static final int BUTTON_ADD = 5;
    public static final int BUTTON_BUILD = 6;
    public static final int BUTTON_CDU = 7;

    private StagePoint position;
    private MenuSprite menuSprite;
    private List<Button> buttons;
    private GUIView guiView;

    public Menu(GUIView guiView, MenuSprite menuSprite, StagePoint position) {
        this.position = position;
        this.menuSprite = menuSprite;
        this.menuSprite.setPosition(position.getScreenX(), position.getScreenY());
        this.guiView = guiView;
        initButtons();
    }

    private void initButtons() {
        buttons = new ArrayList<Button>();

        buttons.add(createButton("Stage", BUTTON_POS_WEST, BUTTON_STAGE));
        //buttons.add(createButton("NE", BUTTON_POS_NORTHEAST));
        buttons.add(createButton("Mode", BUTTON_POS_EAST, BUTTON_MODE));
        buttons.add(createButton("Connect to CDU", BUTTON_POS_SOUTHWEST, BUTTON_CDU));
        buttons.add(createButton("Asetukset", BUTTON_POS_SOUTH, BUTTON_SETTINGS));
        //buttons.add(createButton("SW", BUTTON_POS_SOUTHWEST));
        //buttons.add(createButton("W", BUTTON_POS_WEST));
        //buttons.add(createButton("NW", BUTTON_POS_NORTHWEST));
    }

    public void addButton(String text, int pos, int id) {
        buttons.add(createButton(text, pos, id));
    }
    private Button createButton(String title, int pos, int id) {
        StagePoint point = new StagePoint(position.getStage());
        switch (pos) {
            case BUTTON_POS_NORTH:
                point.setScreenX(position.getScreenX());
                point.setScreenY(position.getScreenY() - 2*menuSprite.targetHeight/3);
                break;
            case BUTTON_POS_NORTHEAST:
                point.setScreenX(position.getScreenX() + 3*menuSprite.targetWidth/4);
                point.setScreenY(position.getScreenY() - 2*menuSprite.targetHeight/5);
                break;
            case BUTTON_POS_EAST:
                point.setScreenX(position.getScreenX() + menuSprite.targetWidth);
                point.setScreenY(position.getScreenY());
                break;
            case BUTTON_POS_SOUTHEAST:
                point.setScreenX(position.getScreenX() + 3*menuSprite.targetWidth/4);
                point.setScreenY(position.getScreenY() + 2*menuSprite.targetHeight/5);
                break;
            case BUTTON_POS_SOUTH:
                point.setScreenX(position.getScreenX());
                point.setScreenY(position.getScreenY() + 2*menuSprite.targetHeight/3);
                break;
            case BUTTON_POS_SOUTHWEST:
                point.setScreenX(position.getScreenX() - 3*menuSprite.targetWidth/4);
                point.setScreenY(position.getScreenY() + 2*menuSprite.targetHeight/5);
                break;
            case BUTTON_POS_WEST:
                point.setScreenX(position.getScreenX() - menuSprite.targetWidth);
                point.setScreenY(position.getScreenY());
                break;
            case BUTTON_POS_NORTHWEST:
                point.setScreenX(position.getScreenX() - 3*menuSprite.targetWidth/4);
                point.setScreenY(position.getScreenY() - 2*menuSprite.targetHeight/5);
                break;
            default:
                point.setScreenX(position.getScreenX());
                point.setScreenY(position.getScreenY() - menuSprite.targetHeight);
                break;
        }
        return new Button(guiView, title, point, id);
    }

    public void onDraw(Canvas canvas) {
        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("MENU", position.getScreenX() - 40, position.getScreenY(), textPaint);
        menuSprite.onDraw(canvas);
        for(Button b : buttons)
            b.onDraw(canvas);
    }

    public StagePoint getPosition() {
        return this.position;
    }

    public MenuSprite getMenuSprite() {
        return this.menuSprite;
    }

    public Button buttonTouched(StagePoint p) {
        for(Button b: buttons) {
            if(b.touched(p)) return b;
        }
        return null;
    }
}
