package scratch.model;

import java.util.ArrayList;
import java.util.List;

public class ExecutionContext {
    private static final int DEFAULT_X = 250;
    private static final int DEFAULT_Y = 250 ;
    private static final int DEFAULT_DIRECTION = 0 ;
    private static final boolean DEFAULT_PEN_DOWN = true ;

    private int x , y , direction ;
    private boolean penDown ;
    private List<Segment> segments ;

    public ExecutionContext(){
        this.segments = new ArrayList<>();
        reset();
    }
    // FONCTION

    public void move(int distance){
        double radians =  Math.toRadians(direction -90);
        int oldX = x , oldY = y ;

        x += (int) Math.round(distance * Math.cos(radians));
        y += (int) Math.round(distance * Math.sin(radians));

        if (penDown) {
            segments.add(new Segment(oldX , oldY , x , y));
        }
    }
    public void turnLeft(int angle){
        direction = (direction - angle + 360) % 360 ;
    }
    public void turnRight(int angle){
        direction = (direction + angle) % 360 ;
    }
    public void penUp(){
        this.penDown = false ;
    }
    public void penDown(){
        this.penDown = true ;
    }
    public void reset(){
        x = DEFAULT_X ;
        y = DEFAULT_Y ;
        direction = DEFAULT_DIRECTION ;
        penDown = DEFAULT_PEN_DOWN ;
        segments.clear();
    }

    // GETTERS

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPositionTortueX() {
        return x - DEFAULT_X;
    }

    public int getPositionTortueY() {
        return DEFAULT_Y - y;
    }

    public int getDirection() {
        return direction;
    }

    public List<Segment> getSegments() {
        return segments;
    }
    public int getSegmentsCount(List<Segment> segments){
        return segments.size();
    }
    public boolean isPenDown(){
        return penDown;
    }
}
