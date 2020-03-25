package View;

import Model.Node;
import Model.SproutModel;
import javafx.animation.FadeTransition;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.util.Duration;


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
        Shape newEdge = model.getNewestEdge();
        Circle newNode = model.getNewestNode();
        gamePane.getChildren().add(0, newEdge);
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
}
