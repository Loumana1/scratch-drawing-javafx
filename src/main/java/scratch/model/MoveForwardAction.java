package scratch.model;

public class MoveForwardAction extends ParameterizedAction{
    public static final int MIN_VALUE = 1;
    public static final int MAX_VALUE = 100;
    public static final int DEFAULT_VALUE = 30;

    public MoveForwardAction() {
        super(DEFAULT_VALUE);
    }
    public MoveForwardAction(int value){
        super(value);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (isVar()) {
            String name = getVarName();
            if (name == null || name.isBlank()) {
                return false;
            }
            return e.hasVariable(name);
        }
        return isValueValid(getValue());
    }

    @Override
    public void execute(ExecutionContext e) {
        int realValue = resolveValue(e);
        e.move(realValue);
    }
    @Override
    protected boolean isValueValid(int value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    @Override
    public int getDefaultValue() {
        return DEFAULT_VALUE ;
    }

    @Override
    public ActionType getType() {
        return ActionType.MOVE_FORWARD;
    }

    @Override
    public Action duplicate() {
        if (isVar()) {
            MoveForwardAction clone = new MoveForwardAction();
            clone.setVarName(this.getVarName());
            return clone;
        } else {
            return new MoveForwardAction(this.getValue());
        }
    }
}
