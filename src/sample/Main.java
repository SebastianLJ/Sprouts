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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import Controller.SproutController;

public class Main extends Application {

    static SproutController controller = new SproutController();
    HashMap<Point, Boolean> map = new HashMap<>();
    ArrayList<Point> currentLine = new ArrayList<>();
    Point prevPoint;
    Point point;
    ArrayList<Point> pointCluster = new ArrayList<>();
    ArrayList<Point> prevPointCluster = new ArrayList<>();
    ArrayList<Shape> lines = new ArrayList<>();

    Path path;
    boolean collision = false;
    Group root = new Group();


    @Override
    public void start(Stage primaryStage) {

        Scene scene = new Scene(root, 500, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sprouts");




        primaryStage.addEventHandler(MouseEvent.MOUSE_PRESSED,
                new EventHandler<MouseEvent>(){

                    @Override
                    public void handle(MouseEvent event) {
                        collision = false;
                        currentLine = new ArrayList<>();
                        prevPoint = point;
                        point = new Point((int) event.getX(), (int) event.getY());
                        addPointsToCluster();

                        path = new Path();
                        path.setStrokeWidth(1);
                        path.setStroke(Color.BLACK);
                        root.getChildren().add(path);

                        path.getElements().add(new MoveTo(point.getX(), point.getY()));

                        if (intersects()) {
                            path.getElements().clear();
                            collision = true;
                            System.out.println("collision at " + point.getX() + ", " + point.getY());
                        } else {
                            currentLine.addAll(pointCluster);
                        }
                        scene.setCursor(Cursor.NONE);
                    }
                });

        primaryStage.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>(){

                    @Override
                    public void handle(MouseEvent event) {
                        if (!collision) {
                            prevPointCluster = new ArrayList<>(pointCluster);
                            prevPoint = point;
                            point = new Point((int) event.getX(), (int) event.getY());
                            addPointsToCluster();
                            Path tempPath = new Path();
                            tempPath.getElements().add(new MoveTo(prevPoint.getX(), prevPoint.getY()));
                            tempPath.getElements().add(new LineTo(point.getX(), point.getY()));
                            if ((!point.equals(prevPoint)) && path.contains(point.getX(), point.getY())) {
                                path.getElements().clear();
                                collision = true;
                                System.out.println("You collided with yourself!");
                            } else {
                                path.getElements().add(new LineTo(point.getX(), point.getY()));

                                if (intersects()) {
                                    path.getElements().clear();
                                    collision = true;
                                    System.out.println("collision at " + point.getX() + ", " + point.getY());
                                } else {
                                    currentLine.addAll(pointCluster);
                                }
                            }
                        }
                    }
                });

        primaryStage.addEventHandler(MouseEvent.MOUSE_RELEASED,
                new EventHandler<MouseEvent>(){

                    @Override
                    public void handle(MouseEvent event) {
                        if (!collision) {
                            for (Point p : currentLine) {
                                map.put(p, true);
                            }
                            lines.add(path);
                        }
                        scene.setCursor(Cursor.DEFAULT);
                    }
                });


        primaryStage.show();
    }

    public static void main(String[] args) throws NotEnoughInitialNodesException, IllegalNodesChosenException {

//        acceptUserInput(new Scanner(System.in));
        launch(args);
    }

    public static void acceptUserInput(Scanner scanner) throws NotEnoughInitialNodesException, IllegalNodesChosenException {

        Scanner stdin = scanner;

        int noOfInitialNodes = stdin.nextInt();
        controller.initializeGame(noOfInitialNodes);

        while (stdin.hasNextInt()) {

            int startNode = stdin.nextInt();
            int endNode = stdin.nextInt();

            controller.choseNodesForDrawing(startNode, endNode);
        }
    }

    private void initDraw(GraphicsContext gc){
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

    private boolean validPoints() {
        for (Point p : pointCluster) {
            if (!prevPointCluster.contains(p) && (map.containsKey(p) || currentLine.contains(p))) {
                return false;
            }
        }
        return true;
    }

    private boolean intersects() {
        boolean res = false;
        Shape temp;
        for (Shape line : lines) {
            temp = Shape.intersect(path, line);
            boolean intersects = temp.getBoundsInLocal().getWidth() != -1;
            if (intersects) {
                res = true;
            }
        }
        return res;
    }

    private void addPointsToCluster() {
        pointCluster = new ArrayList<>();
        pointCluster.add(point);
        pointCluster.add(new Point(point.getX() + 1, point.getY()));
        pointCluster.add(new Point(point.getX() - 1, point.getY()));
        pointCluster.add(new Point(point.getX(), point.getY() + 1));
        pointCluster.add(new Point(point.getX(), point.getY() - 1));
        pointCluster.add(new Point(point.getX() + 1, point.getY() + 1));
        pointCluster.add(new Point(point.getX() + 1, point.getY() - 1));
        pointCluster.add(new Point(point.getX() - 1, point.getY() + 1));
        pointCluster.add(new Point(point.getX() - 1, point.getY() - 1));
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