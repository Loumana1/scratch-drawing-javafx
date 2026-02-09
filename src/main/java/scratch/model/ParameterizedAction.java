package scratch.model;

public abstract class  ParameterizedAction {

    private int value ;

    public ParameterizedAction(int value) {
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
