package scratch.model;

import java.util.function.Consumer;

public class ActionParameter {
    private final String label;
    private String value;
    private final boolean isVariableAllowed;
    private final String unit;
    private final boolean hasButtons;
    private final Consumer<String> updateLogic;


    public ActionParameter(String label, String initialValue, boolean isVariableAllowed, String unit, boolean hasButtons, Consumer<String> updateLogic) {
        this.label = label;
        this.value = initialValue;
        this.isVariableAllowed = isVariableAllowed;
        this.unit = unit;
        this.hasButtons = hasButtons;
        this.updateLogic = updateLogic;
    }


    public ActionParameter(String label, String initialValue, boolean isVariableAllowed, Consumer<String> updateLogic) {
        this(label, initialValue, isVariableAllowed, "", false, updateLogic);
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public boolean isVariableAllowed() {
        return isVariableAllowed;
    }

    public String getUnit() {
        return unit;
    }

    public boolean hasButtons() {
        return hasButtons;
    }

    public void setValue(String value) {
        this.value = value;
        if (updateLogic != null)
            updateLogic.accept(value);
    }
}