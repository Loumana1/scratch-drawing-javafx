package scratch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Program {
    private final List<Action> actions;
    private int currenIndex;

    public Program() {
        this.actions = new ArrayList<>();
        this.currenIndex = 0;
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
            Action duplicate = createDuplicate(original);
            actions.add(index + 1 , duplicate);
        }
    }

    public void clear(){
        actions.clear();
        resetExecution();
    }

    //Methodes d'execution

    public Boolean isValid(ExecutionContext context) {

        // une copie du contexte pour la simulation
        //Sert essentiellemnt a definir  si le boutton chargé va etre actif
        // si on repart d’un contexte initial (reset),
        // et on reinsert tout les action,
        // est-ce que ce programme est cohérent ?
        ExecutionContext tempContext = new ExecutionContext();
        tempContext.reset();

        boolean instructionSeen = false;
        int repeatDepth = 0;
        try {
            for (Action action : actions) {
                if (action instanceof VarDeclarationAction) {
                    if (instructionSeen) {
                        return false;
                    }
                } else {
                    instructionSeen = true;
                }
                if (action instanceof RepeatAction){
                    repeatDepth++;
                } else if (action instanceof EndRepeatAction) {
                    repeatDepth--;
                    if (repeatDepth < 0) return false;
                }

                if (!action.isValid(tempContext)) {
                    return false;
                }
                action.execute(tempContext);
            }
            return repeatDepth == 0;
        } catch (ExecutionException e) {

            return false;
        }
    }

    public void executeNext(ExecutionContext context){
        if (!hasNext()) throw new IllegalStateException("Pas d'action suivante");


        Action a = actions.get(currenIndex);

        if ( a.getType() == ActionType.REPEAT) {
            RepeatAction ra = (RepeatAction) a ;
            int n = ra.resolveCount(context);
            if (n <= 0) {
                currenIndex = findEndRepeat(currenIndex) + 1;
            }else {
                context.pushRepeat(currenIndex , n - 1);
                currenIndex++ ;
            }
        } else if (a.getType() == ActionType.END_REPEAT) {
            if (context.hasRepeat()) {
                int[] top = context.peekRepeat();
                if (top[1] > 0) {
                    top[1]--;
                    currenIndex = top[0] + 1;
                } else {
                    context.popRepeat();
                    currenIndex++;
                }
            } else {
                currenIndex++;
            }
        } else {
            a.execute(context);
            currenIndex++;
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

    public int getCurrenIndex() {
        return currenIndex;
    }

    // Private Fonctions

    private Action createDuplicate(Action original){

        return switch (original.getType()) {
            case MOVE_FORWARD -> new MoveForwardAction(((ParameterizedAction)original).getValue());
            case TURN_LEFT -> new TurnLeftAction(((ParameterizedAction)original).getValue());
            case TURN_RIGHT -> new TurnRightAction(((ParameterizedAction)original).getValue()) ;
            case PEN_UP -> new PenUpAction();
            case PEN_DOWN -> new PenDownAction();
            case REPEAT -> {
                var r = (RepeatAction) original;
                yield r.isCountIsVar()
                        ? new RepeatAction(r.getCountVarName())
                        : new RepeatAction(r.getCount());
            }
            case END_REPEAT -> new EndRepeatAction();
            case VAR_DECLARATION -> new VarDeclarationAction();
            case VAR_ASSIGNMENT -> new VarAssignmentAction();
            case INCREMENT_VARIABLE -> new IncrementVariableAction();

        };
    }
}
