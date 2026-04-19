package scratch.model;

import javafx.scene.paint.Color;

public class PenDownAction extends Action {

    @Override
    public  void execute(ExecutionContext context ) {
        context.penDown();

    }

    @Override
    public boolean isValid(ExecutionContext context){
        return !context.isPenDown() ;

    }
    @Override
    public ActionType getType() { return ActionType.PEN_DOWN; }

    @Override
    public Action duplicate() {
        return new PenDownAction();
    }

    @Override
    public String format() {
        return "PEN_DOWN;";
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() { return "Abaisser le stylo"; }
    @Override
    public Color getColor() {
        return Color.GREEN;
    }

    @Override
    public String toString() {
        return getTitle();
    }
}

