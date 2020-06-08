package Model;

import Exceptions.CollisionException;
import Exceptions.IllegalNodesChosenException;
import Exceptions.PathForcedToEnd;
import Exceptions.InvalidPath;
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
    private Node pathStartNode;
    private boolean leftStartNode = false;


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

    public boolean hasNodeWithName(int name) {
        return name < nodes.size();
    }

    public boolean hasNodeWithName(Circle nodeToFind) {
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                return true;
            }
        }
        return false;
    }

    public Circle getNodeWithName(int name) throws IllegalNodesChosenException {
        return nodes.get(name).getShape();
    }

    public int getNumberOfEdges(int name) {
        return nodes.get(name).getNumberOfConnectingEdges();
    }

    public void drawEdgeBetweenNodes(int startNode, int endNode) throws CollisionException {

        if (startNode == endNode) {
            drawLineFromNodeToItself(startNode);
        } else {
            drawLineBetweenNodes(startNode, endNode);
        }
    }

    public void drawLineBetweenNodes(int startNodeName, int endNodeName) throws CollisionException {

        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);
        Line newLine = getLineBetweenNodes(startNode, endNode);

        if (shapeCollides(newLine)) {
            throw new CollisionException("Line collided with an exisiting line");
        } else {
            edges.add(newLine);
            addNodeOnLine(newLine);

            // Update number of connecting edges
            startNode.incNumberOfConnectingEdges(1);
            endNode.incNumberOfConnectingEdges(1);
            nodes.set(startNodeName, startNode);
            nodes.set(endNodeName, endNode);
        }
    }

    private Line getLineBetweenNodes(Node startNode, Node endNode) {
        Line newLine = new Line();
        newLine.setStartX(startNode.getX());
        newLine.setStartY(startNode.getY());
        newLine.setEndX(endNode.getX());
        newLine.setEndY(endNode.getY());

        /*System.out.println("startCoor: (" + newLine.getStartX() + "," + newLine.getStartY() + ")");
        System.out.println("EndCoor: (" + newLine.getEndX() + "," + newLine.getEndY() + ")");

        System.out.println(newLine.intersects(startNode.getShape().getLayoutBounds()));*/

        /*double c1 = startNode.getShape().getCenterX();
        double c2 = startNode.getShape().getCenterX();
        double hyp = startNode.getNodeRadius();
        double b = Math.abs(startNode.getY()-newLine.getStartY());
        double a = Math.sqrt(Math.pow(hyp,2) - Math.pow(b,2));
        double newStartX = a + c1;
        double newStartY = b + c2;


        c1 = endNode.getShape().getCenterX();
        c2 = endNode.getShape().getCenterX();
        hyp = endNode.getNodeRadius();
        b = Math.abs(endNode.getY()-newLine.getEndY());
        a = Math.sqrt(Math.pow(hyp,2) - Math.pow(b,2));
        double newEndX = a + c1;
        double newEndY = b + c2;

        newLine.setStartX(newStartX);
        newLine.setStartY(newStartY);
        newLine.setEndX(newEndX);
        newLine.setEndY(newEndY);

        System.out.println("startCoor: (" + newLine.getStartX() + "," + newLine.getStartY() + ")");
        System.out.println("EndCoor: (" + newLine.getEndX() + "," + newLine.getEndY() + ")");
        */
        return newLine;
    }

    public void drawLineFromNodeToItself(int nodeName) throws CollisionException {

        Node node = nodes.get(nodeName);
        Circle newCircle = createCircleToDraw(node);

        if (shapeCollides(newCircle)) {
            throw new CollisionException("Line collided with an exisiting line");
        } else {
            edges.add(newCircle);
            addNodeOnCircle(newCircle, node.getX(), node.getY());

            // Update number of connecting edges
            node.incNumberOfConnectingEdges(2);
            nodes.set(nodeName, node);
        }
    }

    private Circle createCircleToDraw(Node node) {
        Circle newCircle = new Circle();

        double radius = width / 50.;                                  // TODO: adjust to various window sizes
        double nodeX = node.getX();
        double nodeY = node.getY();

        Double[] center = getCircleCenterCoordinates(nodeX, nodeY, radius);
        newCircle.setCenterX(center[0]);
        newCircle.setCenterY(center[1]);
        newCircle.setRadius(radius);
        newCircle.setFill(Color.TRANSPARENT);
        newCircle.setStroke(Color.BLACK);

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
     * Adds a new node close to the midpoint of a valid line
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
    public void initializePath(MouseEvent mouseClick) throws InvalidPath {
        isCollided = false;
        leftStartNode = false;
        point = new Point((int) mouseClick.getX(), (int) mouseClick.getY());
        pathStartNode = findNodeFromPoint(point);
        if (pathStartNode != null && pathStartNode.getNumberOfConnectingEdges() < 3) {
            pathStartNode.incNumberOfConnectingEdges(1);
            path = new Path();
            path.getElements().add(new MoveTo(point.getX(), point.getY()));

        } else {
            throw new InvalidPath("The start node has too many connecting edges, or the path does not end in node");
        }
    }

    /**
     * @param mouseDrag A mouse drag
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * This method draws the line the user is tracing with his mouse.
     * The method performs subcalls to pathCollides() to ensure the drawn line is not intersecting with itself or other lines.
     * The current drawing is removed if it violates the rules.
     */

    public void drawPath(MouseEvent mouseDrag) throws PathForcedToEnd {
        if(isCollided){
            System.out.println("you collided draw somewhere else");
        }
        else {
            Path pathTmp = new Path();
            pathTmp.getElements().add(new MoveTo(point.getX(), point.getY()));
            point = new Point((int) mouseDrag.getX(), (int) mouseDrag.getY());
            if (!isPointInsideNode(point)) {
                leftStartNode = true;
            }
            pathTmp.getElements().add(new LineTo(point.getX(), point.getY()));
            if (pathCollides(pathTmp)){
                path.getElements().clear();
                pathTmp.getElements().clear();
                isCollided = true;
                pathStartNode.decNumberOfConnectingEdges(1);
                System.out.println("collision at " + point.getX() + ", " + point.getY());
            } else if (leftStartNode && isPointInsideNode(point)) {
                throw new PathForcedToEnd("Path forcefully ended at: " + point.getX() + ", " + point.getY());
            } else {
                path.getElements().add(new LineTo(point.getX(), point.getY()));
                pathTmp.getElements().clear();
            }
        }
    }
    /**
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * If turn was ended successfully then the drawn line is added to list of valid lines
     */
    public void finishPath(MouseEvent mouseEvent) throws InvalidPath {
        System.out.println("finish path");
        Point point = new Point((int) mouseEvent.getX(), (int) mouseEvent.getY());
        Node endNode = findNodeFromPoint(point);
        if (endNode != null && endNode.getNumberOfConnectingEdges() < 3) {
            endNode.incNumberOfConnectingEdges(1);
            edges.add(path);
        } else {
            //removes path from model
            pathStartNode.decNumberOfConnectingEdges(1);
            path.getElements().clear();
            throw new InvalidPath("The end node has too many connecting edges, or the path does not end in node");
        }
    }

    public Node getCoordinates(PathElement pe) {

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


    public boolean shapeCollides(Shape attemptedLine) {

        boolean collision = false;
        Bounds lineBounds = attemptedLine.getBoundsInLocal();

        for (Shape edge : edges) {
            collision = edge.intersects(lineBounds) || collision;
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
            pathLength += Math.max(pathLine.getBoundsInLocal().getWidth(), pathLine.getBoundsInLocal().getHeight());
            // While the path length remains short => ignore a path head element for collision
            numberOfPathElementsToIgnore += pathLength < 70 ? 1 : 0;
        }

        for (int i = 1; i < pathSize-1-numberOfPathElementsToIgnore; i++) {

            // Create line between every two path elements
            Line pathLine = getLineBetweenPathElements(path.getElements().subList(i-1,i+1));
            Bounds pathLineBounds = pathLine.getBoundsInLocal();

            // Check collision between most recently drawn and previously drawn path segments
//            if (Shape.intersect(tmpPathLine, pathLine).getBoundsInLocal().getWidth() != -1) {
//                return true;
//            }

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

