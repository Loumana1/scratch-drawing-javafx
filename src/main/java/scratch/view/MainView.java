package scratch.view;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import scratch.viewmodel.MainViewModel;

public class MainView extends BorderPane {

    public MainView(MainViewModel viewModel) {

        PaletteView palette = new PaletteView(viewModel);
        ProgramView program = new ProgramView(viewModel);
        SceneView scene = new SceneView(viewModel);

        HBox center = new HBox(20);
        center.getChildren().addAll(program, scene);

        setLeft(palette);
        setCenter(center);
    }
}