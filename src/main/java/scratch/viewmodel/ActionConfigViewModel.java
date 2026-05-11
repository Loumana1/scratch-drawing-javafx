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
    private final ProgramViewModel programViewModel;
    private final SceneViewModel sceneViewModel;

    public ActionConfigViewModel(ProgramViewModel programViewModel, SceneViewModel sceneViewModel) {
        this.programViewModel = programViewModel;
        this.sceneViewModel = sceneViewModel;
    }

    public void updateForAction(Action action) {
        parameters.clear();
        if (action == null) {
            title.set("(aucune action sélectionnée)");
            return;
        }
        title.set(action.getTitle());
        for (ActionParameter param : action.getParameters()) {
            parameters.add(new ParameterViewModel(param, sceneViewModel, this));
        }
    }

    public void validateCurrentAction() {
        int idx = programViewModel.getSelectedIndex();
        if (idx >= 0 && idx < programViewModel.getObservableActions().size()) {
            Action action = programViewModel.getObservableActions().get(idx);
            if (!action.isValid(sceneViewModel.getExecutionContext())) {
                sceneViewModel.errorMessageProperty().set("Erreur : Valeur ou variable invalide.");
            } else {
                sceneViewModel.errorMessageProperty().set("");
            }
        }
    }

    public StringProperty titleProperty() { return title; }
    public ObservableList<ParameterViewModel> getParameters() { return parameters; }
}
