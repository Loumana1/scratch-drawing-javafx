package scratch.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import scratch.model.ActionType;
import scratch.viewmodel.ProgramViewModel;

public class PaletteView extends VBox {
    private final ListView<ActionType> listView = new ListView<>();
    private final ProgramViewModel programViewModel;

    public PaletteView(ProgramViewModel programViewModel) {

        this.programViewModel = programViewModel;
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

        CheckMenuItem modeAvance = new CheckMenuItem("Mode avancé");
        CheckMenuItem modeReligieux = new CheckMenuItem("Mode Religieux");

        modeAvance.selectedProperty().bindBidirectional(
                programViewModel.advancedModeProperty()
        );
        modeReligieux.selectedProperty().bindBidirectional(
                programViewModel.religiousModeProprety()
        );


        listView.setItems(programViewModel.getPaletteItems());

        listView.setCellFactory(lv -> new ListCell<>() {

            @Override
            protected void updateItem(ActionType type, boolean empty) {
                super.updateItem(type, empty);

                if (empty || type == null) {
                    setGraphic(null);
                } else {
                    Color color = programViewModel.getDisplayColor(type);

                    Circle circle = new Circle(5);
                    circle.setFill(color);

                    Label label = new Label(programViewModel.getDisplayTitle(type));
                    label.setTextFill(color);

                    HBox box = new HBox(10, circle, label);
                    box.setAlignment(Pos.CENTER_LEFT);
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
            ActionType selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                programViewModel.addAction(selected);
            }
        });

        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                ActionType selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    programViewModel.addAction(selected);
                }
            }
        });

        getChildren().addAll(title, listView, addButton, modeAvance, modeReligieux);
    }
}