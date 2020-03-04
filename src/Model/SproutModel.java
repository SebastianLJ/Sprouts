package Model;

import java.util.*;

public class SproutModel {

    private Map<Point, Integer> gameBoard;
    private List<Point> nodes;
    private int numberOfEdges;
    private int height = 500;       // TODO: make user-settable
    private int width = 500;        // TODO: make user-settable
    int noOfBetweenPoints = 11;    // TODO: what should determine this?

    public SproutModel() {
        gameBoard = new HashMap<>();
        nodes = new ArrayList<>();
        numberOfEdges = 0;
    }

    public void addRandomNodes(int amount) {

        Random random = new Random();

        for (int i = 0; i < amount; i++) {

            int x = random.nextInt(width);
            int y = random.nextInt(height);

            Point newPoint = new Point(x, y);
            gameBoard.put(newPoint, 0);
            nodes.add(newPoint);
        }
    }

    public void resetGame() {
        gameBoard.clear();
    }

    public Map<Point, Integer> getGameBoard() {
        return gameBoard;
    }

    public int getNumberOfNodes() {
        return nodes.size();
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    // TODO: this function needs modification
    public List<Point> getPointsBetween(Point p1, Point p2) {

        List<Point> pointsBetween = new ArrayList<>();

        int interval_x = Math.abs(p1.getX() - p2.getX())/noOfBetweenPoints;
        int interval_y = Math.abs(p1.getY() - p2.getY())/noOfBetweenPoints;

        int[] directions = getDirections(p1, p2);

        for (int i = 1; i < noOfBetweenPoints; i++) {
            pointsBetween.add(new Point(p1.getX() + interval_x * i * directions[0], p1.getY() + interval_y * i * directions[1]));
        }

        return pointsBetween;
    }

    public int[] getDirections(Point p1, Point p2) {

        int[] directions = new int[2];

        if (p1.getX() < p2.getX()) {
            directions[0] = 1;
        } else {
            directions[0] = -1;
        }

        if (p1.getY() < p2.getY()) {
            directions[1] = 1;
        } else {
            directions[1] = -1;
        }

        return directions;
    }

    public boolean hasNodeWithName(int name) {
        return name < nodes.size();
    }

    public boolean isValidNode(int name) {
        Point nodeCoordinates = nodes.get(name);
        return gameBoard.get(nodeCoordinates) < 3;
    }

    public void addEdgeBetweenNodes(int startNode, int endNode) {

        Point startNodeCoor = nodes.get(startNode);
        Point endNodeCoor = nodes.get(endNode);

        System.out.println("(" + startNodeCoor.getX() + "," + startNodeCoor.getY() + ")");
        System.out.println("(" + endNodeCoor.getX() + "," + endNodeCoor.getY() + ")");

        List<Point> pointsBetween = getPointsBetween(startNodeCoor, endNodeCoor);

        for (Point betweenPoint : pointsBetween) {
            // add edge point
            gameBoard.put(betweenPoint, -1);
            // change middle edge point to a node
            if (pointsBetween.indexOf(betweenPoint) == noOfBetweenPoints/2) {
                gameBoard.put(betweenPoint, 0);
                nodes.add(betweenPoint);
                System.out.println("(" + betweenPoint.getX() + "," + betweenPoint.getY() + ")");
            }
        } numberOfEdges++;
    }

    public Point nodeCoordinates(int name) {
        return nodes.get(name);
    }

}
