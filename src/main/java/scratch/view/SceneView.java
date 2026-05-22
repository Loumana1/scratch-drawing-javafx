package scratch.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
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
    private final SceneViewModel sceneViewModel;
    private final Label lblTurtle = new Label();
    private final Label lblVariablesTitle = new Label("Variables");
    private final TableView<VariableRow> tableVariables = new TableView<>();
    private final RadioButton rbManual = new RadioButton("Execution manuelle");
    private final RadioButton rbAuto = new RadioButton("Execution automatique");
    private final ToggleGroup modelGroup = new ToggleGroup();
    private final Slider speedSlider = new Slider(0.01, 5.0, 1.0);
    private final Label speedLabel = new Label("1.0 s");
    private final Button btnExecute = new Button("Executer");
    private final Button btnStop = new Button("Arreter");
    private final CheckBox cbShowTeleportation = new CheckBox("afficher la téléportation");


    public SceneView(SceneViewModel sceneViewModel) {
        this.sceneViewModel = sceneViewModel;

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

        rbManual.setToggleGroup(modelGroup);
        rbAuto.setToggleGroup(modelGroup);
        rbManual.setSelected(true);


        speedSlider.setVisible(false);
        speedSlider.setManaged(false);
        speedLabel.setVisible(false);
        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMaxWidth(Double.MAX_VALUE);



        rbAuto.setOnAction(e -> sceneViewModel.onAutoModeSelected());
        rbManual.setOnAction(e -> sceneViewModel.onManualModeSelected());

        btnExecute.visibleProperty().bind(sceneViewModel.autoModeProperty());
        btnExecute.managedProperty().bind(sceneViewModel.autoModeProperty());
        btnStop.visibleProperty().bind(sceneViewModel.autoModeProperty());
        btnStop.managedProperty().bind(sceneViewModel.autoModeProperty());
        speedSlider.visibleProperty().bind(sceneViewModel.autoModeProperty());
        speedSlider.managedProperty().bind(sceneViewModel.autoModeProperty());
        speedLabel.visibleProperty().bind(sceneViewModel.autoModeProperty());
        speedLabel.managedProperty().bind(sceneViewModel.autoModeProperty());

        btnNext.visibleProperty().bind(sceneViewModel.autoModeProperty().not());
        btnNext.managedProperty().bind(sceneViewModel.autoModeProperty().not());

        speedSlider.valueProperty().addListener((obs, old, nw) -> {
            speedLabel.setText(String.format("%.2f s", nw.doubleValue()));
            sceneViewModel.onSpeedChanged(nw.doubleValue());
        });


        btnExecute.setOnAction(e -> sceneViewModel.startAutoExecution());
        btnStop.setOnAction(e -> sceneViewModel.stopAutoExecution());

        TableColumn<VariableRow, String> colName = new TableColumn<>("Nom");
        colName.setCellValueFactory(data -> data.getValue().nameProperty());
        TableColumn<VariableRow, Number> colValue = new TableColumn<>("Valeur");
        colValue.setCellValueFactory(data -> data.getValue().valueProperty());
        tableVariables.getColumns().addAll(colName, colValue);
        tableVariables.setItems(sceneViewModel.getObservableVariables());
        tableVariables.setPlaceholder(new Label("Aucun contenu dans la table"));

        lblTurtle.textProperty().bind(sceneViewModel.turtleStateProperty());
        lblTurtle.setStyle("-fx-font-size: 12px;");
        lblVariablesTitle.setStyle("-fx-font-weight: bold;");
        cbShowTeleportation.selectedProperty().bindBidirectional(sceneViewModel.showTeleportationProperty());
        sceneViewModel.showTeleportationProperty().addListener((obs, old, nw) -> drawGrid());


        HBox modeBox = new HBox(10, rbAuto, rbManual);
        modeBox.setAlignment(Pos.CENTER);

        HBox speedBox = new HBox(speedSlider, speedLabel);
        speedBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(speedSlider, Priority.ALWAYS);


        HBox buttonsBox = new HBox(10, btnReset, btnExecute, btnStop, btnNext);
        buttonsBox.setAlignment(Pos.CENTER);


        configActions();


        getChildren().addAll(
                title,
                canvasBox,
                lblTurtle,
                lblVariablesTitle,
                tableVariables,
                cbShowTeleportation,
                modeBox,
                buttonsBox,
                speedBox);


        configButtonsDisabling();
        sceneViewModel.executionStepProperty().addListener((obs, oldVal, newVal) -> drawGrid());
        sceneViewModel.programLoadedProperty().addListener((obs, old, nw) -> drawGrid());

    }

    private void configActions() {
  btnReset.textProperty().bind(sceneViewModel.resetButtonLabelProperty());
        btnReset.setOnAction(e -> {
            sceneViewModel.reloadOrReset();
            drawGrid();
        });
        btnNext.setOnAction(e -> {
            sceneViewModel.executeNext();
            drawGrid();
        });
    }

    private void configButtonsDisabling() {
        btnReset.disableProperty().bind(
                sceneViewModel.canLoad().not());
        btnNext.disableProperty().bind(
                sceneViewModel.programLoadedProperty().not()
                        .or(sceneViewModel.canExecuteNext().not()));

        btnExecute.disableProperty().bind(sceneViewModel.canExecuteNext().not());
        btnStop.disableProperty().bind(sceneViewModel.canExecuteNext().not());
    }

    private void drawGrid() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        ExecutionContext ctx = sceneViewModel.getExecutionContext();

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

        for (Segment seg : ctx.getSegments()) {
            if (seg.isDashed()) {
                if (!sceneViewModel.showTeleportationProperty().get()) continue;
                gc.setLineDashes(10);
            } else {
                gc.setLineDashes(0);
            }
            gc.setStroke(Color.RED);
            gc.strokeLine(seg.getX1(), seg.getY1(), seg.getX2(), seg.getY2());
        }
        gc.setLineDashes(0);

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