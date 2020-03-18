package sample;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import Model.Point;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import Controller.FileSimulationController;
import Controller.SproutController;

public class Main extends Application {
    final static public double COLLISIONWIDTH = 1.5;
    static SproutController controller = new SproutController();

    HashMap<Point, Boolean> map = new HashMap<>();
    ArrayList<Point> currentLine = new ArrayList<>();
    Point point;
    ArrayList<Shape> lines = new ArrayList<>();
    Path path;
    Path pathtmp;
    boolean collision = false;
    Group root = new Group();


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
                            path.setStrokeWidth(40); //view
                            path.setStroke(Color.BLACK); //view
                            root.getChildren().add(path);
                            path.getElements().add(new MoveTo(point.getX(), point.getY()));
                    });

        primaryStage.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                event -> {
                    if (collision) {
                        scene.setCursor(Cursor.DEFAULT);
                        System.out.println("collision has happend draw somewhere else");
                    } else {

                        pathtmp.getElements().add(new MoveTo(point.getX(), point.getY()));
                        point = new Point((int) event.getX(), (int) event.getY());
                        pathtmp.getElements().add(new LineTo(point.getX(), point.getY()));
                        if (intersects()) {
                            collision = true;
                            path.getElements().clear();
                            System.out.println("collision at " + point.getX() + ", " + point.getY());
                        } else {
                            path.getElements().add(new LineTo(point.getX(), point.getY()));
                            pathtmp.getElements().clear();
                        }
                    }
                });
        primaryStage.addEventHandler(MouseEvent.MOUSE_RELEASED,
                event -> {
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

    public static void acceptUserInput(Scanner scanner) throws NotEnoughInitialNodesException, IllegalNodesChosenException {

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
        if (Shape.intersect(pathtmp, path).getBoundsInLocal().getWidth() > COLLISIONWIDTH*40) {
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