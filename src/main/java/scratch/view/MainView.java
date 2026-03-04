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

        //Centre
        DetailPanelView detailPanelView = new DetailPanelView(viewModel, programListView);
        programListView.setItems(viewModel.getObservableActions());


        //droite
        VBox centerBox = new VBox(10, programListView, detailPanelView);
        centerBox.setPadding(new Insets(10));
        setCenter(centerBox);


        // Synchro sélection ListView -ViewModel
        programListView.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        viewModel.selectedIndexProperty().setValue(nw.intValue()));
        viewModel.selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        programListView.getSelectionModel().select(nw.intValue()));



        setLeft(palette);
        setCenter(centerBox);
    }
}