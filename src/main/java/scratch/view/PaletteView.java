package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.ActionType;
import scratch.viewmodel.MainViewModel;

public class PaletteView extends VBox {

    public PaletteView(MainViewModel viewModel) {

        setSpacing(8);
        setPadding(new Insets(10));
        setPrefWidth(260);

        Label title = new Label("Palette d'actions");

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(
                "Avancer",
                "Tourner à gauche",
                "Tourner à droite",
                "Lever stylo",
                "Abaisser stylo"
        );

        listView.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                Circle circle = new Circle(5);
                Label label = new Label(item);

                if (item.startsWith("Avancer")) {
                    circle.setFill(Color.BLUE);
                    label.setTextFill(Color.BLUE);
                }

                else if (item.contains("gauche") || item.contains("droite")) {
                    circle.setFill(Color.RED);
                    label.setTextFill(Color.RED);
                }

                else {
                    circle.setFill(Color.GREEN);
                    label.setTextFill(Color.GREEN);
                }

                HBox box = new HBox(10, circle, label);
                box.setPadding(new Insets(5,0,5,5));

                setGraphic(box);
            }
        });

        Button addButton = new Button("Ajouter au programme");

        addButton.setOnAction(e -> {

            int index = listView.getSelectionModel().getSelectedIndex();

            if (index == 0) viewModel.addAction(ActionType.MOVE_FORWARD);
            if (index == 1) viewModel.addAction(ActionType.TURN_LEFT);
            if (index == 2) viewModel.addAction(ActionType.TURN_RIGHT);
            if (index == 3) viewModel.addAction(ActionType.PEN_UP);
            if (index == 4) viewModel.addAction(ActionType.PEN_DOWN);
        });

        getChildren().addAll(title, listView, addButton);
    }
}