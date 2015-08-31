package fi.tiedeseura.robotstories;

import java.util.Random;

/**
 * Created by mikko on 6.8.15.
 */
public class Stage {
    private StagePoint topLeft;
    private StagePoint topRight;
    private StagePoint bottomRight;
    private StagePoint bottomLeft;

    public int width;
    public int height;

    public Stage() {
        this.width = 400;
        this.height = 250;
    }

    public int getStageWidthPx() {
        return (int)(topRight.getScreenX() - topLeft.getScreenX());
    }

    public int getStageHeightPx() {
        return (int)(bottomLeft.getScreenY() - topLeft.getScreenY());
    }

    public StagePoint getTopLeft() {
        return this.topLeft;
    }

    public StagePoint getTopRight() {
        return this.topRight;
    }

    public StagePoint getBottomRight() {
        return this.bottomRight;
    }

    public StagePoint getBottomLeft() {
        return this.bottomLeft;
    }

    public void setTopLeft(float x, float y) {
        this.topLeft = new StagePoint(this, x, y, 0, 0);
    }

    public void setTopRight(float x, float y) {
        this.topRight = new StagePoint(this, x, y, this.width, 0);
    }

    public void setBottomRight(float x, float y) {
        this.bottomRight = new StagePoint(this, x, y, this.width, this.height);
    }

    public void setBottomLeft(float x, float y) {
        this.bottomLeft = new StagePoint(this, x, y, 0, this.height);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public StagePoint getRandomPosition() {
        Random random = new Random();
        float rx = Sprite.targetWidth + topLeft.getScreenX() + random.nextInt(this.getStageWidthPx() - Sprite.targetWidth);
        float ry = Sprite.targetHeight + topLeft.getScreenY() + random.nextInt(this.getStageHeightPx() - Sprite.targetHeight);
        StagePoint tmp = new StagePoint(this);
        tmp.setScreenX(rx);
        tmp.setScreenY(ry);
        return tmp;
    }
}
