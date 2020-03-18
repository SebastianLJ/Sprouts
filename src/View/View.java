package View;

import Model.Node;
import Model.SproutModel;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.input.MouseEvent;


public class View {
    private SproutModel model;


    public View(SproutModel model) {
        this.model = model;
    }

    public void updateCanvas(Pane gamePane) {
        for (Node node: model.getNodes()) {
            Circle circle = new Circle(node.getX(), node.getY(), 5);
            gamePane.getChildren().add(circle);
        }
    }
    public void setUpDrawingSettings(MouseEvent mousePressed, Pane gamePane){
        Scene scene =  ((javafx.scene.Node) mousePressed.getSource()).getScene();
        scene.setCursor(Cursor.CROSSHAIR);
        model.getPath().setStrokeWidth(1);
        model.getPath().setStroke(Color.BLACK); //view
        model.getPathTmp().setStrokeWidth(1);
        gamePane.getChildren().add(model.getPath());
    }

    public void setUpCollisionSettings (MouseEvent mouseDragged){
        Scene scene =  ((javafx.scene.Node) mouseDragged.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }

    public void setUpSuccessfulPathSettings(MouseEvent mouseReleased){
        Scene scene =  ((javafx.scene.Node) mouseReleased.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }
}
