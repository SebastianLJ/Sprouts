package sample;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import Model.Point;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import Controller.SproutController;

public class Main extends Application {

    static SproutController controller = new SproutController();
    HashMap<Point, Boolean> map = new HashMap<>();
    ArrayList<Point> currentLine = new ArrayList<>();
    Point point;
    Point point2;
    ArrayList<Shape> lines = new ArrayList<>();
    Path path;
    Path pathtmp;
    boolean collision = false;
    Group root = new Group();
    boolean first=true;


    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sprouts");
        path = new Path();
        pathtmp = new Path();
        pathtmp.setStrokeWidth(1);
        path.setStroke(Color.BLACK);
        path.setStrokeWidth(1);
        path.setStroke(Color.BLACK);
        root.getChildren().add(path);


        primaryStage.addEventHandler(MouseEvent.MOUSE_PRESSED,
                event -> {
                    scene.setCursor(Cursor.CROSSHAIR);
                    collision = false;
                    point = new Point((int) event.getX(), (int) event.getY());
                    path = new Path();
                    path.setStrokeWidth(1);
                    pathtmp.setStrokeWidth(1);
                    path.setStroke(Color.BLACK);
                    root.getChildren().add(path);
                    path.getElements().add(new MoveTo(point.getX(), point.getY()));
                });

        primaryStage.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    if (collision) {
                        scene.setCursor(Cursor.DEFAULT);
                        /* System.out.println("collision has happend draw somewhere else");*/
                    } else {
                        point2 = new Point(point.getX(), point.getY());
                        pathtmp.getElements().add(new MoveTo(point.getX(), point.getY()));
                        point = new Point((int) event.getX(), (int) event.getY());
                        pathtmp.getElements().add(new LineTo(point.getX(), point.getY()));
                        Shape test = Shape.intersect(path, pathtmp);
                        System.out.println("width: " + test.getBoundsInLocal().getWidth());
                        if (test.getBoundsInLocal().getWidth() > 0.5) {
                           collision = true;
                            path.getElements().clear();
                            System.out.println("collision at " + point.getX() + ", " + point.getY());
                        } else {
                            pathtmp.getElements().clear();
                            if(!first){
                            path.getElements().add(new LineTo(point2.getX(), point2.getY()));
                            }
                        }
                        first=false;
                    }
                });
        primaryStage.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event ->

                {
                    if (!collision) {
                        for (Point p : currentLine) {
                            map.put(p, true);
                        }
                        lines.add(path);
                    }
                    scene.setCursor(Cursor.DEFAULT);
                });

        primaryStage.show();
    }

    public static void main(String[] args) throws NotEnoughInitialNodesException, IllegalNodesChosenException {

/*
        acceptUserInput(new Scanner(System.in));  // uncomment for console driven game
*/
        launch(args);                             // uncomment for javaFX driven game
    }

    public static void acceptUserInput(Scanner scanner) throws
            NotEnoughInitialNodesException, IllegalNodesChosenException {

        Scanner stdin = scanner;

        int noOfInitialNodes = stdin.nextInt();
        controller.attemptInitializeGame(noOfInitialNodes);

        while (stdin.hasNextInt()) {

            int startNode = stdin.nextInt() - 1;
            int endNode = stdin.nextInt() - 1;

            controller.attemptDrawEdgeBetweenNodes(startNode, endNode);
        }
    }

    private void initDraw(GraphicsContext gc) {
        double canvasWidth = gc.getCanvas().getWidth();
        double canvasHeight = gc.getCanvas().getHeight();

        gc.setFill(Color.LIGHTGRAY);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(5);

        gc.fill();
        gc.strokeRect(
                0,              //x of the upper left corner
                0,              //y of the upper left corner
                canvasWidth,    //width of the rectangle
                canvasHeight);  //height of the rectangle

        gc.setFill(Color.RED);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(3);

    }

    private boolean intersects() {
        boolean res = false;
        Shape temp;
        if (Shape.intersect(pathtmp, path).getBoundsInLocal().getWidth() > 1.5) {
            return true;
        }
        for (Shape line : lines) {
            temp = Shape.intersect(path, line);
            boolean intersects = temp.getBoundsInLocal().getWidth() != -1;
            if (intersects) {
                res = true;
            }
        }
        return res;
    }


    // -----------------------------------------//
    // for testing:

    public void resetSproutController() {
        controller = new SproutController();
    }

    public SproutController getController() {
        return controller;
    }

}