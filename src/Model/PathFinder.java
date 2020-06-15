package Model;


import Exceptions.NoValidEdgeException;
import javafx.scene.shape.*;

import java.util.*;

public class PathFinder {
    private SproutModel model;
    private int gridSize = 250;
    private boolean[][] grid = new boolean[gridSize][gridSize];

    private double scalingFactorX;
    private double scalingFactorY;

    public PathFinder(SproutModel model) {
        this.model = model;
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
        grid = new boolean[gridSize][gridSize];
        List<Shape> edges = model.getEdges();
        List<Node> nodes = model.getNodes();

        initNodes(nodes, startNode, endNode);
        initEdges(edges);
    }

    private void initNodes(List<Node> nodes, Node startNode, Node endNode) {
        for (Node node : nodes) {
            findNodeCoverage(node.getX(), node.getY(), startNode, endNode);
        }
    }

    private void findNodeCoverage(double x, double y, Node startNode, Node endNode) {
        int nX = downScaleX(x);
        int nY = downScaleY(y);

        Point p = new Point((int) x, (int) y);

        if (model.isPointInsideNode(p) && !model.isPointInsideNode(p, startNode) && !model.isPointInsideNode(p, endNode) && !grid[nY][nX]) {
            grid[nY][nX] = true;
            findNodeCoverage(x + scalingFactorX, y, startNode, endNode);
            findNodeCoverage(x - scalingFactorX, y, startNode, endNode);
            findNodeCoverage(x, y + scalingFactorY, startNode, endNode);
            findNodeCoverage(x, y - scalingFactorY, startNode, endNode);
        }
    }

    private void initEdges(List<Shape> edges) {
        for (Shape shape : edges) {
            for (PathElement pe : ((Path) shape).getElements()) {
                String pathElemString = pe.toString();
                if (pe instanceof MoveTo || pe instanceof LineTo) {


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
    }

    /**
     * Grid i build iterating each point in the scaled grid, and checking if there is a point or edge
     * on or very near to that point. This is done by creating a Circle object with a radius of 0.5 and
     * checking for collision between the circle and any edges/nodes.
     *
     * @param startNode user selected start node
     * @param endNode   user selected end node
     * @author Sebastian Lund Jensen
     */
    public void initGridCircle(Node startNode, Node endNode) {
        grid = new boolean[gridSize][gridSize];
        Circle shape = new Circle();
        shape.setRadius(0.5);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Node node = model.findNodeFromPoint(new Point(upScaleX(j), upScaleY(i)));

                //if node is inside a node that is not the start node or the end node
                // else if the node is not inside any point
                if (node != null && !(node.equals(startNode) || node.equals(endNode))) {
                    shape.setCenterX(upScaleX(j));
                    shape.setCenterY(upScaleY(i));
                    grid[i][j] = model.shapeCollides(shape, startNode, endNode);
                } else if (node == null) {
                    shape.setCenterX(upScaleX(j));
                    shape.setCenterY(upScaleY(i));
                    grid[i][j] = model.shapeCollides(shape, startNode, endNode);
                }
            }
        }
    }

    /**
     * Takes parent list from BFS to backtrack path from start point to end point
     *
     * @param parent    list containing corresponding parent to each point
     * @param startNode
     * @param endNode
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
        //System.out.println("path: " + path.toString());
        return path;
    }

    /**
     * Breadth first search used for traversing the grid
     *
     * @param startNode
     * @param endNode
     * @return reversed list of points in path from start node to end point or null if no such path exist
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public ArrayList<Point> BFS(Node startNode, Node endNode) throws NoValidEdgeException {
        Point[][] parent = new Point[gridSize][gridSize];
        boolean[][] visited = new boolean[gridSize][gridSize];
        Queue<Point> queue = new LinkedList<>();

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
            try {
                if (!grid[p0.getY() - 1][p0.getX()] && !visited[p0.getY() - 1][p0.getX()]) {
                    visited[p0.getY() - 1][p0.getX()] = true;
                    queue.add(new Point(p0.getX(), p0.getY() - 1));
                    parent[p0.getY() - 1][p0.getX()] = p0;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
            try {
                if (!grid[p0.getY() + 1][p0.getX()] && !visited[p0.getY() + 1][p0.getX()]) {
                    visited[p0.getY() + 1][p0.getX()] = true;
                    queue.add(new Point(p0.getX(), p0.getY() + 1));
                    parent[p0.getY() + 1][p0.getX()] = p0;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
            try {
                if (!grid[p0.getY()][p0.getX() - 1] && !visited[p0.getY()][p0.getX() - 1]) {
                    visited[p0.getY()][p0.getX() - 1] = true;
                    queue.add(new Point(p0.getX() - 1, p0.getY()));
                    parent[p0.getY()][p0.getX() - 1] = p0;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }
            try {
                if (!grid[p0.getY()][p0.getX() + 1] && !visited[p0.getY()][p0.getX() + 1]) {
                    visited[p0.getY()][p0.getX() + 1] = true;
                    queue.add(new Point(p0.getX() + 1, p0.getY()));
                    parent[p0.getY()][p0.getX() + 1] = p0;
                }
            } catch (IndexOutOfBoundsException ignored) {
            }

        }
        throw new NoValidEdgeException("No valid edge found between nodes " + startNode.getId()
                + " and " + endNode.getId());
    }

//    public ArrayList<Path> SelfLoop(Node startNode) throws NoValidEdgeException {
//        Path path1 = getPath(startNode, endNode);
//
//        Path path2 = getPath(endNode, startNode);
//        System.out.println("path1: " + path1);
//        System.out.println("path2: " + path2);
//
//        ArrayList<Path> test = new ArrayList<>();
//
//        test.add(path1);
//        test.add(path2);
//
//        return test;
//    }

    /**
     * Builds the Path object from the path list from BFS. The path will start at the non-scaled
     * start node, and end at the non-scaled end node, to make sure the path will connect to the points.
     *
     * @param startNode
     * @param endNode
     * @return Path from start node to end node
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public Path getPath(Node startNode, Node endNode) throws NoValidEdgeException {
        this.scalingFactorX = model.getWidth() / gridSize;
        this.scalingFactorY = model.getHeight() / gridSize;
        initGrid(startNode, endNode);
        ArrayList<Point> pathListReversed = BFS(startNode, endNode);
        Path path = new Path();
        path.getElements().add(new MoveTo(startNode.getX(), startNode.getY()));
        for (int i = pathListReversed.size() - 2; i > 0; i--) {
            Point p = pathListReversed.get(i);
            path.getElements().add(new LineTo(upScaleX(p.getX()), upScaleY(p.getY())));
        }
        path.getElements().add(new LineTo(endNode.getX(), endNode.getY()));
        return path;
    }


    public Path getLoopPath(Node startNode) throws NoValidEdgeException {
        System.out.println("modelgetHeight: " + model.getHeight());
        System.out.println("modelgetWidth. " + model.getWidth());
        boolean validPath = false;
        Path pathToTemp = new Path();
        Path pathToStart = new Path();

        outerloop:
        for (int i = gridSize/16; i < gridSize; i++) {
            for (int j = gridSize/16; j < gridSize; j++) {
                Node tempEndNode = new Node(startNode.getX() + i, startNode.getY() + j, 0, -2);
                if (!model.nodeCollides(tempEndNode) && tempEndNode.getX() < model.getWidth() && tempEndNode.getY() < model.getHeight()) {
                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                tempEndNode = new Node(startNode.getX() + i, startNode.getY() - j, 0, -2);
                if (!model.nodeCollides(tempEndNode) &&  tempEndNode.getX() < model.getWidth() &&  tempEndNode.getY() > 0) {

                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }


                tempEndNode = new Node(startNode.getX() - i, startNode.getY() + j, 0, -2);
                if (!model.nodeCollides(tempEndNode) && tempEndNode.getX() > 0 && tempEndNode.getY() < model.getHeight()) {

                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }

                }

                tempEndNode = new Node(startNode.getX() - i, startNode.getY() - j, 0, -2);
                if (!model.nodeCollides(tempEndNode) && tempEndNode.getX() > 0 && tempEndNode.getY() > 0) {
                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                if (!model.nodeCollides(tempEndNode) && tempEndNode.getX() < model.getWidth()) {
                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                tempEndNode = new Node(startNode.getX() - j, startNode.getY(), 0, -2);
                if (!model.nodeCollides(tempEndNode) && tempEndNode.getX() > 0) {
                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

                tempEndNode = new Node(startNode.getX(), startNode.getY() + j, 0, -2);
                if (!model.nodeCollides(tempEndNode) && tempEndNode.getY() < model.getHeight()) {
                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }
                tempEndNode = new Node(startNode.getX(), startNode.getY() - j, 0, -2);
                if (!model.nodeCollides(tempEndNode) && tempEndNode.getY() > 0) {
                    try {
                        pathToTemp = getPath(startNode, tempEndNode);
                        pathToStart = getPath(tempEndNode, startNode);
                        validPath = true;
                        break outerloop;
                    } catch (NoValidEdgeException | IndexOutOfBoundsException e) {
                        e.printStackTrace();
                    }
                }

            }}


            if (!validPath) {
                throw new NoValidEdgeException("No valid selfloop from: " + startNode.getId());
            }

            pathToTemp.getElements().addAll(pathToStart.getElements());

            System.out.println("resultingPath: " + pathToTemp);

            return pathToTemp;


    }

    private int downScaleX(Double coord) {
        return (int) (coord / scalingFactorX);
    }

    private int downScaleY(Double coord) {
        return (int) (coord / scalingFactorY);
    }

    private int upScaleX(int coord) {
        return (int) (coord * scalingFactorX) + 1;
    }

    private int upScaleY(int coord) {
        return (int) (coord * scalingFactorY) + 1;
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            res.append(Arrays.toString(grid[i])).append("\n");
        }
        return res.toString();
    }

    private String print2dArr(boolean[][] arr) {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            res.append(Arrays.toString(arr[i])).append("\n");
        }
        return res.toString();
    }
}
