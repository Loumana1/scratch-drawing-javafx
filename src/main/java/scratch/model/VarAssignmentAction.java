package scratch.model;

public class VarAssignmentAction extends Action{

    private String targetVar;
    private String value; // String car ça peut être "45" (littéral) ou "nb" (variable)

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
        e.setVariable(targetVar, resolvedValue);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (targetVar == null || targetVar.isBlank()) return false;
        if (value == null || value.isBlank()) return false;
        return true;
    }

    // Petite méthode magique pour lire soit un chiffre, soit une autre variable
    private int resolveValue(String valStr, ExecutionContext e) {
        try {
            return Integer.parseInt(valStr); // Si c'est un nombre normal comme "45"
        } catch (NumberFormatException ex) {
            return e.getVariable(valStr); // Si c'est un nom de variable comme "nb"
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
}

