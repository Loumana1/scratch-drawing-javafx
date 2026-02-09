package scratch.model;

import java.util.List;

public class Program {
    private List<Action> actions;
    private int currenIndex;


    //Gestion du programme
    public void addAction(Action action){

    }
    public void removeAction(int index){

    }
    public Action getAction(int index){
        return ;
    }
    public  List<Action> getActions(){
        return ;
    }
    public void moveUp(int index){

    }

    public void moveDown(int index){

    }
    public void duplicateAt(int index ){

    }
    public void clear(){

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
