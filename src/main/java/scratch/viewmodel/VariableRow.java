package scratch.viewmodel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public  class VariableRow {
    private final StringProperty name = new SimpleStringProperty();
    private final IntegerProperty value = new SimpleIntegerProperty();

    public VariableRow(String name, int value) {
        this.name.set(name);
        this.value.set(value);
    }

    public StringProperty nameProperty() { return name; }
    public IntegerProperty valueProperty() { return value; }
}