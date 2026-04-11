package scratch.viewmodel;



public class ActionDetail {
    private final String title;
    private final boolean valueEditable;
    private final String unitText;
    private final int value;

    public ActionDetail(String title, boolean valueEditable, String unitText, int value) {
        this.title = title;
        this.valueEditable = valueEditable;
        this.unitText = unitText;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public boolean isValueEditable() {
        return valueEditable;
    }

    public String getUnitText() {
        return unitText;
    }

    public int getValue() {
        return value;
    }
}