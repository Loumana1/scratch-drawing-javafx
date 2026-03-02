package scratch.model;

public abstract class  Action {

    abstract void execute(ExecutionContext e );

  abstract boolean isValid(ExecutionContext e ) ;

  abstract ActionType getType();
}
