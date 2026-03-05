package scratch.view;

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
import scratch.viewmodel.MainViewModel;

public class SceneView extends VBox {

    private static final double SIZE = 500;
    private static final int GRID = 40;

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


        Button btnReset = new Button("Ré-initialiser");
        Button btnNext = new Button("Suivant");

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(btnReset, btnNext);

        getChildren().addAll(title, canvasBox, buttons);
        btnReset.setOnAction(e -> {
            viewModel.resetExecution();
            drawGrid();           // Redessin immédiat après reset
        });
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

        drawCursor(gc, ctx.getX(), ctx.getY(), ctx.getDirection());
    }

    private void drawCursor(GraphicsContext gc, int x, int y, int direction) {

      //  GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.save();
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
    }
}