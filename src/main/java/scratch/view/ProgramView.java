package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.*;
import scratch.viewmodel.MainViewModel;

public class ProgramView extends VBox {

    public ProgramView(MainViewModel viewModel) {

        setSpacing(10);
        setPadding(new Insets(10));
        setPrefWidth(450);
        setMaxWidth(450);

        Label title = new Label("Programme");

        ListView<Action> programList = new ListView<>();
        programList.setItems(viewModel.getObservableActions());

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

        Button btnUp = new Button("Monter");
        Button btnDown = new Button("Descendre");
        Button btnDuplicate = new Button("Dupliquer");
        Button btnRemove = new Button("Supprimer");
        Button btnClear = new Button("Vider tout");

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
}