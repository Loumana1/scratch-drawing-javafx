package scratch.model;

public abstract class  Action {

    public abstract void execute(ExecutionContext e );

    public abstract boolean isValid(ExecutionContext e ) ;

    public abstract ActionType getType();


    public abstract Action duplicate();


    public abstract String format();


}
