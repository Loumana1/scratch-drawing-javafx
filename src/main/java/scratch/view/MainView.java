package scratch.view;

import javafx.scene.layout.*;

public class MainView extends BorderPane {

    public MainView() {

        PaletteView palette = new PaletteView();

        Pane centerPane = new Pane();
        centerPane.setStyle("-fx-background-color: #f4f4f4;");

        setLeft(palette);
        setCenter(centerPane);
    }
}