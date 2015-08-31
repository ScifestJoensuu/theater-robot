package fi.tiedeseura.robotstories;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by mikko on 5.8.15.
 */
public class GUIView extends SurfaceView {
    public static int screenWidth;
    public static int screenHeight;
    public static final int UIMODE_SCRIPTING = 0;
    public static final int UIMODE_INTERACTIVE = 1;
    public static final int UISTATE_IDLE = 0;
    public static final int UISTATE_SELECTING_TARGET = 1;
    public static final int UISTATE_STAGEMENU_OPEN = 2;
    private SurfaceHolder holder;
    private GUILoopThread guiLoopThread;
    private CDUConnection cduConnection;
    //private List<Robot> robots = new ArrayList<Robot>();
    private SelectedSprite selectedSprite;
    private TargetSprite targetSprite;
    private TargetArrowSprite targetArrowSprite;
    private Robot selectedRobot;
    private Robot targetRobot;
    private StagePoint targetPoint;

    private Button okButton;
    private Button nextButton;
    private Button previousButton;

    private Bitmap background;

    private Play play;

    private Stage stage;
    private Menu menu;
    private MenuSprite menuSprite;

    private int uiState = UISTATE_IDLE;
    private int uiMode = UIMODE_SCRIPTING;

    private float pressStartX = -1;
    private float pressStartY = -1;
    private float pressTargetX = -1;
    private float pressTargetY = -1;

    public GUIView(Activity activity) {
        super(activity);

        guiLoopThread = new GUILoopThread(this);
        cduConnection = new CDUConnection(activity);

        holder = getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                boolean retry = true;
                guiLoopThread.setRunning(false);
                cduConnection.setRunning(false);
                while (retry) {
                    try {
                        guiLoopThread.join();
                        cduConnection.join();
                        retry = false;
                    } catch (InterruptedException e) {
                    }
                }
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                screenWidth = getWidth();
                screenHeight = getHeight();
                Log.d("", "ScreenWidth: " + screenWidth);
                Log.d("", "ScreenHeight:" + screenHeight);

                stage = new Stage();
                stage.setTopLeft((float)(screenWidth*0.1), (float)(screenHeight*0.1));
                stage.setTopRight((float)(screenWidth - screenWidth*0.05), (float)(screenHeight*0.1));
                stage.setBottomRight((float)(screenWidth - screenWidth*0.05), (float)(screenHeight - screenHeight*0.1));
                stage.setBottomLeft((float)(screenWidth*0.1), (float)(screenHeight - screenHeight*0.1));
                play = new Play("Testi", stage);

                createRobots();

                guiLoopThread.setRunning(true);
                guiLoopThread.start();
                cduConnection.setRunning(true);
                cduConnection.start();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });

        background = BitmapFactory.decodeResource(getResources(), R.drawable.sketch);
        selectedSprite = new SelectedSprite(this, -1, -1);
        targetSprite = new TargetSprite(this, -1, -1);
        targetArrowSprite = new TargetArrowSprite(this, -1, -1);
        menuSprite = new MenuSprite(this, -1, -1);
    }

    private void createRobots() {
        //for(int i = 0; i < 1; i++)
        //robots.add(createRobot("Rami", R.drawable.male1));
        //robots.add(createRobot("Pena", R.drawable.male2));
        //robots.add(createRobot("Keijo", R.drawable.male3));
        //robots.add(createRobot("Pirkko", R.drawable.female1));
        //robots.add(createRobot("Riitta", R.drawable.female2));
        //robots.add(createRobot("Sirpa", R.drawable.female3));
        Robot r1 = createRobot("Rami", R.drawable.male1);
        r1.setPosition(120, 60);
        Robot r2 = createRobot("Pena", R.drawable.male2);
        r2.setPosition(260, 100);
        Robot r3 = createRobot("Pirkko", R.drawable.female1);
        r3.setPosition(180, 160);
        play.addRobot(r1);
        play.addRobot(r2);
        play.addRobot(r3);
    }

    private Robot createRobot(String name, int resouce) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
        Sprite sprite = new Sprite(this,bmp);

        return new Robot(name, sprite, stage.getRandomPosition());
    }

    public Stage getStage() {
        return this.stage;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(canvas == null) return;
        canvas.drawColor(Color.BLACK);

        Rect dest = new Rect(0, 0, getWidth(), getHeight());
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.drawBitmap(background, null, dest, paint);

        Paint fgPaintSel = new Paint();
        fgPaintSel.setARGB(255, 0, 0,0);
        fgPaintSel.setStyle(Paint.Style.STROKE);
        fgPaintSel.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        fgPaintSel.setStrokeWidth(10);

        Paint textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        //canvas.drawPaint(textPaint);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);

        // STAGE
        canvas.drawLine(stage.getTopLeft().getScreenX(), stage.getTopLeft().getScreenY(), stage.getTopRight().getScreenX(), stage.getTopRight().getScreenY(), fgPaintSel);
        canvas.drawLine(stage.getTopRight().getScreenX(), stage.getTopRight().getScreenY(), stage.getBottomRight().getScreenX(), stage.getBottomRight().getScreenY(), fgPaintSel);
        canvas.drawLine(stage.getBottomRight().getScreenX(), stage.getBottomRight().getScreenY(), stage.getBottomLeft().getScreenX(), stage.getBottomLeft().getScreenY(), fgPaintSel);
        canvas.drawLine(stage.getBottomLeft().getScreenX(), stage.getBottomLeft().getScreenY(), stage.getTopLeft().getScreenX(), stage.getTopLeft().getScreenY(), fgPaintSel);

        StagePoint center_x = new StagePoint(stage);
        center_x.setStageX(stage.getWidth()/2);
        center_x.setStageY(10);
        StagePoint center_y = new StagePoint(stage);
        center_y.setStageX(5);
        center_y.setStageY(stage.getHeight()/2);

        textPaint.setColor(Color.DKGRAY);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC));
        canvas.drawText(stage.getWidth() + "cm", center_x.getScreenX(), center_x.getScreenY(), textPaint);
        canvas.save();
        canvas.rotate(90, center_y.getScreenX(), center_y.getScreenY());
        canvas.drawText(stage.getHeight() + "cm", center_y.getScreenX() - 50, center_y.getScreenY(), textPaint);
        canvas.restore();

        String modeText = "interactive";
        if(uiMode == this.UIMODE_SCRIPTING) modeText = "scripting";
        StagePoint modePoint = new StagePoint(stage);
        modePoint.setScreenX(20);
        modePoint.setScreenY(40);
        //textPaint.setColor(Color.RED);
        canvas.drawText(modeText, modePoint.getScreenX(), modePoint.getScreenY(), textPaint);

        if(uiMode == this.UIMODE_SCRIPTING) {
            StagePoint pageNumberPoint = new StagePoint(stage);
            pageNumberPoint.setScreenX(screenWidth-80);
            pageNumberPoint.setScreenY(40);
            canvas.drawText("p" + play.getCurrentPage().getPageNumber(), pageNumberPoint.getScreenX(), pageNumberPoint.getScreenY(), textPaint);
        }


        if(pressStartX != -1 && pressStartY != -1 && pressTargetX != -1 && pressTargetY != -1) {
            fgPaintSel.setARGB(255, 255, 0, 0);
            canvas.drawLine(pressStartX, pressStartY, pressTargetX, pressTargetY, fgPaintSel);
        }

        /*
        for (int i = temps.size() - 1; i >= 0; i--) {
            temps.get(i).onDraw(canvas);
        }
        */

        if(targetRobot != null && selectedRobot != null) {
            fgPaintSel.setStrokeWidth(10);
            fgPaintSel.setARGB(255, 255, 0, 0);
            canvas.drawLine(selectedRobot.getPosition().getScreenX(), selectedRobot.getPosition().getScreenY(), targetRobot.getPosition().getScreenX(), targetRobot.getPosition().getScreenY(), fgPaintSel);
        } else if(targetPoint != null && selectedRobot != null) {
            fgPaintSel.setStrokeWidth(10);
            fgPaintSel.setARGB(255, 255, 0, 0);
            canvas.drawLine(selectedRobot.getPosition().getScreenX(), selectedRobot.getPosition().getScreenY(), targetPoint.getScreenX(), targetPoint.getScreenY(), fgPaintSel);
        }

        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setColor(Color.BLACK);

        if(targetRobot != null) {
            canvas.drawText(targetRobot.getName(), targetRobot.getPosition().getScreenX() + Sprite.targetWidth, targetRobot.getPosition().getScreenY(), textPaint);
            canvas.drawText(targetRobot.getPosition().getStageX() + ", " + targetRobot.getPosition().getStageY(), targetRobot.getPosition().getScreenX() + Sprite.targetWidth, targetRobot.getPosition().getScreenY() + 25, textPaint);
            canvas.drawText(targetRobot.getPosition().getScreenX() + ", " + targetRobot.getPosition().getScreenY(), targetRobot.getPosition().getScreenX() + Sprite.targetWidth, targetRobot.getPosition().getScreenY() + 50, textPaint);
        }
        if(targetPoint != null) {
            canvas.drawText(targetPoint.getStageX() + ", " + targetPoint.getStageY(), targetPoint.getScreenX() + Sprite.targetWidth, targetPoint.getScreenY(), textPaint);
            canvas.drawText(targetPoint.getScreenX() + ", " + targetPoint.getScreenY(), targetPoint.getScreenX() + Sprite.targetWidth, targetPoint.getScreenY() + 25, textPaint);
        }
        if(selectedRobot != null) {
            canvas.drawText(selectedRobot.getName(), selectedRobot.getPosition().getScreenX() + Sprite.targetWidth, selectedRobot.getPosition().getScreenY(), textPaint);
            canvas.drawText(selectedRobot.getPosition().getStageX() + ", " + selectedRobot.getPosition().getStageY(), selectedRobot.getPosition().getScreenX() + Sprite.targetWidth, selectedRobot.getPosition().getScreenY() + 25, textPaint);
            canvas.drawText(selectedRobot.getPosition().getScreenX() + ", " + selectedRobot.getPosition().getScreenY(), selectedRobot.getPosition().getScreenX() + Sprite.targetWidth, selectedRobot.getPosition().getScreenY() + 50, textPaint);

            Command c = this.play.getCurrentPage().getCommand(selectedRobot);
            if(c != null) {
                if(c.getType() == Command.CMD_MOVE) {
                    StagePoint target = ((CommandMove) c).getTargetPoint();
                    fgPaintSel.setStrokeWidth(5);
                    fgPaintSel.setARGB(255, 255, 0, 0);
                    canvas.drawLine(selectedRobot.getPosition().getScreenX(), selectedRobot.getPosition().getScreenY(), target.getScreenX(), target.getScreenY(), fgPaintSel);
                }
            }
        }
        if(okButton != null) {
            okButton.onDraw(canvas);
        }
        if(nextButton != null) {
            nextButton.onDraw(canvas);
        }
        if(previousButton != null) {
            previousButton.onDraw(canvas);
        }

        if(play.getRobots() != null) {
            Collections.sort(play.getRobots(), new RobotComparator());
            for (Robot robot : play.getRobots()) {
                robot.getSprite().onDraw(canvas);

                Command c = this.play.getCurrentPage().getCommand(robot);
                if (c != null) {
                    canvas.drawText("C", robot.getPosition().getScreenX() - Sprite.targetWidth, robot.getPosition().getScreenY(), textPaint);
                }
            }
        }

        if(targetRobot != null) {
            targetSprite.setPosition(targetRobot.getPosition().getScreenX(), targetRobot.getPosition().getScreenY());
            targetSprite.onDraw(canvas);
        } else if(targetPoint != null) {
            targetSprite.setPosition(targetPoint.getScreenX(), targetPoint.getScreenY());
            targetArrowSprite.setPosition(targetPoint.getScreenX(), targetPoint.getScreenY());
            targetSprite.onDraw(canvas);
            targetArrowSprite.onDraw(canvas);
        }

        if(selectedRobot != null) {
            selectedSprite.setPosition(selectedRobot.getPosition().getScreenX(), selectedRobot.getPosition().getScreenY());
            selectedSprite.onDraw(canvas);
        }

        if(uiState == UISTATE_STAGEMENU_OPEN && menu != null) {
            Rect layer = new Rect(0, 0, getWidth(), getHeight());
            Paint layerp = new Paint();
            layerp.setARGB(100, 0, 0, 0);
            layerp.setStyle(Paint.Style.FILL);
            canvas.drawRect(layer, layerp);

            if(menu != null) menu.onDraw(canvas);

            //Log.d("", menu.getPosition().getScreenX() + ", " + menu.getPosition().getScreenY());
        }
    }

    private void connectToCDU() {
        Log.d("", "Connecting to CDU");
        closeStageMenu();
        cduConnection.findCDU();
    }

    private void openStageMenu(StagePoint p) {
        uiState = UISTATE_STAGEMENU_OPEN;
        Log.d("", "Stage menu opened");
        this.menu = new Menu(this, menuSprite, p);
        if(this.uiMode == UIMODE_SCRIPTING) {
            int current_page = play.getCurrentPage().getPageNumber();
            int num_of_pages = play.getNumberOfPages();
            this.menu.addButton("Add Page", Menu.BUTTON_POS_NORTH, Menu.BUTTON_ADD);
            if(num_of_pages > current_page) {
                this.menu.addButton("Next", Menu.BUTTON_POS_NORTHEAST, Menu.BUTTON_NEXT);
            }
            if(current_page > 1) {
                this.menu.addButton("Previous", Menu.BUTTON_POS_NORTHWEST, Menu.BUTTON_PREVIOUS);
            }
            this.menu.addButton("Build", Menu.BUTTON_POS_SOUTHEAST, Menu.BUTTON_BUILD);
        }
    }

    private void closeStageMenu() {
        uiState = UISTATE_IDLE;
        Log.d("", "Stage menu closed");
        this.menu = null;
    }

    private void openSettings() {
        closeStageMenu();
        Log.d("", "Settings opened");
    }

    private void openStageSettings() {
        closeStageMenu();
        Log.d("", "Stage settings opened");
    }

    private void toggleMode() {
        closeStageMenu();
        Log.d("", "Toggle mode");
        if(uiMode == this.UIMODE_INTERACTIVE) uiMode = this.UIMODE_SCRIPTING;
        else uiMode = this.UIMODE_INTERACTIVE;
    }

    private void addPage() {
        closeStageMenu();
        Log.d("", "Add a page");
        this.play.addPage();
        nextPage();
    }

    private void nextPage() {
        closeStageMenu();
        Log.d("", "Next page");
        this.play.currentPageUpdated();
        this.play.nextPage();
        addPageButtons();
    }

    private void previousPage() {
        closeStageMenu();
        Log.d("", "Previous page");
        this.play.currentPageUpdated();
        this.play.previousPage();
        addPageButtons();
    }

    private void animateMovement() {
        HashMap<Robot, StagePoint> endPositions = this.play.getCurrentPage().getEndPositions();
        Set<Robot> robots = endPositions.keySet();
        while(true) {
            for(Robot r: robots) {
                StagePoint curPos = r.getPosition();
                StagePoint destination = endPositions.get(r);
            }
        }
    }

    private void buildPlay() {
        ScriptGenerator sg = new ScriptGenerator(this.play);
        String script = sg.generateScript();
        cduConnection.sendMessage(script);
        Log.d("", script);
    }

    private void addPageButtons() {
        if(this.play.getCurrentPage().getPageNumber() < this.play.getNumberOfPages()) {
            this.nextButton = new Button(this, "-->", new StagePoint(stage, getWidth() - Button.getWidthEstimate("-->"), getHeight() - 50), 20);
            this.nextButton.setTextSize(50);
        } else {
            this.nextButton = null;
        }
        if(this.play.getCurrentPage().getPageNumber() > 1) {
            this.previousButton = new Button(this, "<--", new StagePoint(stage, Button.getWidthEstimate("<--"), getHeight() - 50), 21);
            this.previousButton.setTextSize(50);
        } else {
            this.previousButton = null;
        }
    }

    private void clearTargets() {
        selectedRobot = null;
        targetRobot = null;
        targetPoint = null;
        okButton = null;
    }

    final GestureDetector gestureDetector = new GestureDetector(this.getContext(), new GestureDetector.SimpleOnGestureListener() {
        public void onLongPress(MotionEvent e) {
            Log.e("", "Longpress detected");
            float x = e.getX();
            float y = e.getY();
            StagePoint tmp_point = new StagePoint(stage, x, y);
            switch(uiState) {
                case UISTATE_IDLE:
                    synchronized (getHolder()) {
                        boolean found = false;
                        for (int i = play.getRobots().size() - 1; i >= 0; i--) {
                            Robot r = play.getRobots().get(i);
                            if (r.didTouch(x, y)) {
                                if(r == selectedRobot) break;
                                found = true;
                                selectedRobot = r;
                                uiState = UISTATE_SELECTING_TARGET;
                                break;
                            }
                        }
                        if(!found) {
                            selectedRobot = null;
                            targetRobot = null;
                            targetPoint = null;
                            openStageMenu(tmp_point);
                        }
                    }
                    break;
                case UISTATE_SELECTING_TARGET:
                    synchronized (getHolder()) {
                        selectedRobot = null;
                        targetRobot = null;
                        targetPoint = null;
                        uiState = UISTATE_IDLE;
                    }
                    break;
                default:
                    break;
            }
        }

        public boolean onDoubleTap (MotionEvent e) {
            Log.e("", "onDoubleTap");
            return true;
        }

        public boolean onDoubleTapEvent (MotionEvent e) {
            Log.e("", "DoubleTapEvent");
            return true;
        }

        public boolean onDown(MotionEvent e) {
            Log.e("", "Down detected");
            pressStartX = e.getX();
            pressStartY = e.getY();
            return true;
        }
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e("", "Fling detected");
            pressStartX = -1;
            pressStartY = -1;
            pressTargetX = -1;
            pressTargetY = -1;
            return true;
        }
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e("", "Scroll detected");
            pressTargetX = e2.getX();
            pressTargetY = e2.getY();
            return true;
        }

        public void onShowPress (MotionEvent e) {
            Log.e("", "onShowPress");
        }
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.e("", "Singletap detected");
            float x = e.getX();
            float y = e.getY();
            StagePoint point = new StagePoint(stage, x, y);
            switch(uiState) {
                case UISTATE_IDLE:
                    synchronized (getHolder()) {
                        if(nextButton != null) {
                            if(nextButton.touched(point)) {
                                nextPage();
                                break;
                            }
                        }
                        if(previousButton != null) {
                            if(previousButton.touched(point)) {
                                previousPage();;
                                break;
                            }
                        }
                        boolean found = false;
                        for (int i = play.getRobots().size() - 1; i >= 0; i--) {
                            Robot r = play.getRobots().get(i);
                            if (r.didTouch(x, y)) {
                                if(r == selectedRobot) break;
                                found = true;
                                selectedRobot = r;
                                uiState = UISTATE_SELECTING_TARGET;
                                break;
                            }
                        }
                        if(!found) {
                            selectedRobot = null;
                            targetRobot = null;
                            targetPoint = null;
                        }
                    }
                    break;
                case UISTATE_SELECTING_TARGET:
                    synchronized (getHolder()) {
                        boolean found = false;
                        boolean touchSelf = false;
                        if(okButton != null) {
                            boolean t = okButton.touched(point);
                            if(t) {
                                CommandMove c = null;
                                if(targetRobot != null) c = new CommandMove(targetRobot);
                                else if(targetPoint != null) c = new CommandMove(targetPoint);
                                play.getCurrentPage().addCommand(selectedRobot, c);
                                clearTargets();
                                uiState = UISTATE_IDLE;
                                break;
                            }
                        }
                        for (int i = play.getRobots().size() - 1; i >= 0; i--) {
                            Robot r = play.getRobots().get(i);
                            if (r.didTouch(x, y)) {
                                if(r == selectedRobot) {
                                    touchSelf = true;
                                    break;
                                }
                                found = true;
                                targetPoint = null;
                                targetRobot = r;
                                okButton = new Button(GUIView.this, "OK", new StagePoint(stage, r.getPosition().getScreenX() + 100, r.getPosition().getScreenY() + 100), 100);
                                selectedRobot.setDirectionTo(targetRobot.getPosition());
                                break;
                            }
                        }
                        if(!found && !touchSelf) {
                            targetRobot = null;
                            targetPoint = new StagePoint(getStage());
                            targetPoint.setScreenX(x);
                            targetPoint.setScreenY(y);
                            okButton = new Button(GUIView.this, "OK", new StagePoint(stage, targetPoint.getScreenX() + 100, targetPoint.getScreenY() + 100), 100);
                            selectedRobot.setDirectionTo(targetPoint);
                        } else if (touchSelf) {
                            clearTargets();
                            uiState = UISTATE_IDLE;
                        }
                    }
                    break;
                case UISTATE_STAGEMENU_OPEN:
                    //synchronized (getHolder()) {
                        Button b = menu.buttonTouched(point);
                        if(b != null) {
                            try{Thread.sleep(200);}catch(InterruptedException ee){}
                            switch (b.getID()) {
                                case Menu.BUTTON_SETTINGS:
                                    openSettings();
                                    break;
                                case Menu.BUTTON_STAGE:
                                    openStageSettings();
                                    break;
                                case Menu.BUTTON_MODE:
                                    toggleMode();
                                    break;
                                case Menu.BUTTON_ADD:
                                    addPage();
                                    break;
                                case Menu.BUTTON_NEXT:
                                    nextPage();
                                    break;
                                case Menu.BUTTON_PREVIOUS:
                                    previousPage();
                                    break;
                                case Menu.BUTTON_BUILD:
                                    buildPlay();
                                    break;
                                case Menu.BUTTON_CDU:
                                    connectToCDU();
                                    break;
                                default:
                                    Log.d("", "Button not recognized..");
                                    break;
                            }
                        } else {
                            closeStageMenu();
                        }
                    //}
                    break;
                default:
                    break;
            }

            return true;
        }
        public boolean onSingleTapUp (MotionEvent e) {
            Log.e("", "SingletapUp detected");
            return true;
        }
    });

    public boolean onTouchEvent(MotionEvent event) {
        boolean consumed = gestureDetector.onTouchEvent(event);
        if(event.getAction() == MotionEvent.ACTION_UP) {
            Log.e("", "UP");
            pressStartX = -1;
            pressStartY = -1;
            pressTargetX = -1;
            pressTargetY = -1;
            return false;
        }
        return consumed;
    };
    /*
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (System.currentTimeMillis() - lastClick > 500) {
            lastClick = System.currentTimeMillis();
            float x = event.getX();
            float y = event.getY();
            synchronized (getHolder()) {
                for (int i = sprites.size() - 1; i >= 0; i--) {
                    Sprite sprite = sprites.get(i);
                    if (sprite.isCollision(x, y)) {
                        sprite.toggleSelect();
                        //sprites.remove(sprite);
                        //temps.add(new TempSprite(temps, this, x, y, bmpTmp));
                        break;
                    }
                }
            }
        }

        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                moveTouch(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                endTouch();
                invalidate();
                break;
        }

        return true;
    }
*/
    private void startTouch(float x, float y){
        Log.d("touch", "start_touch");
    }

    private void moveTouch(float x, float y) {
        Log.d("touch", "move_touch");
    }

    private void endTouch() {
        Log.d("touch", "end_touch");
    }

    public class RobotComparator implements Comparator<Robot> {
        public int compare(Robot object1, Robot object2) {
            return (int)(object1.getPosition().getScreenY() - object2.getPosition().getScreenY());
        }
    }
}

