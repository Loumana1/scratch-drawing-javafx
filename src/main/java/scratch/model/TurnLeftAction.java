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
    public void execute(ExecutionContext e) {
        int realValue = resolveValue(e);
        if (realValue < MIN_VALUE || realValue > MAX_VALUE) {
            throw new ExecutionException(
                    "Tourner à gauche de " + realValue + " hors born " + MIN_VALUE + "–" + MAX_VALUE);
        }
        e.turnLeft(realValue);
    }

    @Override
    public ActionType getType() {
        return ActionType.TURN_LEFT;
    }
    @Override
    public Action duplicate() {
        TurnLeftAction clone = new TurnLeftAction();
        copyStateTo(clone);
        return clone;
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() {
        return "Tourner à gauche de ";
    }

    @Override
    public String getUnit() {
        return " Degrés";
    }

}
