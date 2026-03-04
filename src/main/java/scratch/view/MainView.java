package scratch.view;

import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import scratch.model.Action;
import scratch.viewmodel.MainViewModel;

import javafx.geometry.Insets;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


public class MainView extends BorderPane {

    private final MainViewModel viewModel;

    private final ListView<Action> programListView = new ListView<>();

    public MainView(MainViewModel viewModel) {
        this.viewModel = viewModel;
    //Gauche
        PaletteView palette = new PaletteView(viewModel);
        ProgramView program = new ProgramView(viewModel);
        SceneView scene = new SceneView(viewModel);

        //


        //
        HBox center = new HBox(20);
        center.getChildren().addAll(program, scene);

        //detailview
        DetailPanelView detailPanelView = new DetailPanelView(viewModel, programListView);
        programListView.setItems(viewModel.getObservableActions());


        // Synchro sélection ListView -ViewModel
        programListView.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        viewModel.selectedIndexProperty().setValue(nw.intValue()));
        viewModel.selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        programListView.getSelectionModel().select(nw.intValue()));




        setLeft(palette);
        setCenter(center);
    }
}