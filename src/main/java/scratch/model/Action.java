package scratch.model;

public abstract class  Action {

    public abstract void execute(ExecutionContext e );
    public abstract boolean isValid(ExecutionContext e ) ;
    public abstract ActionType getType();
    public abstract Action duplicate();
    public abstract String format();
    public abstract String getTitle();
    public String getUnit() { return ""; }
    public boolean isValueEditable() { return false; }
    public int getNumericValue() { return 0; }
    public boolean updateValue(int newValue) { return false; }
    public boolean updateVariable(String varName) { return false; }
    public String getExpression() { return String.valueOf(getNumericValue()); }
    public int resolveCount(ExecutionContext ctx) { return 0; }

}
