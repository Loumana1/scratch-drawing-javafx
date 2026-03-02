package scratch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Program {
    private final List<Action> actions;
    private int currenIndex;

    public Program(List<Action> actions, int currenIndex) {
        this.actions = new ArrayList<>();
        this.currenIndex = currenIndex;
    }


    //Gestion du programme
    public void addAction(Action action){
        if (action == null) throw new IllegalArgumentException("Action null");
        actions.add(action);

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

    public void duplicateAt(int index ){

        Action original = actions.get(index);
        Action duplicate = createDuplicate(original);
        actions.add(index + 1 , duplicate);

    }

    public void clear(){
        actions.clear();
        resetExecution();
    }

    //Methodes d'execution

    public boolean isValid(ExecutionContext context) {

        //  une copie du contexte pour la simulation
        ExecutionContext tempContext = new ExecutionContext();
        tempContext.reset();

        for (Action action : actions) {
            if (!action.isValid(tempContext)) {
                return false;
            }
            action.execute(tempContext);
        }

        return true;
    }

    public void executeNext(ExecutionContext context){
        if (!hasNext()) throw new IllegalStateException("Pas d'action suivante");
        actions.get(currenIndex++).execute(context);
    }

    public boolean hasNext(){
        return currenIndex < actions.size();
    }

    public void  resetExecution(){
        currenIndex = 0 ;
    }

    public int size() {
        return actions.size();
    }

    public boolean isEmpty() {
        return actions.isEmpty();
    }


    // Private Fonctions

    private Action createDuplicate(Action original){

        return switch (original.getType()) {
            case MOVE_FORWARD -> new MoveForwardAction(((ParameterizedAction)original).getValue());
            case TURN_LEFT -> new TurnLeftAction(((ParameterizedAction)original).getValue());
            case TURN_RIGHT -> new TurnRightAction(((ParameterizedAction)original).getValue()) ;
            case PEN_UP -> new PenUpAction();
            case PEN_DOWN -> new PenDownAction();
        };
    }
}
