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

    public boolean equals(Node node) {
        return (x == node.getX() && y == node.getY());
    }

    public boolean atLeastTwoSpacesApart(Node node) {
        System.out.println(Math.abs(x - node.getX()));
        System.out.println(Math.abs(y - node.getY()));
        return (Math.abs(x - node.getX()) > 1.0 || Math.abs(y - node.getY()) > 1.0);
    }

    public double getDistanceToNode(Node node) {
        return Math.sqrt(Math.pow(node.getX()-x, 2) + Math.pow(node.getY()-y, 2));
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
