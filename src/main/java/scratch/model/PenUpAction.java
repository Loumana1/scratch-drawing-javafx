package scratch.model;

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

    @Override
    public Action duplicate() {
        return new PenUpAction();
    }

    @Override
    public String format() {
        return "PEN_UP;";
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() { return "Lever le stylo"; }
}
