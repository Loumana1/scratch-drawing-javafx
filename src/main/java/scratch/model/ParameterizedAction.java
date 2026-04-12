package scratch.model;

public abstract class ParameterizedAction extends Action {
    private int value;
    private boolean isVar = false;
    private String varName = "";

    public ParameterizedAction(int value) {
        this.value = value;
    }

    public int getValue() { return value; }
    public void setValue(int value) {
        this.value = value;
        this.isVar = false;
    }


    public boolean isVar() { return isVar; }
    public String getVarName() { return varName; }

    public void setVarName(String varName) {
        this.varName = varName;
        this.isVar = true;
    }


    public int resolveValue(ExecutionContext e) {
        if (isVar) {
            return e.getVariable(varName);
        }
        return value;
    }
    // ----------------------------------

    protected abstract boolean isValueValid(int value);
    public abstract int getDefaultValue();

    @Override
    public String format() {
        if (isVar()) {
            return getType().name() + ";" + getVarName();
        } else {
            return getType().name() + ";" + getValue();
        }
    }


    @Override
    public boolean isValueEditable() { return !isVar(); }

    @Override
    public int getNumericValue() { return getValue(); }

    @Override
    public boolean updateValue(int newValue) {
        if (isValueValid(newValue)) {
            setValue(newValue);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateVariable(String varName) {
        setVarName(varName);
        return true;
    }
}