package fi.tiedeseura.robotstories;

import android.util.Log;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mikko on 10.8.15.
 */
public class Page {
    private int pageNumber;
    private Map<Robot, Command> commands;
    private Map<Robot, StagePoint> startPositions = null;
    private Map<Robot, StagePoint> endPositions = null;

    private Exception except = null;

    public Page(int number) {
        this.pageNumber = number;
        this.commands = new HashMap<Robot, Command>();
        this.startPositions = new HashMap<Robot, StagePoint>();
        this.endPositions = new HashMap<Robot, StagePoint>();
    }

    public void setStartPositions(HashMap<Robot, StagePoint> positions) {
        this.startPositions = positions;
    }

    public void setStartPosition(Robot r) {
        this.startPositions.put(r, r.getPosition());
    }

    public HashMap<Robot, StagePoint> getStartPositions() {
        return (HashMap<Robot,StagePoint>)this.startPositions;
    }

    public void addCommand(Robot r, Command c) {
        this.commands.put(r, c);
        //ArrayList<Robot> tmp = new ArrayList<Robot>(commands.keySet().toArray());
        calculateEndPositions(commands.keySet());
    }

    public Command getCommand(Robot r) {
        return this.commands.get(r);
    }

    public Map<Robot, Command> getCommands() {
        return this.commands;
    }

    public Exception getExcept() {
        return this.except;
    }

    public HashMap<Robot, StagePoint> getEndPositions() {
        return (HashMap<Robot, StagePoint>)this.endPositions;
    }

    public Collection<Robot> calculateEndPositions(Collection<Robot> robots) {
        endPositions = new HashMap<Robot, StagePoint>();
        try {
            for(Robot r: robots) {
                StagePoint tmp = endPositions.get(r);
                if(tmp == null)
                   tmp = calculateEndPosition(r, null);
                Log.d("", "End position for robot '" + r.getName() + "' is " + tmp.toString());
                this.endPositions.put(r, tmp);
                //r.setPosition(tmp);
            }
        } catch(EndlessMovementException e) {
            this.except = e;
            e.printStackTrace();
            return null;
        }
        except = null;
        return robots;
    }

    private StagePoint calculateEndPosition(Robot r, Robot start) throws EndlessMovementException {
        StagePoint point = null;
        Command cmd = commands.get(r);
        if(cmd != null && cmd.type == Command.CMD_MOVE) {
            CommandMove cmdm = (CommandMove)cmd;
            if(cmdm.getTargetType() == CommandMove.TARGET_ROBOT) {
                Robot target = cmdm.getTargetRobot();
                if(start == null) {
                    point = calculateEndPosition(target, r);
                } else if(start != target) {
                    point = calculateEndPosition(target, start);
                } else {
                    throw new EndlessMovementException();
                }
            } else {
                point = cmdm.getTargetPoint();
            }
        } else {
            point = r.getPosition();
        }
        endPositions.put(r, point);
        return point;
    }

    public class EndlessMovementException extends Exception {
        public EndlessMovementException() {
            super("Endless movement detected..");
        }
    }

    public int getPageNumber() {
        return this.pageNumber;
    }
}
