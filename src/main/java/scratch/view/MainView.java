package scratch.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import scratch.viewmodel.MainViewModel;

public class MainView extends BorderPane {

    public MainView(MainViewModel viewModel) {

        PaletteView palette = new PaletteView(viewModel);

        Pane centerPane = new Pane();
        centerPane.setStyle("-fx-background-color: #f4f4f4;");

        setLeft(palette);
        setCenter(centerPane);
    }
}