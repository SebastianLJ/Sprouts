package Model;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;

import java.util.*;

public class SproutModel {

    private List<Shape> edges;
    private List<Node> nodes;
    private int height = 280;       // TODO: make user-settable
    private int width = 500;        // TODO: make user-settable

    public SproutModel() {
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    public void addRandomNodes(int amount) {

        Random random = new Random();

        for (int i = 0; i < amount; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            nodes.add(new Node(x, y, 0));
        }
    }

    public void resetGame() {
        edges.clear();
        nodes.clear();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Shape> getEdges() {
        return edges;
    }

    public boolean hasNodeWithName(int name) {
        return name < nodes.size();
    }

    public int getNumberOfEdges(int name) {
        return nodes.get(name).getNumberOfConnectingEdges();
    }

    public void drawEdgeBetweenNodes(int startNode, int endNode) {

        if (startNode == endNode) {
            drawCircleFromNodeToItself(startNode);
        } else {
            drawLineBetweenNodes(startNode, endNode);
        }
    }

    public void drawLineBetweenNodes(int startNodeName, int endNodeName) {

        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);
        Shape newLine = new Line();

        ((Line) newLine).setStartX(startNode.getX());
        ((Line) newLine).setStartY(startNode.getY());
        ((Line) newLine).setEndX(endNode.getX());
        ((Line) newLine).setEndY(endNode.getY());

        edges.add(newLine);
        addNodeOnLine((Line) newLine);

        // Update number of connecting edges
        startNode.incNumberOfConnectingEdges(1);
        endNode.incNumberOfConnectingEdges(1);
        nodes.set(startNodeName, startNode);
        nodes.set(endNodeName, endNode);
    }

    public void drawCircleFromNodeToItself(int nodeName) {

        Node node = nodes.get(nodeName);
        Shape newCircle = new Circle();

        double radius = width/100;                                  // TODO: adjust to various window sizes
        double nodeX = node.getX();
        double nodeY = node.getY();

        Double[] center = getCircleCenterCoordinates(nodeX, nodeY, radius);
        ((Circle) newCircle).setCenterX(center[0]);
        ((Circle) newCircle).setCenterY(center[1]);
        ((Circle) newCircle).setRadius(radius);
        newCircle.setStrokeWidth(1.0);

        edges.add(newCircle);
        addNodeOnCircle((Circle) newCircle, nodeX, nodeY);

        // Update number of connecting edges
        node.incNumberOfConnectingEdges(2);
        nodes.set(nodeName, node);
    }

    public void addNodeOnLine(Line edge) {

        double edgeIntervalX = Math.abs(edge.getEndX() - edge.getStartX());
        double edgeIntervalY = Math.abs(edge.getEndY() - edge.getStartY());
        double newNodeX = Math.min(edge.getStartX(), edge.getEndX()) + (edgeIntervalX/2);
        double newNodeY = Math.min(edge.getStartY(), edge.getEndY()) + (edgeIntervalY/2);

        Node newNode = new Node(newNodeX, newNodeY, 2);
        nodes.add(newNode);
    }

    public void addNodeOnCircle(Circle edge, double originNodeX, double originNodeY) {

        double newNodeX = originNodeX + (edge.getCenterX()-originNodeX) * 2;
        double newNodeY = originNodeY + (edge.getCenterY()-originNodeY) * 2;

        Node newNode = new Node(newNodeX, newNodeY, 2);
        nodes.add(newNode);
    }

    public Double[] getCircleCenterCoordinates(double originNodeX, double originNodeY, double radius) {

        Double[] center = {originNodeX, originNodeY};

        if (originNodeX - radius >= 0) {
            center[0] = originNodeX - radius;
        } else if (originNodeX + radius < width) {
            center[0] = originNodeX + radius;
        } else if (originNodeY - radius >= 0) {
            center[1] = originNodeY - radius;
        } else {
            center[1] = originNodeY + radius;
        }

        return  center;
    }
}
