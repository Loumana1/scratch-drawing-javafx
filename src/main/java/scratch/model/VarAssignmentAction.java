package scratch.model;

import javafx.scene.paint.Color;

import java.util.List;

public class VarAssignmentAction extends Action{

    private String targetVar = "var";
    private String value = "0";

    public String getTargetVar() { return targetVar; }
    public void setTargetVar(String targetVar) { this.targetVar = targetVar; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Variable cible", targetVar,false,(newVal) -> {
                    this.targetVar = newVal;
                }),
                new ActionParameter("Valeur", value , true, "", true, (newVal) -> {
                    this.value = newVal;
                })
        );
    }
    @Override
    public void execute(ExecutionContext e) {
        int resolvedValue = e.resolveValue(value);
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
        return e.isValidValue(value);
    }


    @Override
    public ActionType getType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        VarAssignmentAction clone = new VarAssignmentAction();
        clone.targetVar = this.targetVar;
        clone.value = this.value;
        return clone;
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

