package Model;

public class Node {

    private int x;
    private int y;
    private int numberOfConnectingEdges;

    public Node(int x, int y, int numberOfConnectingEdges) {
        this.x = x;
        this.y = y;
        this.numberOfConnectingEdges = numberOfConnectingEdges;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getNumberOfConnectingEdges() {
        return numberOfConnectingEdges;
    }

    public boolean hasMaxNumberOfEdges() {
        return numberOfConnectingEdges == 3;
    }
}
