package scratch.model;

import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.List;

public abstract class  Action {

    public abstract void execute(ExecutionContext e );
    public abstract boolean isValid(ExecutionContext e ) ;
    public abstract ActionType getType();
    public abstract Action duplicate();
    public abstract String format();
    public abstract String getTitle();
    public List<ActionParameter> getParameters() {
        return Collections.emptyList();
    }
    public boolean isVisual() { return false; }

    public String getTargetVar() { return null; }
    public String getValue() { return null; }
    public boolean isCountIsVar() { return false; }
    public int resolveCount(ExecutionContext ctx) { return 0; }

    public abstract Color getColor();
}
