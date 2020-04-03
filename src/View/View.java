package View;

import Model.Node;
import Model.SproutModel;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.security.Key;


public class View {
    private SproutModel model;


    public View(SproutModel model) {
        this.model = model;
    }

    public void initializeNodes(Pane gamePane) {
        for (Node node : model.getNodes()) {
            gamePane.getChildren().add(node.getShape());
        }
    }

    public Circle updateCanvas(Pane gamePane) {
        // Get edge
        Shape newEdge = model.getNewestEdge();

        // Get node
        Circle newNode = model.getNewestNode();

        // Animate edge
        legalEdgeAnimation(gamePane, newEdge);

        // Add new node to view
        gamePane.getChildren().add(newNode);
        return newNode;
    }


    public void setUpDrawingSettings(MouseEvent mousePressed, Pane gamePane) {
        Scene scene = ((javafx.scene.Node) mousePressed.getSource()).getScene();
        scene.setCursor(Cursor.CROSSHAIR);
        model.getPath().setStrokeWidth(1);
        model.getPath().setStroke(Color.BLACK); //view
        model.getPathTmp().setStrokeWidth(1);
        gamePane.getChildren().add(model.getPath());
    }

    public void setUpCollisionSettings(MouseEvent mouseDragged) {
        Scene scene = ((javafx.scene.Node) mouseDragged.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }

    public void setUpSuccessfulPathSettings(MouseEvent mouseReleased) {
        Scene scene = ((javafx.scene.Node) mouseReleased.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }

    public void unPrimeNode(Circle primedNode) {
        primedNode.setStrokeWidth(0);
        primedNode.setStroke(Color.BLACK);
    }

    public void primeNode(Circle primedNode) {
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
            Line lineAnimation = new Line(x,y,x,y);
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
}
