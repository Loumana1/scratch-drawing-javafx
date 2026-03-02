package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.*;



public class MainViewModel {

    private final Program program;


    private final ObservableList<Action> observableActions;

    // -1 = aucune sélection
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);

    public MainViewModel(Program program) {
        this.program = program;
        // initialisation liste observable
        this.observableActions = FXCollections.observableArrayList(program.getActions());
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


    //
    public void clear(){
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

    //Boutton Suprrimer
    public void removeSelectedAction() {
        int index = selectedIndex.get();

        // Validate
        if (index >= 0 && index < observableActions.size()) {

            //  supp Modèle
            this.program.removeAction(index);

            //supp de la liste Observable (ce qui mettra à jour la Vue graphic)
            this.observableActions.remove(index);


            // Si la liste est maintenant vide, on désélectionne (-1)
            if (this.observableActions.isEmpty()) {
                this.selectedIndex.set(-1);
            }
            // Sinon, si on a supprimé le tout dernier élément, on sélectionne le "nouveau" dernier
            else if (index >= this.observableActions.size()) {
                this.selectedIndex.set(this.observableActions.size() - 1);
            }
            // Sinon on a supprimé un élément au milieu, l'index pointe maintenant sur l'élément suivant
        }
    }

//Button supprimer dessactivé
    public BooleanBinding canRemove() {
        // On peut supprimer si la liste n'est pas vide ET qu'un élément est sélectionné
        return Bindings.isEmpty(observableActions).not()
                .and(selectedIndex.greaterThanOrEqualTo(0));
    }





//Vue récupére cette liste et s'y abonner
    public ObservableList<Action> getObservableActions() {
        return observableActions;
    }
    //La Vue récupère la propriété d'index pour s'y lier (
    public IntegerProperty selectedIndexProperty() {
        return selectedIndex;
    }

}
