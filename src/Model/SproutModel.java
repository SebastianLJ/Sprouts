package Model;

import Exceptions.IllegalNodesChosenException;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;

import javafx.scene.input.MouseEvent;

import java.awt.geom.PathIterator;
import java.util.*;

public class SproutModel {
    private static final int DISTANCE_BETWEEN_POINTS = 20;
    private static final int DISTANCE_FROM_BORDER = 20;
    private ArrayList<Shape> lines = new ArrayList<>();
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

    public void drawEdgeBetweenNodes(int startNode, int endNode) {

        if (startNode == endNode) {
            drawCircleFromNodeToItself(startNode);
        } else {
            drawLineBetweenNodes(startNode, endNode);
        }
        // TODO: check for collision
        // slet fra database in case (nodes list, edges list)
    }

    public void drawLineBetweenNodes(int startNodeName, int endNodeName) {

        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);
        Line newLine = createLineToDraw(startNode, endNode);

        edges.add(newLine);
        addNodeOnLine(newLine);

        // Update number of connecting edges
        startNode.incNumberOfConnectingEdges(1);
        endNode.incNumberOfConnectingEdges(1);
        nodes.set(startNodeName, startNode);
        nodes.set(endNodeName, endNode);
    }

    private Line createLineToDraw(Node startNode, Node endNode) {
        Line newLine = new Line();
        newLine.setStartX(startNode.getX());
        newLine.setStartY(startNode.getY());
        newLine.setEndX(endNode.getX());
        newLine.setEndY(endNode.getY());

        return newLine;
    }

    public void drawCircleFromNodeToItself(int nodeName) {

        Node node = nodes.get(nodeName);
        Circle newCircle = createCircleToDraw(node);

        edges.add(newCircle);
        addNodeOnCircle(newCircle, node.getX(), node.getY());

        // Update number of connecting edges
        node.incNumberOfConnectingEdges(2);
        nodes.set(nodeName, node);
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
            if (doPathsCollide(pathTmp)){
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

    public boolean doPathsCollide(Path pathTmp) {

        Shape test = Shape.intersect(pathTmp, path);
        Path test3 = (Path) test;


        if (test3.getElements().size()!=0) {
            return true;
        }
        for (Shape line : edges) {
            if (Shape.intersect(path, line).getBoundsInLocal().getWidth() != -1) {
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

    public void drawEdgeBetweenNodes(Circle startNode, Circle endNode) {
        int nameOfStartNode = findNameOfNode(startNode);
        int nameOfEndNode = findNameOfNode(endNode);
        drawEdgeBetweenNodes(nameOfStartNode, nameOfEndNode);
    }

    public Shape createEdgeBetweenNodes(Circle startNode, Circle endNode) {
        if (startNode == endNode) {
            return createCircleToDraw(findNode(startNode));
        } else {
            return createLineToDraw(startNode, endNode);
        }
    }

    public Circle getNewestNode() {
        return nodes.get(nodes.size()-1).getShape();
    }

    public Shape getNewestEdge() {
        return edges.get(edges.size()-1);
    }

    public Line createLineToDraw(Circle startNode, Circle endNode) {
        return createLineToDraw(findNode(startNode), findNode(endNode));
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

