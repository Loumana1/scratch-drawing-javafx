package scratch.view;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.viewmodel.ActionConfigViewModel;
import scratch.viewmodel.MainViewModel;
import scratch.viewmodel.ParameterViewModel;

public class DetailPanelView extends TitledPane {

    private final VBox container = new VBox(10);
    private final MainViewModel mainViewModel;
    private final ActionConfigViewModel configViewModel;
    private final Label lblRuntimeError = new Label();

    public DetailPanelView(MainViewModel mainViewModel) {
        this.mainViewModel = mainViewModel;
        this.configViewModel = mainViewModel.getConfigViewModel();

        this.setText("Détail de l'action");
        this.setContent(container);

        lblRuntimeError.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");
        lblRuntimeError.setWrapText(true);
        lblRuntimeError.textProperty().bind(mainViewModel.errorMessageProperty());

        // Écouter les changements de sélection
        configViewModel.getParameters().addListener((ListChangeListener<ParameterViewModel>) c -> refreshUI());

        refreshUI();
    }

    private void refreshUI() {
        container.getChildren().clear();

        // 1. Si la liste est vide (pas d'action sélectionnée ou action sans paramètre comme PenUp)
        if (configViewModel.getParameters().isEmpty()) {
            Label noActionLabel = new Label("Aucun paramètre à configurer.");
            noActionLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
            container.getChildren().add(noActionLabel);
            container.getChildren().add(lblRuntimeError);
            return;
        }

        // 2. Création dynamique des champs pour l'action sélectionnée (tous sur la même ligne)
        HBox allParamsRow = new HBox(15);
        allParamsRow.setAlignment(Pos.CENTER_LEFT);

        for (ParameterViewModel paramViewModel : configViewModel.getParameters()) {
            // Le nom du paramètre
            Label label = new Label(paramViewModel.getLabel() + " :");

            // Le champ de texte
            TextField textField = new TextField();
            textField.textProperty().bindBidirectional(paramViewModel.valueProperty());
            textField.setMaxWidth(60);

            // L'unité
            Label unitLabel = new Label(paramViewModel.getUnit());
            unitLabel.setVisible(!paramViewModel.getUnit().isEmpty());
            unitLabel.setManaged(!paramViewModel.getUnit().isEmpty());

            // Les boutons + et -
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