package scratch.view;

import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import scratch.viewmodel.MainViewModel;

public class MainView extends BorderPane {

    public MainView(MainViewModel viewModel) {

        PaletteView palette = new PaletteView(viewModel);
        ProgramView program = new ProgramView(viewModel);

        VBox programWrapper = new VBox(program);
        programWrapper.setPadding(new Insets(0,20,0,0));

        setLeft(palette);
        setCenter(programWrapper);


    }
}