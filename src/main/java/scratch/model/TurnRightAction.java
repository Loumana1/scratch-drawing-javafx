package scratch.model;

public class TurnRightAction  extends ParameterizedAction{

    public static  final int MIN_VALUE = 1 ;
    public static  final int MAX_VALUE = 180 ;
    public static  final int DEFAULT_VALUE = 90 ;

    public TurnRightAction(){
        super(DEFAULT_VALUE);
    }

    public TurnRightAction(int value) {
        super(value);
    }

    @Override
    protected boolean isValueValid(int value) {
        return value >= MIN_VALUE && value <= MAX_VALUE;
    }

    @Override
    public int getDefaultValue() {
        return DEFAULT_VALUE;
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        if (isVar()) return true;
        return isValueValid(getValue());
    }

    @Override
    public void execute(ExecutionContext e) {
        int realValue = resolveValue(e);
        e.turnRight(realValue);
    }

    @Override
    public ActionType getType() {
        return ActionType.TURN_RIGHT;
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
