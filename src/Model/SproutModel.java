package Model;

import Exceptions.*;
import javafx.beans.property.BooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javafx.scene.input.MouseEvent;

import java.util.*;

public class SproutModel {
    private static final int DISTANCE_BETWEEN_POINTS = 20;
    private static final int DISTANCE_FROM_BORDER = 20;
    private List<Shape> edges;
    private List<Node> nodes;

    private double height;
    private double width;
    private Path path;
    private boolean isCollided;
    private Point point;
    private GameFlow gameFlow;
    private Node pathStartNode;
    private boolean leftStartNode = false;

    private PathFinder pf;


    public SproutModel() {
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
        gameFlow = new GameFlow();
        pf = new PathFinder(this);
    }

    /**
     * @author Emil Sommer Desler
     * Creates a certain amount of nodes with a random placement on the map.
     * The method wil ensure the created nodes will have a certain edge from the border of the map
     * and a certain distance from each other.
     * @param amount
     */
    public void addRandomNodes(int amount) {
        Random random = new Random();

        int x;
        int y;
        Circle circle = new Circle();
        circle.setRadius(Node.radius); // TODO make scalable

        for (int i = 0; i < amount; i++) {
            do {
                x = random.nextInt((int) width - 2*DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                y = random.nextInt((int) height - 2*DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                circle.setCenterX(x);
                circle.setCenterY(y);
            } while (invalidPointLocation(circle));
            nodes.add(new Node(x, y, 0,i));
        }
    }

    private boolean invalidPointLocation(Circle circle) {
        for (Node node : nodes) {
            Shape intersect = Shape.intersect(circle, node.getShape());
            if (intersect.getBoundsInLocal().getWidth() != -1) {
                return true;
            }
            if (DISTANCE_BETWEEN_POINTS > distanceBetweenCircleCenter(node.getShape(), circle)) {
                return true;
            }
        }

        return false;
    }

    private int distanceBetweenCircleCenter(Circle circle1, Circle circle2) {
        double dx = circle2.getCenterX()-circle1.getCenterX();
        double dy = circle2.getCenterY()-circle1.getCenterY();

        return (int) Math.ceil(Math.sqrt(dx*dx+dy*dy));
    }


    public void resetGame() {
        edges.clear();
        nodes.clear();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public List<Shape> getEdges() {
        return edges;
    }

    public boolean hasNode(int name) {
        return name < nodes.size();
    }

    /**
     * @author Thea Birk Berger
     * Checks if a node is on the gameboard
     * @param nodeToFind
     * @return boolean
     */
    public boolean hasNode(Circle nodeToFind) {
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                return true;
            }
        }
        return false;
    }

    public Circle getNodeWithName(int name) {
        return nodes.get(name).getShape();
    }

    public int getNumberOfEdges(int name) {
        return nodes.get(name).getNumberOfConnectingEdges();
    }

    /**
     * @author Thea Birk Berger
     * Identifies if a circle or line should be drawn between two chosen nodes and initializes the appropriate process
     * @param startNode
     * @param endNode
     * @throws CollisionException If the line drawn collides with itself or existing lines
     */
    public void drawEdgeBetweenNodes(int startNode, int endNode) throws CollisionException {
        if (startNode == endNode) {
            drawCircleFromNodeToItself(startNode);
        } else {
            drawLineBetweenNodes(startNode, endNode);
        }
    }

    /**
     * @author Thea Birk Berger
     * Adds a straight line between two nodes to the gameboard
     * @param startNodeName
     * @param endNodeName
     * @throws CollisionException If the line drawn collides with itself or existing lines
     */
    public void drawLineBetweenNodes(int startNodeName, int endNodeName) throws CollisionException {
        System.out.println("w: " + width + ", h:" + height);
        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);

        /*if (edgesCollides(newLine)) {
            throw new CollisionException("Line collided with an exisiting line");
        } else {
            // Add edge to gameboard
            edges.add(newLine);
            // Add new node mid edge
            addNodeOnLine(newLine);
            // Update number of connecting edges for the two nodes
            startNode.incNumberOfConnectingEdges(1);
            endNode.incNumberOfConnectingEdges(1);
            nodes.set(startNodeName, startNode);
            nodes.set(endNodeName, endNode);
        }*/
        edges.add(path);
        startNode.incNumberOfConnectingEdges(1);
        endNode.incNumberOfConnectingEdges(1);
        nodes.set(startNodeName, startNode);
        nodes.set(endNodeName, endNode);
    }

    public void drawSmartLine(Circle startNodeCircle, Circle endNodeCircle) throws NoValidEdgeException {
        int nameOfStartNode = findNameOfNode(startNodeCircle);
        int nameOfEndNode = findNameOfNode(endNodeCircle);

        Node startNode = nodes.get(nameOfStartNode);
        Node endNode = nodes.get(nameOfEndNode);


        edges.add(pf.getPath(startNode, endNode));
        startNode.incNumberOfConnectingEdges(1);
        endNode.incNumberOfConnectingEdges(1);

    }

    /**
     * @author Thea Birk Berger
     * Adds a cicular edge to the gameboard - connecting a node to itself
     * @param nodeName
     */
    public void drawCircleFromNodeToItself(int nodeName) throws CollisionException {
        Node node = nodes.get(nodeName);
        Circle newCircle = createCircleToDraw(node);

        if (edgesCollides(newCircle)) {
            throw new CollisionException("Line collided with an exisiting line");
        } else {
            // Add edge to gameboard
            edges.add(newCircle);
            // Add new node mid edge
            addNodeOnCircle(newCircle, node.getX(), node.getY());
            // Update number of connecting edges for the node
            node.incNumberOfConnectingEdges(2);
            nodes.set(nodeName, node);
        }

    }

    /**
     * @author Thea Birk Berger
     * Creates a straight line between the centers of two given nodes
     * @param startNode
     * @param endNode
     * @return a Line object
     */
    private Line getLineBetweenNodes(Node startNode, Node endNode) {

        Line newLine = new Line();

        newLine.setStartX(startNode.getX());
        newLine.setStartY(startNode.getY());
        newLine.setEndX(endNode.getX());
        newLine.setEndY(endNode.getY());

        return newLine;
    }

    /**
     * @author Thea Birk Berger
     * Creates a straight line between the contour of two given nodes
     * @param startNode
     * @param endNode
     * @return a Line object
     */
    private Line getLineBetweenNodeContours(Node startNode, Node endNode) {

        double x1 = startNode.getX();
        double x2 = endNode.getX();
        double y1 = startNode.getY();
        double y2 = endNode.getY();

        // Compute distance between node centers
        double length = getLineLength(x1,y1,x2,y2);

        // Set line end points as the node edge (cut off the radius)
        Line newLine = new Line();

        newLine.setStartX(x1 + (5/length) * (x2-x1));
        newLine.setStartY(y1 + (5/length) * (y2-y1));
        newLine.setEndX(x2 + (5/length) * (x1-x2));
        newLine.setEndY(y2 + (5/length) * (y1-y2));
        // TODO: make sure 5 is not hard coded and put node radius instead

        return newLine;
    }

    private double getLineLength(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(Math.abs(x1-x2),2) + Math.pow(y1-y2,2));
    }

    /**
     * @author Thea Birk Berger
     * Creates a circular edge from a node to itself
     * @param node
     * @return a Circle object
     */
    private Circle createCircleToDraw(Node node) {
        Circle newCircle = new Circle();

        double radius = Node.radius*2;
        double nodeX = node.getX();
        double nodeY = node.getY();

        // Get edge center coordinates so that it does not exceed the game frame
        Double[] center = getCircleCenterCoordinates(nodeX, nodeY, radius);
        // Set edge properties
        newCircle.setCenterX(center[0]);
        newCircle.setCenterY(center[1]);
        newCircle.setRadius(radius);
        newCircle.setFill(Color.TRANSPARENT);
        newCircle.setStroke(Color.BLACK);

        // TODO: Remove part of edge that is held within the node

        return newCircle;
    }
    public void addNodeOnLine(Line edge) {
        double edgeIntervalX = Math.abs(edge.getEndX() - edge.getStartX());
        double edgeIntervalY = Math.abs(edge.getEndY() - edge.getStartY());
        double newNodeX = Math.min(edge.getStartX(), edge.getEndX()) + (edgeIntervalX / 2);
        double newNodeY = Math.min(edge.getStartY(), edge.getEndY()) + (edgeIntervalY / 2);


        Node newNode = new Node(newNodeX, newNodeY, 2, nodes.size());
        nodes.add(newNode);
    }

    /**
     * @author Noah Bastian Christiansen
     * Adds a new node close to the midpoint of a valid line
     */
    public void addNodeOnLineDrag(){
        int size = path.getElements().size();
        LineTo test = (LineTo) (path.getElements().get(size/2));
        Node newNode = new Node(test.getX(), test.getY(), 2, nodes.size());
        nodes.add(newNode);
    }

    public void addNodeOnSmartClick() {
        path = (Path)(edges.get(edges.size()-1));
        int size = path.getElements().size();
        LineTo test = (LineTo) (path.getElements().get(size/2));
        Node newNode = new Node(test.getX(), test.getY(), 2, nodes.size());
        nodes.add(newNode);
    }

    public void addNodeOnCircle(Circle edge, double originNodeX, double originNodeY) {

        double newNodeX = originNodeX + (edge.getCenterX() - originNodeX) * 2;
        double newNodeY = originNodeY + (edge.getCenterY() - originNodeY) * 2;

        Node newNode = new Node(newNodeX, newNodeY, 2, nodes.size());
        nodes.add(newNode);
    }

    /**
     * @author Thea Birk Berger
     * Get circlar edge position given node coordinates.
     * The edge is valid as long as the node is positioned on it.
     * The position of the edge is chosen so that is does not exceed the game frame.
     * @param originNodeX
     * @param originNodeY
     * @param radius
     * @return the edge center coordinates
     */
    public Double[] getCircleCenterCoordinates(double originNodeX, double originNodeY, double radius) {

        Double[] center = {originNodeX, originNodeY};

        if (originNodeX - radius >= 0) {
            center[0] = originNodeX - radius;
        } else if (originNodeX + radius < width) {
            center[0] = originNodeX + radius;
        } else if (originNodeY - radius >= 0) {
            center[1] = originNodeY - radius;
        } else {
            center[1] = originNodeY + radius;
        }

        return center;
    }

    /**
     * @param mouseClick A mouse click
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * Sets up path object and sets coordinates for starting point of drawing (the position on the pane where the click occured)
     */
    public void initializePath(MouseEvent mouseClick) throws InvalidNode {
        isCollided = false;
        leftStartNode = false;
        point = new Point((int) mouseClick.getX(), (int) mouseClick.getY());
        pathStartNode = findNodeFromPoint(point);
        if (pathStartNode != null && pathStartNode.getNumberOfConnectingEdges() < 3) {
            path = new Path();
        } else {
            throw new InvalidNode(pathStartNode);
        }
    }

    /**
     * @param mouseDrag A mouse drag
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * This method draws the line the user is tracing with his mouse.
     * The method performs subcalls to pathCollides() to ensure the drawn line is not intersecting with itself or other lines.
     * The current drawing is removed if it violates the rules.
     */
    public void drawPath(MouseEvent mouseDrag) throws PathForcedToEnd, InvalidPath, CollisionException {
        Path pathTmp = new Path();
        pathTmp.getElements().add(new MoveTo(point.getX(), point.getY()));

        point = new Point((int) mouseDrag.getX(), (int) mouseDrag.getY());
        boolean isPointInsideNodeTemp = isPointInsideNode(point);

        //checks if point is inside the boundaries
        if (point.getX() < 0 || point.getX() > width || point.getY() < 0 || point.getY() > height) {
            throw new InvalidPath(path);
        }

        if (!isPointInsideNodeTemp && !leftStartNode) {
            leftStartNode = true;
            path.getElements().add(new MoveTo(point.getX(), point.getY()));
        } else if (isPointInsideNodeTemp && leftStartNode) {
            throw new PathForcedToEnd("Path forcefully ended at: " + point.getX() + ", " + point.getY());
        }

        pathTmp.getElements().add(new LineTo(point.getX(), point.getY()));

        if (pathCollides(pathTmp)){
            Path exceptionPath = new Path(List.copyOf(path.getElements()));
            path.getElements().clear();
            pathTmp.getElements().clear();
            isCollided = true;
            System.out.println("collision at " + point.getX() + ", " + point.getY());
            throw new CollisionException(exceptionPath);
        } else if (leftStartNode && !isPointInsideNodeTemp) {
            path.getElements().add(new LineTo(point.getX(), point.getY()));
            pathTmp.getElements().clear();
        }
    }

    /**
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * If turn was ended successfully then the drawn line is added to list of valid lines
     */
    public void finishPath(MouseEvent mouseEvent) throws InvalidPath, InvalidNode {
        Point point = new Point((int) mouseEvent.getX(), (int) mouseEvent.getY());
        Node endNode = findNodeFromPoint(point);
        pathStartNode.incNumberOfConnectingEdges(1);
        if (leftStartNode && endNode != null && endNode.getNumberOfConnectingEdges() < 3) {
            endNode.incNumberOfConnectingEdges(1);
            edges.add(path);
        } else if (endNode == null) {
            pathStartNode.decNumberOfConnectingEdges(1);
            Path tempPath = new Path(List.copyOf(path.getElements()));
            path.getElements().clear();
            throw new InvalidPath(tempPath);
        } else {
            //removes path from model
            pathStartNode.decNumberOfConnectingEdges(1);
            path.getElements().clear();
            throw new InvalidNode(endNode);
        }
    }

    public Node getCoordinates(PathElement pe) {

        // TODO: Wasn't there a non-string way to do this?

        String pathElemString = pe.toString();

        double x = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("x")+2, pathElemString.indexOf(",")));
        double y = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("y")+2, pathElemString.indexOf("]")));

        Node node = new Node(x,y,0, nodes.size());

        return node;
    }

    public Line getLineBetweenPathElements(List<PathElement> pathElements) {
        PathElement pe1 = pathElements.get(0);
        PathElement pe2 = pathElements.get(1);

        Node pathCoor1 = getCoordinates(pe1);
        Node pathCoor2 = getCoordinates(pe2);
        return getLineBetweenNodes(pathCoor1, pathCoor2);
    }

    public double[] getLineCoefficients(Line line) {
        double x1 = line.getStartX();
        double x2 = line.getEndX();
        double y1 = line.getStartY();
        double y2 = line.getEndY();
        double a = (y2-y1)/(x2-x1);
        double b = y2 - a * x2;

        double[] coefficients = {a,b};
        return coefficients;
    }

    /**
     * @author Thea Birk Berger
     * Checks for collision between an newly drawn edge and all exisiting edges on the gameboard
     * @param attemptedEdge
     * @return true if there is collision false otherwise
     */
    public boolean edgesCollides(Shape attemptedEdge) {
        boolean collision = false;

        // Traverse all existing lines
        for (Shape edge : edges) {
            // If the attempted and existing edge is a line
            if (attemptedEdge instanceof Line && edge instanceof Line) {
                // Get linear equation coefficients extending the attempted and existing edge
                double[] attemptCoeffs = getLineCoefficients((Line) attemptedEdge);
                double[] edgeCoeffs = getLineCoefficients((Line) edge);
                // Find intersection point between attempted and existing edge
                double x = (edgeCoeffs[1]-attemptCoeffs[1])/(attemptCoeffs[0]-edgeCoeffs[0]);
                double y = attemptCoeffs[0] * x + attemptCoeffs[1];
                collision = collision || attemptedEdge.getBoundsInLocal().contains(x,y) && edge.getBoundsInLocal().contains(x,y);
                // If either attempted or existing edge is a circle
            } else {
                collision = collision || edge.intersects(attemptedEdge.getLayoutBounds());
            }
        }
        return collision;
    }

    public boolean pathCollides(Path tmpPath) {

        Line tmpPathLine = getLineBetweenPathElements(tmpPath.getElements());

        int pathSize = path.getElements().size();
        int numberOfPathElementsToIgnore = 0;
        double pathLength = 0;

        for (int i = 1; i < pathSize-1; i++) {

            // Create line between every two path elements
            Line pathLine = getLineBetweenPathElements(path.getElements().subList(i-1,i+1));
            // Add line length to summed path length
            pathLength += getLineLength(pathLine.getStartX(), pathLine.getStartY(), pathLine.getEndX(), pathLine.getEndY());
            // While the path length remains short => ignore a path head element for collision
            numberOfPathElementsToIgnore += pathLength < 70 ? 1 : 0;
        }

        for (int i = 1; i < pathSize-1-numberOfPathElementsToIgnore; i++) {

            // Create line between every two path elements
            Line pathLine = getLineBetweenPathElements(path.getElements().subList(i-1,i+1));
            Bounds pathLineBounds = pathLine.getBoundsInLocal();

            // Check collision between most recently drawn and previously drawn path segments
            if (tmpPathLine.intersects(pathLineBounds)) {
                return true;
            }
        }

        // Check collision between existing canvas edges and most recently drawn path segment
        for (Shape edge : edges) {
            if (Shape.intersect(path, edge).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check is generated node collides with any preexisting nodes/paths
     * @param shape generated node
     * @return true if collision is detected else false
     * @author Sebastian Lund Jensen
     */
    public boolean shapeCollides(Shape shape, Node startNode, Node endNode) {
        //check collision with other nodes
        for (Node node : nodes) {
            if (shape.getBoundsInLocal().intersects(node.getShape().getBoundsInLocal()) &&
                    !(node.getId() == startNode.getId() || node.getId()==endNode.getId())) {
                return true;
            }
        }
        //check collision with all paths
        for (Shape edge : edges) {
            if (shape.getBoundsInLocal().intersects(edge.getBoundsInLocal())) {
                return true;
            }
        }
        return false;
    }

    public Path getPath() {
        return path;
    }

    public boolean getIsCollided() {
        return isCollided;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
    }
    public int getNumberOfEdges(Circle nodeToFind) {
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                return node.getNumberOfConnectingEdges();
            }
        }
        return -1; // Should never be reached
    }
    /**
     * @param nodeToFind The circle whose node object needs to be found
     * @author Noah Bastian Christiansen
     *  @return The node that whose shape is the given circle object
     */
    public Node findNode(Circle nodeToFind) {
        for (Node n : nodes) {
            if (n.getShape() == nodeToFind) {
                return n;
            }
        }
        return null;
    }

    /**
     * @param nodeToFind The circle whose name we want
     * @author Noah Bastian Christiansen
     * @return the name/number of the node whose shape is the given circle object.
     */
    public int findNameOfNode(Circle nodeToFind) {
        int nameOfNode = 0;
        int i = 0;
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                nameOfNode = i;
            }
            i++;
        }
        return nameOfNode;
    }

    /**
     * @author Sebastian Lund Jensen
     * @param point user mouseEvent
     * @return null if the point is not inside any node, else the node surrounding the point
     */
    public Node findNodeFromPoint(Point point) {
        for (Node node : nodes) {
            if (node.isPointInsideNode(point)) {
                return node;
            }
        }
        return null;
    }

    /**
     * @author Sebastian Lund Jensen
     * @param point user mouseEvent
     * @return true if point is inside node
     */
    public boolean isPointInsideNode(Point point) {
        return findNodeFromPoint(point) != null;
    }

    public void drawEdgeBetweenNodes(Circle startNode, Circle endNode) throws CollisionException {
        int nameOfStartNode = findNameOfNode(startNode);
        int nameOfEndNode = findNameOfNode(endNode);
        drawEdgeBetweenNodes(nameOfStartNode, nameOfEndNode);
    }

    public Shape createEdgeBetweenNodes(Circle startNode, Circle endNode) {
        if (startNode == endNode) {
            return createCircleToDraw(findNode(startNode));
        } else {
            return getLineBetweenNodeContours(startNode, endNode);
        }
    }

    public Node getNewestNode() {
        return nodes.get(nodes.size()-1);
    }

    public Shape getNewestEdge() {
        return edges.get(edges.size()-1);
    }

    public Line getLineBetweenNodeContours(Circle startNode, Circle endNode) {
        return getLineBetweenNodeContours(findNode(startNode), findNode(endNode));
    }

    public void changeTurns() {
        gameFlow.changeTurn();
    }

    public boolean hasNoRemainingLegalMoves() {
        return gameFlow.noRemainingLegalMoves(nodes);
    }

    public int getCurrentPlayer() {
        return gameFlow.getCurrentPlayer();

    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}


