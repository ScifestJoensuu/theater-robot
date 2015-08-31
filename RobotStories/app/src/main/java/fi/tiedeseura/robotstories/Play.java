package fi.tiedeseura.robotstories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mikko on 12.8.15.
 */
public class Play {
    private String name;
    private Stage stage;
    private List<Page> pages;
    private List<Robot> robots;
    private int currentPage;

    public Play() {
        this.pages = new ArrayList<Page>();
        this.robots = new ArrayList<Robot>();
        addPage();
        this.currentPage = 1;
    }

    public Play(String name, Stage stage) {
        this();
        this.name = name;
        this.stage = stage;
    }

    public void addRobot(Robot r) {
        this.robots.add(r);
        this.getCurrentPage().setStartPosition(r);
    }

    public List<Robot> getRobots() {
        return this.robots;
    }

    public Stage getStage() {
        return this.stage;
    }

    public String getName() {
        return this.name;
    }

    public void addPage() {
        this.pages.add(new Page(pages.size()+1));
        if(pages.size() > 1)
            this.pages.get(pages.size()-1).setStartPositions(pages.get(pages.size()-2).getEndPositions());
    }

    public Page getCurrentPage() {
        return this.pages.get(currentPage-1);
    }

    public List<Page> getPages() {
        return this.pages;
    }
    public int getNumberOfPages() {
        return this.pages.size();
    }

    public Page nextPage() {
        if(this.currentPage+1 <= this.pages.size()) {
            this.currentPage++;
            loadPositionsFromPage();
            return getCurrentPage();
        }
        return null;
    }

    public Page previousPage() {
        if(this.currentPage-1 >= 1) {
            this.currentPage--;
            loadPositionsFromPage();
            return getCurrentPage();
        }
        return null;
    }

    private void loadPositionsFromPage() {
        HashMap<Robot, StagePoint> positions = getCurrentPage().getStartPositions();
        if(positions == null) return;
        for(Robot r: robots) {
            r.setPosition(positions.get(r));
        }
    }

    private boolean updatePositionsFromPage(Page p) {
        p.calculateEndPositions(robots);
        if(p.getExcept() != null) return false;
        if(p.getPageNumber() < pages.size()) {
            pages.get(p.getPageNumber()).setStartPositions(p.getEndPositions());
            pages.get(p.getPageNumber()).calculateEndPositions(robots);
        }
        return true;
    }

    public void currentPageUpdated() {
        this.updatePositionsFromPage(this.getCurrentPage());
    }

}
