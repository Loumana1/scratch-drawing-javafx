package scratch.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.Action;
import scratch.model.ActionParameter;

public class ActionConfigViewModel {

    private final StringProperty title = new SimpleStringProperty("(aucune action sélectionnée)");
    private final ObservableList<ParameterViewModel> parameters = FXCollections.observableArrayList();
    private final MainViewModel mainViewModel;

    public ActionConfigViewModel(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
    }

    public void updateForAction(Action action) {
        parameters.clear();
        if (action == null) {
            title.set("(aucune action sélectionnée)");
            return;
        }
        title.set(action.getTitle());
        for (ActionParameter param : action.getParameters()) {
            parameters.add(new ParameterViewModel(param, mainViewModel, this));
        }
    }

    public void validateCurrentAction() {
        int idx = mainViewModel.getSelectedIndex();
        if (idx >= 0) {
            Action action = mainViewModel.getObservableActions().get(idx);
            if (!action.isValid(mainViewModel.getExecutionContext())) {
                mainViewModel.errorMessageProperty().set("Erreur : Valeur ou variable invalide.");
            } else {
                mainViewModel.errorMessageProperty().set("");
            }
        }
    }

    public StringProperty titleProperty() { return title; }
    public ObservableList<ParameterViewModel> getParameters() { return parameters; }
}
