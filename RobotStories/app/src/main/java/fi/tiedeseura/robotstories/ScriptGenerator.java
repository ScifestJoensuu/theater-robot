package fi.tiedeseura.robotstories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mikko on 6.8.15.
 */
public class ScriptGenerator {
    private Play play;

    public ScriptGenerator() {

    }

    public ScriptGenerator(Play play) {
        this();
        this.play = play;
    }

    public String generateScript() {
        StringBuilder sb = new StringBuilder();
        if(play != null) {
            ArrayList<Robot> robots = (ArrayList<Robot>)play.getRobots();
            ArrayList<Page> pages = (ArrayList<Page>)play.getPages();
            sb.append("{\"play\":\"");
            sb.append(play.getName());
            sb.append("\",");
            sb.append("\"stage\": {");
            sb.append("\"width\":");
            sb.append(play.getStage().getWidth());
            sb.append(",\"height\":");
            sb.append(play.getStage().getHeight());
            sb.append("},\"robots\":[");
            for(int i = 0; i < robots.size(); i++) {
                Robot r = robots.get(i);
                sb.append("{\"id\":\"");
                sb.append(r.getId());
                sb.append("\", \"name\":\"");
                sb.append(r.getName());
                sb.append("\"}");
                if(i < robots.size()-1) sb.append(",");
            }
            sb.append("], \"pages\": [");
            for(int i = 0; i < pages.size(); i++) {
                Page p = pages.get(i);
                HashMap<Robot, Command> cmds = (HashMap<Robot, Command>)p.getCommands();
                sb.append("{\"number\":");
                sb.append(p.getPageNumber());
                sb.append(", \"commands\":[");
                Iterator it = cmds.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry entry = (Map.Entry)it.next();
                    Command c = (Command)entry.getValue();
                    //Command c = cmds.get(r);
                    sb.append(c.getCommandString());
                    if(it.hasNext()) sb.append(",");
                }
                sb.append("]}");
                if(i < pages.size()-1) sb.append(",");
            }
            sb.append("]}");
            return sb.toString();
        }
        return null;
    }
}
