package scratch.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.*;



public class MainViewModel {

    private final Program program;


    private final ObservableList<Action> observableActions;

    public MainViewModel(Program program) {
        this.program = program;
        // initialisation liste observable
        this.observableActions = FXCollections.observableArrayList(program.getActions());
    }

    public void addPenUp() {
        PenUpAction action = new PenUpAction();
        // mis a jou du model
        this.program.addAction(action);
        // ajoute de l'action dans list observabl
        // C'est CET AJOUT qui va prévenir les Listeners dans Vue
        this.observableActions.add(action);
    }
    public void addPenDown() {
        PenDownAction action = new PenDownAction();
        this.program.addAction(action);
        this.observableActions.add(action);
    }





//Vue récupére cette liste et s'y abonner
    public ObservableList<Action> getObservableActions() {
        return observableActions;
    }

}
