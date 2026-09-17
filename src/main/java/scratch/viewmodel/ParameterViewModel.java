package scratch.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scratch.model.ActionParameter;

public class ParameterViewModel {
    private final ActionParameter parameter;
    private final StringProperty valueProperty;
    private final StringProperty labelProperty;
    private final StringProperty unitProperty;
    private final BooleanProperty showButtonsProperty;
    private final BooleanProperty unitVisibleProperty;
    private final SceneViewModel sceneViewModel;
    private final ActionConfigViewModel configViewModel;

    public ParameterViewModel(ActionParameter parameter, SceneViewModel sceneViewModel, ActionConfigViewModel configViewModel) {
        this.parameter = parameter;
        this.sceneViewModel = sceneViewModel;
        this.configViewModel = configViewModel;
        this.valueProperty = new SimpleStringProperty(parameter.getValue());

        this.labelProperty = new SimpleStringProperty(parameter.getLabel());
        this.unitProperty = new SimpleStringProperty(parameter.getUnit());
        this.showButtonsProperty = new SimpleBooleanProperty(parameter.hasButtons());

        String unit = parameter.getUnit();
        this.unitVisibleProperty = new SimpleBooleanProperty(
                unit != null && !unit.isEmpty()
        );

        this.valueProperty.addListener((obs, oldVal, newVal) -> {
            parameter.setValue(newVal);
            configViewModel.validateCurrentAction();
            sceneViewModel.notifyProgramContentChanged();
        });
    }

    public StringProperty valueProperty() {
        return valueProperty;
    }

    public StringProperty labelProperty() {
        return labelProperty;
    }

    public StringProperty unitProperty() {
        return unitProperty;
    }

    public BooleanProperty showButtonsProperty() {
        return showButtonsProperty;
    }

    public BooleanProperty unitVisibleProperty() {
        return unitVisibleProperty;
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
