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

    public void pushRepeat(int repeatIndex, int iterations) {
        repeatStack.push(new int[]{repeatIndex, iterations});
    }

    public int[] peekRepeat() {
        return repeatStack.peek();
    }

    public void popRepeat() {
        repeatStack.pop();
    }

    public boolean hasRepeat() {
        return !repeatStack.isEmpty();
    }

    public int resolveValue(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return getVariable(value);
        }
    }

    public boolean isValidValue(String value) {
        if (value == null || value.isBlank()) return false;
        try {
            Integer.parseInt(value.trim());
            return true;
        } catch (NumberFormatException e) {
            return hasVariable(value.trim());
        }
    }

    public void polygone(int size, int num) {
        List<double[]> lines = getPolygonLines(getX(), getY(), getDirection(), size, num);
        for (double[] line : lines) {
            double x1 = line[0];
            double y1 = line[1];
            double x2 = line[2];
            double y2 = line[3];
            segments.add(new Segment((int) x1, (int) y1, (int) x2, (int) y2));
        }
    }

    private List<double[]> getPolygonLines(double x, double y, double angle, int size, int num) {
        List<double[]> lines = new ArrayList<>();

        double currentAngle = Math.toRadians(angle);
        double stepAngle = 2 * Math.PI / num;

        double currentX = x;
        double currentY = y;

        for (int i = 0; i < num; i++) {
            double nextX = currentX + size * Math.cos(currentAngle);
            double nextY = currentY + size * Math.sin(currentAngle);

            lines.add(new double[]{currentX, currentY, nextX, nextY});

            currentX = nextX;
            currentY = nextY;

            currentAngle += stepAngle;
        }

        return lines;
    }
}
