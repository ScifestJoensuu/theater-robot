package fi.tiedeseura.robotstories;

/**
 * Created by mikko on 6.8.15.
 */
public class Robot {
    private Sprite sprite;
    private String name;
    private String id;
    private StagePoint position;

    public Robot(String name, Sprite sprite, StagePoint point) {
        this.name = name;
        this.position = point;
        this.sprite = sprite;
        this.sprite.setPosition(point.getScreenX(), point.getScreenY());
        this.id = "-1";
    }

    public void setPosition(int x, int y) {
        this.position.setStageX(x);
        this.position.setStageY(y);
        this.sprite.setPosition(this.position);
    }
    public void setPosition(StagePoint p) {
        this.position = p;
        this.sprite.setPosition(p);
    }

    public String getName() {
        return this.name;
    }

    /*
    public float getX() {
        return this.position.getX();
    }

    public float getY() {
        return this.position.getY();
    }
    */
    public StagePoint getPosition() {
        return this.position;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public boolean didTouch(float x, float y) {
        return sprite.isCollision(x,y);
    }

    public String getId() {
        return this.id;
    }
    public void setDirectionTo(StagePoint p) {
        double dirDouble = (Math.atan2(p.getScreenX()-position.getScreenX(),p.getScreenY()-position.getScreenY()) / (Math.PI / 2) + 2);
        int direction = (int) Math.round(dirDouble) % Sprite.BMP_ROWS;
        //Log.d("", "dirdouble: "+dirDouble);
        //Log.d("", "dirint: "+direction);
        sprite.setDirection(direction);
    }
}
