package scratch.model;
import scratch.model.*;

public class PenDownAction extends Action {

    @Override
    public  void execute(ExecutionContext context ) {
        context.penDown();

    }

    @Override
    public boolean isValid(ExecutionContext context){
        return !context.isPenDown() ;

    }
    @Override
    public ActionType getType() { return ActionType.PEN_DOWN; }
}

