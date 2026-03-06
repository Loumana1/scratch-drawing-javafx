package scratch.view;

import javafx.application.Platform;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import scratch.model.Action;
import scratch.viewmodel.MainViewModel;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import javafx.geometry.Insets;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.File;


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
        newItem.setOnAction(e -> viewModel.newProgram());
        MenuItem openItem = new MenuItem("Open...");
        openItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Ouvrir");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
            );

            File defaultDir = new File("data");

            if (defaultDir.exists() && defaultDir.isDirectory()) {
                fileChooser.setInitialDirectory(defaultDir);
            } else {
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            }

            File file = fileChooser.showOpenDialog(getScene().getWindow());

            if (file != null) {
                viewModel.loadFromFile(file);
            }
        });

        MenuItem saveItem = new MenuItem("Save As...");
        saveItem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer sous");

            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
            );

            File defaultDir = new File("data");

            if (defaultDir.exists() && defaultDir.isDirectory()) {
                fileChooser.setInitialDirectory(defaultDir);
            } else {
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            }

            fileChooser.setInitialFileName("programme.scr");

            File file = fileChooser.showSaveDialog(getScene().getWindow());

            if (file != null) {
                if (!file.getName().toLowerCase().endsWith(".scr")) {
                    file = new File(file.getAbsolutePath() + ".scr");
                }
                viewModel.saveToFile(file);
            }
        });
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);

        menuBar.getMenus().add(fileMenu);

        setTop(menuBar);


        setLeft(palette);
        setCenter(center);
    }
}