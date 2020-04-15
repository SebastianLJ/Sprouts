package Model;

import javafx.scene.shape.Circle;

public class Node {

    private double x;
    private double y;
    private Circle shape;
    private int numberOfConnectingEdges;

    public Node(double x, double y, int numberOfConnectingEdges) {
        this.x = x;
        this.y = y;
        this.numberOfConnectingEdges = numberOfConnectingEdges;
        this.shape = new Circle(x, y, 5);
        // TODO: Make radius fit window size
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getNumberOfConnectingEdges() {
        return numberOfConnectingEdges;
    }

    public void incNumberOfConnectingEdges(int amountOfNewEdges) {
        numberOfConnectingEdges += amountOfNewEdges;
    }

    public Circle getShape() {
        return shape;
    }
}
