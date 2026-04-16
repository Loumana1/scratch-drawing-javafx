package scratch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Program {
    private final List<Action> actions;
    private int currentIndex;

    public Program() {
        this.actions = new ArrayList<>();
        this.currentIndex = 0;
    }


    //Gestion du programme
    public void addAction(Action action){
        if (action == null) throw new IllegalArgumentException("Action null");
        actions.add(action);

    }
    //insertion au "milieu" du program
    public void insertAction(int index, Action action) {
        if (action == null) {
            throw new IllegalArgumentException("Action null");
        }
        if (index < 0 || index >= actions.size()) {
            actions.add(action);
        } else {
            actions.add(index + 1, action);
        }
    }


    public void removeAction(int index) {
       //index valide ?
        if (index >= 0 && index < actions.size()) {

            actions.remove(index);
        }
    }

    public Action getAction(int index){

        return actions.get(index);
    }
    public  List<Action> getActions(){
        return Collections.unmodifiableList(actions);
    }
    public void moveUp(int index){
        if (index > 0)
            Collections.swap(actions , index , index - 1);
    }

    public void moveDown(int index){
        if (index < actions.size() - 1)
            Collections.swap(actions , index , index + 1);
    }

    public void duplicateAt(int index){
        if (index >= 0 && index < actions.size()) {
            Action original = actions.get(index);
            Action duplicate = original.duplicate();

            actions.add(index + 1 , duplicate);
        }
    }

    public void clear(){
        actions.clear();
        resetExecution();
    }

    //Methodes d'execution

    public boolean isValid(ExecutionContext context) {

        // une copie du contexte pour la simulation
        //Sert essentiellemnt a definir  si le boutton chargé va etre actif
        // si on repart d'un contexte initial (reset),
        // et on reinsert tout les action,
        // est-ce que ce programme est cohérent ?
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
                if (action.getType() == ActionType.REPEAT){
                    repeatDepth++;
                } else if (action.getType() == ActionType.END_REPEAT) {
                    repeatDepth--;
                    if (repeatDepth < 0) return false;
                }

                if (!action.isValid(tempContext)) {
                    return false;
                }
                action.execute(tempContext);
            }


            // Vérifier variable repeat n'est pas modifiée dans la boucle
            if (!checkRepeatVarNotModified()) {
                return false;
            }

            return repeatDepth == 0 && hasVisualAction;

        } catch (ExecutionException e) {

            return false;
        }
    }

    private boolean checkRepeatVarNotModified() {
        for (int i = 0; i < actions.size(); i++) {
            Action action = actions.get(i);
            if (action.getType() == ActionType.REPEAT && action.isCountIsVar()) {
                String varName = action.getExpression();
                int endIndex = findEndRepeat(i);
                for (int j = i + 1; j < endIndex; j++) {
                    Action inner = actions.get(j);
                    if (inner.getType() == ActionType.INCREMENT_VARIABLE
                            || inner.getType() == ActionType.VAR_ASSIGNMENT) {
                        if (varName.equals(inner.getTargetVar())) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    public void executeNext(ExecutionContext context){
        if (!hasNext()) throw new IllegalStateException("Pas d'action suivante");


        Action a = actions.get(currentIndex);

        if ( a.getType() == ActionType.REPEAT) {

            int n = a.resolveCount(context);
            if (n <= 0) {
                currentIndex = findEndRepeat(currentIndex) + 1;
            }else {
                context.pushRepeat(currentIndex , n - 1);
                currentIndex++ ;
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
        int count = 0 ;
        for (int i = from; i < actions.size(); i++) {
            if (actions.get(i).getType() == ActionType.REPEAT)
                count++ ;
            else if (actions.get(i).getType() == ActionType.END_REPEAT) {
                if (--count == 0)
                    return  i;
            }
        }
        return actions.size() -1;
    }

    public boolean hasNext(){
        return currentIndex < actions.size();
    }

    public void  resetExecution(){
        currentIndex = 0 ;
    }

    public int size() {
        return actions.size();
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }

    public int getCurrentIndex() {
        return currentIndex;
    }
}
