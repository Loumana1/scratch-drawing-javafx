package scratch.model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class PolygonAction extends Action{

    public static final int MIN_SIZE = 3;
    public static final int MIN_SIDES = 3;

    private String size;
    private String num;

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
        try {
            if (!e.hasVariable(size)) {
                int Nsize = e.resolveValue(size);
                if (Nsize < MIN_SIZE) return false;
            }
            if (!e.hasVariable(num)) {
                int Nnum = e.resolveValue(num);
                if (Nnum < MIN_SIDES) return false;
            }
            return true;
        } catch (ExecutionException ex) {
            return false;
        }
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
        int Nsize = e.resolveValue(size);
        int Nnum = e.resolveValue(num);
        if (Nsize < MIN_SIZE || Nnum < MIN_SIDES)
            throw new ExecutionException(
                    "Polygone : taille >= " + MIN_SIZE + " et côtés >= " + MIN_SIDES + " requis.");
        e.polygone(Nsize, Nnum);
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
        return getType().name() + ";" + size + ";" + num;
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
