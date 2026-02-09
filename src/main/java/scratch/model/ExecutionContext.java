package scratch.model;

import java.util.ArrayList;
import java.util.List;

public class ExecutionContext {
    private int x ;
    private int y ;
    private int direction ;
    private boolean penDown ;
    private List<Segment> segmentList = new ArrayList<>();

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getDirection() {
        return direction;
    }

    public boolean isPenDown() {
        return penDown;
    }

    public List<Segment> getSegmentList() {
        return segmentList;
    }
    public int getSegmentsCount(List<Segment> segments){
        return segments.size();
    }
    public void move(int indx){

    }
    public void turnLeft(int indx){

    }
    public void turnRight(int indx){

    }
    public void penUp(int indx){

    }
    public void penDown(int indx){

    }
    public void reset(){
        
    }
}
