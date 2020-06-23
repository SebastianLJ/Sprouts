package Model;

import javafx.scene.shape.Circle;

public class Node {

    public static int radius = 10;
    private double x;
    private double y;
    private int id;
    private Circle shape;
    private int numberOfConnectingEdges;

    public Node(double x, double y, int numberOfConnectingEdges, int id) {
        this.x = x;
        this.y = y;
        this.numberOfConnectingEdges = numberOfConnectingEdges;
        this.shape = new Circle(x, y, radius);
        this.id=id;
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

    public void decNumberOfConnectingEdges(int amountOfNewEdges) {
        numberOfConnectingEdges -= amountOfNewEdges;
    }

    public int getNodeRadius() {
        return radius;
    }

    public Circle getShape() {
        return shape;
    }

    /**
     * @author Sebastian Lund Jensen
     * @param point
     * @return true if the point is inside the node
     */
    public boolean isPointInsideNode(Point point) {
        double distance = Math.sqrt(Math.pow(point.getX() - x,2) + Math.pow(point.getY() - y, 2));
        return distance <= radius;
    }

    public int getId() {
        return id;
    }
}
