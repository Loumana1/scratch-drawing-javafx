package scratch.viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import scratch.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class MainViewModel {

    private final Program program;
    private final ObservableList<Action> observableActions;
    private final ObservableList<VariableRow> observableVariables =
            FXCollections.observableArrayList();
    // -1 = aucune sélection
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);

    private final ExecutionContext executionContext = new ExecutionContext();

    // Compteur d'exécution
    private final IntegerProperty executionStep = new SimpleIntegerProperty(0);

    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final StringProperty turtleState = new SimpleStringProperty("");

    private final BooleanProperty autoMode = new SimpleBooleanProperty(false);
    private final DoubleProperty speed = new SimpleDoubleProperty(1.0);
    private Timeline autoTimeline ;
    private final IntegerProperty programChangeCounter = new SimpleIntegerProperty(0);
    private final IntegerProperty executionFaultLineIndex = new SimpleIntegerProperty(-1);

    public MainViewModel(Program program) {
        this.program = program;
        this.observableActions = FXCollections.observableArrayList(program.getActions());
        turtleState.set(buildTurtleStateString());
        refreshVariablesFromContext();

        selectedIndex.addListener((obs, oldVal, newVal) -> {
            int fault = executionFaultLineIndex.get();
            if (fault >= 0 && newVal != null && newVal.intValue() != fault) {
                executionFaultLineIndex.set(-1);
                errorMessage.set("");
            }
        });
    }



    //--------------------------ACTIONS---------------------------

    public void addAction(ActionType type) {
        Action action = createAction(type);

        int idx = selectedIndex.get();

        if (idx >= 0 && idx < observableActions.size()) {
            program.insertAction(idx, action);
            observableActions.add(idx + 1, action);
            selectedIndex.set(idx + 1);
        } else {
            program.addAction(action);
            observableActions.add(action);
            selectedIndex.set(observableActions.size() - 1);
        }
        program.resetExecution();
        executionStep.set(0);
        programLoaded.set(false);
        turtleState.set(buildTurtleStateString());
        notifyProgramChanged();
    }



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




    //Move Up
    public void moveUp() {
        int index = selectedIndex.get();
        if (index > 0 && index < observableActions.size()){
            program.moveUp(index);
            Action action = observableActions.remove(index);
            observableActions.add(index - 1 ,  action);
            this.selectedIndex.set(index - 1);
            program.resetExecution();
            executionStep.set(0);
            programLoaded.set(false);
        }
        notifyProgramChanged();
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
            programLoaded.set(false);
        }
        notifyProgramChanged();

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
            programLoaded.set(false);
        }
        notifyProgramChanged();
    }


    public void clearProgram(){
        program.clear();
        observableActions.clear();
        selectedIndex.set(-1);
        executionStep.set(0);
        errorMessage.set("");
        executionContext.reset();
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        stopAutoExecution();
        programLoaded.set(false);
        notifyProgramChanged();
    }


    public void removeSelectedAction() {
        int index = selectedIndex.get();


        if (index >= 0 && index < observableActions.size()) {
            //  supp action Modèle
            this.program.removeAction(index);
            //supp de la liste Observable
            this.observableActions.remove(index);

            // Si la liste est maintenant vide, on désélectionne
            if (this.observableActions.isEmpty()) {
                this.selectedIndex.set(-1);
            }
            // Sinon, si on a supprimé le tout dernier élément, on sélectionne le nv dernier
            else if (index >= this.observableActions.size()) {
                this.selectedIndex.set(this.observableActions.size() - 1);
            }
            // Sinon on a supprimé un élément au milieu
            program.resetExecution();
            executionStep.set(0);
            programLoaded.set(false);
        }
        notifyProgramChanged();

    }


    public void executeNext(){
        if (!program.hasNext()) {
            return;


        }
            try {


                program.executeNext(executionContext);
                refreshVariablesFromContext();
       
                turtleState.set(buildTurtleStateString());

                //Declenche le redessin du Canvas
                executionStep.set(executionStep.get() + 1);
                executionFaultLineIndex.set(-1);
                errorMessage.set("");
                if (program.hasNext()) {
                    selectedIndex.set(program.getCurrentIndex());
                }
            } catch (ExecutionException e) {
                stopAutoExecution();
                executionFaultLineIndex.set(program.getCurrentIndex());
                if (observableActions.isEmpty()) {
                    selectedIndex.set(-1);
                } else {
                    selectedIndex.set(program.getCurrentIndex());
                }

                String msg = e.getMessage();
                errorMessage.set(msg != null && !msg.isBlank() ? msg : "Runtime");
            }

    }

    public void loadOnScene() {
        if (!program.isValid(new ExecutionContext())) {
            errorMessage.set("Programme invalide");
            programLoaded.set(false);
            return;
        }
        errorMessage.set("");
        executionContext.reset();
        program.resetExecution();
        executionStep.set(0);
        programLoaded.set(true);
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        if (observableActions.isEmpty()) {
            selectedIndex.set(-1);
        } else {
            selectedIndex.set(program.getCurrentIndex());
        }


    }

    public void resetExecution() {
        program.resetExecution();
        executionContext.reset();
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        executionStep.set(0);
        errorMessage.set("");


     if (observableActions.isEmpty()) {
        selectedIndex.set(-1);
    } else {
        selectedIndex.set(0);
    }
    }

    public void saveToFile(File file) {
        try {

            ProgramFileService.save(file, program.getActions());
            errorMessage.set("");
        } catch (IOException e) {
            errorMessage.set("Erreur de sauvegarde : " + e.getMessage());
        }
    }


    public void loadFromFile(File file) {
        try {
            List<Action> loaded = ProgramFileService.load(file);
            clearProgram();
            for (Action a : loaded) {
                program.addAction(a);
                observableActions.add(a);
            }
            selectedIndex.set(observableActions.isEmpty() ? -1 : 0);
        } catch (Exception e) {
            System.err.println("Erreur chargement : " + e.getMessage());
        }
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

        return "Tortue : x = " + x + ", y = " + y + ", direction = " + angleAffiche + " °";
    }




    //--------------------------BINDINGS----------------------

    public BooleanBinding canRemove() {
        // On peut supprimer si la liste n'est pas vide ET qu'un élément est sélectionné
        return Bindings.isEmpty(observableActions).not()
                .and(selectedIndex.greaterThanOrEqualTo(0));
    }
    public BooleanBinding canExecuteNext() {
        return Bindings.createBooleanBinding(
                () -> {
                    String err = errorMessage.get();
                    boolean hasError = err != null && !err.isBlank();
                    return program.hasNext() && !hasError;
                },
                executionStep,
                programLoaded,
                errorMessage
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
                () -> !observableActions.isEmpty()
                && program.isValid(new ExecutionContext()),
        observableActions,
                programChangeCounter
        );
    }
    private void notifyProgramChanged() {
        programChangeCounter.set(programChangeCounter.get() + 1);
    }

    public IntegerProperty programChangeCounterProperty() { return programChangeCounter; }


    private Action createAction(ActionType type) {
        return switch (type) {
            case MOVE_FORWARD -> new MoveForwardAction();
            case TURN_LEFT -> new TurnLeftAction();
            case TURN_RIGHT -> new TurnRightAction();
            case PEN_UP -> new PenUpAction();
            case PEN_DOWN -> new PenDownAction();
            case REPEAT -> new RepeatAction(4);
            case END_REPEAT -> new EndRepeatAction();
            case VAR_DECLARATION -> new VarDeclarationAction();
            case VAR_ASSIGNMENT -> new VarAssignmentAction();
            case INCREMENT_VARIABLE -> new IncrementVariableAction();
        };
    }

    public void newProgram() {
        clearProgram();
    }

    public void startAutoExecution(){
        stopAutoExecution();

        autoTimeline = new Timeline(
                new KeyFrame(Duration.seconds(speed.get()), e -> {
                    if (program.hasNext()){
                        executeNext();
                    } else {
                        stopAutoExecution();
                    }
                })
        );
        autoTimeline.setCycleCount(Timeline.INDEFINITE);
        autoTimeline.play();
    }

    public void stopAutoExecution(){
        if (autoTimeline != null){
            autoTimeline.stop();
            autoTimeline = null ;
        }
    }
    public void notifyProgramContentChanged() {
        errorMessage.set("");
        notifyProgramChanged();
    }

//----------------------- GETTERS POUR VUE ------------------------


    public ObservableList<Action> getObservableActions() {
        return observableActions;
    }

    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex.get();
    }

    public ActionDetail getSelectedActionDetail() {
        Action action = getSelectedAction();
        if (action == null) return null;

        return new ActionDetail(
                action.getTitle(),
                action.isValueEditable(),
                action.getUnit(),
                action.getNumericValue()
        );
    }


    public int getExecutionFaultLineIndex() {
        return executionFaultLineIndex.get();
    }
    public ReadOnlyIntegerProperty executionFaultLineIndexProperty() {
        return executionFaultLineIndex;
    }


    public boolean tryUpdateSelectedActionWithText(String text) {
        Action action = getSelectedAction();
        if (action == null) return false;

        if (text.matches("^-?\\d+$")) {
            return action.updateValue(Integer.parseInt(text));
        }

        if (text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            return action.updateVariable(text);
        }

        return false;
    }


    private void refreshVariablesFromContext() {
        observableVariables.clear();
        executionContext.getVariablesSnapshot()
                .forEach((name, val) -> observableVariables.add(new VariableRow(name, val)));
    }

    public ObservableList<VariableRow> getObservableVariables() {
        return observableVariables;
    }

    public IntegerProperty executionStepProperty() { return executionStep; }
    public ExecutionContext getExecutionContext()   { return executionContext; }
    public StringProperty errorMessageProperty()   { return errorMessage; }
    public BooleanProperty programLoadedProperty() { return programLoaded; }
    public BooleanProperty autoModeProperty() { return  autoMode; }
    public boolean isAutoMode() { return autoMode.get(); }
    public DoubleProperty speedProperty() { return speed;}
    public double getSpeed() { return speed.get(); }
}
