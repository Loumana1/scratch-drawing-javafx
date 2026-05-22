package scratch.viewmodel;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import scratch.model.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ProgramViewModel {

    private final Program program;
    private final ObservableList<Action> observableActions;
    private final IntegerProperty selectedIndex = new SimpleIntegerProperty(-1);
    private final IntegerProperty programChangeCounter = new SimpleIntegerProperty(0);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private static BooleanProperty advancedMode = new SimpleBooleanProperty(false);

    public static BooleanProperty advancedModeProperty() {
        return advancedMode;
    }

    public static boolean isAdvancedMode() {
        return advancedMode.get();
    }

    public void toggleMode() {
        advancedMode.set(!advancedMode.get());
    }

    public ProgramViewModel(Program program) {
        this.program = program;
        this.observableActions = FXCollections.observableArrayList(program.getActions());
    }

    public void addAction(ActionType type) {
        Action action = createAction(type);

        int idx = selectedIndex.get();

        if (idx >= 0 && idx < observableActions.size()) {
            program.insertAction(idx, action);
            observableActions.add(idx + 1, action);
            selectedIndex.set(idx + 1);
        } else {
            program.addAction(action);
            observableActions.add(action);
            selectedIndex.set(observableActions.size() - 1);
        }
        program.resetExecution();
        notifyProgramChanged();
    }

    public Action getSelectedAction() {
        int idx = selectedIndex.get();
        if (idx >= 0 && idx < observableActions.size()) {
            return observableActions.get(idx);
        }
        return null;
    }

    public void moveUp() {
        int index = selectedIndex.get();
        if (index > 0 && index < observableActions.size()) {
            program.moveUp(index);
            Action action = observableActions.remove(index);
            observableActions.add(index - 1, action);
            this.selectedIndex.set(index - 1);
            program.resetExecution();
        }
        notifyProgramChanged();
    }

    public void moveDown() {
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size() - 1) {
            program.moveDown(index);
            Action action = observableActions.remove(index);
            observableActions.add(index + 1, action);
            this.selectedIndex.set(index + 1);
            program.resetExecution();
        }
        notifyProgramChanged();
    }

    public void duplicateSelected() {
        int index = selectedIndex.get();
        if (index >= 0 && index < observableActions.size()) {
            program.duplicateAt(index);
            Action duplicate = program.getAction(index + 1);
            observableActions.add(index + 1, duplicate);
            selectedIndex.set(index + 1);
            program.resetExecution();
        }
        notifyProgramChanged();
    }

    public void clearProgram() {
        program.clear();
        observableActions.clear();
        selectedIndex.set(-1);
        notifyProgramChanged();
    }

    public void removeSelectedAction() {
        int index = selectedIndex.get();

        if (index >= 0 && index < observableActions.size()) {
            this.program.removeAction(index);
            this.observableActions.remove(index);

            if (this.observableActions.isEmpty()) {
                this.selectedIndex.set(-1);
            } else if (index >= this.observableActions.size()) {
                this.selectedIndex.set(this.observableActions.size() - 1);
            }
            program.resetExecution();
        }
        notifyProgramChanged();
    }

    public void newProgram() {
        clearProgram();
    }

    public void saveToFile(File file) {
        try {
            ProgramFileService.save(file, program.getActions());
            errorMessage.set("");
        } catch (IOException e) {
            errorMessage.set("Erreur de sauvegarde : " + e.getMessage());
        }
    }

    public void loadFromFile(File file) {
        try {
            List<Action> loaded = ProgramFileService.load(file);
            clearProgram();
            for (Action a : loaded) {
                program.addAction(a);
                observableActions.add(a);
            }
            selectedIndex.set(observableActions.isEmpty() ? -1 : 0);
        } catch (Exception e) {
            errorMessage.set("Erreur chargement : " + e.getMessage());
        }
    }

    public BooleanBinding canRemove() {
        return Bindings.isEmpty(observableActions).not()
                .and(selectedIndex.greaterThanOrEqualTo(0));
    }

    public BooleanBinding canMoveUp() {
        return selectedIndex.greaterThan(0);
    }

    public BooleanBinding canMoveDown() {
        return Bindings.createBooleanBinding(() -> {
            int idx = getSelectedIndex();
            return idx >= 0 && idx < observableActions.size() - 1;
        }, observableActions, selectedIndex);
    }

    public BooleanBinding canDuplicate() {
        return canRemove();
    }

    public Action createAction(ActionType type) {
        return switch (type) {
            case DRAW_POLYGON -> new PolygonAction();
            case MOVE_FORWARD -> new MoveForwardAction();
            case TURN_LEFT -> new TurnLeftAction();
            case TURN_RIGHT -> new TurnRightAction();
            case PEN_UP -> new PenUpAction();
            case PEN_DOWN -> new PenDownAction();
            case REPEAT -> new RepeatAction(4);
            case END_REPEAT -> new EndRepeatAction();
            case VAR_DECLARATION -> new VarDeclarationAction();
            case VAR_ASSIGNMENT -> new VarAssignmentAction();
            case INCREMENT_VARIABLE -> new IncrementVariableAction();
            case DRAW_RECTANGLE -> new DrawRectangleAction();
        };
    }

    public int getIndentDepth(int index) {
        int depth = 0;
        List<Action> actions = program.getActions();
        for (int i = 0; i < index; i++) {
            Action a = actions.get(i);
            if (a.getType() == ActionType.REPEAT) depth++;
            else if (a.getType() == ActionType.END_REPEAT) depth--;
        }
        if (index < actions.size() && actions.get(index).getType() == ActionType.END_REPEAT) depth--;
        return Math.max(depth, 0);
    }

    public void bindListSelection(ListView<Action> listView) {
        listView.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.intValue() >= 0) {
                selectedIndex.set(newVal.intValue());
            }
        });
        selectedIndex.addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0 && newVal.intValue() < observableActions.size()) {
                listView.getSelectionModel().select(newVal.intValue());
            } else {
                listView.getSelectionModel().clearSelection();
            }
        });
    }

    public String getDisplayTitle(ActionType type) {
        return createAction(type).getTitle();
    }

    public javafx.scene.paint.Color getDisplayColor(ActionType type) {
        return createAction(type).getColor();
    }

    private void notifyProgramChanged() {
        programChangeCounter.set(programChangeCounter.get() + 1);
    }

    public Program getProgram() { return program; }
    public ObservableList<Action> getObservableActions() { return observableActions; }
    public IntegerProperty selectedIndexProperty() { return selectedIndex; }
    public int getSelectedIndex() { return selectedIndex.get(); }
    public IntegerProperty programChangeCounterProperty() { return programChangeCounter; }
    public StringProperty errorMessageProperty() { return errorMessage; }
}
