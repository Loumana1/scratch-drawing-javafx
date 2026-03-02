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
        if (action == null)
            return;
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

    }

    public void moveDown(int index){

    }
    public void duplicateAt(int index ){

    }
    public void clear(){
        actions.clear();

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

    }
    public boolean hasNext(){

    }
    public void  resetExecution(){

    }
}
