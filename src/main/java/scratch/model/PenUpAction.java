package scratch.model;
import scratch.model.*;
public class PenUpAction extends Action {

    @Override
    public  void execute(ExecutionContext context ) {
        context.penUp();
    }

    @Override
    public boolean isValid(ExecutionContext context){
        return context.isPenDown() ;

    }

    @Override
    public ActionType getType() { return ActionType.PEN_UP; }
}
