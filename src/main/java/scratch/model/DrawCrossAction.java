package scratch.model;

import javafx.scene.paint.Color;

import java.util.List;

public class DrawCrossAction extends Action{
    public int MIN_ARM_LENGTGH = 20;
    public int MIN_THICKNESS = 5;

    public String ArmLength ;
    public String Thickness;


    public DrawCrossAction() {
        this.ArmLength = "20";
        this.Thickness = "5";
    }

    public DrawCrossAction(String armLength, String thickness) {
        this.ArmLength = armLength;
        this.Thickness = thickness;
    }


    @Override
    public void execute(ExecutionContext e) {
        int armLength = e.resolveValue(getArmLength());
        int thickness = e.resolveValue(getThickness());
        if (armLength < MIN_ARM_LENGTGH || thickness < MIN_THICKNESS)
            throw new ExecutionException(
                    "Cross: Arm >= " + MIN_ARM_LENGTGH + " et thickn >= " + MIN_THICKNESS + " requis.");
        e.drawCross(armLength, thickness);
    }


    @Override
    public boolean isValid(ExecutionContext e) {
        try {
            if (!e.hasVariable(getArmLength())) {
                int armLength = e.resolveValue(getArmLength());
                if (armLength < MIN_ARM_LENGTGH ) return false;
            }
            if (!e.hasVariable(getThickness())) {
                int thickness = e.resolveValue(getThickness());
                if (thickness < MIN_THICKNESS) return false;
            }
            return true;
        } catch (ExecutionException ex) {
            return false;
        }
    }

    @Override
    public Action duplicate() {

        return new DrawCrossAction(ArmLength, Thickness);
    }
    @Override
    public ActionType getType() {
        return ActionType.DRAW_CROSS;
    }
    public String getArmLength(){
        return ArmLength;
    }
    public String getThickness(){
        return Thickness;
    }
    @Override
    public boolean isVisual() {
        return true;
    }

    public void setArmLength(String armLength){
        this.ArmLength =armLength;
    }

public void  setThickness(String thickness){
        this.Thickness = thickness;
}

    @Override
    public String format() {
        return getType().name() +";" + ArmLength + ";" + Thickness;
    }

    @Override
    public String getTitle() {
        return "Cross";
    }

    @Override
    public Color getColor() {
        return Color.BLACK;
    }

    @Override
    public String toString() {
        return getTitle() + " " + getArmLength() + "," + getThickness();
    }
    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Arm", getArmLength(),true,"",true,(newVal) -> {
                    this.ArmLength = newVal;
                }),
                new ActionParameter("Thickness", getThickness() , true, "", true, (newVal) -> {
                    this.Thickness = newVal;
                })
        );
    }
}
