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
    public void execute(ExecutionContext e) {
        int realValue = resolveValue(e);
        if (realValue < MIN_VALUE || realValue > MAX_VALUE) {
            throw new ExecutionException(
                    "Avancer de " + realValue + " hors plage " + MIN_VALUE + "–" + MAX_VALUE);
        }
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
        MoveForwardAction clone = new MoveForwardAction();
        copyStateTo(clone);
        return clone;
    }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public String getTitle() {
        return  "Avance de ";
    }

    @Override
    public String getUnit() {
        return " Pixels";
    }

}
