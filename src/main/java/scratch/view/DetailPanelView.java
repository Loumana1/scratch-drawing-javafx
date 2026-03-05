package scratch.view;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import javafx.geometry.Insets;
import scratch.model.Action;
import scratch.model.ParameterizedAction;
import scratch.viewmodel.MainViewModel;

public class DetailPanelView extends VBox {

    private final MainViewModel viewModel;
    private final ListView<Action> programListView;  // Référence pour refresh()
    // juste mis-a-j
    private final Label lblDetailTitle = new Label("(aucune action sélectionnée)");
    private final TextField txtValue = new TextField();
    private final Label lblError = new Label("Error valeur");





    public DetailPanelView(MainViewModel viewModel, ListView<Action> programListView) {
        this.viewModel = viewModel;
        this.programListView = programListView;

        // ----------------- Style  ----------
        lblError.setStyle("-fx-text-fill: red;");
        lblError.setVisible(false);
        txtValue.setDisable(true);
        txtValue.setMaxWidth(150);
        // ---------Layout--------
        getChildren().addAll(lblDetailTitle, txtValue, lblError);

        //-----------Listener -------------------
        viewModel.selectedIndexProperty().addListener((obs, old, nw) -> updateDetailPane());
    }



    private void updateDetailPane() {
        Action action = viewModel.getSelectedAction();
        lblError.setVisible(false);  // reset pour chaque changement

        //Default : pas d'action
        if (action == null) {
            lblDetailTitle.setText("(aucune action sélectionnée)");
            txtValue.setText("");
            txtValue.setDisable(true);
            return;
        }

        // cas
        switch (action.getType()) {

            case TURN_LEFT ->{
                configTextField("Tourner à gauche de",
                        (ParameterizedAction) action, 1, 180);
            txtValue.setText("90");
            txtValue.setDisable(false);
            }

            case TURN_RIGHT -> {
                configTextField("Tourner à droite de",
                    (ParameterizedAction) action, 1, 180);
            txtValue.setText("90");
            txtValue.setDisable(false);
            }



            case PEN_UP -> {
                lblDetailTitle.setText("Lever le stylo");
                txtValue.setText("0");
                txtValue.setDisable(true);
            }
            case PEN_DOWN -> {
                lblDetailTitle.setText("Abaisser le stylo");
                txtValue.setText("0");
                txtValue.setDisable(true);
            }
        }
    }
    private void configTextField(String title, ParameterizedAction action, int min, int max) {
        lblDetailTitle.setText(title);
        txtValue.setText(String.valueOf(action.getValue()));
        txtValue.setDisable(false);

        // validation txt
        txtValue.textProperty().addListener((obs, old, text) -> {
            try {
                int val = Integer.parseInt(text);

                if (val < min || val > max) { // pas dans les born
                    lblError.setVisible(true);
                } else {
                    lblError.setVisible(false);
                    action.setValue(val);
                    programListView.refresh();
                }
            } catch (NumberFormatException e) { // pans un chiffre
                lblError.setVisible(true);
            }
        });
    }

}
