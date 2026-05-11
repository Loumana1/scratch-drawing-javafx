package scratch.model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PolygonAction extends Action{

    private  String size ;
    private  String num ;

    public PolygonAction(){
        this.size = "30";
        this.num = "6";
    }
    public PolygonAction(String size ,String num ) {
        this.size = size;
        this.num = num;
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        int Nsize = e.resolveValue(size);
        int Nnum = e.resolveValue(num);

        return Nsize > 30 && Nnum > 6;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getSize() {
        return size;
    }

    public String getNum() {
        return num;
    }

    @Override
    public boolean isVisual() {
        return true;
    }

    @Override
    public void execute(ExecutionContext e) {
        if (!isValid(e))
            throw new ExecutionException("Les nombre des cote ou la taille sont inferieur a 3");

        e.polygone(Integer.parseInt(size) , Integer.parseInt(num));
    }

    @Override
    public ActionType getType() {
        return ActionType.DRAW_POLYGON;
    }

    @Override
    public Action duplicate() {

        return new PolygonAction(size , num);
    }

    @Override
    public String format() {
        return "";
    }

    @Override
    public String getTitle() {
        return "Polygone : ";
    }

    @Override
    public Color getColor() {
        return Color.DARKORANGE;
    }


    @Override
    public String toString() {
        return getTitle() +" "+getSize() +","+getNum();
    }

    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Size", size,true,"",true,(newVal) -> {
                    this.size = newVal;
                }),
                new ActionParameter("Number", num , true, "", true, (newVal) -> {
                    this.num = newVal;
                })
        );
    }

}
