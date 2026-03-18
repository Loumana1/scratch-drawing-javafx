package scratch.model;

public class EndRepeatAction extends Action {
    @Override
    public void execute(ExecutionContext e) {

    }

    @Override
    public boolean isValid(ExecutionContext e) {
        return true;
    }

    @Override
    public ActionType getType() {
        return ActionType.END_REPEAT;
    }

    @Override
    public String toString() {
        return "Fin repeter";
    }
}
