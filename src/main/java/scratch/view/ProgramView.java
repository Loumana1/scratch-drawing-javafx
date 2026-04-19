package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;
import javafx.geometry.Pos;

public class ProgramView extends VBox {


    private final Button btnUp = new Button("Monter");
    private final Button btnDown = new Button("Descendre");
    private final Button btnDuplicate = new Button("Dupliquer");
    private final Button btnRemove = new Button("Supprimer");
    private final Button btnClear = new Button("Vider tout");
    private MainViewModel viewModel ;

    public ProgramView(MainViewModel viewModel) {

        this.viewModel = viewModel;

        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(450);
        setMaxWidth(450);

        Label title = new Label("Programme");

        ListView<Action> programList = new ListView<>();
        programList.setItems(viewModel.getObservableActions());
        addListeners();
        configurationBindings();

        // Quand on clique sur la liste, on met à jour l'index sélectionné dans le ViewModel
        programList.getSelectionModel().selectedIndexProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() >= 0) {
                viewModel.selectedIndexProperty().set(newVal.intValue());
            }
        });
        viewModel.selectedIndexProperty().addListener((obs, old, nw) -> {
            programList.getSelectionModel().select(nw.intValue());
        });


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

                // Profondeur d indentation
                int depth = 0 ;
                int currentIdx = getIndex();
                for (int i = 0; i < currentIdx; i++) {
                    Action a = getListView().getItems().get(i);
                    if (a.getType() == ActionType.REPEAT)
                        depth++;
                    else if (a.getType() == ActionType.END_REPEAT)
                        depth--;
                }
                // EndRepeat au meme niveau que repeat
                if (action.getType() == ActionType.END_REPEAT)
                    depth--;
                if (depth < 0)
                    depth = 0 ;

                int leftPadding = 5 + (depth * 20);

                HBox box = new HBox(10, circle, label);
                box.setAlignment(Pos.CENTER_LEFT);
                box.setPadding(new Insets(5, 0, 5, leftPadding));

                setGraphic(box);
                setText(null);

                // Encadrer en rouge erreur
                boolean hasError = viewModel.errorMessageProperty().get() != null
                        && !viewModel.errorMessageProperty().get().isEmpty();
                int faultIdx = viewModel.getExecutionFaultLineIndex();
                boolean isFaultLine = faultIdx >= 0 && getIndex() == faultIdx;
                if (hasError && isFaultLine) {
                    setStyle("-fx-border-color: red; -fx-border-width: 2;");
                } else {
                    setStyle("");
                }
                setGraphic(box);
                setText(null);

            }
        });
        // Forcer le rrefresh qd nouvelle erreur
        viewModel.executionFaultLineIndexProperty().addListener((obs, o, n) -> programList.refresh());
        HBox buttons = new HBox(10);


        buttons.getChildren().addAll(
                btnUp,
                btnDown,
                btnDuplicate,
                btnRemove,
                btnClear
        );

        DetailPanelView detailPanel =
                new DetailPanelView(viewModel, programList);

        getChildren().addAll(
                title,
                programList,
                buttons,
                detailPanel
        );

    }
    // Listiners
    private void addListeners(){
        btnRemove.setOnAction(e -> viewModel.removeSelectedAction());
        btnClear.setOnAction(e -> viewModel.clearProgram());
        btnDown.setOnAction(e -> viewModel.moveDown());
        btnUp.setOnAction(e -> viewModel.moveUp());
        btnDuplicate.setOnAction(e -> viewModel.duplicateSelected());
    }

    // Bindins Button
    private void configurationBindings(){
        btnRemove.disableProperty().bind(viewModel.canRemove().not());
        btnClear.disableProperty().bind(Bindings.isEmpty(viewModel.getObservableActions()));
        btnUp.disableProperty().bind(viewModel.canMoveUp().not());
        btnDown.disableProperty().bind(viewModel.canMoveDown().not());
        btnDuplicate.disableProperty().bind(viewModel.canDuplicate().not());
    }
}