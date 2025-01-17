package Model;

import Utility.Exceptions.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SproutModel {

    // Game board
    private List<Shape> edges;
    private List<Node> nodes;

    // Canvas related variables
    private double height;
    private double width;
    private static final int DISTANCE_BETWEEN_POINTS = 20;
    private static final int DISTANCE_FROM_BORDER = 20;

    // Drag-to-draw related variables
    private Path path;
    private boolean hasCollided;
    private Point mousePosition;
    private Node pathStartNode;
    private boolean leftStartNode = false;

    // Path finder related variables
    private PathFinder pf;
    private List<Node> failedNodes;

    // Classes with other model responsibilities
    private EdgeTools edgeTools;
    private GameFlow gameFlow;

    public SproutModel() {
        edges = new ArrayList<>();
        nodes = new ArrayList<>();
        failedNodes = new ArrayList<>();
        gameFlow = new GameFlow();
        pf = new PathFinder(this);
        edgeTools = new EdgeTools();
    }

    /**
     * Creates a certain amount of nodes with a random placement on the map.
     * The method wil ensure the created nodes will have a certain edge from the border of the map
     * and a certain distance from each other.
     *
     * @param amount number of nodes to create
     * @author Emil Sommer Desler
     */
    public void addRandomNodes(int amount) {
        Random random = new Random();

        int x;
        int y;
        Circle circle = new Circle();
        circle.setRadius(Node.radius);

        for (int i = 0; i < amount; i++) {
            do {
                x = random.nextInt((int) width - 2 * DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                y = random.nextInt((int) height - 2 * DISTANCE_FROM_BORDER) + DISTANCE_FROM_BORDER;
                circle.setCenterX(x);
                circle.setCenterY(y);
            } while (invalidPointLocation(circle));
            nodes.add(new Node(x, y, 0, i + 1));
        }
    }

    private boolean invalidPointLocation(Circle circle) {
        for (Node node : nodes) {
            Shape intersect = Shape.intersect(circle, node.getShape());
            if (intersect.getBoundsInLocal().getWidth() != -1) {
                return true;
            }
            if (DISTANCE_BETWEEN_POINTS > (int) edgeTools.distanceBetweenCircleCenter(node.getShape(), circle)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Identifies if a circle or line should be drawn between two chosen nodes and initializes the appropriate process
     *
     * @param startNode : Start node ID
     * @param endNode : End node ID
     * @param validEdgeCheck : True if the method is used to check if the edge is valid without drawing it, false otherwise
     * @throws CollisionException If the line drawn collides with itself or existing lines
     * @author Thea Birk Berger
     */
    private void drawEdgeBetweenNodes(int startNode, int endNode, boolean validEdgeCheck) throws CollisionException {
        if (startNode == endNode) {
            drawCircleFromNodeToItself(startNode, validEdgeCheck);
        } else {
            drawLineBetweenNodes(startNode, endNode, validEdgeCheck);
        }
    }

    /**
     * Checks if there is a legal move left in the game.
     * If yes, the turn changes, otherwise a GameEnded exception is thrown.
     *
     * @param smartMode : True if the game is in smart mode, false otherwise
     * @throws GameEndedException if there are no valid edges left to draw
     * @author Thea Birk Berger
     */
    public void updateGameState(boolean smartMode) throws GameEndedException {
        if (!atLeastOneRemainingLegalMove(smartMode)) {
            GameEndedException gameEnded = new GameEndedException(gameFlow.getGameResponseText());
            gameEnded.setWinner(gameFlow.getCurrentPlayer());
            throw gameEnded;
        } else {
            gameFlow.changeTurn();
        }
    }

    /**
     * Identifies if a circle or line should be drawn between two chosen nodes and initializes the appropriate process
     *
     * @param startNode : Start node shape
     * @param endNode : End node shape
     * @param validEdgeCheck
     * @throws CollisionException If the line drawn collides with itself or existing lines
     * @author Thea Birk Berger
     */
    public void drawEdgeBetweenNodes(Circle startNode, Circle endNode, boolean validEdgeCheck) throws CollisionException {
        int nameOfStartNode = findNameOfNode(startNode);
        int nameOfEndNode = findNameOfNode(endNode);
        drawEdgeBetweenNodes(nameOfStartNode, nameOfEndNode, validEdgeCheck);
    }

    /**
     * Gets edge between nodes without checking for collision
     * - used for drawing in view
     *
     * @param startNode
     * @param endNode
     * @return
     * @author Thea Birk Berger
     */
    public Shape getIllegalEdgeBetweenNodes(Circle startNode, Circle endNode) {
        Node node1 = findNode(startNode);
        Node node2 = findNode(endNode);
        return edgeTools.createEdgeBetweenNodes(node1, node2);
    }

    /**
     * Adds a straight line between two nodes to the gameboard
     *
     * @param startNodeName : Name of start node
     * @param endNodeName : Name of end node
     * @param validEdgeCheck : True if the method is used to check if the edge is valid without drawing it, false otherwise
     * @throws CollisionException If the line drawn collides with itself or existing lines
     * @author Thea Birk Berger
     */
    public void drawLineBetweenNodes(int startNodeName, int endNodeName, boolean validEdgeCheck) throws CollisionException {
        Node startNode = nodes.get(startNodeName);
        Node endNode = nodes.get(endNodeName);
        Line newLine = edgeTools.createLineBetweenNodeContours(startNode, endNode);
        Node newNode = getNewNodeForLine(startNode, endNode);
        int collidingNodeId = newEdgeCollidesWithExistingNodes(newLine, startNodeName, endNodeName);

        // If edge is invalid
        if (newEdgeCollidesWithExistingEdges(newLine)) {
            throw new CollisionException("The new line collided with another line");
        } else if (collidingNodeId != -1) {
            throw new CollisionException("The new line collided with node " + collidingNodeId);
        } else if (newNode == null) {
            throw new CollisionException("There is no space for a new node on this edge");
        // If edge is valid
        } else if (!validEdgeCheck) {
            // Add new edge to gameboard
            edges.add(newLine);
            // Add new node to gameboard
            nodes.add(newNode);
            // Update number of connecting edges for the two nodes
            startNode.incNumberOfConnectingEdges(1);
            endNode.incNumberOfConnectingEdges(1);
        }
    }

    public void drawSmartEdge(Circle startNodeCircle, Circle endNodeCircle, boolean validEdgeCheck) throws NoValidEdgeException, InvalidPath {
        int nameOfStartNode = findNameOfNode(startNodeCircle);
        int nameOfEndNode = findNameOfNode(endNodeCircle);

        Node startNode = nodes.get(nameOfStartNode);
        Node endNode = nodes.get(nameOfEndNode);

        path = startNodeCircle == endNodeCircle ? pf.getLoopPath(startNode) : pf.findPath(startNode, endNode);
        Node newNode = getNewNodeForPath(path);

        // If edge is invalid
        if (newNode == null) {
            InvalidPath invalidPath = new InvalidPath("There is no space for a new node on this edge");
            invalidPath.setPath(path);
            throw invalidPath;
        // If edge is valid => add to model
        } else if (!validEdgeCheck) {
            // Update model
            edges.add(path);
            nodes.add(newNode);
            startNode.incNumberOfConnectingEdges(1);
            endNode.incNumberOfConnectingEdges(1);
        }
    }

    /**
     * Adds a circular edge to the gameboard - connecting a node to itself
     *
     * @param nodeName : Name of node to self-connect
     * @param validEdgeCheck : True if the method is used to check if the edge is valid without drawing it, false otherwise
     * @author Thea Birk Berger
     */
    private void drawCircleFromNodeToItself(int nodeName, boolean validEdgeCheck) throws CollisionException {
        Node node = nodes.get(nodeName);
        Circle newCircle = edgeTools.createCircleToDraw(node);
        Node newNode = getNewNodeForCircle(newCircle);
        int collidingNodeId = newEdgeCollidesWithExistingNodes(newCircle, nodeName, nodeName);

        // If edge is invalid
        if (newEdgeCollidesWithExistingEdges(newCircle)) {
            throw new CollisionException("The new line collided with another line");
        } else if (collidingNodeId != -1) {
            throw new CollisionException("The new line collided with node " + collidingNodeId);
        } else if (circleExceedsGameFrame(newCircle)) {
            throw new CollisionException("The new line exceeds the game frame");
        }  else if (newNode == null) {
            throw new CollisionException("There is no space for a new node on this line");
        // If edge is valid
        } else if (!validEdgeCheck) {
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
     *
     * @param attemptedEdge : The newly drawn edge
     * @return true if there is collision, false otherwise
     * @author Thea Birk Berger
     */
    private boolean newEdgeCollidesWithExistingEdges(Shape attemptedEdge) {

        // Traverse all existing lines
        for (Shape edge : edges) {
            // If both the attempted and existing edge is a line
            if (attemptedEdge instanceof Line && edge instanceof Line
                    && Shape.intersect(attemptedEdge, edge).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
            // If the attempted edge is a line and the existing edge is a circle
            else if (attemptedEdge instanceof Line && edge instanceof Circle
                    && edgeTools.lineAndCircleCollide(attemptedEdge, edge)) {
                return true;
            }
            // If the attempted edge is a circle and the existing edge is a line
            else if (attemptedEdge instanceof Circle && edge instanceof Line
                    && edgeTools.lineAndCircleCollide(edge, attemptedEdge)) {
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
     *
     * @param edge          : The newly drawn edge
     * @param startNodeName : The start node chosen
     * @param endNodeName   : The end node chosen
     * @return the node ID if there is collision with edge, -1 otherwise
     * @author Thea Birk Berger
     */
    private int newEdgeCollidesWithExistingNodes(Shape edge, int startNodeName, int endNodeName) {

        // Traverse all nodes that are not at the edge endpoints
        for (int i = 0; i < nodes.size(); i++) {
            if (i != startNodeName && i != endNodeName) {
                // If the attempted edge is a line
                if (edge instanceof Line && edgeTools.lineAndCircleCollide(edge, nodes.get(i).getShape())) {
                    return i + 1;
                    // If the attempted edge is a circle
                } else if (edge instanceof Circle && edgeTools.twoCirclesCollide((Circle) edge, nodes.get(i).getShape())) {
                    return i + 1;
                }
            }
        }
        return -1;
    }

    /**
     * Checks for self-collision of a newly drawn edge and collision with all existing edges
     *
     * @param tmpPath : The newly drawn edge
     * @return true if there is collision, false otherwise
     * @author Thea Birk Berger
     */
    private boolean pathSelfCollides(Path tmpPath) {

        Line tmpPathLine = edgeTools.getLineBetweenPathElements(tmpPath.getElements(), nodes.size());

        int pathSize = path.getElements().size();
        int numberOfPathElementsToIgnore = 0;
        double pathLength = 0;

        for (int i = 1; i < pathSize - 1; i++) {
            // Create line between every two path elements
            Line pathLine = edgeTools.getLineBetweenPathElements(path.getElements().subList(i - 1, i + 1), nodes.size());
            // Add line length to summed path length
            pathLength += edgeTools.getDistanceBetweenTwoPoints(pathLine.getStartX(), pathLine.getStartY(), pathLine.getEndX(), pathLine.getEndY());
            // While the path length remains short => ignore a path head element for collision
            numberOfPathElementsToIgnore += pathLength < 50 ? 1 : 0;
        }

        for (int i = 1; i < pathSize - 1 - numberOfPathElementsToIgnore; i++) {
            // Create line between every two path elements
            Line pathLine = edgeTools.getLineBetweenPathElements(path.getElements().subList(i - 1, i + 1), nodes.size());
            // Check collision between most recently drawn and previously drawn path segments
            if (Shape.intersect(tmpPathLine, pathLine).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks collision between existing canvas edges and most recently drawn path segment
     *
     * @return true if there is collision, false otherwise
     * @author Thea Birk Berger
     */
    public boolean pathCollidesWithOtherEdges() {

        for (Shape edge : edges) {
            if (Shape.intersect(path, edge).getBoundsInLocal().getWidth() != -1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a node or circular edge exceeds the game frame
     *
     * @param edge : A node or a circular edge
     * @return true if there is collision, false otherwise
     * @author Thea Birk Berger
     */
    private boolean circleExceedsGameFrame(Circle edge) {
        return (edge.getCenterX() - edge.getRadius() <= 0 ||
                edge.getCenterY() - edge.getRadius() <= 0 ||
                edge.getCenterX() + edge.getRadius() >= width ||
                edge.getCenterY() + edge.getRadius() >= height);
    }

    /**
     * Checks if node collides with any edges or nodes expect the newest edge
     *
     * @param nNode generated node
     * @return true if collision is detected else false
     * @author Sebastian Lund Jensen
     */
    public boolean nodeCollides(Node nNode) {
        //check collision with other nodes
        for (Node node : nodes) {
            if (nNode.getShape().getBoundsInLocal().intersects(node.getShape().getBoundsInLocal())) {
                return true;
            }
        }
        //check collision with all paths
        for (int i = 0; i < edges.size() - 2; i++) {
            if (nNode.getShape().getBoundsInLocal().intersects(edges.get(i).getBoundsInLocal())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Creates a new node to appear on new line
     * and adjusts its position so that there is no collision with existing nodes or edges.
     * Returns null if no such node can be found.
     *
     * @param startNode : the new edge start mousePosition
     * @param endNode   : the new edge end mousePosition
     * @return non-colliding node if one exists, null otherwise
     * @author Thea Birk Berger
     */
    private Node getNewNodeForLine(Node startNode, Node endNode) {

        double edgeLength = edgeTools.getDistanceBetweenTwoPoints(startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY());
        double r = startNode.getNodeRadius();
        double d = edgeLength / 2; // Initial node position - line midpoint
        Node newNode;

        boolean nodeCollision;

        do {
            // Define new node with distance d from the line start mousePosition
            double newNodeX = edgeTools.getPointOnLine(startNode.getX(), endNode.getX(), d, edgeLength);
            double newNodeY = edgeTools.getPointOnLine(startNode.getY(), endNode.getY(), d, edgeLength);
            newNode = new Node(newNodeX, newNodeY, 2, nodes.size() + 1);

            nodeCollision = false;

            // If there is collision => change position d of new node
            if (newNodeCollidesWithExistingNodes(newNode) || newNodeCollidesWithExistingEdges(newNode)) {
                // If the new node has reached the end of the line
                if (edgeTools.twoCirclesCollide(newNode.getShape(), endNode.getShape())) {
                    // Move new node to beginning of line
                    d = r + (r / 10);
                } else {
                    // Move new node further towards line end mousePosition
                    d += 2 * r + (r / 10);
                }
                nodeCollision = true;
            }

            // If the new node is back at the starting mousePosition
            if (nodeCollision && d >= edgeLength / 2 - (r * 2) && d <= edgeLength / 2 + (r * 2)) {
                return null;
            }

        } while (nodeCollision);

        return newNode;
    }

    /**
     * Creates a new node to appear on new circular edge
     * and adjusts its position so that there is no collision with existing nodes or edges.
     * Returns null if no such node can be found.
     *
     * @param edge : the new edge
     * @return non-colliding node if one exists, null otherwise
     * @author Thea Birk Berger
     */
    private Node getNewNodeForCircle(Circle edge) {

        int angle = 0;   // Initial node position - on circle top mousePosition
        Node newNode;
        boolean nodeCollision;

        do {
            double newNodeX = edge.getCenterX() + edge.getRadius() * Math.sin(angle);
            double newNodeY = edge.getCenterY() + edge.getRadius() * Math.cos(angle);
            newNode = new Node(newNodeX, newNodeY, 2, nodes.size() + 1);

            nodeCollision = false;

            // If there is collision => change angle to relocate new node
            if (newNodeCollidesWithExistingNodes(newNode)
                    || newNodeCollidesWithExistingEdges(newNode)
                        || circleExceedsGameFrame(newNode.getShape())) {
                nodeCollision = true;
                // Increment angle
                angle += newNode.getNodeRadius() + (newNode.getNodeRadius() / 10);

                // If the new node is back at the starting mousePosition
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
     *
     * @return non-colliding node if one exists, null otherwise
     * @author Thea Birk Berger
     */
    Node getNewNodeForPath(Path path) {

        int pathSize = path.getElements().size();
        int d = pathSize / 2;  // Initial node position - mid path element
        failedNodes.clear();
        Node newNode;
        boolean nodeCollision;

        do {
            newNode = edgeTools.getCoordinates(path.getElements().get(d), 2, nodes.size());
            nodeCollision = false;

            // If there is collision => chose new path element for the node position
            if (newNodeCollidesWithExistingNodes(newNode) ||
                    newNodeCollidesWithExistingEdges(newNode) ||
                        circleExceedsGameFrame(newNode.getShape())) {
                nodeCollision = true;
                // Add failed node to a list, in order to find a new path if necessary in dynamic node
                if (!circleExceedsGameFrame(newNode.getShape())) {
                    failedNodes.add(newNode);
                }

                if (circleExceedsGameFrame(newNode.getShape())) {
                    System.out.println("Exceeded frame");
                }

                // If the new node has reached the end of path => place node on path beginning
                d = d + 1 < pathSize ? d + 1 : 1;

                // If the new node is back on the middle of the line
                if (d == path.getElements().size() / 2) {
                    return null;
                }
            }

        } while (nodeCollision);

        return newNode;
    }

    /**
     * Checks if there is room for a new node on a path
     *
     * @param path : The path in question
     * @return : True if there is room, false otherwise
     * @author Thea Birk Berger
     */
    boolean isThereRoomForNewNodeOnPath(Path path) {
        int pathSize = path.getElements().size();
        int d = pathSize / 2;  // Initial node position - mid path element
        Node newNode;
        boolean nodeCollision;

        do {
            newNode = edgeTools.getCoordinates(path.getElements().get(d), 2, nodes.size());
            nodeCollision = false;

            // If there is collision => chose new path element for the node position
            if (newNodeCollidesWithExistingNodes(newNode) ||
                    newNodeCollidesWithExistingEdges(newNode) ||
                        circleExceedsGameFrame(newNode.getShape())) {
                nodeCollision = true;

                // If the new node has reached the end of path => place node on path beginning
                d = d + 1 < pathSize ? d + 1 : 1;

                // If the new node is back on the middle of the line
                if (d == path.getElements().size() / 2) {
                    return false;
                }
            }

        } while (nodeCollision);

        return true;
    }

    /**
     * Checks if the input node collides with any existing nodes on the gameboard
     *
     * @param newNode : A suggested new node to appear on a new edge
     * @return true for collision, false otherwise
     * @author Thea Birk Berger
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
     *
     * @param newNode : A suggested new node to appear on a new edge
     * @return true for collision, false otherwise
     * @author Thea Birk Berger
     */
    private boolean newNodeCollidesWithExistingEdges(Node newNode) {

        // Traverse through all existing edges on the gameboard
        for (Shape edge : edges) {
            // If the new node collides with existing edge
            if ((edge instanceof Line && edgeTools.lineAndCircleCollide(edge, newNode.getShape()))
                    || (edge instanceof Circle && edgeTools.twoCirclesCollide((Circle) edge, newNode.getShape()))) {
                return true;
            }

            if (edge instanceof Path) {
                for (int i = 1; i < ((Path) edge).getElements().size() - 1; i++) {
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
     * Sets up path object and sets coordinates for starting mousePosition of drawing (the position on the pane where the click occured)
     *
     * @param mouseClick A mouse click
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    public void initializePath(MouseEvent mouseClick) throws InvalidNode {
        hasCollided = false;
        leftStartNode = false;
        mousePosition = new Point((int) mouseClick.getX(), (int) mouseClick.getY());
        pathStartNode = findNodeFromPoint(mousePosition);
        if (pathStartNode != null && pathStartNode.getNumberOfConnectingEdges() < 3) {
            path = new Path();
        } else if (pathStartNode != null) {
            InvalidNode invalidNode = new InvalidNode("Node " + pathStartNode.getId() + " already has 3 connecting lines");
            invalidNode.setNode(pathStartNode);
            throw invalidNode;
        } else {
            throw new InvalidNode("No node selected");
        }
    }

    /**
     * This method draws the line the user is tracing with their mouse.
     * The method performs sub calls to pathSelfCollides() and pathCollidesWithOtherEdges()
     * to ensure the drawn line is not intersecting with itself or other lines.
     * The current drawing is removed if it violates the rules.
     *
     * @param mouseDrag A mouse drag
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     * @author Thea Birk Berger
     */
    public void drawPath(MouseEvent mouseDrag) throws PathForcedToEnd, InvalidPath, CollisionException {

        // Add mouse drag start-position to temporary path
        Path pathTmp = new Path();
        pathTmp.getElements().add(new MoveTo(mousePosition.getX(), mousePosition.getY()));

        mousePosition = new Point((int) mouseDrag.getX(), (int) mouseDrag.getY());
        boolean isPointInsideNodeTemp = isPointInsideNode(mousePosition);

        // Throw exception if mouse has exceeded canvas frame
        checkIfMouseHasExceededCanvasFrame();
        // Check if path should start or end and add path elements accordingly
        startOrEndPath(isPointInsideNodeTemp);

        // Add mouse drag end-position to temporary path
        pathTmp.getElements().add(new LineTo(mousePosition.getX(), mousePosition.getY()));

        boolean selfCollision = pathSelfCollides(pathTmp);
        boolean edgeCollision = pathCollidesWithOtherEdges();

        // If temporary path collides with itself or other edges
        if (selfCollision || edgeCollision) {
            Path exceptionPath = new Path(List.copyOf(path.getElements()));
            // Reset path storage
            path.getElements().clear();
            pathTmp.getElements().clear();
            hasCollided = true;
            System.out.println("collision at " + mousePosition.getX() + ", " + mousePosition.getY());

            // Set and throw exception
            CollisionException collisionException = new CollisionException(selfCollision ? "The line cannot cross itself" : "The new line collided with another line");
            collisionException.setPath(exceptionPath);
            throw collisionException;
        // If temporary path does not collide and has not yet reached an end node
        } else if (leftStartNode && !isPointInsideNodeTemp) {
            // Add mouse drag end-position to path (the path now has an extra path element)
            path.getElements().add(new LineTo(mousePosition.getX(), mousePosition.getY()));
            pathTmp.getElements().clear();
        }
    }

    /**
     * This method throws an exception if the mouse event exceeds the pane (gamePane)
     * @throws InvalidPath If mouse has exceeded canvas frame
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    private void checkIfMouseHasExceededCanvasFrame() throws InvalidPath {

        if (mousePosition.getX() < 0 || mousePosition.getX() > width || mousePosition.getY() < 0 || mousePosition.getY() > height) {
            InvalidPath invalidPath = new InvalidPath("The line cannot exceed the game frame");
            invalidPath.setPath(path);
            throw invalidPath;
        }
    }

    /**
     * Checks if drawing has left the start node, and starts the path with its first path element if it has.
     * Checks if drawing has reached an end node, and ends the path with the final path element if it has.
     *
     * @param mouseIsInsideANode : True if mouse is currently on a node
     * @throws PathForcedToEnd if the mouse has hit an end node
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    private void startOrEndPath(boolean mouseIsInsideANode) throws PathForcedToEnd {

        // If drawing has left the start node and has not yet reached an end node
        if (!mouseIsInsideANode && !leftStartNode) {
            // Get start node contour mousePosition and add to path (close path start-gap)
            double[] contourPoint = edgeTools.getContourPoint(pathStartNode.getX(), pathStartNode.getY(), mousePosition.getX(), mousePosition.getY(), pathStartNode.getNodeRadius());
            path.getElements().add(new MoveTo(contourPoint[0], contourPoint[1]));
            leftStartNode = true;
        // If drawing has reached an end node
        } else if (mouseIsInsideANode && leftStartNode) {
            // Get end node contour mousePosition and add to path (close path end-gap)
            Node endNode = findNodeFromPoint(mousePosition);
            double[] contourPoint = edgeTools.getContourPoint(endNode.getX(), endNode.getY(), mousePosition.getX(), mousePosition.getY(), endNode.getNodeRadius());
            path.getElements().add(new LineTo(contourPoint[0], contourPoint[1]));
            System.out.println("Path forcefully ended at: " + contourPoint[0] + ", " + contourPoint[1]);
            throw new PathForcedToEnd("Path forcefully ended at: " + contourPoint[0] + ", " + contourPoint[1]);
        }
    }

    /**
     * If turn was ended successfully then the drawn line is added to list of valid lines
     *
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     * @author Thea Birk Berger
     */
    public void finishPath(MouseEvent mouseEvent) throws InvalidPath, InvalidNode {
        if (path.getElements().size() == 0) return;
        Point point = new Point((int) mouseEvent.getX(), (int) mouseEvent.getY());
        Node endNode = findNodeFromPoint(point);
        pathStartNode.incNumberOfConnectingEdges(1);
        Node newNode = getNewNodeForPath(path);
        if (leftStartNode && endNode != null && endNode.getNumberOfConnectingEdges() < 3 && newNode != null) {
            endNode.incNumberOfConnectingEdges(1);
            System.out.println("path added");
            edges.add(path);
            nodes.add(newNode);
        } else if (endNode == null || newNode == null) {
            pathStartNode.decNumberOfConnectingEdges(1);
            Path tempPath = new Path(List.copyOf(path.getElements()));
            path.getElements().clear();
            // Set and throw exception
            InvalidPath invalidPath = new InvalidPath("There is no space for a new node on this line");
            invalidPath.setPath(tempPath);
            throw invalidPath;
        } else {
            // Remove path from model
            pathStartNode.decNumberOfConnectingEdges(1);
            path.getElements().clear();
            // Set and throw exception
            InvalidNode invalidNode = new InvalidNode("Node " + endNode.getId() + " already has 3 connecting lines");
            invalidNode.setNode(endNode);
            throw invalidNode;
        }
    }

    /**
     * Analyzes if there is at least one legal move left in the game by making draw edge simulations.
     * Used to update the game flow.
     *
     * @param smartMode : True if the game is dynamic or drag-to-draw, false otherwise
     * @return true if there is at least one legal move left in the game, false otherwise
     * @author Thea Birk Berger
     */
    private boolean atLeastOneRemainingLegalMove(boolean smartMode) {

        // Collect all nodes with less than 3 connecting edges
        List<Node> availableNodes = new ArrayList<>();
        for (Node node : nodes) {
            if (node.getNumberOfConnectingEdges() < 3) { availableNodes.add(node); }
        }

        // Examine first the connect possibility between any two available nodes
        for (Node node1 : availableNodes) {
            for (Node node2 : availableNodes) {
                if (node1 != node2) {
                    if (thereIsALegalEdge(smartMode, node1, node2)) return true;
                }
            }
        }

        // Examine second the self-connect possibility for any available node
        for (Node node1 : availableNodes) {
            for (Node node2 : availableNodes) {
                if (node1 == node2 && node1.getNumberOfConnectingEdges() <= 1) {
                    if (thereIsALegalEdge(smartMode, node1, node2)) return true;
                }
            }
        }

        return false;
    }

    /**
     * Checks if there is a legal edge between the input nodes.
     * Calls the draw methods and returns true if no exceptions are thrown.
     *
     * @param smartMode : True if the function is called when game is in smart mode, false otherwise
     * @param node1 : First mode
     * @param node2 : Second node
     * @return true if there is a legal edge between the nodes, false otherwise
     * @author Thea Birk Berger
     */
    private boolean thereIsALegalEdge(boolean smartMode, Node node1, Node node2) {
        try {
            // Simulate a drawing between the two edges
            if (smartMode) {
                drawSmartEdge(node1.getShape(), node2.getShape(), true);
            } else {
                drawEdgeBetweenNodes(node1.getId() - 1, node2.getId() - 1, true);
            }
            return true;
            // If no exception is cast => a legal move is found
        } catch (CollisionException | InvalidPath | NoValidEdgeException ignore) {}
        return false;
    }

    /**
     * @param nodeToFind : The node in question
     * @return number of connecting edges at a node
     * @author Thea Birk Berger
     */
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
     *
     * @param nodeToFind The circle whose node object needs to be found
     * @return The node that whose shape is the given circle object
     * @author Noah Bastian Christiansen
     */
    private Node findNode(Circle nodeToFind) {
        for (Node n : nodes) {
            if (n.getShape() == nodeToFind) {
                return n;
            }
        }
        return null;
    }

    /**
     * Given a Circle object it finds the id of the corresponding node.
     *
     * @param nodeToFind The circle whose name we want
     * @return the name/number of the node whose shape is the given circle object.
     * @author Noah Bastian Christiansen
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
     * @param point user mouseEvent
     * @return null if the mousePosition is not inside any node, else the node surrounding the mousePosition
     * @author Sebastian Lund Jensen
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

    /**
     * Resets game.
     * Clears the gameboard as all edges and nodes are deleted.
     * Restarts the game flow.
     *
     * @author Thea Birk Berger
     */
    public void resetGame() {
        edges.clear();
        nodes.clear();
        gameFlow.restartWithCurrentPlayerNames();
    }

    /**
     * @return the list of current nodes in the game
     * @author Thea Birk Berger
     */
    public List<Node> getNodes() {
        return nodes;
    }

    /**
     * @return the list of current edges in the game
     * @author Thea Birk Berger
     */
    List<Shape> getEdges() {
        return edges;
    }

    /**
     * Checks if a node is on the gameboard
     *
     * @param id : The ID of the node in question
     * @return true if the node is in the game, false otherwise
     * @author Thea Birk Berger
     */
    public boolean hasNode(int id) {
        return id < nodes.size();
    }

    /**
     * Checks if a node is on the gameboard
     *
     * @param nodeToFind : The shape of the node in question
     * @return true if the node is in the game, false otherwise
     * @author Thea Birk Berger
     */
    public boolean hasNode(Circle nodeToFind) {
        for (Node node : nodes) {
            if (node.getShape() == nodeToFind) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the list of nodes there was not room for at the most recently drawn path (the path instance variable)
     * @author Thea Birk Berger
     */
    public List<Node> getFailedNodesForMostRecentPath() {
        return failedNodes;
    }

    /**
     * @param id : The ID of the node in question
     * @return the shape of the node in question
     * @author Thea Birk Berger
     */
    public Circle getNodeFromId(int id) {
        return nodes.get(id).getShape();
    }

    /**
     * @param point user mouseEvent
     * @return true if mousePosition is inside node
     * @author Sebastian Lund Jensen
     */
    public boolean isPointInsideNode(Point point) {
        return findNodeFromPoint(point) != null;
    }

    /**
     * @return the node most recently added to the game
     */
    public Node getNewestNode() {
        return nodes.get(nodes.size() - 1);
    }

    /**
     * @return the edge most recently added to the game
     * @author Thea Birk Berger
     */
    public Shape getNewestEdge() {
        return edges.get(edges.size() - 1);
    }

    /**
     * @return the gameboard height
     * @author Thea Birk Berger
     */
    public double getHeight() {
        return height;
    }

    /**
     * @return the gameboard weight
     * @author Thea Birk Berger
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the current player name as given by the game flow
     * @author Thea Birk Berger
     */
    public String getCurrentPlayerName() {
        return gameFlow.getCurrentPlayer();
    }

    /**
     * Called by the view to set player names as given by the main menu
     *
     * @param player1Name : Player name 1
     * @param player2Name : Player name 2
     * @author Thea Birk Berger
     */
    public void setPlayerNames(String player1Name, String player2Name) {
        gameFlow.setPlayerNames(player1Name, player2Name);
    }

}

