package View;

import Model.Node;
import Model.SproutModel;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Stack;


public class View {
    private SproutModel model;


    public View(SproutModel model) {
        this.model = model;
    }
    /**
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     *
     *
     *
     */
    public void initializeNodes(Pane gamePane) {
        for (Node node : model.getNodes()) {
            gamePane.getChildren().add(addNumberOnNode(node));
        }
    }
    /**
     * @author Noah Bastian Christiansen
     *
     *
     *
     */
    public StackPane updateCanvasClick(Pane gamePane) {
        // Get edge
        Shape newEdge = model.getNewestEdge();

        // Get node
        Node newNode = model.getNewestNode();

        // Animate edge
        legalEdgeAnimation(gamePane, newEdge);

        // Add new node to view
        StackPane newStackPane = addNumberOnNode(newNode);
        gamePane.getChildren().add(newStackPane);
        return newStackPane;
    }
    /**
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     *
     *
     *
     */
    public void updateCanvasDrag(Pane gamePane){
        Node newNode = model.getNewestNode();
        gamePane.getChildren().add(addNumberOnNode(newNode));
    }

    /**
     * @author Noah Bastian Christiansen
     *
     *
     *
     */
    public void setUpDrawingSettings(MouseEvent mousePressed, Pane gamePane) {
        Scene scene = ((javafx.scene.Node) mousePressed.getSource()).getScene();
        scene.setCursor(Cursor.CROSSHAIR);
        gamePane.getChildren().add(model.getPath());
    }

    /**
     * @author Noah Bastian Christiansen
     *
     *
     *
     */
    public void setUpCollisionSettings(MouseEvent mouseDragged) {
        Scene scene = ((javafx.scene.Node) mouseDragged.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }
    /**
     * @author Noah Bastian Christiansen
     *
     *
     *
     */
    public void setUpSuccessfulPathSettings(MouseEvent mouseReleased) {
        Scene scene = ((javafx.scene.Node) mouseReleased.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }

    public void deselectNode(Circle primedNode) {
        primedNode.setStrokeWidth(0);
        primedNode.setStroke(Color.BLACK);
    }

    public void selectNode(Circle primedNode) {
        primedNode.setStrokeWidth(2.0);
        primedNode.setStroke(Color.GREEN);
    }

    public void illegalEdgeAnimation(Pane gamePane, Shape edge) {
        edge.setStroke(Color.RED);
        gamePane.getChildren().add(0, edge);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), edge);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.4);
        fadeTransition.setCycleCount(2);
        fadeTransition.setOnFinished(e -> gamePane.getChildren().remove(edge));
        fadeTransition.play();
    }

    public void legalEdgeAnimation(Pane gamePane, Shape shape) {
        if (shape instanceof Line) {
            double x = ((Line) shape).getStartX();
            double y = ((Line) shape).getStartY();
            Line lineAnimation = new Line(x, y, x, y);
            gamePane.getChildren().add(0, lineAnimation);
            Timeline timeline = new Timeline();
            KeyValue kvX = new KeyValue(lineAnimation.endXProperty(), ((Line) shape).getEndX());
            KeyValue kvY = new KeyValue(lineAnimation.endYProperty(), ((Line) shape).getEndY());
            KeyFrame kf = new KeyFrame(Duration.millis(500), kvX, kvY);
            timeline.getKeyFrames().add(kf);
            timeline.play();
        } else {
            gamePane.getChildren().add(shape);
        }
    }

    public void resetGameView(Pane gamePane) {
        gamePane.getChildren().clear();
    }

    public void resetCells(ArrayList<ListCell<String>> cells) {
        int i = 0;
        for (ListCell<String> cell : cells) {
            cells.get(i).setTooltip(null);
            cell.setStyle(i++ % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: GHOSTWHITE;");
        }
    }

    public void setColorForCell(String s, ListCell<String> cell) {
        cell.setStyle(s);
    }

    public void prepareTooltip(String message, ListCell<String> cell) {
        Tooltip toolTip = new Tooltip();
        setUpTooltipPreferences(toolTip);
        toolTip.setText(message);
        cell.setTooltip(toolTip);
    }

    private void setUpTooltipPreferences(Tooltip toolTip) {
        toolTip.setShowDelay(Duration.ZERO);
        toolTip.setShowDuration(Duration.INDEFINITE);
        toolTip.setHideDelay(Duration.ZERO);
    }
    /**
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     *
     *
     *
     */
    private StackPane addNumberOnNode(Node node){
        final Text text = new Text(""+node.getId());
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: 900" + ";-fx-font-size:" + (1.4*node.getNodeRadius()));
        final StackPane stack = new StackPane();
        stack.getChildren().addAll(node.getShape(), text);
        stack.relocate(node.getX()-node.getNodeRadius(),node.getY()-node.getNodeRadius());
        return stack;
    }

    public void setGameResponseLabelText(Label gameResponseLabel, String s) {
        gameResponseLabel.setText(s);
    }
}
