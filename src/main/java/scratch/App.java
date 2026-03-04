package scratch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import scratch.model.Program;
import scratch.view.MainView;
import scratch.view.PaletteView;
import scratch.viewmodel.MainViewModel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        Program program = new Program();

        MainViewModel viewModel = new MainViewModel(program);

        MainView mainView = new MainView(viewModel);

        Scene scene = new Scene(mainView, 1000, 600);

        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}