package scratch.model;

import javafx.scene.paint.Color;

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
        if (varName == null || varName.isBlank()) return false;
        return varName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    @Override
    public ActionType getType() {
        return ActionType.VAR_DECLARATION;
    }

    @Override
    public String toString() { return getTitle(); }

    @Override
    public Action duplicate() {
        return new VarDeclarationAction(this.varName);
    }

    @Override
    public String format() {
        return getType().name() + ";" + varName;
    }

    @Override
    public String getTitle() { return "Déclaration variable " + varName; }

    @Override
    public Color getColor() {
        return Color.LIGHTSEAGREEN;
    }

}