package Model;


import javafx.scene.shape.*;

import java.util.*;

public class PathFinder {
    private SproutModel model;
    private int gridSize = 10;
    private boolean[][] grid = new boolean[gridSize][gridSize];

    public PathFinder(SproutModel model) {
        this.model = model;
    }

    /**
     * Grid is initialized using the already drawn nodes and edges. This method will use a scale, which is
     * decrementing after each unsuccessful search for a valid path, to increase precision and number of
     * possible edges.
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
                
                double x = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("x")+2,
                        pathElemString.indexOf(",")));
                double y = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("y")+2,
                        pathElemString.indexOf("]")));

                int nX = scale(x);
                int nY = scale(y);

                grid[nY][nX] = true;
            }
        }

        for (Node node : nodes) {
            int x = scale(node.getX());
            int y = scale(node.getY());
            grid[y][x] = true;
        }
    }

    /**
     * Takes parent list from BFS to backtrack path from start point to end point
     * @param parent list containing corresponding parent to each point
     * @param startNode
     * @param endNode
     * @return reversed list of points in the path from the start node to the end node
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    private ArrayList<Point> backtrace(Point[][] parent, Node startNode, Node endNode) {
        ArrayList<Point> path = new ArrayList<>();
        path.add(new Point(scale(endNode.getX()), scale(endNode.getY())));
        while(!path.get(path.size()-1).equals(new Point(scale(startNode.getX()), scale(startNode.getY())))) {
            path.add(parent[(path.get(path.size()-1)).getY()][path.get(path.size()-1).getX()]);
        }
        return path;
    }

    /**
     * Breadth first search used for traversing the grid
     * @param startNode
     * @param endNode
     * @return reversed list of points in path from start node to end point or null if no such path exist
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public ArrayList<Point> BFS(Node startNode, Node endNode) {
        Point[][] parent = new Point[gridSize][gridSize];
        boolean[][] visited = new boolean[gridSize][gridSize];
        Queue<Point> queue = new PriorityQueue<>();

        visited[scale(startNode.getY())][scale(startNode.getX())] = true;
        queue.add(new Point(scale(startNode.getX()), scale(startNode.getY())));

        while(queue.size() != 0) {
            Point p0 = queue.poll();

            if (p0.equals(new Point(scale(endNode.getX()), scale(endNode.getY())))) {
                return backtrace(parent, startNode, endNode);
            }


            if (!grid[p0.getY() - 1][p0.getX()] && !visited[p0.getY() - 1][p0.getX()]) {
                visited[p0.getY() - 1][p0.getX()] = true;
                queue.add(new Point(p0.getX(), p0.getY() - 1));
                parent[p0.getY() - 1][p0.getX()] = p0;
            }
            if (!grid[p0.getY() + 1][p0.getX()] && !visited[p0.getY() + 1][p0.getX()]) {
                visited[p0.getY() + 1][p0.getX()] = true;
                queue.add(new Point(p0.getX(), p0.getY() + 1));
                parent[p0.getY() + 1][p0.getX()] = p0;
            }
            if (!grid[p0.getY()][p0.getX() - 1] && !visited[p0.getY()][p0.getX() - 1]) {
                visited[p0.getY()][p0.getX() - 1] = true;
                queue.add(new Point(p0.getX() - 1, p0.getY()));
                parent[p0.getY()][p0.getX() - 1] = p0;
            }
            if (!grid[p0.getY()][p0.getX() + 1] && !visited[p0.getY()][p0.getX() + 1]) {
                visited[p0.getY()][p0.getX() + 1] = true;
                queue.add(new Point(p0.getX() + 1, p0.getY()));
                parent[p0.getY()][p0.getX() + 1] = p0;
            }
        }

        return null;
    }

    /**
     * Builds the Path object from the path list from BFS. The path will start at the non-scaled
     * start node, and end at the non-scaled end node, to make sure the path will connect to the points.
     * @param startNode
     * @param endNode
     * @return Path from start node to end node
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     *  @author Sebastian Lund Jensen
     */
    public Path getPath(Node startNode, Node endNode) {
        ArrayList<Point> pathListReversed = BFS(startNode, endNode);
        Path path = new Path();
        path.getElements().add(new MoveTo(startNode.getX(), startNode.getY()));
        for (int i = pathListReversed.size() - 1; i >= 0; i--) {
            Point p = pathListReversed.get(i);
            path.getElements().add(new LineTo(p.getX(), p.getY()));
        }
        path.getElements().add(new LineTo(endNode.getX(), endNode.getY()));
        return path;
    }

    private int scale(Double coord) {
        return (int) (coord/(model.getWidth()/grid.length));
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for(int i = 0; i < grid.length; i++) {
            res.append(Arrays.toString(grid[i])).append("\n");
        }
        return res.toString();
    }
}
