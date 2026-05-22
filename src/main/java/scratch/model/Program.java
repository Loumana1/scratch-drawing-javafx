package scratch.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

public class Program {
    private final List<Action> actions;
    private int currentIndex;

    public Program() {
        this.actions = new ArrayList<>();
        this.currentIndex = 0;
    }

    public void addAction(Action action) {
        if (action == null)
            return;
        actions.add(action);
    }

    public void insertAction(int index, Action action) {
        if (action == null)
            return;

        if (index < 0 || index >= actions.size()) {
            actions.add(action);
        } else {
            actions.add(index + 1, action);
        }
    }

    public void removeAction(int index) {
        if (index >= 0 && index < actions.size()) {
            actions.remove(index);
        }
    }

    public Action getAction(int index) {
        return actions.get(index);
    }

    public List<Action> getActions() {
        return Collections.unmodifiableList(actions);
    }

    public void moveUp(int index) {
        if (index > 0)
            Collections.swap(actions, index, index - 1);
    }

    public void moveDown(int index) {
        if (index < actions.size() - 1)
            Collections.swap(actions, index, index + 1);
    }

    public void duplicateAt(int index) {
        if (index >= 0 && index < actions.size()) {
            Action original = actions.get(index);
            Action duplicate = original.duplicate();
            actions.add(index + 1, duplicate);
        }
    }

    public void clear() {
        actions.clear();
        resetExecution();
    }

    public boolean isValid(ExecutionContext context) {
        ExecutionContext tempContext = new ExecutionContext();

        boolean instructionSeen = false;
        int repeatDepth = 0;
        boolean hasVisualAction = false;

        try {
            for (Action action : actions) {
                if (action.getType() == ActionType.VAR_DECLARATION) {
                    if (instructionSeen) {
                        return false;
                    }
                } else {
                    instructionSeen = true;
                }
                if (action.isVisual()) hasVisualAction = true;
                if (action.getType() == ActionType.REPEAT) {
                    repeatDepth++;
                } else if (action.getType() == ActionType.END_REPEAT) {
                    repeatDepth--;
                    if (repeatDepth < 0) return false;
                }

                if (!action.isValid(tempContext)) {
                    return false;
                }
                try {
                    action.execute(tempContext);
                } catch (ExecutionException e) {
                    if (action.getType() != ActionType.DRAW_POLYGON && action.getType() != ActionType.TELEPORTATION) return false;
                }
            }
            if (!checkRepeatVarNotModified()) {
                return false;
            }
            if (!checkNoPenActionInLoop()) {
                return false;
            }

            if (!checkPolycount())
                return false;

            if (!checkRectangle())
                return false;

            if (!checkNoTeleportInLoop())
                return false;

            return repeatDepth == 0 && hasVisualAction;
        } catch (ExecutionException e) {
            return false;
        }
    }


    public void runValidationInternDepuisDebut(int lastIndexInclusive, ExecutionContext context) {
        if (lastIndexInclusive < 0 || lastIndexInclusive >= actions.size()) {
            throw new IllegalArgumentException("Index hors limites: " + lastIndexInclusive);
        }
        for (int i = 0; i <= lastIndexInclusive; i++) {
            Action a = actions.get(i);
            if (!a.isValid(context)) {
                throw new ExecutionException("Erreur valeur ou variable ");
            }
            try {
                a.execute(context);
            } catch (ExecutionException e) {
                if (a.getType() != ActionType.DRAW_POLYGON && a.getType() != ActionType.TELEPORTATION) throw e;
            }
        }
    }

    private boolean checkRepeatVarNotModified() {
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);
            if (action.getType() == ActionType.REPEAT && action.isCountIsVar()) {
                String varName = action.getValue();
                int endIndex = findEndRepeat(i);
                for (int j = i + 1; j < endIndex; j++) {
                    Action inner = actions.get(j);
                    if (inner.getType() == ActionType.INCREMENT_VARIABLE) {
                        if (varName.equals(inner.getTargetVar())) {
                            return false;
                        }
                    } else if (inner.getType() == ActionType.VAR_ASSIGNMENT) {
                        if (varName.equals(inner.getTargetVar())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    private boolean checkPolycount(){
        int polyCount = 0 ;
        for (Action a : actions){
            if (a.getType() == ActionType.DRAW_POLYGON){
                polyCount++;
            }
        }
        if (polyCount >= 4)
            return false;
        else
            return true;
    }
    private boolean checkRectangle(){
        if (actions.getLast().getType() == ActionType.DRAW_RECTANGLE )
            return false;
        return true;
    }

    private boolean checkNoPenActionInLoop() {
        int depth = 0;
        int penBalance = 0;

        for (Action a : actions) {
            if (a.getType() == ActionType.REPEAT) {
                depth++;
            } else if (a.getType() == ActionType.END_REPEAT) {
                depth--;
                if (depth == 0 && penBalance != 0) return false;
                if (depth == 0) penBalance = 0;
            }
            if (depth > 0) {
                if (a.getType() == ActionType.PEN_UP) penBalance++;
                if (a.getType() == ActionType.PEN_DOWN) penBalance--;
            }
        }
        return true;
    }

    private boolean checkNoTeleportInLoop() {
        int depth = 0;
        for (Action a : actions) {
            if (a.getType() == ActionType.REPEAT) depth++;
            else if (a.getType() == ActionType.END_REPEAT) depth--;
            else if (a.getType() == ActionType.TELEPORTATION && depth > 0) return false;
        }
        return true;
    }

    public void executeNext(ExecutionContext context) {
        if (!hasNext()) return;

        Action a = actions.get(currentIndex);

        if (a.getType() == ActionType.REPEAT) {
            int n = a.resolveCount(context);
            if (n <= 0) {
                currentIndex = findEndRepeat(currentIndex) + 1;
            } else {
                context.pushRepeat(currentIndex, n - 1);
                currentIndex++;
            }
        } else if (a.getType() == ActionType.END_REPEAT) {
            if (context.hasRepeat()) {
                int[] top = context.peekRepeat();
                if (top[1] > 0) {
                    top[1]--;
                    currentIndex = top[0] + 1;
                } else {
                    context.popRepeat();
                    currentIndex++;
                }
            } else {
                currentIndex++;
            }
        } else {
            a.execute(context);
            currentIndex++;
        }
    }

    private int findEndRepeat(int from) {
        int count = 0;
        for (int i = from; i < actions.size(); i++) {
            if (actions.get(i).getType() == ActionType.REPEAT)
                count++;
            else if (actions.get(i).getType() == ActionType.END_REPEAT) {
                if (--count == 0)
                    return i;
            }
        }
        return actions.size() - 1;
    }

    public boolean hasNext() {
        return currentIndex < actions.size();
    }

    public void resetExecution() {
        currentIndex = 0;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
