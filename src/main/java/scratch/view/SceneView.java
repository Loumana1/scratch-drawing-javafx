package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import scratch.model.ExecutionContext;
import scratch.model.Segment;
import scratch.viewmodel.*;

public class SceneView extends VBox {

    private static final double SIZE = 500;
    private static final int GRID = 50;
    private final Button btnNext = new Button("Suivant");
    private final Button btnReset = new Button("Charger");
    private final Canvas canvas;
    private final MainViewModel viewModel;
    private final Label lblTurtle = new Label();
    private final Label lblVariablesTitle = new Label("Variables");
    private final TableView<VariableRow> tableVariables = new TableView<>();
    private final Label lblError = new Label();
    private final RadioButton rbManual = new RadioButton("Execution manuelle");
    private final RadioButton rbAuto = new RadioButton("Execution automatique");
    private final ToggleGroup modelGroup = new ToggleGroup();
    private final Slider speedSlider = new Slider(0.01 , 5.0 , 1.0);
    private final Label speedLabel = new Label("1.0 s");
    private final Button btnExecute = new Button("Executer");
    private final Button btnStop = new Button("Arreter");

    public SceneView(MainViewModel viewModel) {
        this.viewModel = viewModel;
        setSpacing(8);
        setPadding(new Insets(10));
        setAlignment(Pos.TOP_LEFT);

        Label title = new Label("Scène");

        canvas = new Canvas(SIZE, SIZE);

        StackPane canvasBox = new StackPane(canvas);
        canvasBox.setPrefSize(SIZE, SIZE);
        canvasBox.setMaxSize(SIZE, SIZE);
        canvasBox.setMinSize(SIZE, SIZE);
        canvasBox.setStyle("-fx-border-color: black; -fx-border-width: 1;");

        drawGrid();




        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(btnReset, btnNext);
        rbManual.setToggleGroup(modelGroup);
        rbAuto.setToggleGroup(modelGroup);
        rbManual.setSelected(true);
        btnExecute.setVisible(false);
        btnExecute.setManaged(false);
        btnStop.setVisible(false);
        btnStop.setManaged(false);
        speedSlider.setVisible(false);
        speedSlider.setManaged(false);
        speedLabel.setVisible(false);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(speedSlider, javafx.scene.layout.Priority.ALWAYS);


        modelGroup.selectedToggleProperty().addListener((obs, old , nw) ->{
            boolean isAuto = (nw == rbAuto);
            viewModel.autoModeProperty().set(isAuto);

            // Slider vitesse
            speedSlider.setVisible(isAuto);
            speedSlider.setManaged(isAuto);
            speedLabel.setVisible(isAuto);
            speedLabel.setManaged(isAuto);

            // Boutons auto (Exécuter / Arrêter)
            btnExecute.setVisible(isAuto);
            btnExecute.setManaged(isAuto);
            btnStop.setVisible(isAuto);
            btnStop.setManaged(isAuto);

            // Bouton manuel (Suivant)
            btnNext.setVisible(!isAuto);
            btnNext.setManaged(!isAuto);

            if (!isAuto) viewModel.stopAutoExecution();
        });


        speedSlider.valueProperty().addListener((obs , old , nw) ->{
            viewModel.speedProperty().set(nw.doubleValue());
            speedLabel.setText(String.format("%.2f s", nw.doubleValue()));
            if (viewModel.isAutoMode() && viewModel.programLoadedProperty().get()){
                viewModel.startAutoExecution();
            }
        });
        btnExecute.setOnAction(e -> viewModel.startAutoExecution());
        btnStop.setOnAction(e-> viewModel.stopAutoExecution());



        //Zone info
        TableColumn<VariableRow, String> colName = new TableColumn<>("Nom");
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        TableColumn<VariableRow, Number> colValue = new TableColumn<>("Valeur");
        colValue.setCellValueFactory(data -> data.getValue().valueProperty());
        tableVariables.getColumns().addAll(colName, colValue);
        tableVariables.setItems(viewModel.getObservableVariables());
        tableVariables.setPlaceholder(new Label("Aucun contenu dans la table"));

        lblTurtle.textProperty().bind(viewModel.turtleStateProperty());
        lblTurtle.setStyle("-fx-font-size: 12px;");
        lblVariablesTitle.setStyle("-fx-font-weight: bold;");

        HBox modeBox = new HBox(10, rbAuto, rbManual);
        modeBox.setAlignment(Pos.CENTER);

        HBox speedBox = new HBox(speedSlider);
        speedBox.setMaxWidth(Double.MAX_VALUE);


        HBox buttonsBox = new HBox(10, btnReset, btnExecute, btnStop, btnNext);
        buttonsBox.setAlignment(Pos.CENTER);



        getChildren().addAll(
                title,
                canvasBox,
                lblTurtle,
                lblVariablesTitle,
                tableVariables,
                modeBox,
                buttonsBox,
                speedBox);


        //config
        configActions();
        configButtonsDisabling();
        viewModel.executionStepProperty().addListener((obs, oldVal, newVal) -> drawGrid());

    }

//Connection excution action
    private void configActions() {
        btnReset.setOnAction(e -> {
            if (viewModel.programLoadedProperty().get()) {
                viewModel.resetExecution();
                viewModel.loadOnScene();
            } else {
               viewModel.loadOnScene();
            }
            drawGrid();


        });
        btnNext.setOnAction(e -> {
            viewModel.executeNext();
            drawGrid();
        });
    }

//connextion Desacitver bouttons
    private void configButtonsDisabling() {
        btnReset.disableProperty().bind(
                viewModel.canLoad().not());
        btnNext.disableProperty().bind(
                viewModel.programLoadedProperty().not()
                        .or(viewModel.canExecuteNext().not()));
        viewModel.programLoadedProperty().addListener((obs, old, nw) ->
                btnReset.setText(nw ? "Ré-initialiser" : "Charger"));
        btnExecute.disableProperty().bind(viewModel.canExecuteNext().not());
        btnStop.disableProperty().bind(viewModel.canExecuteNext().not());
    }



    private void drawGrid() {

        GraphicsContext gc = canvas.getGraphicsContext2D();
        ExecutionContext ctx = viewModel.getExecutionContext();

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, SIZE, SIZE);

        gc.setStroke(Color.web("#E6E6E6"));
        gc.setLineWidth(1);

        for (int x = 0; x <= SIZE; x += GRID) {
            gc.strokeLine(x + 0.5, 0, x + 0.5, SIZE);
        }

        for (int y = 0; y <= SIZE; y += GRID) {
            gc.strokeLine(0, y + 0.5, SIZE, y + 0.5);
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, SIZE, SIZE);

        //dessin du segment
        for (Segment seg : ctx.getSegments()) {
            gc.setStroke(Color.RED);
            gc.strokeLine(seg.getX1(), seg.getY1(), seg.getX2(), seg.getY2());

        }
        drawCursor(gc, ctx.getX(), ctx.getY(), ctx.getDirection());
    }

    private void drawCursor(GraphicsContext gc, int x, int y, int direction) {


        gc.save();
        gc.translate(x, y);
        gc.rotate(direction);

        double s = 10;
        gc.setFill(Color.CYAN);
        gc.fillPolygon(
                new double[]{0, -s * 0.8, s * 0.8},
                new double[]{-s, s * 0.8, s * 0.8}, 3);
        gc.setFill(Color.BLACK);
        gc.fillOval(-2, -s - 2, 4, 4);

        gc.restore();


    }
}