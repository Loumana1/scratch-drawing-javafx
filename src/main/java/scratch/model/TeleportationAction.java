package scratch.model;

import javafx.scene.paint.Color;
import java.util.List;

public class TeleportationAction extends Action {

    private String x;
    private String y;

    public TeleportationAction() {
        this.x = "0";
        this.y = "0";
    }

    public TeleportationAction(String x, String y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void execute(ExecutionContext e) {
        int targetX = e.resolveValue(x);
        int targetY = e.resolveValue(y);
        int currentX = e.getPositionTortueX();
        int currentY = e.getPositionTortueY();
        int dx = Math.abs(targetX - currentX);
        int dy = Math.abs(targetY - currentY);
        if (dx < 10 && dy < 10)
            throw new ExecutionException("distance insuffisante (dx ou dy >= 10 requis).");
        e.teleport(targetX, targetY);
    }

    @Override
    public boolean isValid(ExecutionContext e) {
        try {
            if (!e.hasVariable(x)) {
                e.resolveValue(x);
            }
            if (!e.hasVariable(y)) {
                e.resolveValue(y);
            }
            return true;
        } catch (ExecutionException ex) {
            return false;
        }
    }

    @Override public ActionType getType() { return ActionType.TELEPORTATION; }
    @Override public boolean isVisual() { return true; }
    @Override public Action duplicate() { return new TeleportationAction(x, y); }
    @Override public String format() { return getType().name() + ";" + x + ";" + y; }
    @Override public String getTitle() { return "Téléportation : "; }
    @Override public Color getColor() { return Color.DARKORANGE; }
    @Override public String toString() { return getTitle() + x + ", " + y; }

    @Override
    public List<ActionParameter> getParameters() {
        return List.of(
                new ActionParameter("Téléporter vers (x,y)", x, false, "", false, v -> this.x = v),
                new ActionParameter(",", y, false, "", false, v -> this.y = v)
        );
    }

    public void setX(String x) { this.x = x; }
    public void setY(String y) { this.y = y; }
}