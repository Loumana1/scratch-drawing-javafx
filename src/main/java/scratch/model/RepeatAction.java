package scratch.model;

public class RepeatAction extends Action {

    private int count;
    private String countVarName;
    private boolean countIsVar;

    public RepeatAction(int count) {
        this.count = count;
        this.countIsVar = false;
    }

    public RepeatAction(String countVarName) {
        this.countVarName = countVarName;
        this.countIsVar = true;
    }

    public int resolveCount(ExecutionContext ctx) {
        if (countIsVar) {
            return ctx.getVariable(countVarName);
        }
        return count;
    }


    @Override
    public void execute(ExecutionContext e) {

    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (countIsVar) {
            if (countVarName == null || countVarName.isBlank()) {
                return false;
            }
            return e.hasVariable(countVarName) && e.getVariable(countVarName) > 0;
        }
        return count > 0;
    }

    @Override
    public ActionType getType() {
        return ActionType.REPEAT;
    }

    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public String getCountVarName() {
        return countVarName;
    }
    public void setCountVarName(String countVarName) {
        this.countVarName = countVarName;
    }

    public boolean isCountIsVar() {
        return countIsVar;
    }
    public void setCountIsVar(boolean countIsVar) {
        this.countIsVar = countIsVar;
    }

    @Override
    public String toString() {
        return "Repeter " + (countIsVar ? countVarName : count) + " fois";
    }


    @Override
    public Action duplicate() {
        if (countIsVar) {
            return new RepeatAction(this.countVarName);
        } else {
            return new RepeatAction(this.count);
        }
    }

    @Override
    public String format() {
        if (countIsVar) {
            return getType().name() + ";" + countVarName;
        } else {
            return getType().name() + ";" + count;
        }
    }

    @Override
    public String getTitle() {
        return "Repeter ";
    }

    @Override
    public String getUnit() {
        return " fois";
    }

    @Override
    public boolean isValueEditable() {
        return true;
    }

    @Override
    public int getNumericValue() {
        return count;
    }

    @Override
    public boolean updateValue(int newValue) {
        if (newValue > 0) {
            this.count = newValue;
            this.countIsVar = false;
            return true;
        }
        return false;
    }

    @Override
    public boolean updateVariable(String varName) {
        this.countVarName = varName;
        this.countIsVar = true;
        return true;
    }

    @Override
    public String getExpression() {
        return isCountIsVar() ? getCountVarName() : String.valueOf(getCount());
    }
}
