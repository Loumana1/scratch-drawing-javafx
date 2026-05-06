package scratch.model;

import javafx.scene.paint.Color;

import java.util.List;

public class TurnRightAction extends Action {
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 180;
    private String value = "90";

    @Override
    public void execute(ExecutionContext e) {
        int realValue = e.resolveValue(value);
        if (realValue < MIN_VALUE || realValue > MAX_VALUE) {
            throw new ExecutionException(
                    "Tourner à droite de " + realValue + " hors plage " + MIN_VALUE + "–" + MAX_VALUE);
        }
        e.turnRight(realValue);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (!e.isValidValue(value)) return false;

        try {
            int val = Integer.parseInt(value.trim());
            return val >= MIN_VALUE && val <= MAX_VALUE;
        } catch (NumberFormatException exception) {
            return true;
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.TURN_RIGHT;
    }

    @Override
    public Action duplicate() {
        TurnRightAction clone = new TurnRightAction();
        clone.value = this.value;
        return clone;
    }

    @Override
    public String format() {
        return getType().name() + ";" + value;
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() {
        return "Tourner à droite de ";
    }

    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Tourner de", value, true, "Degrés", false, (newVal) -> {
                    this.value = newVal;
                })
        );
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return getTitle() +" "+ getValue();
    }
}
