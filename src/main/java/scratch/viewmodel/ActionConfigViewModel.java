package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.Action;
import scratch.model.ActionParameter;
import scratch.model.ExecutionContext;
import scratch.model.ExecutionException;
import scratch.model.Program;

public class ActionConfigViewModel {

    private final StringProperty title = new SimpleStringProperty("(aucune action sélectionnée)");
    private final ObservableList<ParameterViewModel> parameters = FXCollections.observableArrayList();
    private final ProgramViewModel programViewModel;
    private final SceneViewModel sceneViewModel;
    private final BooleanBinding empty;

    public ActionConfigViewModel(ProgramViewModel programViewModel, SceneViewModel sceneViewModel) {
        this.programViewModel = programViewModel;
        this.sceneViewModel = sceneViewModel;
        this.empty = Bindings.createBooleanBinding(parameters::isEmpty, parameters);


    }
    public BooleanBinding emptyProperty() { return empty; }

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
        if (idx < 0 || idx >= programViewModel.getObservableActions().size()) {
            return;
        }
        Program program = programViewModel.getProgram();
        ExecutionContext scratch = new ExecutionContext();
        try {
            program.runValidationInternDepuisDebut(idx, scratch);
            sceneViewModel.errorMessageProperty().set("");
        } catch (ExecutionException ex) {
            String msg = ex.getMessage();
            sceneViewModel.errorMessageProperty().set(
                    msg != null && !msg.isBlank()
                            ? msg
                            : "Erreur : Valeur ou variable invalide.");
        }
    }

    public StringProperty titleProperty() { return title; }
    public ObservableList<ParameterViewModel> getParameters() { return parameters; }
}
