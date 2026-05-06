package scratch.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scratch.model.Action;
import scratch.model.ActionParameter;

public class ActionConfigViewModel {

    private final StringProperty title = new SimpleStringProperty("(aucune action sélectionnée)");
    private final ObservableList<ActionParameter> parameters = FXCollections.observableArrayList();
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
        parameters.setAll(action.getParameters());
    }

    public StringProperty titleProperty() { return title; }
    public ObservableList<ActionParameter> getParameters() { return parameters; }
}
