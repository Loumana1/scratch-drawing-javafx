package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import scratch.model.Action;
import scratch.model.ParameterizedAction;
import scratch.viewmodel.ActionDetail;
import scratch.viewmodel.MainViewModel;

public class DetailPanelView extends TitledPane {

    private final MainViewModel viewModel;
    private final ListView<Action> programListView;

    private final Label lblDetailTitle = new Label("(aucune action sélectionnée)");
    private final TextField txtValue = new TextField();
    private final Label lblError = new Label("Error valeur");
    private final Label lblPixels = new Label("");
    private final Label lblRuntimeError = new Label();

    private final TextField txtTargetVar;
    private final Label lblAssignValue;
    private final Button btnPlus;
    private final Button btnMinus;

    private VBox detailPane = new VBox(5);
    private javafx.beans.value.ChangeListener<String> currentListener;
    private javafx.beans.value.ChangeListener<String> targetVarListener;

    public DetailPanelView(MainViewModel viewModel, ListView<Action> programListView) {
        this.viewModel = viewModel;
        this.programListView = programListView;

        txtTargetVar = new TextField();
        txtTargetVar.setMaxWidth(40);
        lblAssignValue = new Label(" valeur : ");
        btnPlus = new Button("+");
        btnMinus = new Button("-");

        txtTargetVar.setVisible(false);
        txtTargetVar.setManaged(false);
        lblAssignValue.setVisible(false);
        lblAssignValue.setManaged(false);
        btnPlus.setVisible(false);
        btnPlus.setManaged(false);
        btnMinus.setVisible(false);
        btnMinus.setManaged(false);

        setText("Détails de l'action");
        lblError.setStyle("-fx-text-fill: red;");
        lblError.setVisible(false);
        lblError.setManaged(false);
        txtValue.setVisible(false);
        txtValue.setManaged(false);
        txtValue.setMaxWidth(40);

        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(lblDetailTitle, txtTargetVar, lblAssignValue, txtValue, lblPixels, btnPlus, btnMinus);

        detailPane.getChildren().addAll(row, lblError);
        setContent(detailPane);

        lblRuntimeError.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        lblRuntimeError.setWrapText(true);
        lblRuntimeError.textProperty().bind(viewModel.errorMessageProperty());
        detailPane.getChildren().add(lblRuntimeError);

        viewModel.selectedIndexProperty().addListener((obs, old, nw) -> updateDetailPane());
    }

    private void updateDetailPane() {
        detachTargetVarListener();

        if (currentListener != null) {
            txtValue.textProperty().removeListener(currentListener);
            currentListener = null;
        }

        lblError.setVisible(false);
        lblError.setManaged(false);

        // On cache tout par défaut
        txtTargetVar.setVisible(false);
        txtTargetVar.setManaged(false);
        lblAssignValue.setVisible(false);
        lblAssignValue.setManaged(false);
        btnPlus.setVisible(false);
        btnPlus.setManaged(false);
        btnMinus.setVisible(false);
        btnMinus.setManaged(false);

        Action action = viewModel.getSelectedAction();

        // -------- BLOC 1 : LA DECLARATION --------
        if (action != null && action.getType() == scratch.model.ActionType.VAR_DECLARATION) {
            scratch.model.VarDeclarationAction varAction = (scratch.model.VarDeclarationAction) action;
            lblDetailTitle.setText("Déclaration de la variable ");

            txtValue.setDisable(false);
            txtValue.setVisible(true);
            txtValue.setManaged(true);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }

            txtValue.setText(varAction.getVarName());
            configVarTextField(varAction);
            return;
        }

        // -------- BLOC 2 : L'ASSIGNATION --------
        if (action != null && action.getType() == scratch.model.ActionType.VAR_ASSIGNMENT) {
            scratch.model.VarAssignmentAction assignAction = (scratch.model.VarAssignmentAction) action;

            lblDetailTitle.setText("Assignation de la variable ");
            lblAssignValue.setText(" valeur : "); // Texte de l'assignation

            txtTargetVar.setVisible(true);
            txtTargetVar.setManaged(true);
            lblAssignValue.setVisible(true);
            lblAssignValue.setManaged(true);
            txtValue.setVisible(true);
            txtValue.setManaged(true);
            txtValue.setDisable(false);
            btnPlus.setVisible(true);
            btnPlus.setManaged(true);
            btnMinus.setVisible(true);
            btnMinus.setManaged(true);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }

            txtTargetVar.setText(assignAction.getTargetVar());
            txtValue.setText(assignAction.getValue());
            configAssignFields(assignAction);
            return;
        }

        // -------- BLOC 3 : L'INCREMENTATION --------
        if (action != null && action.getType() == scratch.model.ActionType.INCREMENT_VARIABLE) {
            scratch.model.IncrementVariableAction incAction = (scratch.model.IncrementVariableAction) action;

            lblDetailTitle.setText("Incrémentation de la variable "); // Texte de ta photo
            lblAssignValue.setText(" de "); // Texte de ta photo

            txtTargetVar.setVisible(true);
            txtTargetVar.setManaged(true);
            lblAssignValue.setVisible(true);
            lblAssignValue.setManaged(true);
            txtValue.setVisible(true);
            txtValue.setManaged(true);
            txtValue.setDisable(false);
            btnPlus.setVisible(true);
            btnPlus.setManaged(true);
            btnMinus.setVisible(true);
            btnMinus.setManaged(true);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }

            txtTargetVar.setText(incAction.getTargetVar());
            txtValue.setText(incAction.getValue());
            configIncFields(incAction);
            return;
        }

        // -------- LE RESTE DES ACTIONS --------
        ActionDetail detail = viewModel.getSelectedActionDetail();

        if (detail == null) {
            lblDetailTitle.setText("(aucune action sélectionnée)");
            txtValue.setText("");
            txtValue.setDisable(true);
            txtValue.setVisible(false);
            txtValue.setManaged(false);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);
            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }
            return;
        }

        lblDetailTitle.setText(detail.getTitle());

        if (!detail.isValueEditable()) {
            txtValue.setText("0");
            txtValue.setDisable(true);
            txtValue.setVisible(false);
            txtValue.setManaged(false);
            lblPixels.setVisible(false);
            lblPixels.setManaged(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }
            lblError.setVisible(false);
            lblError.setManaged(false);
            return;
        }

        txtValue.setDisable(false);
        txtValue.setVisible(true);
        txtValue.setManaged(true);
        lblPixels.setVisible(true);
        lblPixels.setManaged(true);
        lblPixels.setText(detail.getUnitText());

        txtValue.setText(action.getExpression());

        configTextField();
    }

    private void configVarTextField(scratch.model.VarDeclarationAction action) {
        lblError.setVisible(false);
        lblError.setManaged(false);

        currentListener = ((obs, old, text) -> {
            if (text == null || text.isBlank() || !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                lblError.setText("Erreur valeur");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setVarName(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);
    }

    private void configTextField() {
        lblError.setVisible(false);
        lblError.setManaged(false);

        if (currentListener != null){
            txtValue.textProperty().removeListener(currentListener);
        }

        currentListener = ((obs, old, text) -> {
            lblError.setVisible(false);
            lblError.setManaged(false);

            if (text == null || text.isBlank()) {
                lblError.setVisible(true);
                lblError.setManaged(true);
                return;
            }

            // On utilise la magie de notre nouvelle méthode !
            boolean ok = viewModel.tryUpdateSelectedActionWithText(text);

            if (!ok) {
                lblError.setText("Erreur : Valeur invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);
    }

    // --- CONFIGURATION ASSIGNATION ---
    private void configAssignFields(scratch.model.VarAssignmentAction action) {
        lblError.setVisible(false);
        lblError.setManaged(false);

            targetVarListener = (obs, old, text) ->{
            if (text == null || text.isBlank() || !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                lblError.setText("Erreur : Nom cible invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setTargetVar(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        };
        txtTargetVar.textProperty().addListener(targetVarListener);

        currentListener = ((obs, old, text) -> {
            if (text == null || text.isBlank() || (!text.matches("^-?\\d+$") && !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                lblError.setText("Erreur : Valeur invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setValue(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);

        btnPlus.setOnAction(null);
        btnMinus.setOnAction(null);
        btnPlus.setOnAction(e -> updateNumericValueAssign(action, 1));
        btnMinus.setOnAction(e -> updateNumericValueAssign(action, -1));
    }

    private void updateNumericValueAssign(scratch.model.VarAssignmentAction action, int delta) {
        try {
            int currentVal = Integer.parseInt(txtValue.getText());
            int newVal = currentVal + delta;
            txtValue.setText(String.valueOf(newVal));
        } catch (NumberFormatException ex) {}
    }

    // --- CONFIGURATION INCREMENTATION ---
    private void configIncFields(scratch.model.IncrementVariableAction action) {
        lblError.setVisible(false);
        lblError.setManaged(false);

        targetVarListener = (obs, old, text) -> {
            if (text == null || text.isBlank() || !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
                lblError.setText("Erreur : Nom cible invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setTargetVar(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        };
        txtTargetVar.textProperty().addListener(targetVarListener);

        currentListener = ((obs, old, text) -> {
            if (text == null || text.isBlank() || (!text.matches("^-?\\d+$") && !text.matches("^[a-zA-Z_][a-zA-Z0-9_]*$"))) {
                lblError.setText("Erreur : Valeur invalide");
                lblError.setVisible(true);
                lblError.setManaged(true);
            } else {
                lblError.setVisible(false);
                lblError.setManaged(false);
                action.setValue(text);
                programListView.refresh();
                viewModel.notifyProgramContentChanged();
            }
        });
        txtValue.textProperty().addListener(currentListener);

        btnPlus.setOnAction(null);
        btnMinus.setOnAction(null);
        btnPlus.setOnAction(e -> updateNumericValueInc(action, 1));
        btnMinus.setOnAction(e -> updateNumericValueInc(action, -1));
    }

    private void updateNumericValueInc(scratch.model.IncrementVariableAction action, int delta) {
        try {
            int currentVal = Integer.parseInt(txtValue.getText());
            int newVal = currentVal + delta;
            txtValue.setText(String.valueOf(newVal));
        } catch (NumberFormatException ex) {}
    }

    private void detachTargetVarListener() {
        if (targetVarListener != null) {
            txtTargetVar.textProperty().removeListener(targetVarListener);
            targetVarListener = null;
        }
    }
}