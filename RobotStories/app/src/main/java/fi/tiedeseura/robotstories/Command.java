package fi.tiedeseura.robotstories;

/**
 * Created by mikko on 12.8.15.
 */
public class Command {
    public static final int CMD_MOVE = 1;
    public static final int CMD_LED = 2;
    public static final int CMD_SERVO = 3;

    //protected String cmdString;

    protected int type;

    public Command() {

    }

    public int getType() {
        return this.type;
    }

    public String getCommandString() {
        return "cmd_string";
    }

}
