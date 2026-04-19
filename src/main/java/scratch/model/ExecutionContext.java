package scratch.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ExecutionContext {
    private static final int DEFAULT_X = 250;
    private static final int DEFAULT_Y = 250;
    private static final int DEFAULT_DIRECTION = 0;
    private static final boolean DEFAULT_PEN_DOWN = true;

    private int x, y, direction;
    private boolean penDown;
    private List<Segment> segments;
    private Deque<int[]> repeatStack = new ArrayDeque<>();
    private final Map<String, Integer> variables;

    public ExecutionContext() {
        this.segments = new ArrayList<>();
        this.variables = new HashMap<>();
        reset();
    }

    public void declareVariable(String name) {

        if (variables.containsKey(name)) {
            return;
        }
        variables.put(name, 0);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    public void setVariable(String name, int val) {
        if (!variables.containsKey(name)) {
            throw new ExecutionException("Variable non déclarée : " + name);
        }
        variables.put(name, val);
    }

    public int getVariable(String name) {
        //getOrDefault faisait croire qu’une var inexistante vaut default
        //on veux verifier le key (var) et envoyer une erreur
        // si elle existe pas  avant de la consulter son value
        if (!variables.containsKey(name)) {
            throw new ExecutionException("Variable indéfinie : " + name);
        }
        return variables.get(name);
    }

    public Map<String, Integer> getVariablesSnapshot() {
        return new HashMap<>(variables);
    }

    public void move(int distance) {
        double radians = Math.toRadians(direction - 90);
        int oldX = x, oldY = y;

        x += (int) Math.round(distance * Math.cos(radians));
        y += (int) Math.round(distance * Math.sin(radians));

        if (penDown) {
            segments.add(new Segment(oldX, oldY, x, y));
        }
    }

    public void turnLeft(int angle) {
        direction = (direction - angle + 360) % 360;
    }

    public void turnRight(int angle) {
        direction = (direction + angle) % 360;
    }

    public void penUp() {
        this.penDown = false;
    }

    public void penDown() {
        this.penDown = true;
    }

    public void reset() {
        x = DEFAULT_X;
        y = DEFAULT_Y;
        direction = DEFAULT_DIRECTION;
        penDown = DEFAULT_PEN_DOWN;
        segments.clear();
        repeatStack.clear();
        variables.clear();
    }

    // GETTERS

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPositionTortueX() {
        return x - DEFAULT_X;
    }

    public int getPositionTortueY() {
        return DEFAULT_Y - y;
    }

    public int getDirection() {
        return direction;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public boolean isPenDown() {
        return penDown;
    }

    // Empiler nouvelle Boucle
    public void pushRepeat(int repeatIndex, int iterations) {
        repeatStack.push(new int[] { repeatIndex, iterations });
    }

    // Regarder la boucle en cours sans retirer
    public int[] peekRepeat() {
        return repeatStack.peek();
    }

    // Retirer la boucle
    public void popRepeat() {
        repeatStack.pop();
    }

    // Y a t il une boucle active
    public boolean hasRepeat() {
        return !repeatStack.isEmpty();
    }

    public int resolveExpression(String expr) {
        try {
            return Integer.parseInt(expr);
        } catch (NumberFormatException e) {
            return getVariable(expr);
        }
    }

    public boolean isValidExpression(String expr) {
        if (expr == null || expr.isBlank()) return false;
        try {
            Integer.parseInt(expr.trim());
            return true;
        } catch (NumberFormatException e) {
            return hasVariable(expr.trim());
        }
    }

}
