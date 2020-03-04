package Model;

import javafx.scene.shape.Line;

import java.util.*;

public class SproutModel {

    private List<Line> edges;
    private List<Node> nodes;
    private int height = 500;       // TODO: make user-settable
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

    public List<Line> getEdges() {
        return edges;
    }

    public boolean hasNodeWithName(int name) {
        return name < nodes.size();
    }

    public boolean hasMaxNumberOfEdges(int name) {
        return nodes.get(name).hasMaxNumberOfEdges();
    }

    public void drawEdgeBetweenNodes(int startNode, int endNode) {
        Line newLine = new Line();
        newLine.setStartX(nodes.get(startNode).getX());
        newLine.setStartY(nodes.get(startNode).getY());
        newLine.setEndX(nodes.get(endNode).getX());
        newLine.setEndY(nodes.get(endNode).getY());
        edges.add(newLine);
        addNodeOnEdge(newLine);
    }

    public void addNodeOnEdge(Line edge) {
        double edgeIntervalX = Math.abs(edge.getEndX() - edge.getStartX());
        double edgeIntervalY = Math.abs(edge.getEndY() - edge.getStartY());
        double newNodeX = Math.min(edge.getStartX(), edge.getEndX()) + (edgeIntervalX/2);
        double newNodeY = Math.min(edge.getStartY(), edge.getEndY()) + (edgeIntervalY/2);
        Node newNode = new Node(newNodeX, newNodeY, 2);
        nodes.add(newNode);
    }

}
