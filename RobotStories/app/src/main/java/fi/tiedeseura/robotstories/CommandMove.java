package fi.tiedeseura.robotstories;

/**
 * Created by mikko on 12.8.15.
 */
public class CommandMove extends Command {
    public static final int TARGET_ROBOT = 1;
    public static final int TARGET_STAGEPOINT = 2;

    private int targetType;
    private Robot targetRobot;
    private StagePoint targetPoint;

    public CommandMove() {
        this.type = Command.CMD_MOVE;
    }

    public CommandMove(StagePoint p) {
        this();
        this.targetType = this.TARGET_STAGEPOINT;
        this.targetPoint = p;
    }

    public CommandMove(Robot r) {
        this();
        this.targetType = this.TARGET_ROBOT;
        this.targetRobot = r;
    }

    public int getTargetType() {
        return this.targetType;
    }

    public Robot getTargetRobot() {
        return this.targetRobot;
    }

    public StagePoint getTargetPoint() {
        if(targetRobot != null) return targetRobot.getPosition();
        return this.targetPoint;
    }

    public String getCommandString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"cmd_type\": 1, \"target_type\": ");
        sb.append(this.targetType);
        if(this.targetType == this.TARGET_ROBOT) {
            sb.append(", \"target_robot\": ");
            sb.append(this.targetRobot.getId());
        } else if(this.targetType == this.TARGET_STAGEPOINT) {
            sb.append(", \"target_x\": ");
            sb.append(this.targetPoint.getStageX());
            sb.append(", \"target_y\": ");
            sb.append(this.targetPoint.getStageY());
        }
        sb.append("}");

        return sb.toString();
    }
}
