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
        return countIsVar || count > 0 ;
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
}
