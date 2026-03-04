package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import scratch.model.ActionType;
import scratch.viewmodel.MainViewModel;

public class PaletteView extends VBox {

    public PaletteView(MainViewModel viewModel) {

        setSpacing(10);
        setPadding(new Insets(10));

        Label title = new Label("Palette d'actions");

        ListView<String> listView = new ListView<>();

        listView.getItems().addAll(
                "Avancer",
                "Tourner gauche",
                "Tourner droite",
                "Lever stylo",
                "Abaisser stylo"
        );

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