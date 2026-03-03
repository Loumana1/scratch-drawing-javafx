package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PaletteView extends VBox {

    public PaletteView() {

        setSpacing(8);
        setPadding(new Insets(10));
        setPrefWidth(260);

        Label title = new Label("Palette d'actions");

        ListView<String> listView = new ListView<>();
        listView.getItems().addAll(
                "Avancer de",
                "Tourner à gauche de",
                "Tourner à droite de",
                "Lever stylo",
                "Abaisser stylo"
        );


        listView.setFixedCellSize(28);
        double maxHeight = 28 * 15;
        listView.setPrefHeight(maxHeight);
        listView.setMaxHeight(maxHeight);

        listView.setStyle(
                "-fx-border-color: #3FA9F5;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );

        // Cellule personnalisée
        listView.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {

                    Circle circle = new Circle(5);

                    Label label = new Label(item);

                    if (item.startsWith("Avancer")) {
                        circle.setFill(Color.BLUE);
                        label.setTextFill(Color.BLUE);
                    }
                    else if (item.contains("gauche")) {
                        circle.setFill(Color.RED);
                        label.setTextFill(Color.RED);
                    }
                    else if (item.contains("droite")) {
                        circle.setFill(Color.RED);
                        label.setTextFill(Color.RED);
                    }
                    else if (item.startsWith("Lever")) {
                        circle.setFill(Color.GREEN);
                        label.setTextFill(Color.GREEN);
                    }
                    else {
                        circle.setFill(Color.GREEN);
                        label.setTextFill(Color.GREEN);
                    }

                    HBox box = new HBox(10, circle, label);
                    box.setPadding(new Insets(5, 0, 5, 5));

                    setGraphic(box);
                }
            }
        });

        Button addButton = new Button("Ajouter au programme");
        addButton.setDisable(true);



        getChildren().addAll(title, listView, addButton);
    }
}