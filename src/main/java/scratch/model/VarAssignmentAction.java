package scratch.model;

import javafx.scene.paint.Color;

public class VarAssignmentAction extends Action{

    private String targetVar;
    private String value;

    public VarAssignmentAction() {
        this.targetVar = "var";
        this.value = "0";
    }

    public VarAssignmentAction(String targetVar, String value) {
        this.targetVar = targetVar;
        this.value = value;
    }

    @Override
    public String getTargetVar() { return targetVar; }
    public void setTargetVar(String targetVar) { this.targetVar = targetVar; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public void execute(ExecutionContext e) {
        int resolvedValue = e.resolveExpression(value);
        if (resolvedValue > 180) {
            throw new ExecutionException(
                    "La variable " + targetVar + " dépasse 180 (" + resolvedValue + ")");
        }
        e.setVariable(targetVar, resolvedValue);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (targetVar == null || targetVar.isBlank()) return false;
        if (!e.hasVariable(targetVar)) return false;
        return e.isValidExpression(value);
    }


    @Override
    public ActionType getType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new VarAssignmentAction(this.targetVar, this.value);
    }

    @Override
    public String format() {
        return getType().name() + ";" + targetVar + ";" + value;
    }

    @Override
    public String getTitle() { return "Assignation : " + targetVar + " = " + value; }

    @Override
    public Color getColor() {
        return Color.LIGHTSEAGREEN;
    }
}

