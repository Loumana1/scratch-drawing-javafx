package scratch.model;

public class VarDeclarationAction extends Action {

    private String varName;

    public VarDeclarationAction() {
        this.varName = "var";
    }

    public VarDeclarationAction(String varName) {
        this.varName = varName;
    }

    public String getVarName() {
        return varName;
    }

    public void setVarName(String varName) {
        this.varName = varName;
    }

    @Override
    public void execute(ExecutionContext e) {
        e.declareVariable(varName);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (varName == null || varName.isBlank()) {
            return false;
        }

       if (!varName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return false;
        }

        return true;
    }

    @Override
    public ActionType getType() {
        return ActionType.VAR_DECLARATION;
    }

    @Override
    public String toString() {
        return "Déclaration variable " + varName;
    }
}