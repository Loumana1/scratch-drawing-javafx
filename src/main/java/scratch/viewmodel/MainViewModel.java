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
import java.nio.file.Files;
import java.sql.Time;
import java.util.ArrayList;
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
                errorMessage.set("");
            } catch (ExecutionException e) {
                selectedIndex.set(program.getCurrenIndex());
                String msg = e.getMessage();
                errorMessage.set(msg != null && !msg.isBlank() ? msg : "Erreur d'exécution");
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
            errorMessage.set("");
        } catch (ExecutionException e) {
            String detail = e.getMessage();
            errorMessage.set("Programme invalide : "
                    + (detail != null && !detail.isBlank() ? detail : "erreur inconnue"));
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
        turtleState.set(buildTurtleStateString());
        executionStep.set(0);
        errorMessage.set("");
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

                () -> !observableActions.isEmpty()
                        && program.isValid(new ExecutionContext()),

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
            case REPEAT -> new RepeatAction(4);
            case END_REPEAT -> new EndRepeatAction();
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

    public ActionDetail getSelectedActionDetail() {
        Action action = getSelectedAction();
        if (action == null) return null;
        return switch (action.getType()) {
            case TURN_LEFT -> {
                ParameterizedAction p = (ParameterizedAction) action;
                yield new ActionDetail("Tourner à gauche de ", true, " Degres", p.getValue());
            }
            case TURN_RIGHT -> {
                ParameterizedAction p = (ParameterizedAction) action;
                yield new ActionDetail("Tourner à droite de ", true, " Degres", p.getValue());
            }
            case MOVE_FORWARD -> {
                ParameterizedAction p = (ParameterizedAction) action;
                yield new ActionDetail("Avance de ", true, " Pixels", p.getValue());
            }
            case PEN_UP -> new ActionDetail("Lever le stylo ", false, "", 0);
            case PEN_DOWN -> new ActionDetail("Abaisser le stylo ", false, "", 0);
            case REPEAT -> {
                RepeatAction r = (RepeatAction) action;
                // Si le compteur est une variable =pas de champ entier côté UI
                if (r.isCountIsVar()) {
                    yield new ActionDetail("Repeter " + r.getCountVarName() + " fois", false, "", 0);
                }
                // Compteur littéral = champ entier éditable
                yield new ActionDetail("Repeter ", true, " fois", r.getCount());
            }
            case END_REPEAT -> new ActionDetail("Fin repeter", false, "", 0);
        };
    }

    public boolean tryUpdateSelectedActionValue(int newValue) {

        Action action = getSelectedAction();
        if (action == null) return false;

        ExecutionContext temp = new ExecutionContext();

        // Cas MOVE_FORWARD / TURN_LEFT / TURN_RIGHT (ParameterizedAction)
        if (action instanceof ParameterizedAction p) {
            int oldValue = p.getValue();
            p.setValue(newValue);

            boolean ok = action.isValid(temp);
            if (!ok) {
                p.setValue(oldValue); // on annule cote modèle

            }
            return ok;
        }

        // Cas REPEAT (pas un ParameterizedAction)
        if (action instanceof RepeatAction r) {
            int oldCount = r.getCount();
            boolean oldIsVar = r.isCountIsVar();
            String oldVarName = r.getCountVarName();
            // On force le mode "littéral" quand l'utilisateur édite un entier
            r.setCount(newValue);
            r.setCountIsVar(false);
            r.setCountVarName(oldVarName); // garde la valeur si jamais

            boolean ok = action.isValid(temp);
            if (!ok) {
                r.setCount(oldCount);
                r.setCountIsVar(oldIsVar);
                r.setCountVarName(oldVarName);
                return false;
            }
            return ok;
        }
        return false;
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
