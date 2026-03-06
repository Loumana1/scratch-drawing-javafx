package scratch.view;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import scratch.model.ExecutionContext;
import scratch.model.Segment;
import scratch.viewmodel.MainViewModel;

public class SceneView extends VBox {

    private static final double SIZE = 500;
    private static final int GRID = 40;
    private final Button btnNext = new Button("Suivant");
    private final Button btnReset = new Button("Charger");
    private final Canvas canvas;
    private final MainViewModel viewModel;

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

        getChildren().addAll(title, canvasBox, buttons);


        //config
        configActions();
        configButtonsDisabling();

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
                Bindings.isEmpty(viewModel.getObservableActions()));


        viewModel.programLoadedProperty().addListener((obs, old, nw) ->
                btnReset.setText(nw ? "Ré-initialiser" : "Charger"));

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
        gc.setLineWidth(1);
        gc.strokeRect(0, 0, SIZE, SIZE);

        //dessin du segment
        for (Segment seg : ctx.getSegments()) {
            gc.strokeLine(seg.getX1(), seg.getY1(), seg.getX2(), seg.getY2());
        }
        drawCursor(gc, ctx.getX(), ctx.getY(), ctx.getDirection());
    }

    private void drawCursor(GraphicsContext gc, int x, int y, int direction) {
/*
      //  GraphicsContext gc = canvas.getGraphicsContext2D();


        double cx = SIZE / 2.0;
        double cy = SIZE / 2.0;

        double topX = cx;
        double topY = cy - 10;

        double leftX = cx - 8;
        double leftY = cy + 8;

        double rightX = cx + 8;
        double rightY = cy + 8;

        gc.setFill(Color.CYAN);

        gc.fillPolygon(
                new double[]{topX, leftX, rightX},
                new double[]{topY, leftY, rightY},
                3
        );



        gc.setFill(Color.BLACK);
        gc.fillOval(topX - 2, topY - 2, 4, 4);
        gc.restore();


  */
        //fonctionne bien car segment aligné avec mvmt curseur

        gc.save();
        gc.translate(x, y);       //  utilise x, y
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