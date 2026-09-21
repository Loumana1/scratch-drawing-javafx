package scratch;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scratch.model.Program;
import scratch.view.MainView;
import scratch.viewmodel.MainViewModel;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {

        Program program = new Program();

        MainViewModel viewModel = new MainViewModel(program);

        MainView mainView = new MainView(viewModel);

        Scene scene = new Scene(mainView, 1400, 800);

        primaryStage.setTitle("Scratch");
        primaryStage.setScene(scene);

        primaryStage.setMinWidth(1200);
        primaryStage.setMinHeight(700);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}