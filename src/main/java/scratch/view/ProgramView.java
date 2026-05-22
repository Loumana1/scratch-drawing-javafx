package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import scratch.model.*;
import scratch.viewmodel.ActionConfigViewModel;
import scratch.viewmodel.ProgramViewModel;
import scratch.viewmodel.SceneViewModel;
import javafx.geometry.Pos;

public class ProgramView extends VBox {

    private final Button btnUp = new Button("Monter");
    private final Button btnDown = new Button("Descendre");
    private final Button btnDuplicate = new Button("Dupliquer");
    private final Button btnRemove = new Button("Supprimer");
    private final Button btnClear = new Button("Vider tout");
    private final ProgramViewModel programViewModel;
    private final SceneViewModel sceneViewModel;
    private final ActionConfigViewModel configViewModel;

    public ProgramView(ProgramViewModel programViewModel, SceneViewModel sceneViewModel, ActionConfigViewModel configViewModel) {

        this.programViewModel = programViewModel;
        this.sceneViewModel = sceneViewModel;
        this.configViewModel = configViewModel;

        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(450);
        setMaxWidth(450);

        Label title = new Label("Programme");

        ListView<Action> programList = new ListView<>();
        programList.setItems(programViewModel.getObservableActions());
        addListeners();
        configurationBindings();

        programViewModel.bindListSelection(programList);

        programList.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(Action action, boolean empty) {
                super.updateItem(action, empty);

                if (empty || action == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                    return;
                }

                Circle circle = new Circle(5);
                Label label = new Label();

                circle.setFill(action.getColor());
                label.setTextFill(action.getColor());
                label.setText(action.toString());

                int depth = programViewModel.getIndentDepth(getIndex());
                int leftPadding = 5 + (depth * 20);

                HBox box = new HBox(10, circle, label);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(5, 0, 5, leftPadding));

                setGraphic(box);
                setText(null);

                if (sceneViewModel.isErrorLine(getIndex())) {
                    setStyle("-fx-border-color: red; -fx-border-width: 2;");
                } else {
                    setStyle("");
                }
            }
        });

        Runnable refreshList = programList::refresh;
        sceneViewModel.executionFaultLineIndexProperty().addListener((obs, o, n) -> refreshList.run());
        programViewModel.programChangeCounterProperty().addListener((obs, o, n) ->  refreshList.run());
        sceneViewModel.errorMessageProperty().addListener((obs, o, n) ->  refreshList.run());

        HBox buttons = new HBox(10);

        buttons.getChildren().addAll(
                btnUp,
                btnDown,
                btnDuplicate,
                btnRemove,
                btnClear
        );

        DetailPanelView detailPanel =
                new DetailPanelView(configViewModel, sceneViewModel);

        getChildren().addAll(
                title,
                programList,
                buttons,
                detailPanel
        );
    }

    private void addListeners() {
        btnRemove.setOnAction(e -> programViewModel.removeSelectedAction());
        btnClear.setOnAction(e -> programViewModel.clearProgram());
        btnDown.setOnAction(e -> programViewModel.moveDown());
        btnUp.setOnAction(e -> programViewModel.moveUp());
        btnDuplicate.setOnAction(e -> programViewModel.duplicateSelected());
    }

    private void configurationBindings() {
        btnRemove.disableProperty().bind(programViewModel.canRemove().not());
        btnClear.disableProperty().bind(Bindings.isEmpty(programViewModel.getObservableActions()));
        btnUp.disableProperty().bind(programViewModel.canMoveUp().not());
        btnDown.disableProperty().bind(programViewModel.canMoveDown().not());
        btnDuplicate.disableProperty().bind(programViewModel.canDuplicate().not());
    }
}