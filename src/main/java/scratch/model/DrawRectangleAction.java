package scratch.model;

import javafx.scene.paint.Color;
import java.util.List;

public class DrawRectangleAction extends Action {

    public static final int MIN_VALUE = 20;

    private String width;
    private String height;

    public DrawRectangleAction() {
        this.width = "20";
        this.height = "20";
    }

    public DrawRectangleAction(String width, String height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute(ExecutionContext e) {
        int w = e.resolveValue(width);
        int h = e.resolveValue(height);
        if (w < MIN_VALUE || h < MIN_VALUE)
            throw new ExecutionException(
                    "Rectangle : largeur et hauteur >= " + MIN_VALUE + " requis.");
        e.drawRectangle(w, h);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        try {
            int w = e.resolveValue(width);
            int h = e.resolveValue(height);
            return w >= MIN_VALUE && h >= MIN_VALUE;
        } catch (ExecutionException ex) {
            return false;
        }
    }

    @Override
    public ActionType getType() { return ActionType.DRAW_RECTANGLE; }

    @Override
    public boolean isVisual() { return true; }

    @Override
    public Action duplicate() { return new DrawRectangleAction(width, height); }

    @Override
    public String format() { return getType().name() + ";" + width + ";" + height; }

    @Override
    public String getTitle() { return "Rectangle : "; }

    @Override
    public Color getColor() { return Color.DARKRED; }

    @Override
    public String toString() { return getTitle() + width + " , " + height; }

    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Hauteur", width, true, "", true, v -> this.width = v),
                new ActionParameter("Longueur", height, true, "", true, v -> this.height = v)
        );
    }

    public void setWidth(String width) { this.width = width; }
    public void setHeight(String height) { this.height = height; }
}