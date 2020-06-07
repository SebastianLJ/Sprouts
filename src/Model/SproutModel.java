package Model;

import Exceptions.CollisionException;
import Exceptions.IllegalNodesChosenException;
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
    private final static double COLLISION_WIDTH = 1.5;
    private boolean isCollided;
    private Point point;
    private GameFlow gameFlow;


    public SproutModel() {
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
        gameFlow = new GameFlow();
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
        circle.setRadius(5); // TODO make scalable

        for (int i = 0; i < amount; i++) {
            do {
                x = random.nextInt((int) width - 2*DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                y = random.nextInt((int) height - 2*DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                circle.setCenterX(x);
                circle.setCenterY(y);
            } while (invalidPointLocation(circle));
            nodes.add(new Node(x, y, 0));
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
        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);
        Line newLine = getLineBetweenNodes(startNode, endNode);

        if (LineCollides(newLine)) {
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
        }
    }

    /**
     * @author Thea Birk Berger
     * Creates a straight line between two given nodes
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

        // TODO: Remove part of line that is held within the nodes

        return newLine;
    }

    /**
     * @author Thea Birk Berger
     * Adds a cicular edge to the gameboard - connecting a node to itself
     * @param nodeName
     */
    public void drawCircleFromNodeToItself(int nodeName) {
        Node node = nodes.get(nodeName);
        Circle newCircle = createCircleToDraw(node);

        // Add edge to gameboard
        edges.add(newCircle);
        // Add new node mid edge
        addNodeOnCircle(newCircle, node.getX(), node.getY());
        // Update number of connecting edges for the node
        node.incNumberOfConnectingEdges(2);
        nodes.set(nodeName, node);
    }

    /**
     * @author Thea Birk Berger
     * Creates a circular edge from a node to itself
     * @param node
     * @return a Circle object
     */
    private Circle createCircleToDraw(Node node) {
        Circle newCircle = new Circle();

        double radius = width / 50.;    // TODO: adjust to various window sizes
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

        Node newNode = new Node(newNodeX, newNodeY, 2);
        nodes.add(newNode);
    }
    /**
     * @author Noah Bastian Christiansen
     */
    public void addNodeOnLineDrag(){
        int size = path.getElements().size();
        LineTo test = (LineTo) (path.getElements().get(size/2));
        Node newNode = new Node(test.getX(), test.getY(), 2);
        nodes.add(newNode);
    }

    public void addNodeOnCircle(Circle edge, double originNodeX, double originNodeY) {

        double newNodeX = originNodeX + (edge.getCenterX() - originNodeX) * 2;
        double newNodeY = originNodeY + (edge.getCenterY() - originNodeY) * 2;

        Node newNode = new Node(newNodeX, newNodeY, 2);
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
     * @author Noah Bastian Christiansen
     */
    public void initializePath(MouseEvent event) {
        isCollided = false;
        point = new Point((int) event.getX(), (int) event.getY());
        path = new Path();
        path.getElements().add(new MoveTo(point.getX(), point.getY()));
    }

    /**
     * @author Noah Bastian Christiansen
     */
    public void drawPath(MouseEvent event) {
        if(isCollided){
            System.out.println("you collided draw somewhere else");
        }
        else {
            Path pathTmp = new Path();
            pathTmp.getElements().add(new MoveTo(point.getX(), point.getY()));
            point = new Point((int) event.getX(), (int) event.getY());
            pathTmp.getElements().add(new LineTo(point.getX(), point.getY()));
            if (doesPathCollide(pathTmp)){
                path.getElements().clear();
                pathTmp.getElements().clear();
                isCollided = true;
                System.out.println("collision at " + point.getX() + ", " + point.getY());
            } else {
                path.getElements().add(new LineTo(point.getX(), point.getY()));
                pathTmp.getElements().clear();
            }
        }
    }

    /**
     * @author Noah Bastian Christiansen
     */
    public void finishPath(){
        edges.add(path);
    }

    public Node getCoordinates(PathElement pe) {

        // TODO: Wasn't there a non-string way to do this?

        String pathElemString = pe.toString();

        double x = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("x")+2, pathElemString.indexOf(",")));
        double y = Double.parseDouble(pathElemString.substring(pathElemString.indexOf("y")+2, pathElemString.indexOf("]")));

        Node node = new Node(x,y,0);

        return node;
    }

    public Line getLineBetweenPathElements(List<PathElement> pathElements) {

        PathElement pe1 = pathElements.get(0);
        PathElement pe2 = pathElements.get(1);

        Node pathCoor1 = getCoordinates(pe1);
        Node pathCoor2 = getCoordinates(pe2);
        return getLineBetweenNodes(pathCoor1, pathCoor2);
    }

    public boolean LineCollides(Line attemptedLine) {

        boolean collision = false;
        Bounds lineBounds = attemptedLine.getBoundsInLocal();

        for (Shape edge : edges) {
            collision = edge.intersects(lineBounds) || collision;
        }

        return collision;
    }

    public boolean doesPathCollide(Path tmpPath) {

        Line tmpPathLine = getLineBetweenPathElements(tmpPath.getElements());

        int pathSize = path.getElements().size();
        int numberOfPathElementsToIgnore = 0;
        double pathLength = 0;

        for (int i = 1; i < pathSize-1; i++) {

            // Create line between every two path elements
            Line pathLine = getLineBetweenPathElements(path.getElements().subList(i-1,i+1));
            // Add line length to summed path length
            pathLength += Math.max(pathLine.getBoundsInLocal().getWidth(), pathLine.getBoundsInLocal().getHeight());
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

    public Node findNode(Circle nodeToFind) {
        for (Node n : nodes) {
            if (n.getShape() == nodeToFind) {
                return n;
            }
        }
        return null;
    }

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

    public void drawEdgeBetweenNodes(Circle startNode, Circle endNode) throws CollisionException {
        int nameOfStartNode = findNameOfNode(startNode);
        int nameOfEndNode = findNameOfNode(endNode);
        drawEdgeBetweenNodes(nameOfStartNode, nameOfEndNode);
    }

    public Shape createEdgeBetweenNodes(Circle startNode, Circle endNode) {
        if (startNode == endNode) {
            return createCircleToDraw(findNode(startNode));
        } else {
            return getLineBetweenNodes(startNode, endNode);
        }
    }

    public Circle getNewestNode() {
        return nodes.get(nodes.size()-1).getShape();
    }

    public Shape getNewestEdge() {
        return edges.get(edges.size()-1);
    }

    public Line getLineBetweenNodes(Circle startNode, Circle endNode) {
        return getLineBetweenNodes(findNode(startNode), findNode(endNode));
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

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }
}

