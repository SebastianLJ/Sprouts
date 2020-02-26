package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;


public class Main extends Application {

    HashMap<Point, Boolean> map = new HashMap<>();
    ArrayList<Point> currentLine = new ArrayList<>();
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
                        point = new Point((int) event.getX(), (int) event.getY());
                        addPointsToCluster();

                        path = new Path();
                        path.setStrokeWidth(3);
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
                    }
                });

        primaryStage.addEventHandler(MouseEvent.MOUSE_DRAGGED,
                new EventHandler<MouseEvent>(){

                    @Override
                    public void handle(MouseEvent event) {
                        if (!collision) {
                            prevPointCluster = new ArrayList<>(pointCluster);
                            point = new Point((int) event.getX(), (int) event.getY());
                            addPointsToCluster();

                            System.out.println("before: " + path.intersects(point.getX(), point.getY(), 3, 3));

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
                    }
                });


        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
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

}