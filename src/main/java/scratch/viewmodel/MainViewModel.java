package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.*;

import java.io.File;
import java.util.List;


public class MainViewModel {

    private final Program program;
    private final ObservableList<Action> observableActions;
    // -1 = aucune sélection
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);

    private final ExecutionContext executionContext = new ExecutionContext();

    // Compteur d'exécution : la Vue l'observe et redessine le Canvas à chaque changement
    private final IntegerProperty executionStep = new SimpleIntegerProperty(0);



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
        selectedIndex.set(observableActions.size() - 1);
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

    // Boutton vider
    public void clearProgram(){
        program.clear();
        observableActions.clear();
        selectedIndex.set(-1);
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

    //Move Up
    public void moveUp() {
        int index = selectedIndex.get();
        if (index > 0 && index < observableActions.size()){
            program.moveUp(selectedIndex.get());
            Action action = observableActions.remove(index);
            observableActions.add(index - 1 ,  action);
            this.selectedIndex.set(selectedIndex.get() - 1);
        }
    }

    //Move Down
    public void moveDown() {
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size()){
            program.moveDown(index);
            Action action = observableActions.remove(index);
            observableActions.add(index + 1 , action);
            this.selectedIndex.set(selectedIndex.get() + 1);
        }

    }

    //Duplicate
    public void duplicateSelected(){
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size() - 1 ){
            program.duplicateAt(index);
            Action duplicate = program.getAction(index + 1);
            observableActions.add(index + 1 , duplicate);
            selectedIndex.set(index + 1);
        }
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
        }
    }

    //Execution du prochain instruction
    public void executeNext(){
        if (program.hasNext()){
            program.executeNext(executionContext);
            //Declenche le redessin du Canvas
            executionStep.set(executionStep.get() + 1);
            if (program.hasNext()){
                //Next Action
                selectedIndex.set(program.getCurrenIndex());
            }
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
                executionStep
        );
    }


    private Action createAction(ActionType type) {
        return switch (type) {
            case MOVE_FORWARD -> new MoveForwardAction();
            case TURN_LEFT -> new TurnLeftAction();
            case TURN_RIGHT -> new TurnRightAction();
            case PEN_UP -> new PenUpAction();
            case PEN_DOWN -> new PenDownAction();
            case TURN_LEFT -> new TurnRightAction();
            case TURN_RIGHT -> new TurnRightAction();
            case MOVE_FORWARD -> new MoveForwardAction();
        };
    }




//----------------------- GETTERS POUR VUE---------------

//Vue récupére  liste et s'y abonner
    public ObservableList<Action> getObservableActions() {
        return observableActions;
    }

    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }



}
