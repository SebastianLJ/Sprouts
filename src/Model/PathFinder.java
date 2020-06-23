package Model;


import Utility.Exceptions.NoValidEdgeException;
import javafx.scene.shape.*;

import java.util.*;

public class PathFinder {
    private SproutModel model;
    private int gridSizeX = 348;
    private int gridSizeY = 236;
    private boolean[][] grid = new boolean[gridSizeY][gridSizeX];

    private double scalingFactorX;
    private double scalingFactorY;

    private List<Node> failedNodes;
    private List<Node> permanentlyFailedNodes;

    public PathFinder(SproutModel model) {
        this.model = model;
        this.failedNodes = new ArrayList<>();
        permanentlyFailedNodes = new ArrayList<>();
    }

    /**
     * Grid is initialized using the already drawn nodes and edges. This method will use a downScale, which is
     * decrementing after each unsuccessful search for a valid path, to increase precision and number of
     * possible edges.
     *
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public void initGrid(Node startNode, Node endNode) {
        this.scalingFactorX = model.getWidth() / this.gridSizeX;
        this.scalingFactorY = model.getHeight() / this.gridSizeY;
        grid = new boolean[this.gridSizeY][this.gridSizeX];
        List<Shape> edges = model.getEdges();
        List<Node> nodes = model.getNodes();

        initFailedNodes();
        initNodes(nodes);

        removeStartAndEndNodeCoverage(startNode.getX(), startNode.getY(), startNode);
        removeStartAndEndNodeCoverage(endNode.getX(), endNode.getY(), endNode);

        initEdges(edges);

        clearCenterOfNode(startNode);
        clearCenterOfNode(endNode);
    }

    /**
     * Clears the center of a node. This is used to make sure the center of the start node and end node is
     * vacant to be visited by the BFS algorithm
     * @param node start node and end node
     */
    private void clearCenterOfNode(Node node) {
        int x = downScaleX(node.getX());
        int y = downScaleY(node.getY());

        if (0 < x && x < gridSizeX && 0 < y && y < gridSizeY) grid[y][x] = false;
    }

    /**
     * Takes every node in the sproutModel and marks their location in the grid
     * @param nodes every node in the game
     */
    private void initNodes(List<Node> nodes) {
        for (Node node : nodes) {
            findNodeCoverage(node.getX(), node.getY(), node);
        }
    }

    /**
     * When we create a new line we want to ensure there is room for a point on it that does not visually collide with other
     * game objects. This takes the points that has been put on the new line but failed and marks them in the grid.
     * This forces the BFS to traverse around these in order to find a new path with room for a node.
     */
    private void initFailedNodes() {
        for (Node failedNode : failedNodes) {
            findNodeCoverageOfFailedNode(failedNode.getX(), failedNode.getY(), failedNode);
        }

        for (Node permanentlyFailedNode : permanentlyFailedNodes) {
            findNodeCoverageOfFailedNode(permanentlyFailedNode.getX(), permanentlyFailedNode.getY(), permanentlyFailedNode);
        }
    }

    /**
     * Recursively finds the area in the grid the point covers and marks it *true* in the grid
     * @param x center x of point
     * @param y center y of point
     * @param failedNode the node to find coverage of
     */
    private void findNodeCoverageOfFailedNode(double x, double y, Node failedNode) {
        int nX = downScaleX(x);
        int nY = downScaleY(y);

        Point p = new Point((int) x, (int) y);

        if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY && failedNode.isPointInsideNode(p) && !grid[nY][nX]) {
            grid[nY][nX] = true;
            findNodeCoverageOfFailedNode(x + scalingFactorX, y, failedNode);
            findNodeCoverageOfFailedNode(x - scalingFactorX, y, failedNode);
            findNodeCoverageOfFailedNode(x, y + scalingFactorY, failedNode);
            findNodeCoverageOfFailedNode(x, y - scalingFactorY, failedNode);
        } else if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY) grid[nY][nX] = true;
    }

    /**
     * Recursively finds the area in the grid the point covers and marks it *true* in the grid
     * @param x center x of point
     * @param y center y of point
     * @param node the node to find coverage of
     */
    private void findNodeCoverage(double x, double y, Node node) {
        int nX = downScaleX(x);
        int nY = downScaleY(y);

        Point p = new Point((int) x, (int) y);

        if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY && node.isPointInsideNode(p) && !grid[nY][nX]) {
            grid[nY][nX] = true;
            findNodeCoverage(x + scalingFactorX, y, node);
            findNodeCoverage(x - scalingFactorX, y, node);
            findNodeCoverage(x, y + scalingFactorY, node);
            findNodeCoverage(x, y - scalingFactorY, node);
        } else if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY) grid[nY][nX] = true;
    }

    /**
     * Recursively finds the area in the grid the start and end point covers and marks it *false* in the grid
     * @param x center x of point
     * @param y center y of point
     * @param node the node to find coverage of
     */
    private void removeStartAndEndNodeCoverage(double x, double y, Node node) {
        int nX = downScaleX(x);
        int nY = downScaleY(y);

        Point p = new Point((int) x, (int) y);

        if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY && node.isPointInsideNode(p) && grid[nY][nX] && !isPointInsideFailedNode(p)) {
            grid[nY][nX] = false;
            removeStartAndEndNodeCoverage(x + scalingFactorX, y, node);
            removeStartAndEndNodeCoverage(x - scalingFactorX, y, node);
            removeStartAndEndNodeCoverage(x, y + scalingFactorY, node);
            removeStartAndEndNodeCoverage(x, y - scalingFactorY, node);
        } else if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY) grid[nY][nX] = false;
    }

    private boolean isPointInsideFailedNode(Point p) {
        for (Node failedNode : failedNodes) {
            if (failedNode.isPointInsideNode(p)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Takes every element of the lines in the game and marks them as *true* in the grid
     * @param edges every line in the game
     */
    private void initEdges(List<Shape> edges) {
        for (Shape shape : edges) {
            for (PathElement pe : ((Path) shape).getElements()) {
                String pathElemString = pe.toString();

                double x = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("x") + 2,
                        pathElemString.indexOf(",")));
                double y = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("y") + 2,
                        pathElemString.indexOf("]")));

                int nX = downScaleX(x);
                int nY = downScaleY(y);

                grid[nY][nX] = true;
            }
        }
    }

    /**
     * Takes parent list from BFS to backtrack path from start point to end point
     *
     * @param parent    list containing corresponding parent to each point
     * @param startNode start node
     * @param endNode goal node
     * @return reversed list of points in the path from the start node to the end node
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    private ArrayList<Point> backtrace(Point[][] parent, Node startNode, Node endNode) {
        ArrayList<Point> path = new ArrayList<>();
        path.add(new Point(downScaleX(endNode.getX()), downScaleY(endNode.getY())));
        while (!path.get(path.size() - 1).equals(new Point(downScaleX(startNode.getX()), downScaleY(startNode.getY())))) {
            path.add(parent[(path.get(path.size() - 1)).getY()][path.get(path.size() - 1).getX()]);
        }
        return path;
    }

    /**
     * Breadth first search used for traversing the grid
     *
     * @param startNode start node
     * @param endNode goal node
     * @return reversed list of points in path from start node to end point or null if no such path exist
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public ArrayList<Point> BFS(Node startNode, Node endNode) throws NoValidEdgeException {
        initGrid(startNode, endNode);
        Point[][] parent = new Point[gridSizeY][gridSizeX];
        boolean[][] visited = new boolean[gridSizeY][gridSizeX];
        Queue<Point> queue = new LinkedList<>();
        Operator[][] opCombs = {{Operator.UNARY, Operator.SUBTRACTION},
                {Operator.UNARY, Operator.ADDITION},
                {Operator.SUBTRACTION, Operator.UNARY},
                {Operator.ADDITION, Operator.UNARY}};

        //mark end node as false
        grid[downScaleY(endNode.getY())][downScaleX(endNode.getX())] = false;

        visited[downScaleY(startNode.getY())][downScaleX(startNode.getX())] = true;

        queue.add(new Point(downScaleX(startNode.getX()), downScaleY(startNode.getY())));

        while (queue.size() != 0) {
            Point p0 = queue.poll();

            //check if we have reached end node
            if (p0.equals(new Point(downScaleX(endNode.getX()), downScaleY(endNode.getY())))) {
                return backtrace(parent, startNode, endNode);
            }

            //visits all available points around p0 in a cross shape
            for (Operator[] opComb : opCombs) {
                try {
                    int newY = (int) opComb[1].apply(p0.getY(), 1);
                    int newX = (int) opComb[0].apply(p0.getX(), 1);
                    if (!grid[newY][newX] && !visited[newY][newX]) {
                        visited[newY][newX] = true;
                        queue.add(new Point(newX, newY));
                        parent[newY][newX] = p0;
                    }
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }

        throw new NoValidEdgeException("No valid line found between nodes " + startNode.getId()
                + " and " + endNode.getId());

    }

    /**
     * Builds the Path object from the path list from BFS. The path will start at the non-scaled
     * start node, and end at the non-scaled end node, to make sure the path will connect to the points.
     * Also ensures that the path returned has room for a new node that does not collide visually with other game objects
     * @param startNode start node
     * @param endNode goal node
     * @return Path from start node to end node
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public Path findPath(Node startNode, Node endNode) throws NoValidEdgeException {
        failedNodes.clear();
        permanentlyFailedNodes.clear();

        ArrayList<Point> pathListReversed = BFS(startNode, endNode);
        Path pathToTest = generatePathFromPoints(startNode, endNode, pathListReversed);

        while (model.getNewNodeForPath(pathToTest) == null) {
            for (Node failedNode : model.getFailedNodesForMostRecentPath()) {
                pathListReversed = searchForLargerLegalPath(startNode, endNode, failedNode);
                if (pathListReversed == null) {
                    continue;
                }

                pathToTest = generatePathFromPoints(startNode, endNode, pathListReversed);
                if (model.isThereRoomForNewNodeOnPath(pathToTest)) {
                    return pathToTest;
                }
            }
            permanentlyFailedNodes.addAll(model.getFailedNodesForMostRecentPath());
            pathToTest = generatePathFromPoints(startNode, endNode, BFS(startNode, endNode));
        }

        return pathToTest;
    }

    /**
     * Searches for a larger path around the points on a new line that overlapped other game objects
     * @param startNode start node
     * @param endNode goal node
     * @param failedNode the node to find a path around
     * @return a new path to test for legal point or null if no such path was found
     */
    private ArrayList<Point> searchForLargerLegalPath(Node startNode, Node endNode, Node failedNode) {
        failedNodes.clear();
        failedNodes.add(failedNode);
        try {
            return BFS(startNode, endNode);
        } catch (NoValidEdgeException e) {
            return null;
        }
    }

    /**
     * Generates the Path object by going through the points found by BFS and scaling them back to real coordinates and putting them together
     * @param startNode start node
     * @param endNode goal node
     * @param pathListReversed list of coordinates found in BFS
     * @return the new path between the start and end point
     */
    private Path generatePathFromPoints(Node startNode, Node endNode, ArrayList<Point> pathListReversed) {
        Path path = new Path();
        path.getElements().add(new MoveTo(startNode.getX(), startNode.getY()));

        for (int i = pathListReversed.size() - 2; i > 0; i--) {
            Point p = pathListReversed.get(i);
            path.getElements().add(new LineTo(upScaleX(p.getX()), upScaleY(p.getY())));
        }
        path.getElements().add(new LineTo(endNode.getX(), endNode.getY()));

        return path;
    }

    /**
     * It will use a helper point for the BFS method and then use BFS back to the start point
     * @param startNode Node to make self loop for
     * @return Looping path from the start node
     * @throws NoValidEdgeException
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public Path getLoopPath(Node startNode) throws NoValidEdgeException {
        boolean validPath = false;
        Path[] pathHolder = null;
        Operator[][] opCombs = {{Operator.ADDITION, Operator.ADDITION}, {Operator.ADDITION, Operator.SUBTRACTION},
                {Operator.SUBTRACTION, Operator.ADDITION}, {Operator.SUBTRACTION, Operator.SUBTRACTION}};
        outerloop:
        for (int i = 15; i < gridSizeY * 16; i++) {
            for (int j = 15; j < gridSizeX * 16; j++) {
                for (Operator[] opComb : opCombs) {
                    pathHolder = selfLoopTestNode(startNode, i, j, opComb);
                    if (pathHolder[0] != null && pathHolder[1] != null) {
                        validPath = true;
                        break outerloop;
                    }
                }
            }
        }


        if (!validPath) {
            throw new NoValidEdgeException("No valid self loop from: " + startNode.getId() + " found");
        }

        pathHolder[0].getElements().addAll(pathHolder[1].getElements());

        return pathHolder[0];

    }

    /**
     * Places a test node i,j away from the start node
     * @param startNode start node for self loop
     * @param i y-coordinate offset
     * @param j x-coordinate offset
     * @param ops operator pair
     * @return 2 paths one from start node to test node, and one from
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public Path[] selfLoopTestNode(Node startNode, int i, int j, Operator[] ops) {
        Path pathToTemp = null, pathToStart = null;
        Node tempEndNode = new Node(ops[0].apply(startNode.getX(), j), ops[1].apply(startNode.getY(), i), 0, -2);
        if (0 < tempEndNode.getX() && 0 < tempEndNode.getY() && tempEndNode.getX() < model.getWidth() && tempEndNode.getY() < model.getHeight() && !model.nodeCollides(tempEndNode)) {
            try {
                pathToTemp = findPath(startNode, tempEndNode);
                pathToStart = findPath(tempEndNode, startNode);
            } catch (NoValidEdgeException ignored) {
            }

        }
        Path[] res = new Path[2];
        res[0] = pathToTemp;
        res[1] = pathToStart;
        return res;
    }

    /**
     * Takes a real coordinate of a game objects and return the downscaled or fitted value to the grid
     * @param coord the actual coordinate
     * @return the fitted coordinate
     */
    private int downScaleX(Double coord) {
        return (int) (coord / scalingFactorX);
    }

    /**
     * Takes a real coordinate of a game objects and return the downscaled or fitted value to the grid
     * @param coord the actual coordinate
     * @return the fitted coordinate
     */
    private int downScaleY(Double coord) {
        return (int) (coord / scalingFactorY);
    }

    /**
     * Takes a fitted coordinate of a game objects and return the upscaled or real value of the object
     * @param coord the actual coordinate
     * @return the fitted coordinate
     */
    private int upScaleX(int coord) {
        return (int) (coord * scalingFactorX) + 1;
    }

    /**
     * Takes a fitted coordinate of a game objects and return the upscaled or real value of the object
     * @param coord the actual coordinate
     * @return the fitted coordinate
     */
    private int upScaleY(int coord) {
        return (int) (coord * scalingFactorY) + 1;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (boolean[] rows : grid) {
            for (boolean cell : rows) {
                if (cell) res.append(" 1 ");
                else {
                    res.append(" 0 ");
                }
            }
            res.append("\n");
        }
        return res.toString();
    }
}
