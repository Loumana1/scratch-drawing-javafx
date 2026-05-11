package scratch.viewmodel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Duration;
import scratch.model.*;

public class SceneViewModel {

    private final Program program;
    private final ProgramViewModel programViewModel;

    private final ExecutionContext executionContext = new ExecutionContext();
    private final IntegerProperty executionStep = new SimpleIntegerProperty(0);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    private final BooleanProperty programLoaded = new SimpleBooleanProperty(false);
    private final StringProperty turtleState = new SimpleStringProperty("");
    private final BooleanProperty autoMode = new SimpleBooleanProperty(false);
    private final DoubleProperty speed = new SimpleDoubleProperty(1.0);
    private Timeline autoTimeline;
    private final IntegerProperty executionFaultLineIndex = new SimpleIntegerProperty(-1);

    private final ObservableList<VariableRow> observableVariables =
            FXCollections.observableArrayList();

    private final IntegerProperty contentChangeCounter = new SimpleIntegerProperty(0);

    public SceneViewModel(Program program, ProgramViewModel programViewModel) {
        this.program = program;
        this.programViewModel = programViewModel;
        turtleState.set(buildTurtleStateString());
        refreshVariablesFromContext();
    }

    public void executeNext() {
        if (!program.hasNext()) {
            return;
        }
        try {
            program.executeNext(executionContext);
            refreshVariablesFromContext();
            turtleState.set(buildTurtleStateString());
            executionStep.set(executionStep.get() + 1);
            executionFaultLineIndex.set(-1);
            errorMessage.set("");
            if (program.hasNext()) {
                programViewModel.selectedIndexProperty().set(program.getCurrentIndex());
            }
        } catch (ExecutionException e) {
            stopAutoExecution();
            executionFaultLineIndex.set(program.getCurrentIndex());
            if (programViewModel.getObservableActions().isEmpty()) {
                programViewModel.selectedIndexProperty().set(-1);
            } else {
                programViewModel.selectedIndexProperty().set(program.getCurrentIndex());
            }

            String msg = e.getMessage();
            errorMessage.set(msg != null && !msg.isBlank() ? msg : "Runtime");
        }
    }

    public void loadOnScene() {
        if (!program.isValid(new ExecutionContext())) {
            errorMessage.set("Programme invalide");
            programLoaded.set(false);
            return;
        }
        errorMessage.set("");
        executionContext.reset();
        program.resetExecution();
        executionStep.set(0);
        programLoaded.set(true);
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        if (programViewModel.getObservableActions().isEmpty()) {
            programViewModel.selectedIndexProperty().set(-1);
        } else {
            programViewModel.selectedIndexProperty().set(program.getCurrentIndex());
        }
    }

    public void resetExecution() {
        program.resetExecution();
        executionContext.reset();
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        executionStep.set(0);
        errorMessage.set("");

        if (programViewModel.getObservableActions().isEmpty()) {
            programViewModel.selectedIndexProperty().set(-1);
        } else {
            programViewModel.selectedIndexProperty().set(0);
        }
    }

    public void startAutoExecution() {
        stopAutoExecution();

        autoTimeline = new Timeline(
                new KeyFrame(Duration.seconds(speed.get()), e -> {
                    if (program.hasNext()) {
                        executeNext();
                    } else {
                        stopAutoExecution();
                    }
                })
        );
        autoTimeline.setCycleCount(Timeline.INDEFINITE);
        autoTimeline.play();
    }

    public void stopAutoExecution() {
        if (autoTimeline != null) {
            autoTimeline.stop();
            autoTimeline = null;
        }
    }

    public void notifyProgramContentChanged() {
        errorMessage.set("");
        contentChangeCounter.set(contentChangeCounter.get() + 1);
    }

    public void onProgramStructureChanged() {
        program.resetExecution();
        executionStep.set(0);
        programLoaded.set(false);
        turtleState.set(buildTurtleStateString());
    }

    public void clearFaultLine() {
        executionFaultLineIndex.set(-1);
        errorMessage.set("");
    }

    public void onProgramCleared() {
        executionStep.set(0);
        errorMessage.set("");
        executionContext.reset();
        refreshVariablesFromContext();
        turtleState.set(buildTurtleStateString());
        stopAutoExecution();
        programLoaded.set(false);
    }

    public BooleanBinding canExecuteNext() {
        return Bindings.createBooleanBinding(
                () -> {
                    String err = errorMessage.get();
                    boolean hasError = err != null && !err.isBlank();
                    return program.hasNext() && !hasError;
                },
                executionStep,
                programLoaded,
                errorMessage
        );
    }

    public BooleanBinding canLoad() {
        return Bindings.createBooleanBinding(
                () -> !programViewModel.getObservableActions().isEmpty()
                        && program.isValid(new ExecutionContext()),
                programViewModel.getObservableActions(),
                programViewModel.programChangeCounterProperty(),
                contentChangeCounter
        );
    }

    private String buildTurtleStateString() {
        int x = executionContext.getPositionTortueX();
        int y = executionContext.getPositionTortueY();
        int direction = executionContext.getDirection();
        int angleAffiche = Math.min(direction, 360 - direction);

        return "Tortue : x = " + x + ", y = " + y + ", direction = " + angleAffiche + " °";
    }

    private void refreshVariablesFromContext() {
        observableVariables.clear();
        executionContext.getVariablesSnapshot()
                .forEach((name, val) -> observableVariables.add(new VariableRow(name, val)));
    }

    public IntegerProperty executionStepProperty() { return executionStep; }
    public ExecutionContext getExecutionContext() { return executionContext; }
    public StringProperty errorMessageProperty() { return errorMessage; }
    public BooleanProperty programLoadedProperty() { return programLoaded; }
    public StringProperty turtleStateProperty() { return turtleState; }
    public BooleanProperty autoModeProperty() { return autoMode; }
    public boolean isAutoMode() { return autoMode.get(); }
    public DoubleProperty speedProperty() { return speed; }
    public double getSpeed() { return speed.get(); }
    public int getExecutionFaultLineIndex() { return executionFaultLineIndex.get(); }
    public ReadOnlyIntegerProperty executionFaultLineIndexProperty() { return executionFaultLineIndex; }
    public ObservableList<VariableRow> getObservableVariables() { return observableVariables; }
    public IntegerProperty contentChangeCounterProperty() { return contentChangeCounter; }
}
