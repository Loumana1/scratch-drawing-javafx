package scratch.view;

import javafx.collections.ListChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import scratch.model.ActionParameter;
import scratch.viewmodel.ActionConfigViewModel;
import scratch.viewmodel.MainViewModel;

public class DetailPanelView extends TitledPane {

    private final VBox container = new VBox(10);
    private final MainViewModel mainViewModel;
    private final ActionConfigViewModel configViewModel;
    private final ListView<?> programListView;
    private final Label lblRuntimeError = new Label();

    public DetailPanelView(MainViewModel mainViewModel, ListView<?> programListView) {
        this.mainViewModel = mainViewModel;
        this.programListView = programListView;
        this.configViewModel = mainViewModel.getConfigViewModel();

        this.setText("Détail de l'action");
        this.setContent(container);

        lblRuntimeError.setStyle("-fx-text-fill: red; -fx-font-size: 15px;");
        lblRuntimeError.setWrapText(true);
        lblRuntimeError.textProperty().bind(mainViewModel.errorMessageProperty());

        // Écouter les changements de sélection
        configViewModel.getParameters().addListener((ListChangeListener<ActionParameter>) c -> refreshUI());

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

        for (ActionParameter param : configViewModel.getParameters()) {
            // Le nom du paramètre
            Label label = new Label(param.getLabel() + " :");

            // Le champ de texte
            TextField textField = new TextField(param.getValue());
            textField.setMaxWidth(60);

            // L'unité
            Label unitLabel = new Label(param.getUnit());
            unitLabel.setVisible(!param.getUnit().isEmpty());
            unitLabel.setManaged(!param.getUnit().isEmpty());

            // Les boutons + et -
            Button btnMinus = new Button("-");
            Button btnPlus = new Button("+");

            btnMinus.setOnAction(e -> updateNumericValue(textField, -1));
            btnPlus.setOnAction(e -> updateNumericValue(textField, 1));

            // Binding : quand on tape, on met à jour le modèle et on vérifie la validité
            textField.textProperty().addListener((obs, oldVal, newVal) -> {
                param.setValue(newVal);
                mainViewModel.notifyProgramContentChanged();

                // Validation réactive
                int idx = mainViewModel.getSelectedIndex();
                if (idx >= 0) {
                    scratch.model.Action action = mainViewModel.getObservableActions().get(idx);
                    if (!action.isValid(mainViewModel.getExecutionContext())) {
                        mainViewModel.errorMessageProperty().set("Erreur : Valeur ou variable '" + newVal + "' invalide.");
                    } else {
                        mainViewModel.errorMessageProperty().set("");
                    }
                }
                programListView.refresh();
            });
            if (param.hasButtons())
                allParamsRow.getChildren().addAll(label, textField, unitLabel, btnMinus, btnPlus);
            else
                allParamsRow.getChildren().addAll(label, textField, unitLabel);
        }
        container.getChildren().add(allParamsRow);

        container.getChildren().add(lblRuntimeError);
    }

    /**
     * Petite méthode utilitaire pour les boutons + et -
     */
    private void updateNumericValue(TextField textField, int delta) {
        try {
            int currentVal = Integer.parseInt(textField.getText());
            textField.setText(String.valueOf(currentVal + delta));
        } catch (NumberFormatException ex) {
            // Si c'est du texte (une variable), on ne fait rien avec les boutons + et -
        }
    }
}