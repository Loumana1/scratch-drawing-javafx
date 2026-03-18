package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.model.ActionType;
import scratch.viewmodel.MainViewModel;

import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PaletteView extends VBox {
    private final ListView<ActionType> listView = new ListView<>();
    private final MainViewModel viewModel;

    public PaletteView(MainViewModel viewModel) {

        this.viewModel = viewModel;
        setSpacing(8);
        setPadding(new Insets(10));
        setPrefWidth(260);

        Label title = new Label("Palette d'actions");




        listView.setFixedCellSize(28);
        double maxHeight = 28 * 15;
        listView.setPrefHeight(maxHeight);
        listView.setMaxHeight(maxHeight);

        listView.setStyle(
                "-fx-border-color: #3FA9F5;" +
                        "-fx-border-width: 2;" +
                        "-fx-background-color: white;"
        );
        // Remplir avec l'enum au lieu de Strings
        listView.getItems().addAll(ActionType.values());

        // Cellule personnalisée
        listView.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(ActionType type, boolean empty) {


                    super.updateItem(type, empty);

                    if (empty || type == null) {
                        setGraphic(null);
                    } else {
                        Circle circle = new Circle(5);
                        Label label = new Label(getDisplayName(type));
                        Color color = switch (type) {
                            case MOVE_FORWARD -> Color.BLUE;
                            case TURN_LEFT, TURN_RIGHT -> Color.RED;
                            case PEN_UP, PEN_DOWN -> Color.GREEN;
                            case REPEAT , END_REPEAT -> Color.CHOCOLATE;
                        };
                        circle.setFill(color);
                        label.setTextFill(color);

                        HBox box = new HBox(10, circle, label);
                        box.setPadding(new Insets(5, 0, 5, 5));

                        setGraphic(box);
                    }

            }
        });

        Button addButton = new Button("Ajouter au programme");

        addButton.disableProperty().bind(
                listView.getSelectionModel().selectedItemProperty().isNull()
        );

        addButton.setOnAction(e -> {

            int index = listView.getSelectionModel().getSelectedIndex();

            if (index == 0) viewModel.addAction(ActionType.MOVE_FORWARD);
            if (index == 1) viewModel.addAction(ActionType.TURN_LEFT);
            if (index == 2) viewModel.addAction(ActionType.TURN_RIGHT);
            if (index == 3) viewModel.addAction(ActionType.PEN_UP);
            if (index == 4) viewModel.addAction(ActionType.PEN_DOWN);
        });

        // Double_clic
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ActionType selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null){
                    viewModel.addAction(selected);
                }
            }
        });

        getChildren().addAll(title, listView, addButton);


    }

    public static String getDisplayName(ActionType type) {
        return switch (type) {
            case MOVE_FORWARD -> "Avancer de";
            case TURN_LEFT -> "Tourner à gauche de";
            case TURN_RIGHT -> "Tourner à droite de";
            case PEN_UP -> "Lever le stylo";
            case PEN_DOWN -> "Abaisser le stylo";
            case REPEAT -> "Repeter";
            case END_REPEAT -> "Fin Repeter";
        };
    }
}