package scratch.model;

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

    public String getTargetVar() { return targetVar; }
    public void setTargetVar(String targetVar) { this.targetVar = targetVar; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public void execute(ExecutionContext e) {
        int resolvedValue = resolveValue(value, e);
        if (resolvedValue > 100) {
            throw new ExecutionException(
                    "La variable " + targetVar + " dépasse 100 (" + resolvedValue + ")");
        }
        e.setVariable(targetVar, resolvedValue);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (targetVar == null || targetVar.isBlank()) return false;
        if (value == null || value.isBlank()) return false;
        if (!e.hasVariable(targetVar)) return false;
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException ex) {
            return e.hasVariable(value.trim());
        }
    }


    private int resolveValue(String valStr, ExecutionContext e) {
        try {
            return Integer.parseInt(valStr);
        } catch (NumberFormatException ex) {
            return e.getVariable(valStr);
        }
    }

    @Override
    public ActionType getType() {
        return ActionType.VAR_ASSIGNMENT;
    }

    @Override
    public String toString() {
        return "Assignation : " + targetVar + " = " + value;
    }

    @Override
    public Action duplicate() {
        return new VarAssignmentAction(this.targetVar, this.value);
    }

    @Override
    public String format() {
        return "VAR_ASSIGNMENT;" + targetVar + ";" + value;
    }

    @Override
    public String getTitle() { return "Assignation : " + targetVar + " = " + value; }

    @Override
    public boolean isValueEditable() { return false; }
    @Override
    public String getUnit() { return ""; }
    @Override
    public int getNumericValue() { return 0; }
}

