package Model;

import javafx.scene.shape.Circle;

public class Node {

    public static int radius = 10;
    private double x;
    private double y;
    private int id;
    private Circle shape;
    private int numberOfConnectingEdges;

    /**
     * Constructor.
     *
     * @param x : Node center x-coordinate
     * @param y : Node center y-coordinate
     * @param numberOfConnectingEdges : Number of edges connected to this node at the time of initialization
     * @param id : The node ID corresponding to the current number of nodes in the game + 1
     */
    public Node(double x, double y, int numberOfConnectingEdges, int id) {
        this.x = x;
        this.y = y;
        this.numberOfConnectingEdges = numberOfConnectingEdges;
        this.shape = new Circle(x, y, radius);
        this.id=id;
    }

    /**
     * @return Node center x-coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @return Node center y-coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * @return number of edges connected to this node
     * @author Thea Birk Berger
     */
    public int getNumberOfConnectingEdges() {
        return numberOfConnectingEdges;
    }

    /**
     * @param amountOfNewEdges : Number of new edges to register for this node
     * @author Thea Birk Berger
     */
    public void incNumberOfConnectingEdges(int amountOfNewEdges) {
        numberOfConnectingEdges += amountOfNewEdges;
    }

    /**
     * @param amountOfNewEdges : Number of connecting edges to decrement for this node
     * @author Thea Birk Berger
     */
    public void decNumberOfConnectingEdges(int amountOfNewEdges) {
        numberOfConnectingEdges -= amountOfNewEdges;
    }

    /**
     * @return node radius
     * @author Thea Birk Berger
     */
    public int getNodeRadius() {
        return radius;
    }

    /**
     * @return node shape
     * @author Thea Birk Berger
     */
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

    /**
     * @return node ID/name
     * @author Thea Birk Berger
     */
    public int getId() {
        return id;
    }
}
