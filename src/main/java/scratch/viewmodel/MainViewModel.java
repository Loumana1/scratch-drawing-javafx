package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class MainViewModel {

    private final Program program;
    private final ObservableList<Action> observableActions;
    // -1 = aucune sélection
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);

    private final ExecutionContext executionContext = new ExecutionContext();

    // Compteur d'exécution
    private final IntegerProperty executionStep = new SimpleIntegerProperty(0);

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final StringProperty turtleState = new SimpleStringProperty("");


    public MainViewModel(Program program) {
        this.program = program;
        // initialisation liste observable
        this.observableActions = FXCollections.observableArrayList(program.getActions());
    }



    //--------------------------ACTIONS---------------------------

    // Methode générique---> la Palette passe le type,  ViewModel crée l'action
    public void addAction(ActionType type) {
        Action action = createAction(type);
        program.addAction(action);
        observableActions.add(action);
        selectedIndex.set(-1);
        program.resetExecution();
        executionStep.set(0);
       programLoaded.set(false);
    }

    // Zone de detail: choisir quel template d'info aficher
    public Action getSelectedAction() {
        int idx = selectedIndex.get();
        // quel index est sélectionné ?
        if (idx >= 0 && idx < observableActions.size()) {
            //index valide ?
            return observableActions.get(idx);
            //si oui retourne l'action
        }
        //si non
        return null;

    }
    //actions peuvent être supprimées une fois que createAction() gère tous les types
    // et que la PaletteView appelle addAction(type)

    /*
    //Button pen up
    public void addPenUp() {
        PenUpAction action = new PenUpAction();
        // mis a jou du model
        this.program.addAction(action);
        // ajoute de l'action dans list observabl
        // C'est CET AJOUT qui va prévenir les Listeners dans Vue
        this.observableActions.add(action);
        this.selectedIndex.set(this.observableActions.size() - 1);
    }



    //Button pen down
    public void addPenDown() {
        PenDownAction action = new PenDownAction();
        this.program.addAction(action);
        this.observableActions.add(action);
        this.selectedIndex.set(this.observableActions.size() - 1);
    }
    //Turn Right
    public void addTurnRight() {
        TurnRightAction action = new TurnRightAction(90);
        this.program.addAction(action);
        this.observableActions.add(action);
        this.selectedIndex.set(this.observableActions.size() - 1);
    }

     */

    //Move Up
    public void moveUp() {
        int index = selectedIndex.get();
        if (index > 0 && index < observableActions.size()){
            program.moveUp(selectedIndex.get());
            Action action = observableActions.remove(index);
            observableActions.add(index - 1 ,  action);
            this.selectedIndex.set(index - 1);
            program.resetExecution();
            executionStep.set(0);
        }
    }

    //Move Down
    public void moveDown() {
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size() - 1){
            program.moveDown(index);
            Action action = observableActions.remove(index);
            observableActions.add(index + 1 , action);
            this.selectedIndex.set(index + 1);
            program.resetExecution();
            executionStep.set(0);
        }

    }

    //Duplicate
    public void duplicateSelected(){
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size()){
            program.duplicateAt(index);
            Action duplicate = program.getAction(index + 1);
            observableActions.add(index + 1 , duplicate);
            selectedIndex.set(index + 1);
            program.resetExecution();
            executionStep.set(0);
        }
    }

    // Boutton vider
    public void clearProgram(){
        program.clear();
        observableActions.clear();
        selectedIndex.set(-1);
        program.resetExecution();
        executionStep.set(0);
    }

    //Boutton Suprrimer une action
    public void removeSelectedAction() {
        int index = selectedIndex.get();

        // Validate
        if (index >= 0 && index < observableActions.size()) {
            //  supp Modèle
            this.program.removeAction(index);
            //supp de la liste Observable m-a-j la Vue graphic)
            this.observableActions.remove(index);

            // Si la liste est maintenant vide, on désélectionne (-1)
            if (this.observableActions.isEmpty()) {
                this.selectedIndex.set(-1);
            }
            // Sinon, si on a supprimé le tout dernier élément, on sélectionne le nv dernier
            else if (index >= this.observableActions.size()) {
                this.selectedIndex.set(this.observableActions.size() - 1);
            }
            // Sinon on a supprimé un élément au milieu, l'index pointe maintenant sur l'élément suivant
            program.resetExecution();
            executionStep.set(0);
        }

    }

    //Execution du prochain instruction
    public void executeNext(){
        if (!program.hasNext()) {
            return;
        }
            try {


                program.executeNext(executionContext);

                // Met à jour la zone info pour l'état de la tortue et des variables
                turtleState.set(buildTurtleStateString());

                //Declenche le redessin du Canvas
                executionStep.set(executionStep.get() + 1);
                if (program.hasNext()) {
                    //Next Action
                    selectedIndex.set(program.getCurrenIndex());
                }
            } catch (ExecutionException e) {
                errorMessage.set(e.getMessage());
            }

    }
  //Charger sur scène
    public void loadOnScene() {
        executionContext.reset();
        program.resetExecution();
        executionStep.set(0);

        try {


            // Valider le programme avant de le charger
            if (!program.isValid(executionContext)) {
                errorMessage.set("Programme invalide : vérifiez vos actions");
                programLoaded.set(false);
                return;
            }
        } catch (ExecutionException e) {
            errorMessage.set("Programme invalide : " + e.getMessage());
            programLoaded.set(false);
            return;
        }
            // Re-reset après la validation
            executionContext.reset();
            program.resetExecution();
            programLoaded.set(true);
            turtleState.set(buildTurtleStateString());


    }

    public void resetExecution() {
        program.resetExecution();
        executionContext.reset();
        executionStep.set(0);
     //   selectedIndex.set(observableActions.isEmpty() ? -1 : 0);
    }

    public void saveToFile(File file) {
        try {
            List<String> lines = new ArrayList<>();
            for (Action action : program.getActions()) {
                String type = action.getType().name();
                int value = 0;
                if (action instanceof ParameterizedAction p) {
                    value = p.getValue();
                }
                lines.add(type + ";" + value);
            }
            Files.write(file.toPath(), lines);
            errorMessage.set("");
        } catch (IOException e) {
            errorMessage.set("Erreur : " + e.getMessage());
        }
    }

    //Sauvegarde des Actions
    public  void loadFromFile(File file){
        try {
            List<Action> loaded = ProgramFileService.load(file);
            program.clear();
            observableActions.clear();
            for (Action a : loaded){
                program.addAction(a);
                observableActions.add(a);
            }
            selectedIndex.set(observableActions.isEmpty() ? -1 : 0);
            // Si la gestion d'erreur est ajoutée (errorMessageProperty)
            // errorMessage.set("");
        }catch (Exception e){
            // errorMessage.set("Erreur chargement : " + e.getMessage());
            System.err.println("Erreur chargement : " + e.getMessage());
        }
        program.resetExecution();
        executionStep.set(0);
    }

    // ---------- Zone info --------------
    public StringProperty turtleStateProperty() {
        return turtleState;
    }

    private String buildTurtleStateString() {
        int x = executionContext.getPositionTortueX();
        int y = executionContext.getPositionTortueY();
        int direction = executionContext.getDirection();
        int angleAffiche = Math.min(direction, 360 - direction);

        return "Tortue: " + "x = " + x +
                ", y = " + y +
                ", direction = " + angleAffiche + "°";
    }




    //--------------------------BINDINGS----------------------
//Button supprimer dessactivé
    public BooleanBinding canRemove() {
        // On peut supprimer si la liste n'est pas vide ET qu'un élément est sélectionné
        return Bindings.isEmpty(observableActions).not()
                .and(selectedIndex.greaterThanOrEqualTo(0));
    }
    public BooleanBinding canExecuteNext() {
        return Bindings.createBooleanBinding(
                () -> program.hasNext(),
                executionStep,programLoaded
        );
    }
    public BooleanBinding canMoveUp() {
        return selectedIndex.greaterThan(0);
    }
    public BooleanBinding canMoveDown() {
        return Bindings.createBooleanBinding(() -> {
            int idx = getSelectedIndex();
            return idx >= 0 && idx < observableActions.size() - 1;
        }, observableActions, selectedIndex);
    }
    public BooleanBinding canDuplicate() {
        return canRemove();
    }

    public BooleanBinding canLoad() {
        return Bindings.createBooleanBinding(
                () -> {
                    if (observableActions.isEmpty()) return false;
                    ExecutionContext contextTemp = new ExecutionContext();
                    for (Action action : observableActions) {
                        if (!action.isValid(contextTemp)) return false;
                        action.execute(contextTemp);
                    }
                    return true;
                },
                observableActions, executionStep
        );
    }


    private Action createAction(ActionType type) {
        return switch (type) {
            case MOVE_FORWARD -> new MoveForwardAction();
            case TURN_LEFT -> new TurnLeftAction();
            case TURN_RIGHT -> new TurnRightAction();
            case PEN_UP -> new PenUpAction();
            case PEN_DOWN -> new PenDownAction();
        };
    }

    public void newProgram() {
        program.clear();
        observableActions.clear();
        selectedIndex.set(-1);
        executionContext.reset();
        program.resetExecution();
        programLoadedProperty().set(false);
        executionStep.set(executionStep.get() + 1);
    }


//----------------------- GETTERS POUR VUE ------------------------

//Vue récupére  liste et s'y abonner
    public ObservableList<Action> getObservableActions() {
        return observableActions;
    }

    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex.get();
    }


    private final ObservableList<VariableRow> observableVariables =
            FXCollections.observableArrayList();

    public ObservableList<VariableRow> getObservableVariables() {
        return observableVariables;
    }

    public IntegerProperty executionStepProperty() { return executionStep; }
    public ExecutionContext getExecutionContext()   { return executionContext; }
    public StringProperty errorMessageProperty()   { return errorMessage; }
    public BooleanProperty programLoadedProperty() { return programLoaded; }
}
