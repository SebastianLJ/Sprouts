package Model;

import Exceptions.CollisionException;
import Exceptions.InvalidNode;
import Exceptions.PathForcedToEnd;
import Exceptions.InvalidPath;
import javafx.scene.shape.*;

import javafx.scene.input.MouseEvent;

import java.util.*;


public class SproutModel {

    // Canvas related variables
    private List<Shape> edges;
    private List<Node> nodes;
    private double height;
    private double width;
    private static final int DISTANCE_BETWEEN_POINTS = 20;
    private static final int DISTANCE_FROM_BORDER = 20;

    // Drag-to-draw related variable
    private Path path;
    private boolean hasCollided;
    private Point point;
    private Node pathStartNode;
    private boolean leftStartNode = false;
    private PathFinder pf;

    // Classes with other model responsibilities
    public EdgeTools edgeTools;
    private GameFlow gameFlow;

    public SproutModel() {
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
        gameFlow = new GameFlow();
        pf = new PathFinder(this);
        edgeTools = new EdgeTools();
    }

    /**
     * Creates a certain amount of nodes with a random placement on the map.
     * The method wil ensure the created nodes will have a certain edge from the border of the map
     * and a certain distance from each other.
     * @author Emil Sommer Desler
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
            nodes.add(new Node(x, y, 0,i+1));
        }
    }

    private boolean invalidPointLocation(Circle circle) {
        for (Node node : nodes) {
            Shape intersect = Shape.intersect(circle, node.getShape());
            if (intersect.getBoundsInLocal().getWidth() != -1) {
                return true;
            }
            if (DISTANCE_BETWEEN_POINTS > edgeTools.distanceBetweenCircleCenter(node.getShape(), circle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Identifies if a circle or line should be drawn between two chosen nodes and initializes the appropriate process
     * @author Thea Birk Berger
     * @param startNode : Start node ID
     * @param endNode : End node ID
     * @throws CollisionException If the line drawn collides with itself or existing lines
     */
    private void drawEdgeBetweenNodes(int startNode, int endNode) throws CollisionException {
        if (startNode == endNode) {
            drawCircleFromNodeToItself(startNode);
        } else {
            drawLineBetweenNodes(startNode, endNode);
        }
    }

    /**
     * Identifies if a circle or line should be drawn between two chosen nodes and initializes the appropriate process
     * @author Thea Birk Berger
     * @param startNode : Start node shape
     * @param endNode : End node shape
     * @throws CollisionException If the line drawn collides with itself or existing lines
     */
    public void drawEdgeBetweenNodes(Circle startNode, Circle endNode) throws CollisionException {
        int nameOfStartNode = findNameOfNode(startNode);
        int nameOfEndNode = findNameOfNode(endNode);
        drawEdgeBetweenNodes(nameOfStartNode, nameOfEndNode);
    }

    /**
     * Gets edge between nodes without checking for collision
     * - used for drawing in view
     * @author Thea Birk Berger
     * @param startNode
     * @param endNode
     * @return
     */
    public Shape getIllegalEdgeBetweenNodes(Circle startNode, Circle endNode) {
        Node node1 = findNode(startNode);
        Node node2 = findNode(endNode);
        return edgeTools.createEdgeBetweenNodes(node1,node2);
    }

    /**
     * Adds a straight line between two nodes to the gameboard
     * @author Thea Birk Berger
     * @param startNodeName
     * @param endNodeName
     * @throws CollisionException If the line drawn collides with itself or existing lines
     */
    public void drawLineBetweenNodes(int startNodeName, int endNodeName) throws CollisionException {
        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);
        Line newLine = edgeTools.createLineBetweenNodeContours(startNode, endNode);
        Node newNode = getNewNodeForLine(startNode, endNode);
        int collidingNodeId = newEdgeCollidesWithExistingNodes(newLine, startNodeName, endNodeName);

        if (newEdgeCollidesWithExistingEdges(newLine)) {
            throw new CollisionException("The line collided with another line");
        } else if (collidingNodeId != -1) {
            throw new CollisionException("The line collided with node " + collidingNodeId);
        } else if (newNode == null) {
            throw new CollisionException("There is no space for a new node on this edge");
        } else {
            // Add edge to gameboard
            edges.add(newLine);
            // Add new node to gameboard
            nodes.add(newNode);
            // Update number of connecting edges for the two nodes
            startNode.incNumberOfConnectingEdges(1);
            endNode.incNumberOfConnectingEdges(1);
            nodes.set(startNodeName, startNode);
            nodes.set(endNodeName, endNode);
        }
    }

    /**
     * Adds a circular edge to the gameboard - connecting a node to itself
     * @author Thea Birk Berger
     * @param nodeName
     */
    public void drawCircleFromNodeToItself(int nodeName) throws CollisionException {
        Node node = nodes.get(nodeName);
        Circle newCircle = edgeTools.createCircleToDraw(node);
        Node newNode = getNewNodeForCircle(newCircle);
        int collidingNodeId = newEdgeCollidesWithExistingNodes(newCircle, nodeName, nodeName);

        if (newEdgeCollidesWithExistingEdges(newCircle)) {
            throw new CollisionException("The new line collided with another line");
        } else if (collidingNodeId != -1) {
            throw new CollisionException("The new line collided with node " + collidingNodeId);
        } else if (newNode == null) {
            throw new CollisionException("There is no space for a new node on this line");
        } else if (circleExceedsGameFrame(newCircle)) {
            throw new CollisionException("The line exceeds the game frame");
        } else {
            // Add edge to gameboard
            edges.add(newCircle);
            // Add new node to gameboard
            nodes.add(newNode);
            // Update number of connecting edges for the node
            node.incNumberOfConnectingEdges(2);
            nodes.set(nodeName, node);
        }
    }

    /**
     * Checks for collision between an newly drawn edge and all existing edges on the gameboard
     * @author Thea Birk Berger
     * @param attemptedEdge
     * @return true if there is collision, false otherwise
     */
    public boolean newEdgeCollidesWithExistingEdges(Shape attemptedEdge) {

        // Traverse all existing lines
        for (Shape edge : edges) {
            // If both the attempted and existing edge is a line
            if (attemptedEdge instanceof Line && edge instanceof Line
                    && Shape.intersect(attemptedEdge, edge).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
            // If the attempted edge is a line and the existing edge is a circle
            else if (attemptedEdge instanceof Line && edge instanceof Circle
                    && edgeTools.lineAndCircleCollide(attemptedEdge,edge)) {
                return true;
            }
            // If the attempted edge is a circle and the existing edge is a line
            else if (attemptedEdge instanceof Circle && edge instanceof Line
                    && edgeTools.lineAndCircleCollide(edge,attemptedEdge)) {
                return true;
            }
            // If both the attempted and existing edge is a circle
            else if (attemptedEdge instanceof Circle && edge instanceof Circle
                    && edgeTools.twoCirclesCollide((Circle) attemptedEdge, (Circle) edge)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks for collision between an newly drawn edge and all existing nodes on the gameboard
     * @author Thea Birk Berger
     * @param edge : The newly drawn edge
     * @param startNodeName : The start node chosen
     * @param endNodeName : The end node chosen
     * @return the node ID if there is collision with edge, -1 otherwise
     */
    private int newEdgeCollidesWithExistingNodes(Shape edge, int startNodeName, int endNodeName) {

        // Traverse all nodes that are not at the edge endpoints
        for (int i = 0; i < nodes.size(); i++) {
            if (i != startNodeName && i != endNodeName) {
                // If the attempted edge is a line
                if (edge instanceof Line && edgeTools.lineAndCircleCollide(edge, nodes.get(i).getShape())) {
                    return i+1;
                    // If the attempted edge is a circle
                } else if (edge instanceof Circle && edgeTools.twoCirclesCollide((Circle) edge, nodes.get(i).getShape())) {
                    return i+1;
                }
            }
        }
        return -1;
    }

    /**
     * Checks for self-collision of a newly drawn edge and collision with all existing edges
     * @author Thea Birk Berger
     * @param tmpPath
     * @return true if there is collision, false otherwise
     */
    public boolean pathSelfCollides(Path tmpPath) {

        Line tmpPathLine = edgeTools.getLineBetweenPathElements(tmpPath.getElements(), nodes.size());

        int pathSize = path.getElements().size();
        int numberOfPathElementsToIgnore = 0;
        double pathLength = 0;

        for (int i = 1; i < pathSize-1; i++) {
            // Create line between every two path elements
            Line pathLine = edgeTools.getLineBetweenPathElements(path.getElements().subList(i-1,i+1), nodes.size());
            // Add line length to summed path length
            pathLength += edgeTools.getDistanceBetweenTwoPoints(pathLine.getStartX(), pathLine.getStartY(), pathLine.getEndX(), pathLine.getEndY());
            // While the path length remains short => ignore a path head element for collision
            numberOfPathElementsToIgnore += pathLength < 50 ? 1 : 0;
        }

        for (int i = 1; i < pathSize-1-numberOfPathElementsToIgnore; i++) {
            // Create line between every two path elements
            Line pathLine = edgeTools.getLineBetweenPathElements(path.getElements().subList(i-1,i+1), nodes.size());
            // Check collision between most recently drawn and previously drawn path segments
            if (Shape.intersect(tmpPathLine, pathLine).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }

        return false;
    }

    public boolean pathCollidesWithOtherEdges(Path tmpPath) {

        // Check collision between existing canvas edges and most recently drawn path segment
        for (Shape edge : edges) {
            if (Shape.intersect(path, edge).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        return false;
    }

    public boolean circleExceedsGameFrame(Circle edge) {
        return (edge.getCenterX() - edge.getRadius() < 0 ||
                edge.getCenterY() - edge.getRadius() < 0 ||
                edge.getCenterX() + edge.getRadius() > width ||
                edge.getCenterY() + edge.getRadius() > height);
    }

    /**
     * Check is generated node collides with any preexisting nodes/paths
     * @param node generated node
     * @return true if collision is detected else false
     * @author Sebastian Lund Jensen
     */
    public boolean nodeCollides(Node node) {
        //check collision with all paths except the path it is generated on
        for (int i = 0; i < edges.size()-1; i++) {
            if (Shape.intersect(node.getShape(), edges.get(i)).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        //check collision with other nodes
        for (int i = 0; i < nodes.size(); i++) {
            if (Shape.intersect(node.getShape(),nodes.get(0).getShape()).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new node to appear on new line
     * and adjusts its position so that there is no collision with existing nodes or edges.
     * Returns null if no such node can be found.
     * @author Thea Birk Berger
     * @param startNode : the new edge start point
     * @param endNode : the new edge end point
     * @return non-colliding node if one exists, null otherwise
     */
    private Node getNewNodeForLine(Node startNode, Node endNode) {

        double edgeLength = edgeTools.getDistanceBetweenTwoPoints(startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY());
        double r = startNode.getNodeRadius();
        double d = edgeLength/2; // Initial node position - line midpoint
        Node newNode;

        boolean nodeCollision;

        do {
            // Define new node with distance d from the line start point
            double newNodeX = edgeTools.getPointOnLine(startNode.getX(), endNode.getX(), d, edgeLength);
            double newNodeY = edgeTools.getPointOnLine(startNode.getY(), endNode.getY(), d, edgeLength);
            newNode = new Node(newNodeX, newNodeY, 2, nodes.size()+1);

            nodeCollision = false;

            // If there is collision => change position d of new node
            if (newNodeCollidesWithExistingNodes(newNode) || newNodeCollidesWithExistingEdges(newNode)) {
                // If the new node has reached the end of the line
                if (edgeTools.twoCirclesCollide(newNode.getShape(), endNode.getShape())) {
                    // Move new node to beginning of line
                    d = r + (r/10);
                } else {
                    // Move new node further towards line end point
                    d += 2 * r + (r/10);
                }
                nodeCollision = true;
            }

            // If the new node is back at the starting point
            if (nodeCollision && d >= edgeLength/2 - (r * 2) && d <= edgeLength/2 + (r * 2)) {
                return null;
            }

        } while (nodeCollision);

        return newNode;
    }

    /**
     * Creates a new node to appear on new circular edge
     * and adjusts its position so that there is no collision with existing nodes or edges.
     * Returns null if no such node can be found.
     * @author Thea Birk Berger
     * @param edge : the new edge
     * @return non-colliding node if one exists, null otherwise
     */
    public Node getNewNodeForCircle(Circle edge) {

        int angle = 0;   // Initial node position - on circle top point
        Node newNode;
        boolean nodeCollision;

        do {
            double newNodeX = edge.getCenterX() + edge.getRadius() * Math.sin(angle);
            double newNodeY = edge.getCenterY() + edge.getRadius() * Math.cos(angle);
            newNode = new Node(newNodeX, newNodeY, 2, nodes.size()+1);

            nodeCollision = false;

            // If there is collision => change angle to relocate new node
            if (newNodeCollidesWithExistingNodes(newNode)
                    || newNodeCollidesWithExistingEdges(newNode)
                    || circleExceedsGameFrame(newNode.getShape())) {
                nodeCollision = true;
                // Increment angle
                angle += newNode.getNodeRadius() + (newNode.getNodeRadius()/10);

                // If the new node is back at the starting point
                if (angle >= 360) {
                    return null;
                }
            }
        } while (nodeCollision);

        return newNode;
    }


    /**
     * Creates a new node to appear on a newly drawn edge
     * and adjusts its position so that there is no collision with existing nodes or edges.
     * Returns null if no such node can be found.
     * The new node is allowed to overlap other parts of its own edge.
     * @author Thea Birk Berger
     * @return non-colliding node if one exists, null otherwise
     */
    public Node getNewNodeForDrawnLine() {

        int pathSize = path.getElements().size();
        int d = pathSize/2;  // Initial node position - mid path element
        Node newNode;
        boolean nodeCollision;

        do {
            LineTo pathElem = (LineTo) (path.getElements().get(d));
            newNode = new Node(pathElem.getX(), pathElem.getY(), 2, nodes.size()+1);
            nodeCollision = false;

            // If there is collision => chose new path element for the node position
            if (newNodeCollidesWithExistingNodes(newNode) || newNodeCollidesWithExistingEdges(newNode)) {
                nodeCollision = true;

                // If the new node has reached the end of path => place node on path beginning
                d = d+1 < pathSize ? d+1 : 1;

                // If the new node is back on the middle of the line
                if (d == path.getElements().size()/2) {
                    return null;
                }
            }

        } while (nodeCollision);

        return newNode;
    }

    /**
     * Checks if the input node collides with any existing nodes on the gameboard
     * @author Thea Birk Berger
     * @param newNode : A suggested new node to appear on a new edge
     * @return true for collision, false otherwise
     */
    private boolean newNodeCollidesWithExistingNodes(Node newNode) {

        // Traverse through all existing nodes on the gameboard
        for (Node node : nodes) {
            // If new node collides with existing node
            if (edgeTools.twoCirclesCollide(node.getShape(), newNode.getShape())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the input node collides with any existing edges on the gameboard
     * @author Thea Birk Berger
     * @param newNode : A suggested new node to appear on a new edge
     * @return true for collision, false otherwise
     */
    private boolean newNodeCollidesWithExistingEdges(Node newNode) {

        // Traverse through all existing edges on the gameboard
        for (Shape edge: edges) {
            // If the new node collides with existing edge
            if ((edge instanceof Line && edgeTools.lineAndCircleCollide(edge, newNode.getShape()))
                    || (edge instanceof Circle && edgeTools.twoCirclesCollide((Circle) edge, newNode.getShape()))) {
                return true;
            }

            if (edge instanceof Path) {
                for (int i = 1; i < ((Path) edge).getElements().size()-1; i++) {
                    // Create line between every two path elements
                    Line pathLine = edgeTools.getLineBetweenPathElements(((Path) edge).getElements().subList(i - 1, i + 1), nodes.size());

                    if (Shape.intersect(newNode.getShape(), pathLine).getBoundsInLocal().getWidth() != -1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Sets up path object and sets coordinates for starting point of drawing (the position on the pane where the click occured)
     * @param mouseClick A mouse click
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     */
    public void initializePath(MouseEvent mouseClick) throws InvalidNode {
        hasCollided = false;
        leftStartNode = false;
        point = new Point((int) mouseClick.getX(), (int) mouseClick.getY());
        pathStartNode = findNodeFromPoint(point);
        if (pathStartNode != null && pathStartNode.getNumberOfConnectingEdges() < 3) {
            path = new Path();
        } else {
            InvalidNode invalidNode = new InvalidNode("This node already has 3 connecting lines");
            invalidNode.setNode(pathStartNode);
            throw invalidNode;
        }
    }

    /**
     * This method draws the line the user is tracing with his mouse.
     * The method performs subcalls to pathSelfCollides() to ensure the drawn line is not intersecting with itself or other lines.
     * The current drawing is removed if it violates the rules.
     * @param mouseDrag A mouse drag
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     */
    public void drawPath(MouseEvent mouseDrag) throws PathForcedToEnd, InvalidPath, CollisionException {
        // Convert current mouse position to a path element
        Path pathTmp = new Path();
        pathTmp.getElements().add(new MoveTo(point.getX(), point.getY()));

        // 
        point = new Point((int) mouseDrag.getX(), (int) mouseDrag.getY());
        boolean isPointInsideNodeTemp = isPointInsideNode(point);

        // If drawing exceeds the canvas frame
        if (point.getX() < 0 || point.getX() > width || point.getY() < 0 || point.getY() > height) {
            InvalidPath invalidPath = new InvalidPath("The line cannot exceed the game frame");
            invalidPath.setPath(path);
            throw invalidPath;
        }

        // If drawing has left the start node
        if (!isPointInsideNodeTemp && !leftStartNode) {
            // Get start node contour point and add to path (close path start gap)
            double[] contourPoint = edgeTools.getContourPoint(pathStartNode.getX(),pathStartNode.getY(),point.getX(),point.getY(),pathStartNode.getNodeRadius());
            path.getElements().add(new MoveTo(contourPoint[0],contourPoint[1]));
            leftStartNode = true;
        // If drawing has reached an end node
        } else if (isPointInsideNodeTemp && leftStartNode) {
            // Get end node contour point and add to path (close path end gap)
            Node endNode = findNodeFromPoint(point);
            double[] contourPoint = edgeTools.getContourPoint(endNode.getX(),endNode.getY(),point.getX(),point.getY(),endNode.getNodeRadius());
            path.getElements().add(new LineTo(contourPoint[0],contourPoint[1]));
            System.out.println("Path forcefully ended at: " + contourPoint[0] + ", " + contourPoint[1]);
            throw new PathForcedToEnd("Path forcefully ended at: " + contourPoint[0] + ", " + contourPoint[1]);
        }
        // Save current mouse position in temporary path
        pathTmp.getElements().add(new LineTo(point.getX(), point.getY()));

        boolean selfCollision = pathSelfCollides(pathTmp);
        boolean edgeCollision = pathCollidesWithOtherEdges(pathTmp);

        if (selfCollision || edgeCollision) {
            Path exceptionPath = new Path(List.copyOf(path.getElements()));
            path.getElements().clear();
            pathTmp.getElements().clear();
            hasCollided = true;
            System.out.println("collision at " + point.getX() + ", " + point.getY());

            // Set and throw exception
            CollisionException collisionException = new CollisionException(selfCollision ? "The line cannot cross itself" : "The line collided with another line");
            collisionException.setPath(exceptionPath);
            throw collisionException;
        } else if (leftStartNode && !isPointInsideNodeTemp) {
            path.getElements().add(new LineTo(point.getX(), point.getY()));
            pathTmp.getElements().clear();
        }
    }

    /**
     * If turn was ended successfully then the drawn line is added to list of valid lines
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     */
    public void finishPath(MouseEvent mouseEvent) throws InvalidPath, InvalidNode {
        Point point = new Point((int) mouseEvent.getX(), (int) mouseEvent.getY());
        Node endNode = findNodeFromPoint(point);
        pathStartNode.incNumberOfConnectingEdges(1);
        Node newNode = getNewNodeForDrawnLine();
        if (leftStartNode && endNode != null && endNode.getNumberOfConnectingEdges() < 3 && newNode != null) {
            endNode.incNumberOfConnectingEdges(1);
            edges.add(path);
            nodes.add(newNode);
        } else if (endNode == null || newNode == null) {
            pathStartNode.decNumberOfConnectingEdges(1);
            Path tempPath = new Path(List.copyOf(path.getElements()));
            path.getElements().clear();
            // Set and throw exception
            InvalidPath invalidPath = new InvalidPath("There must be space on the line for a new node");
            invalidPath.setPath(tempPath);
            throw invalidPath;
        } else {
            // Remove path from model
            pathStartNode.decNumberOfConnectingEdges(1);
            path.getElements().clear();
            // Set and throw exception
            InvalidNode invalidNode = new InvalidNode("This node already has 3 connecting lines");
            invalidNode.setNode(endNode);
            throw invalidNode;
        }
        pf.initGrid();
        System.out.println(pf);

    }

    public int getNumberOfEdgesAtNode(Circle nodeToFind) {
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                return node.getNumberOfConnectingEdges();
            }
        }
        return -1; // Should never be reached
    }

    /**
     * Given a Circle object this method finds the corresponding node object.
     * @param nodeToFind The circle whose node object needs to be found
     * @author Noah Bastian Christiansen
     * @return The node that whose shape is the given circle object
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
     * Given a Circle object it finds the id of the corresponding node.
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

    public Path getMostRecentlyDrawnPath() {
        return path;
    }

    public boolean hasNewestPathCollided() {
        return hasCollided;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWidth(double width) {
        this.width = width;
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

    public boolean hasNode(int id) {
        return id < nodes.size();
    }

    /**
     * Checks if a node is on the gameboard
     * @author Thea Birk Berger
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

    public Circle getNodeFromId(int id) {
        return nodes.get(id).getShape();
    }

    public int getNumberOfEdgesAtNode(int id) {
        return nodes.get(id).getNumberOfConnectingEdges();
    }

    /**
     * @author Sebastian Lund Jensen
     * @param point user mouseEvent
     * @return true if point is inside node
     */
    public boolean isPointInsideNode(Point point) {
        return findNodeFromPoint(point) != null;
    }

    public Node getNewestNode() {
        return nodes.get(nodes.size()-1);
    }

    public Shape getNewestEdge() {
        return edges.get(edges.size()-1);
    }

    public void changeTurns() {
        gameFlow.changeTurn();
    }

    public boolean hasNoRemainingLegalMoves() {
        return gameFlow.noRemainingLegalMovesSimpleGame(nodes);
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

}

