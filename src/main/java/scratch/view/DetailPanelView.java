package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import scratch.model.Action;

import scratch.viewmodel.ActionDetail;
import scratch.viewmodel.MainViewModel;

public class DetailPanelView extends TitledPane {

    private final MainViewModel viewModel;
    private final ListView<Action> programListView;  // Référence pour refresh()
    // juste mis-a-j
    private final Label lblDetailTitle = new Label("(aucune action sélectionnée)");
    private final TextField txtValue = new TextField();
    private final Label lblError = new Label("Error valeur");
    private final Label lblPixels = new Label("");

    private VBox detailPane = new VBox(5);
    private javafx.beans.value.ChangeListener<String> currentListener ;




    public DetailPanelView(MainViewModel viewModel, ListView<Action> programListView) {
        this.viewModel = viewModel;
        this.programListView = programListView;

        // ----------------- Style  ----------
        setText("Détails de l'action");
        lblError.setStyle("-fx-text-fill: red;");
        lblError.setVisible(false);
        txtValue.setVisible(false);
        txtValue.setMaxWidth(40);
        // ---------Layout--------
        HBox row = new HBox(5);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getChildren().addAll(lblDetailTitle, txtValue, lblPixels);
        detailPane.getChildren().addAll(row, lblError);
        setContent(detailPane);


        //-----------Listener -------------------
        viewModel.selectedIndexProperty().addListener((obs, old, nw) -> updateDetailPane());
    }



    private void updateDetailPane() {

        lblError.setVisible(false);
        lblError.setManaged(false);

        Action action = viewModel.getSelectedAction();
        if (action != null && action.getType() == scratch.model.ActionType.VAR_DECLARATION) {
            scratch.model.VarDeclarationAction varAction = (scratch.model.VarDeclarationAction) action;
            lblDetailTitle.setText("Déclaration de la variable ");

            txtValue.setDisable(false);
            txtValue.setVisible(true);
            lblPixels.setVisible(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }

            txtValue.setText(varAction.getVarName());
            configVarTextField(varAction);
            return;
        }

        ActionDetail detail = viewModel.getSelectedActionDetail();

        if (detail == null) {
            lblDetailTitle.setText("(aucune action sélectionnée)");
            txtValue.setText("");
            txtValue.setDisable(true);
            txtValue.setVisible(false);
            lblPixels.setVisible(false);
            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }
            return;
        }

        lblDetailTitle.setText(detail.getTitle());

        if (!detail.isValueEditable()) {
            // PEN_UP / PEN_DOWN : pas de champ à modifier
            txtValue.setText("0");
            txtValue.setDisable(true);
            txtValue.setVisible(false);
            lblPixels.setVisible(false);

            if (currentListener != null) {
                txtValue.textProperty().removeListener(currentListener);
                currentListener = null;
            }
            lblError.setVisible(false);
            lblError.setManaged(false);
            return;
        }

        // Actions paramétrées : champ visible + validation déléguée au VM
        txtValue.setDisable(false);
        txtValue.setVisible(true);
        lblPixels.setVisible(true);
        lblPixels.setText(detail.getUnitText());
        txtValue.setText(String.valueOf(detail.getValue()));

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


        // validation txt
        currentListener = ((obs, old, text) -> {
            lblError.setVisible(false);
            lblError.setManaged(false);


            try {
                //verfie via vue model
                int val = Integer.parseInt(text);
                boolean ok = viewModel.tryUpdateSelectedActionValue(val);

                if (!ok) {
                    lblError.setVisible(true);
                    lblError.setManaged(true);
                } else {
                    programListView.refresh();
                }
            } catch (NumberFormatException e) {
                lblError.setVisible(true);
                lblError.setManaged(true);
            }
        });
        txtValue.textProperty().addListener(currentListener);
    }
}
