package Model;


import Exceptions.NoValidEdgeException;
import javafx.scene.shape.*;

import java.util.*;

public class PathFinder {
    private SproutModel model;
    private int gridSizeX = 348;
    private int gridSizeY = 236;
    private boolean[][] grid = new boolean[gridSizeY][gridSizeX];
    private int counter = 0;

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
        //System.out.println(this);
        removeStartAndEndNodeCoverage(startNode.getX(), startNode.getY(), startNode);
        removeStartAndEndNodeCoverage(endNode.getX(), endNode.getY(), endNode);
        //System.out.println(this);
        //  initEdges(edges);
        // initEdges2(edges);
        // initEdges3(edges);
        initEdges4(edges);
    }

    private void initNodes(List<Node> nodes) {
        for (Node node : nodes) {
            findNodeCoverage(node.getX(), node.getY());
        }
    }

    private void initFailedNodes() {
        for (Node failedNode : failedNodes) {
            findNodeCoverageOfFailedNode(failedNode.getX(), failedNode.getY(), failedNode);
        }

        for (Node permanentlyFailedNode : permanentlyFailedNodes) {
            findNodeCoverageOfFailedNode(permanentlyFailedNode.getX(), permanentlyFailedNode.getY(), permanentlyFailedNode);
        }
    }

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

    private void findNodeCoverage(double x, double y) {
        int nX = downScaleX(x);
        int nY = downScaleY(y);

        Point p = new Point((int) x, (int) y);

        if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY && model.isPointInsideNode(p) && !grid[nY][nX]) {
            grid[nY][nX] = true;
            findNodeCoverage(x + scalingFactorX, y);
            findNodeCoverage(x - scalingFactorX, y);
            findNodeCoverage(x, y + scalingFactorY);
            findNodeCoverage(x, y - scalingFactorY);
        } else if (0 < nX && nX < gridSizeX && 0 < nY && nY < gridSizeY) grid[nY][nX] = true;
    }

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

    private void initEdges3(List<Shape> edges) {
        for (Shape x : edges) {
            Path tmp = (Path) x;
            int size = tmp.getElements().size();
            MoveTo startVec0 = (MoveTo) tmp.getElements().get(0);
            LineTo startVec1 = (LineTo) tmp.getElements().get(1);
            LineTo endVec1 = (LineTo) tmp.getElements().get(size - 1);
            findPathCoverage2(startVec0.getX(), startVec0.getY(), startVec1.getX(), startVec1.getY());


            if (tmp.getElements().get(size - 2) instanceof MoveTo) {
                MoveTo endVec0 = (MoveTo) tmp.getElements().get(size - 2);
                findPathCoverage2(endVec0.getX(), endVec0.getY(), endVec1.getX(), endVec1.getY());
            } else {
                LineTo endVec0 = (LineTo) tmp.getElements().get(size - 2);
                findPathCoverage2(endVec0.getX(), endVec0.getY(), endVec1.getX(), endVec1.getY());
            }

        }
    }

    private void initEdges4(List<Shape> edges) {
        String pathElemString1;
        String pathElemString2;

        for (Shape shape : edges) {
            for (int i = 0; i < ((Path) shape).getElements().size() - 1; i++) {
                pathElemString1 = ((Path) shape).getElements().get(i).toString();
                pathElemString2 = ((Path) shape).getElements().get(i + 1).toString();

                double x1 = Double.parseDouble(pathElemString1.substring(pathElemString1.indexOf("x") + 2,
                        pathElemString1.indexOf(",")));
                double y1 = Double.parseDouble(pathElemString1.substring(pathElemString1.indexOf("y") + 2,
                        pathElemString1.indexOf("]")));

                double x2 = Double.parseDouble(pathElemString2.substring(pathElemString2.indexOf("x") + 2,
                        pathElemString2.indexOf(",")));
                double y2 = Double.parseDouble(pathElemString2.substring(pathElemString2.indexOf("y") + 2,
                        pathElemString2.indexOf("]")));

                findPathCoverage2(x1, y1, x2, y2);
            }
        }
    }

    private void findPathCoverage2(double startX, double startY, double endX, double endY) { //gaps happen during grid resizing, perhaps off by one or some round off error?
        double vecX = endX - startX;
        double vecY = endY - startY;
        double vecLength = Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2));
        vecX = vecX / scalingFactorX;
        vecY = vecY / scalingFactorY;
        double vecScaledLength = Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2));
//        double vecX = endX - startX;
//        double vecY = endY - startY;
//        double vecLength = Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2));
//        double scale = Math.min(scalingFactorX, scalingFactorY);
//        vecX = vecX/scale;
//        vecY = vecY /scale;
//        double vecScaledLength = Math.sqrt(Math.pow(vecX, 2) + Math.pow(vecY, 2));
        for (int i = 0; i <= (vecLength / vecScaledLength); i++) {
            grid[downScaleY(startY + i * vecY)][downScaleX(startX + i * vecX)] = true;
        }
    }

    private void initEdges2(List<Shape> edges) {
        for (Shape x : edges) {
            Path tmp = (Path) x;
            findPathCoverage(tmp);
        }
    }

    private void findPathCoverage(Path path) {
        int direction = -1;  //0 for right, 1 for left, 2 for down 3 for right
        int j;
        int cellIndex = -1;
        double endPointX = -1;
        double endPointY = -1;
        double startPointX = -1;
        double startPointY = -1;

        for (int i = 0; i < path.getElements().size() - 1; i++) {
            j = 0;
            direction = -1;
            cellIndex = -1;
            if (path.getElements().get(i) instanceof MoveTo) {
                startPointX = ((MoveTo) path.getElements().get(i)).getX();
                startPointY = ((MoveTo) path.getElements().get(i)).getY();
            } else {
                startPointX = ((LineTo) path.getElements().get(i)).getX();
                startPointY = ((LineTo) path.getElements().get(i)).getY();
            }
            if (path.getElements().get(i + 1) instanceof MoveTo) {
                endPointX = ((MoveTo) path.getElements().get(i + 1)).getX();
                endPointY = ((MoveTo) path.getElements().get(i + 1)).getY();

            } else {
                endPointX = ((LineTo) path.getElements().get(i + 1)).getX();
                endPointY = ((LineTo) path.getElements().get(i + 1)).getY();

            }
            double diffX = endPointX - startPointX;
            double diffY = endPointY - startPointY;
            if (diffX > 0 && diffY == 0) {
                direction = 0;
                cellIndex = downScaleX(startPointX);
            } else if (diffX < 0 && diffY == 0) {
                direction = 1;
                cellIndex = downScaleX(startPointX);

            }
            if (diffY > 0 && diffX == 0) {
                direction = 2;
                cellIndex = downScaleY(startPointY);
            } else if (diffY < 0 && diffX == 0) {
                direction = 3;
                cellIndex = downScaleY(startPointY);
            }

            while (direction == 0 && upScaleX(cellIndex + j) <= endPointX) { //moving towards right
                grid[downScaleY(startPointY)][cellIndex + j] = true;
                j++;
            }

            while (direction == 1 & upScaleX(cellIndex - j) >= endPointX) { //moving towards left
                grid[downScaleY(startPointY)][cellIndex - j] = true;
                j++;

            }

            while (direction == 2 && upScaleY(cellIndex + j) <= endPointY) { //moving down
                grid[cellIndex + j][downScaleX(startPointX)] = true;
                j++;
            }
            while (direction == 3 && upScaleY(cellIndex - j) >= endPointY) { //moving up
                grid[cellIndex - j][downScaleX(startPointX)] = true;
                j++;
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
        grid = new boolean[gridSizeY][gridSizeX];
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

        initGrid(startNode, endNode);
        //System.out.println(this);

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
                    if (!grid[(int) opComb[1].apply(p0.getY(), 1)][(int) opComb[0].apply(p0.getX(), 1)]
                            && !visited[(int) opComb[1].apply(p0.getY(), 1)][(int) opComb[0].apply(p0.getX(), 1)]) {

                        visited[(int) opComb[1].apply(p0.getY(), 1)][(int) opComb[0].apply(p0.getX(), 1)] = true;
                        queue.add(new Point((int) opComb[0].apply(p0.getX(), 1),
                                (int) opComb[1].apply(p0.getY(), 1)));

                        parent[(int) opComb[1].apply(p0.getY(), 1)][(int) opComb[0].apply(p0.getX(), 1)] = p0;
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
     *
     * @param startNode
     * @param endNode
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
                // why not (model.getNewNodeForPath(pathToTest) != null) ?
                if (model.isThereRoomForNewNodeOnPath(pathToTest)) {
                    System.out.println("There's room! " + pathToTest);
                    return pathToTest;
                }
            }
            permanentlyFailedNodes.addAll(model.getFailedNodesForMostRecentPath());
            pathToTest = generatePathFromPoints(startNode, endNode, BFS(startNode, endNode));
        }
        return pathToTest;
    }

    private ArrayList<Point> searchForLargerLegalPath(Node startNode, Node endNode, Node failedNode) {
        failedNodes.clear();
        failedNodes.add(failedNode);
        try {
            return BFS(startNode, endNode);
        } catch (NoValidEdgeException e) {
            return null;
        }
    }

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

    public Path[] selfLoopTestNode(Node startNode, int i, int j, Operator[] ops) throws NoValidEdgeException {
        Path pathToTemp = null, pathToStart = null;
        Node tempEndNode = new Node(ops[0].apply(startNode.getX(), i), ops[1].apply(startNode.getY(), j), 0, -2);
        if (tempEndNode.getX() < model.getWidth() && tempEndNode.getY() < model.getHeight() && !model.nodeCollides(tempEndNode)) {
            try {
                pathToTemp = findPath(startNode, tempEndNode);
                pathToStart = findPath(tempEndNode, startNode);
            } catch (NoValidEdgeException e) {
            }

        }
        Path[] res = new Path[2];
        res[0] = pathToTemp;
        res[1] = pathToStart;
        return res;

    }

    private int downScaleX(Double coord) {
        return (int) (coord / scalingFactorX);
    }

    private int downScaleY(Double coord) {
        return (int) (coord / scalingFactorY);
    }

    private int upScaleX(int coord) {
        return (int) (coord * scalingFactorX);
    }

    private int upScaleY(int coord) {
        return (int) (coord * scalingFactorY);
    }

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j]) res.append(" 1 ");
                else {
                    res.append(" 0 ");
                }
            }
            res.append("\n");
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
