package scratch.view;

import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import scratch.model.Action;
import scratch.viewmodel.MainViewModel;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

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
        programListView.setItems(viewModel.getObservableActions());


        // Synchro sélection ListView -ViewModel
        programListView.getSelectionModel().selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        viewModel.selectedIndexProperty().setValue(nw.intValue()));
        viewModel.selectedIndexProperty()
                .addListener((obs, old, nw) ->
                        programListView.getSelectionModel().select(nw.intValue()));


        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New...");
        MenuItem openItem = new MenuItem("Open...");
        MenuItem saveItem = new MenuItem("Save As...");
        MenuItem exitItem = new MenuItem("Exit");

        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);

        menuBar.getMenus().add(fileMenu);

        setTop(menuBar);


        setLeft(palette);
        setCenter(center);
    }
}