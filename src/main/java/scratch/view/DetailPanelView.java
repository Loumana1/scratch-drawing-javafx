package scratch.view;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.viewmodel.ActionConfigViewModel;
import scratch.viewmodel.SceneViewModel;
import scratch.viewmodel.ParameterViewModel;

public class DetailPanelView extends TitledPane {

    private final VBox container = new VBox(10);
    private final ActionConfigViewModel configViewModel;
    private final SceneViewModel sceneViewModel;
    private final Label lblRuntimeError = new Label();

    public DetailPanelView(ActionConfigViewModel configViewModel, SceneViewModel sceneViewModel) {
        this.configViewModel = configViewModel;
        this.sceneViewModel = sceneViewModel;

        this.setText("Détail de l'action");
        this.setContent(container);

        lblRuntimeError.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");
        lblRuntimeError.setWrapText(true);
        lblRuntimeError.textProperty().bind(sceneViewModel.errorMessageProperty());

        configViewModel.getParameters().addListener((ListChangeListener<ParameterViewModel>) c -> refreshUI());

        refreshUI();
    }

    private void refreshUI() {
        container.getChildren().clear();

        if (configViewModel.getParameters().isEmpty()) {
            Label noActionLabel = new Label("Aucun paramètre à configurer.");
            noActionLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            container.getChildren().add(noActionLabel);
            container.getChildren().add(lblRuntimeError);
            return;
        }

        HBox allParamsRow = new HBox(15);
        allParamsRow.setAlignment(Pos.CENTER_LEFT);

        for (ParameterViewModel paramViewModel : configViewModel.getParameters()) {
            Label label = new Label(paramViewModel.getLabel() + " :");

            TextField textField = new TextField();
            textField.textProperty().bindBidirectional(paramViewModel.valueProperty());
            textField.setMaxWidth(60);

            Label unitLabel = new Label(paramViewModel.getUnit());
            unitLabel.setVisible(!paramViewModel.getUnit().isEmpty());
            unitLabel.setManaged(!paramViewModel.getUnit().isEmpty());

            Button btnMinus = new Button("-");
            Button btnPlus = new Button("+");

            btnMinus.setOnAction(e -> paramViewModel.decrement());
            btnPlus.setOnAction(e -> paramViewModel.increment());

            if (paramViewModel.hasButtons())
                allParamsRow.getChildren().addAll(label, textField, unitLabel, btnMinus, btnPlus);
            else
                allParamsRow.getChildren().addAll(label, textField, unitLabel);
        }
        container.getChildren().add(allParamsRow);

        container.getChildren().add(lblRuntimeError);
    }
}