package scratch.view;

import javafx.application.Platform;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import scratch.viewmodel.MainViewModel;
import scratch.viewmodel.ProgramViewModel;
import scratch.viewmodel.SceneViewModel;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

import java.io.File;
import javafx.scene.layout.BorderPane;

public class MainView extends BorderPane {

    private final ProgramViewModel programViewModel;
    private final SceneViewModel sceneViewModel;

    public MainView(MainViewModel viewModel) {
        this.programViewModel = viewModel.getProgramViewModel();
        this.sceneViewModel = viewModel.getSceneViewModel();

        PaletteView palette = new PaletteView(programViewModel);
        ProgramView program = new ProgramView(programViewModel, sceneViewModel, viewModel.getConfigViewModel());
        SceneView scene = new SceneView(sceneViewModel);

        HBox center = new HBox(20);
        center.getChildren().addAll(program, scene);

        setTop(createMenuBar());
        setLeft(palette);
        setCenter(center);
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu fileMenu = new Menu("File");

        MenuItem newItem = new MenuItem("New...");
        newItem.setOnAction(e -> programViewModel.newProgram());

        MenuItem openItem = new MenuItem("Open...");
        openItem.setOnAction(e -> {
            FileChooser fileChooser = createScratchFileChooser("Ouvrir");
            File file = fileChooser.showOpenDialog(getScene().getWindow());

            if (file != null) {
                programViewModel.loadFromFile(file);
            }
        });

        MenuItem saveItem = new MenuItem("Save As...");
        saveItem.setOnAction(e -> {
            FileChooser fileChooser = createScratchFileChooser("Enregistrer sous");
            fileChooser.setInitialFileName("programme.scr");

            File file = fileChooser.showSaveDialog(getScene().getWindow());

            if (file != null) {
                if (!file.getName().toLowerCase().endsWith(".scr")) {
                    file = new File(file.getAbsolutePath() + ".scr");
                }
                programViewModel.saveToFile(file);
            }
        });

        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> Platform.exit());

        fileMenu.getItems().addAll(newItem, openItem, saveItem, exitItem);
        menuBar.getMenus().add(fileMenu);

        return menuBar;
    }

    private FileChooser createScratchFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Scratch files (*.scr)", "*.scr")
        );

        File defaultDir = new File("data");

        if (defaultDir.exists() && defaultDir.isDirectory()) {
            fileChooser.setInitialDirectory(defaultDir);
        } else {
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        return fileChooser;
    }
}
