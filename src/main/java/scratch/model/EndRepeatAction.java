package scratch.model;

import javafx.scene.paint.Color;

public class EndRepeatAction extends Action {
    @Override
    public void execute(ExecutionContext e) {

    }

    @Override
    public boolean isValid(ExecutionContext e) {
        return true;
    }

    @Override
    public ActionType getType() {
        return ActionType.END_REPEAT;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new EndRepeatAction();
    }

    @Override
    public String format() {
        return getType().name() + ";";
    }

    @Override
    public String getTitle() { return "Fin repeter"; }

    public Color getColor() {
        return Color.LIGHTSEAGREEN;
    }

}
