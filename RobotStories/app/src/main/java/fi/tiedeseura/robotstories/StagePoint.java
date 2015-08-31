package fi.tiedeseura.robotstories;

/**
 * Created by mikko on 6.8.15.
 */
public class StagePoint {
    private Stage stage;
    private float screen_x;
    private float screen_y;
    private float stage_x;
    private float stage_y;

    public StagePoint(Stage stage) {
        this.stage = stage;
        this.screen_x = -1;
        this.screen_y = -1;
        this.stage_x = -1;
        this.stage_x = -1;
    }

    public StagePoint(Stage stage, float screen_x, float screen_y) {
        this.stage = stage;
        setScreenX(screen_x);
        setScreenY(screen_y);
    }

    public StagePoint(Stage stage, float screen_x, float screen_y, float stage_x, float stage_y) {
        this.stage = stage;
        this.screen_x = screen_x;
        this.screen_y = screen_y;
        this.stage_x = stage_x;
        this.stage_y = stage_y;
    }

    public void setStageX(float x) {
        this.stage_x = x;
        this.screen_x = calculateScreenXFromStageX(x);
    }

    public void setStageY(float y) {
        this.stage_y = y;
        this.screen_y = calculateScreenYFromStageY(y);
    }

    public float getStageX() {
        return this.stage_x;
    }

    public float getStageY() {
        return this.stage_y;
    }

    public void setScreenX(float x) {
        this.screen_x = x;
        this.stage_x = calculateStageXFromScreenX(x);
    }

    public void setScreenY(float y) {
        this.screen_y = y;
        this.stage_y = calculateStageYFromScreenY(y);
    }

    public float getScreenX() {
        return this.screen_x;
    }

    public float getScreenY() {
        return this.screen_y;
    }


    public int calculateStageXFromScreenX(float x) {
        int stageWidth = stage.getWidth();
        int stageWidthPx = stage.getStageWidthPx();
        float m = (float)stageWidthPx / (float)stageWidth;
        return (int)((x - stage.getTopLeft().getScreenX()) / m);
    }

    public int calculateStageYFromScreenY(float y) {
        int stageHeight = stage.getHeight();
        int stageHeightPx = stage.getStageHeightPx();
        float m = (float)stageHeightPx / (float)stageHeight;
        return (int)((y - stage.getTopLeft().getScreenX()) / m);
    }

    public float calculateScreenXFromStageX(float x) {
        int stageWidth = stage.getWidth();
        int stageWidthPx = stage.getStageWidthPx();
        float m = (float)stageWidthPx / (float)stageWidth;
        return stage.getTopLeft().getScreenX() + x * m;
    }

    public float calculateScreenYFromStageY(float y) {
        int stageHeight = stage.getHeight();
        int stageHeightPx = stage.getStageHeightPx();
        float m = (float)stageHeightPx / (float)stageHeight;
        return stage.getTopLeft().getScreenY() + y * m;
    }

    public Stage getStage() {
        return this.stage;
    }

    public String toString() {
        return this.getStageX() + ", " + this.getStageY();
    }
}
