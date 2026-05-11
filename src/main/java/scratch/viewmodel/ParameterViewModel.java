package scratch.viewmodel;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scratch.model.ActionParameter;

public class ParameterViewModel {
    private final ActionParameter parameter;
    private final StringProperty valueProperty;
    private final MainViewModel mainViewModel;
    private final ActionConfigViewModel configViewModel;

    public ParameterViewModel(ActionParameter parameter, MainViewModel mainViewModel, ActionConfigViewModel configViewModel) {
        this.parameter = parameter;
        this.mainViewModel = mainViewModel;
        this.configViewModel = configViewModel;
        this.valueProperty = new SimpleStringProperty(parameter.getValue());

        // When the property changes (from the view), update the model
        this.valueProperty.addListener((obs, oldVal, newVal) -> {
            parameter.setValue(newVal);
            mainViewModel.notifyProgramContentChanged();
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
            // Ignore if not a number
        }
    }

    public void decrement() {
        try {
            int currentVal = Integer.parseInt(valueProperty.get());
            valueProperty.set(String.valueOf(currentVal - 1));
        } catch (NumberFormatException ex) {
            // Ignore if not a number
        }
    }
}
