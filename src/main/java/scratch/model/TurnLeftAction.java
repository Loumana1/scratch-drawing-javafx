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
        e.turnLeft(realValue);
    }

    @Override
    public ActionType getType() {
        return ActionType.TURN_LEFT;
    }
    @Override
    public Action duplicate() {
        if (isVar()) {
            TurnLeftAction clone = new TurnLeftAction();
            clone.setVarName(this.getVarName());
            return clone;
        } else {
            return new TurnLeftAction(getValue());
        }
    }

    @Override
    public String getTitle() {
        return "Tourner à gauche de ";
    }

    @Override
    public String getUnit() {
        return " Degrés";
    }

    @Override
    public int getNumericValue() {
        return this.getValue();
    }

    @Override
    public boolean isValueEditable() {
        return true;
    }
}
