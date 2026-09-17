package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.viewmodel.ActionConfigViewModel;
import scratch.viewmodel.SceneViewModel;
import scratch.viewmodel.ParameterViewModel;

import java.util.ArrayList;
import java.util.List;

public class DetailPanelView extends TitledPane {

    private final VBox container = new VBox(10);
    private final ActionConfigViewModel configViewModel;
    private final Label lblRuntimeError = new Label();
    private final Label noActionLabel = new Label("Aucun paramètre à configurer.");
    private final  HBox allParamsRow = new HBox(15);

    public DetailPanelView(ActionConfigViewModel configViewModel, SceneViewModel sceneViewModel) {
        this.configViewModel = configViewModel;

        this.setText("Détail de l'action");
        this.setContent(container);

        lblRuntimeError.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");
        lblRuntimeError.setWrapText(true);
        lblRuntimeError.textProperty().bind(sceneViewModel.errorMessageProperty());

       allParamsRow.setAlignment(Pos.CENTER_LEFT);

        configViewModel.getParameters().addListener(
                (ListChangeListener<? super ParameterViewModel>) c -> refreshUI());

        refreshUI();
    }

    private void refreshUI() {
        allParamsRow.getChildren().clear();
        container.getChildren().clear();


     if (configViewModel.emptyProperty().get()){
            container.getChildren().addAll(noActionLabel, lblRuntimeError);
            return;
        }




        for (ParameterViewModel paramViewModel : configViewModel.getParameters()) {
            Label label = new Label();
            label.textProperty().bind(Bindings.concat(paramViewModel.labelProperty(), " :"));

            TextField textField = new TextField();
            textField.textProperty().bindBidirectional(paramViewModel.valueProperty());
            textField.setPrefWidth(50);
            textField.setMaxWidth(60);

            Label unitLabel = new Label();
            unitLabel.textProperty().bind(paramViewModel.unitProperty());
            unitLabel.visibleProperty().bind(paramViewModel.unitVisibleProperty());
            unitLabel.managedProperty().bind(paramViewModel.unitVisibleProperty());


            Button btnMinus = new Button("-");
            Button btnPlus = new Button("+");
            btnMinus.setOnAction(e -> paramViewModel.decrement());
            btnPlus.setOnAction(e -> paramViewModel.increment());
            btnMinus.visibleProperty().bind(paramViewModel.showButtonsProperty());
            btnMinus.managedProperty().bind(paramViewModel.showButtonsProperty());
            btnPlus.visibleProperty().bind(paramViewModel.showButtonsProperty());
            btnPlus.managedProperty().bind(paramViewModel.showButtonsProperty());


            allParamsRow.getChildren().addAll(label, textField, unitLabel, btnMinus, btnPlus);
        }
        container.getChildren().add(allParamsRow);

        container.getChildren().add(lblRuntimeError);
    }
}