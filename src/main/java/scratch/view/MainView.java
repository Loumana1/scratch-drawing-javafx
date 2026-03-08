package scratch.view;

import javafx.scene.layout.*;
import scratch.viewmodel.MainViewModel;

import javafx.geometry.Insets;

import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final MainViewModel viewModel;

    public MainView(MainViewModel viewModel) {
        this.viewModel = viewModel;

        PaletteView palette = new PaletteView(viewModel);
        ProgramView program = new ProgramView(viewModel);
        SceneView scene = new SceneView(viewModel);

        HBox center = new HBox(20);
        center.getChildren().addAll(program, scene);

        setLeft(palette);
        setCenter(center);
    }
}
