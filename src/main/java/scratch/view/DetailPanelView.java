package scratch.view;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javafx.geometry.Insets;
import scratch.model.Action;
import scratch.model.ParameterizedAction;
import scratch.model.TurnLeftAction;
import scratch.model.TurnRightAction;
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
        Action action = viewModel.getSelectedAction();
        lblError.setVisible(false);  // reset pour chaque changement

        //Default : pas d'action
        if (action == null) {
            lblDetailTitle.setText("(aucune action sélectionnée)");
            txtValue.setText("");
            txtValue.setDisable(true);
            lblError.setManaged(false);
            return;
        }

        // cas
        switch (action.getType()) {

            case TURN_LEFT ->{
                configTextField("Tourner à gauche de ",
                        (ParameterizedAction) action, 1, 180);
            txtValue.setDisable(false);
            txtValue.setVisible(true);
            lblPixels.setVisible(true);
            lblPixels.setText(" Degres");
            lblError.setManaged(false);
            }

            case TURN_RIGHT -> {
                configTextField("Tourner à droite de ",
                    (ParameterizedAction) action, 1, 180);
            txtValue.setDisable(false);
            txtValue.setVisible(true);
            lblPixels.setVisible(true);
            lblPixels.setText(" Degres");
            lblError.setManaged(false);
            }

            case PEN_UP -> {
                lblDetailTitle.setText("Lever le stylo ");
                txtValue.setText("0");
                txtValue.setDisable(true);
                lblPixels.setVisible(false);
                lblError.setVisible(false);
                lblError.setManaged(false);
            }

            case PEN_DOWN -> {
                lblDetailTitle.setText("Abaisser le stylo ");
                txtValue.setText("0");
                txtValue.setDisable(true);
                lblPixels.setVisible(false);
                lblError.setVisible(false);
                lblError.setManaged(false);
            }

            case MOVE_FORWARD -> {
                configTextField("Avance de ",
                        (ParameterizedAction) action, 1, 100);
                txtValue.setDisable(false);
                txtValue.setVisible(true);
                lblPixels.setVisible(true);
                lblPixels.setText(" Pixels");
                lblError.setManaged(false);
            }

        }
    }
    private void configTextField(String title, ParameterizedAction action, int min, int max) {
        lblDetailTitle.setText(title);

        if (currentListener != null){
            txtValue.textProperty().removeListener(currentListener);
        }

        txtValue.setText(String.valueOf(action.getValue()));
        txtValue.setDisable(false);

        // validation txt
        currentListener = ((obs, old, text) -> {
            try {
                int val = Integer.parseInt(text);

                if (val < min || val > max) { // pas dans les born
                    lblError.setVisible(true);
                    lblError.setManaged(true);
                } else {
                    lblError.setVisible(false);
                    lblError.setManaged(false);
                    action.setValue(val);
                    programListView.refresh();
                }
            } catch (NumberFormatException e) { // pans un chiffre
                lblError.setVisible(true);
                lblError.setManaged(true);
            }
        });
        txtValue.textProperty().addListener(currentListener);
    }
}
