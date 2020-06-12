package Model;


import Exceptions.NoValidEdgeException;
import javafx.scene.shape.*;

import java.util.*;

public class PathFinder {
    private SproutModel model;
    private int gridSizeStart = 30;
    private int gridSize = gridSizeStart;
    private boolean[][] grid;

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
    public void initGrid() {
        List<Shape> edges = model.getEdges();
        List<Node> nodes = model.getNodes();

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

        for (Node node : nodes) {
            int x = downScaleX(node.getX());
            int y = downScaleY(node.getY());
            grid[y][x] = true;
        }
    }

    public void initGridRec(Node startNode, Node endNode) {
        grid = new boolean[gridSize][gridSize];
        Rectangle shape = new Rectangle();
        shape.setWidth(model.getWidth()/gridSize);
        shape.setHeight(model.getHeight()/gridSize);
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                shape.setX(upScaleX(j));
                shape.setY(upScaleY(i));
                grid[i][j] = model.shapeCollides(shape, startNode, endNode);
            }
        }
    }

    public void initGridCircle(Node startNode, Node endNode) {
        grid = new boolean[gridSize][gridSize];
        Circle shape = new Circle();
        shape.setRadius(1);
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Node node = model.findNodeFromPoint(new Point(upScaleX(j),upScaleY(i)));
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
        System.out.println(this);
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
    public ArrayList<Point> BFS(Node startNode, Node endNode, int gridSize) throws NoValidEdgeException {
        if (gridSize > model.getWidth()) {
            //throw new NoValidEdgeException("No valid edge");
            System.out.println("no valid edge");
            return null;
        }
        this.gridSize = gridSize;
        initGridCircle(startNode, endNode);
        Point[][] parent = new Point[gridSize][gridSize];
        boolean[][] visited = new boolean[gridSize][gridSize];
        Queue<Point> queue = new LinkedList<>();

        visited[downScaleY(startNode.getY())][downScaleX(startNode.getX())] = true;
        queue.add(new Point(downScaleX(startNode.getX()), downScaleY(startNode.getY())));

        while (queue.size() != 0) {
            Point p0 = queue.poll();

            if (p0.equals(new Point(downScaleX(endNode.getX()), downScaleY(endNode.getY())))) {
                return backtrace(parent, startNode, endNode);
            }
            try {
                if (!grid[p0.getY() - 1][p0.getX()] && !visited[p0.getY() - 1][p0.getX()]) {
                    visited[p0.getY() - 1][p0.getX()] = true;
                    queue.add(new Point(p0.getX(), p0.getY() - 1));
                    parent[p0.getY() - 1][p0.getX()] = p0;
                }
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                if (!grid[p0.getY() + 1][p0.getX()] && !visited[p0.getY() + 1][p0.getX()]) {
                    visited[p0.getY() + 1][p0.getX()] = true;
                    queue.add(new Point(p0.getX(), p0.getY() + 1));
                    parent[p0.getY() + 1][p0.getX()] = p0;
                }
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                if (!grid[p0.getY()][p0.getX() - 1] && !visited[p0.getY()][p0.getX() - 1]) {
                    visited[p0.getY()][p0.getX() - 1] = true;
                    queue.add(new Point(p0.getX() - 1, p0.getY()));
                    parent[p0.getY()][p0.getX() - 1] = p0;
                }
            } catch (IndexOutOfBoundsException e) {
            }
            try {
                if (!grid[p0.getY()][p0.getX() + 1] && !visited[p0.getY()][p0.getX() + 1]) {
                    visited[p0.getY()][p0.getX() + 1] = true;
                    queue.add(new Point(p0.getX() + 1, p0.getY()));
                    parent[p0.getY()][p0.getX() + 1] = p0;
                }
            } catch (IndexOutOfBoundsException e) {
            }

        }

        return BFS(startNode, endNode, gridSize*2);
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
        ArrayList<Point> pathListReversed = BFS(startNode, endNode, gridSizeStart);
        if (pathListReversed == null) {
            return new Path();
        }
        System.out.println(pathListReversed + "\n");
        Path path = new Path();
        path.getElements().add(new MoveTo(startNode.getX(), startNode.getY()));
        for (int i = pathListReversed.size() - 1; i >= 0; i--) {
            Point p = pathListReversed.get(i);
            path.getElements().add(new LineTo(upScaleX(p.getX()), upScaleY(p.getY())));
        }
        path.getElements().add(new LineTo(endNode.getX(), endNode.getY()));
        return path;

    }

    private int downScaleX(Double coord) {
        return (int) (coord / (model.getWidth() / grid.length));
    }

    private int downScaleY(Double coord) {
        return (int) (coord / (model.getHeight() / grid.length));
    }

    private int upScaleX(int coord) {
        return (int) (coord * (model.getWidth() / grid.length));
    }

    private int upScaleY(int coord) {
        return (int) (coord * (model.getHeight() / grid.length));
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
