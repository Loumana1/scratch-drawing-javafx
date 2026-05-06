package scratch.model;

import javafx.scene.paint.Color;

import java.util.List;

public class IncrementVariableAction extends Action {

    private String targetVar;
    private String value;

    public IncrementVariableAction() {
        this.targetVar = "var";
        this.value = "1";
    }

    public IncrementVariableAction(String targetVar, String value) {
        this.targetVar = targetVar;
        this.value = value;
    }

    public String getTargetVar() { return targetVar; }
    public void setTargetVar(String targetVar) { this.targetVar = targetVar; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public void execute(ExecutionContext e) {
        int step = e.resolveValue(value);
        int newVal = e.getVariable(targetVar) + step;
        e.setVariable(targetVar, newVal);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (targetVar == null || targetVar.isBlank()) return false;
        if (!e.hasVariable(targetVar)) return false;
        return e.isValidValue(value);
    }

    @Override
    public ActionType getType() {
        return ActionType.INCREMENT_VARIABLE;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new IncrementVariableAction(this.targetVar, this.value);
    }

    @Override
    public String format() {
        return getType().name() + ";" + this.targetVar + ";" + this.value;
    }

    @Override
    public String getTitle() { return "Inc/Dec variable : " + targetVar + " de " + value; }

    @Override
    public Color getColor() {
        return Color.LIGHTSEAGREEN;
    }

    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Variable cible", targetVar, false, (newVal) -> {
                    this.targetVar = newVal;
                }),
                new ActionParameter("de", value, true, "", true, (newVal) -> {
                    this.value = newVal;
                })
        );
    }
}