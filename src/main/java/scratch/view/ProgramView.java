package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;

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
            viewModel.selectedIndexProperty().set(newVal.intValue());
        });

        programList.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(Action action, boolean empty) {
                super.updateItem(action, empty);

                if (empty || action == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Circle circle = new Circle(5);
                Label label = new Label();

                switch (action.getType()) {

                    case MOVE_FORWARD -> {
                        MoveForwardAction a = (MoveForwardAction) action;
                        circle.setFill(Color.BLUE);
                        label.setTextFill(Color.BLUE);
                        label.setText("Avancer de " + a.getValue());
                    }

                    case TURN_LEFT -> {
                        TurnLeftAction a = (TurnLeftAction) action;
                        circle.setFill(Color.RED);
                        label.setTextFill(Color.RED);
                        label.setText("Tourner à gauche de " + a.getValue());
                    }

                    case TURN_RIGHT -> {
                        TurnRightAction a = (TurnRightAction) action;
                        circle.setFill(Color.RED);
                        label.setTextFill(Color.RED);
                        label.setText("Tourner à droite de " + a.getValue());
                    }

                    case PEN_UP -> {
                        circle.setFill(Color.GREEN);
                        label.setTextFill(Color.GREEN);
                        label.setText("Lever stylo");
                    }

                    case PEN_DOWN -> {
                        circle.setFill(Color.GREEN);
                        label.setTextFill(Color.GREEN);
                        label.setText("Abaisser stylo");
                    }
                }

                HBox box = new HBox(10, circle, label);
                box.setPadding(new Insets(5, 0, 5, 5));

                setGraphic(box);
                setText(null);
            }
        });

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