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

    protected void copyStateTo(ParameterizedAction target) {
        if (isVar()) {
            target.setVarName(getVarName());
        } else {
            target.setValue(getValue());
        }
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (isVar()) {
            String name = getVarName();
            if (name == null || name.isBlank()) return false;
            return e.hasVariable(name);
        }
        return isValueValid(getValue());
    }

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

    @Override
    public String getExpression() {
        return isVar() ? getVarName() : String.valueOf(getValue());
    }
}