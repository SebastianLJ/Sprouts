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
        } else if (!model.isPointInsideNode(p, startNode) && !model.isPointInsideNode(p, endNode)) {
            if (nY < grid.length && nX < grid.length) grid[nY][nX] = true;
        }
    }

    private void initEdges(List<Shape> edges) {
        for (Shape shape : edges) {
            if (shape instanceof Circle) {
                System.out.println("Circle found handling hereof required");
            } else {
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
        //System.out.println(this);
        ArrayList<Point> pathListReversed;
        if (startNode == endNode) {
            // TODO
            pathListReversed = null;
        } else {
            pathListReversed = BFS(startNode, endNode);
        }
        return createPathFromPointSequence(startNode, endNode, pathListReversed);
    }

    private Path createPathFromPointSequence(Node startNode, Node endNode, ArrayList<Point> pathListReversed) {
        Path path = new Path();
        path.getElements().add(new MoveTo(startNode.getX(), startNode.getY()));
        for (int i = pathListReversed.size() - 2; i > 0; i--) {
            Point p = pathListReversed.get(i);
            path.getElements().add(new LineTo(upScaleX(p.getX()), upScaleY(p.getY())));
        }
        path.getElements().add(new LineTo(endNode.getX(), endNode.getY()));
        return path;
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
