package scratch.model;

import javafx.scene.paint.Color;

import java.util.List;

public class RepeatAction extends Action {

    private int count;
    private String countVarName;
    private boolean countIsVar;

    public RepeatAction(int count) {
        this.count = count;
        this.countIsVar = false;
    }

    public RepeatAction(String countVarName) {
        this.countVarName = countVarName;
        this.countIsVar = true;
    }

    public int resolveCount(ExecutionContext ctx) {
        if (countIsVar) {
            return ctx.getVariable(countVarName);
        }
        return count;
    }


    @Override
    public void execute(ExecutionContext e) {

    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (countIsVar) {
            if (countVarName == null || countVarName.isBlank()) {
                return false;
            }
            return e.hasVariable(countVarName) && e.getVariable(countVarName) > 0;
        }
        return count > 0;
    }

    @Override
    public ActionType getType() {
        return ActionType.REPEAT;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public String getCountVarName() {
        return countVarName;
    }
    public void setCountVarName(String countVarName) {
        this.countVarName = countVarName;
    }

    public boolean isCountIsVar() {
        return countIsVar;
    }
    public void setCountIsVar(boolean countIsVar) {
        this.countIsVar = countIsVar;
    }

    @Override
    public String toString() {
        return getTitle() + getValue() + " fois";
    }


    @Override
    public Action duplicate() {
        if (countIsVar) {
            return new RepeatAction(this.countVarName);
        } else {
            return new RepeatAction(this.count);
        }
    }

    @Override
    public String format() {
        return getType().name() + ";" + getValue();
    }

    @Override
    public Color getColor() {
        return Color.LIGHTSEAGREEN;
    }

    @Override
    public String getTitle() {
        return "Repeter ";
    }

    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Nombre de fois", getValue(), true, "fois", true, (newVal) -> {
                    if (newVal.matches("^-?\\d+$")) {
                        this.count = Integer.parseInt(newVal);
                        this.countIsVar = false;
                    } else {
                        this.countVarName = newVal;
                        this.countIsVar = true;
                    }
                })
        );
    }

    public String getValue() {
        return isCountIsVar() ? getCountVarName() : String.valueOf(getCount());
    }
}
