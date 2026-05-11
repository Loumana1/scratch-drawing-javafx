package scratch.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scratch.model.ActionParameter;

public class ParameterViewModel {
    private final ActionParameter parameter;
    private final StringProperty valueProperty;
    private final SceneViewModel sceneViewModel;
    private final ActionConfigViewModel configViewModel;

    public ParameterViewModel(ActionParameter parameter, SceneViewModel sceneViewModel, ActionConfigViewModel configViewModel) {
        this.parameter = parameter;
        this.sceneViewModel = sceneViewModel;
        this.configViewModel = configViewModel;
        this.valueProperty = new SimpleStringProperty(parameter.getValue());

        this.valueProperty.addListener((obs, oldVal, newVal) -> {
            parameter.setValue(newVal);
            sceneViewModel.notifyProgramContentChanged();
            configViewModel.validateCurrentAction();
        });
    }

    public StringProperty valueProperty() {
        return valueProperty;
    }

    public String getLabel() {
        return parameter.getLabel();
    }

    public String getUnit() {
        return parameter.getUnit();
    }

    public boolean hasButtons() {
        return parameter.hasButtons();
    }

    public void increment() {
        try {
            int currentVal = Integer.parseInt(valueProperty.get());
            valueProperty.set(String.valueOf(currentVal + 1));
        } catch (NumberFormatException ex) {
        }
    }

    public void decrement() {
        try {
            int currentVal = Integer.parseInt(valueProperty.get());
            valueProperty.set(String.valueOf(currentVal - 1));
        } catch (NumberFormatException ex) {
        }
    }
}
