package scratch.model;

public class TurnLeftAction extends ParameterizedAction {

    public static final int MIN_VALUE = 1 ;
    public static final int MAX_VALUE = 180 ;
    public static final int DEFAULT_VALUE = 90 ;

    public TurnLeftAction(){
        super(DEFAULT_VALUE);
    }
    public TurnLeftAction(int value) {
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
    void execute(ExecutionContext e) {
        e.turnLeft(getValue());
    }

    @Override
    boolean isValid(ExecutionContext e) {
        return isValueValid(getValue());
    }

    @Override
    ActionType getType() {
        return ActionType.TURN_LEFT;
    }
}
