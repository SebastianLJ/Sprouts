package Model;

import javafx.scene.shape.Shape;

import java.util.*;

public class SproutModel {

    private List<Shape> edges;
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

    public int getNumberOfNodes() {
        return nodes.size();
    }

    public int getNumberOfEdges() {
        return edges.size();
    }

    public boolean hasNodeWithName(int name) {
        return name < nodes.size();
    }

    public boolean hasMaxNumberOfEdges(int name) {
        return nodes.get(name).hasMaxNumberOfEdges();
    }

    public void addEdgeBetweenNodes(int startNode, int endNode) {
        // TODO;
    }

}
