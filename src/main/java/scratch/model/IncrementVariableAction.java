package scratch.model;

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
        // On récupère la valeur à ajouter (ex: 15 ou la valeur de "nb")
        int step = resolveValue(value, e);
        // On récupère la valeur actuelle de la variable
        int currentVal = e.getVariable(targetVar);
        // On additionne les deux et on sauvegarde !
        e.setVariable(targetVar, currentVal + step);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (targetVar == null || targetVar.isBlank()) return false;
        if (value == null || value.isBlank()) return false;
        return true;
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
        return ActionType.INCREMENT_VARIABLE;
    }

    @Override
    public String toString() {
        return "Inc/Dec variable : " + targetVar + " de " + value;
    }
}