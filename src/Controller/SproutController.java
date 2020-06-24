package Controller;

import Utility.Exceptions.*;
import Model.Node;
import Model.SproutModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;


public class SproutController extends Controller {
    private SproutModel sproutModel;

    /**
     * Constructor.
     * Once the SproutController is initialized for a new game, the player names are retrieved from the main menu.
     *
     * @author Thea Birk Berger
     */
    SproutController() {
        sproutModel = new SproutModel();
        sproutModel.setPlayerNames(MainMenuController.player1Name, MainMenuController.player2Name);
    }

    /**
     * Adds initial nodes to the game if the input number of nodes are within bounds
     *
     * @param noOfInitialNodes : Number of initial nods\
     * @return true if initialization exceeded
     * @throws NumberOfInitialNodesException if the number of nodes are out of bounds
     * @author Thea Birk Berger
     */
    boolean attemptInitializeGame(int noOfInitialNodes) throws NumberOfInitialNodesException {
        if (noOfInitialNodes > 0 && noOfInitialNodes < 100) {
            sproutModel.addRandomNodes(noOfInitialNodes);
            return true;
        } else {
            throw new NumberOfInitialNodesException("You must start the game with at least 1 node and at most 99 nodes");
        }
    }

    /**
     * Initializes the drawing of a new edge if the nodes chosen exist in the game
     * Used in strict mode.
     *
     * @param startNodeName : Name of start node
     * @param endNodeName : Name of end node
     * @return true if the new edge is valid
     * @throws IllegalNodesChosenException if the nodes chosen are not in the game
     * @throws CollisionException if the new edge is a line or circle, and collides with existing edges, nodes or the canvas frame
     * @throws NoValidEdgeException if the is no valid edge between the chosen nodes
     * @throws InvalidPath if the new edge is a drawn path, and collides with existing edges, nodes or the canvas frame
     * @throws GameEndedException if the edge is valid, and there are no valid moves left on the gameboard
     * @author Thea Birk Berger
     */
    boolean attemptDrawEdgeBetweenNodes(int startNodeName, int endNodeName) throws IllegalNodesChosenException, CollisionException, NoValidEdgeException, InvalidPath, GameEndedException {
        if (!(sproutModel.hasNode(startNodeName) && sproutModel.hasNode(endNodeName))) {
            throw new IllegalNodesChosenException("One or both nodes chosen does not exist");
        } else {
            Circle startNode = sproutModel.getNodeFromId(startNodeName);
            Circle endNode = sproutModel.getNodeFromId(endNodeName);
            attemptDrawEdgeBetweenNodes(startNode, endNode);
        }
        return true;
    }

    /**
     * Initializes the drawing of a new edge if the nodes chosen exist in the game.
     * Used in dynamic mode.
     *
     * @param startNodeName
     * @param endNodeName
     * @return true if the new edge is valid
     * @throws IllegalNodesChosenException if the nodes chosen are not in the game
     * @throws NoValidEdgeException if the is no valid edge between the chosen nodes
     * @throws InvalidPath if the new edge collides with existing edges, nodes or the canvas frame
     * @throws GameEndedException if the edge is valid, and there are no valid moves left on the gameboard
     * @author Thea Birk Berger
     */
    boolean attemptDrawSmartEdgeBetweenNodes(int startNodeName, int endNodeName) throws IllegalNodesChosenException, NoValidEdgeException, InvalidPath, GameEndedException {
        if (!(sproutModel.hasNode(startNodeName) && sproutModel.hasNode(endNodeName))) {
            throw new IllegalNodesChosenException("One or both nodes chosen does not exist");
        } else {
            Circle startNode = sproutModel.getNodeFromId(startNodeName);
            Circle endNode = sproutModel.getNodeFromId(endNodeName);
            attemptDrawSmartEdgeBetweenNodes(startNode, endNode);
            return true;
        }
    }

    /** This method tells the model to initialize the path/drawing.
     *
     * @param mousePressed The starting point of the path/drawing
     * @author Noah Bastian Christiansen
     */
    void setupDrawing(MouseEvent mousePressed) throws InvalidNode {
        sproutModel.initializePath(mousePressed);
    }

    /**
     * This method communicates to the model's drawPath method that a drawing has started
     *
     * @param mouseDragged The mousedrag that begins the drawing
     * @author Noah Bastian Christiansen
     */
    void beginDrawing(MouseEvent mouseDragged) throws InvalidPath, CollisionException, PathForcedToEnd {
         sproutModel.drawPath(mouseDragged);
    }

    /**
     * This method let's the model's finishPath method know that the path should now end.
     *
     * @param mouseEvent The last mouseevent that ends the drawing.
     * @author Noah Bastian Christiansen
     */
    void completeDrawing(MouseEvent mouseEvent) throws InvalidNode, InvalidPath, GameEndedException {
        sproutModel.finishPath(mouseEvent);
        sproutModel.updateGameState(true);
    }

    /**
     * This method let's the gameController know if a collision has occurred by letting the sproutController ask the model.
     *
     * @author Noah Bastian Christiansen
     */
    boolean isCollided() {
        return sproutModel.hasNewestPathCollided();
    }

    /**
     * @author Thea Birk Berger
     * @return SproutModel instance variable
     */
    SproutModel getSproutModel() {
        return sproutModel;
    }

    void updateSize(double width, double height) {
        sproutModel.setWidth(width);
        sproutModel.setHeight(height);
    }

    /**
     * Calls the resetGame() method of sprout model, clearing the gameboard.
     *
     * @author Thea Birk Berger
     */
    void resetGame() {
        sproutModel.resetGame();
    }

    /**
     * Initializes the drawing of a new edge if the nodes chosen are eligible
     * Used in strict mode.
     *
     * @param startNode : Start node shape
     * @param endNode : End node shape
     * @throws IllegalNodesChosenException if the nodes chosen are not in the game
     * @throws CollisionException if the new edge collides with existing edges, nodes or the canvas frame
     * @throws NoValidEdgeException if the is no valid edge between the chosen nodes
     * @throws GameEndedException if the edge is drawn, and there are no valid moves left on the gameboard
     * @author Thea Birk Berger
     */
    public void attemptDrawEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, CollisionException, NoValidEdgeException, InvalidPath, GameEndedException {
        checkIfNodesAreEligible(startNode, endNode);
        sproutModel.drawEdgeBetweenNodes(startNode, endNode, false);
        sproutModel.updateGameState(false);
    }

    /**
     * Initializes the drawing of a new edge if the nodes chosen are eligible
     * Used in dynamic mode.
     *
     * @param startNode : Start node shape
     * @param endNode : End node shape
     * @throws IllegalNodesChosenException if the nodes chosen are not in the game
     * @throws NoValidEdgeException if the is no valid edge between the chosen nodes
     * @throws InvalidPath if the new edge collides with existing edges, nodes or the canvas frame
     * @throws GameEndedException if the edge is valid, and there are no valid moves left on the gameboard
     * @author Thea Birk Berger
     */
    void attemptDrawSmartEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, NoValidEdgeException, InvalidPath, GameEndedException {
        checkIfNodesAreEligible(startNode, endNode);
        sproutModel.drawSmartEdge(startNode, endNode, false);
        sproutModel.updateGameState(true);
    }

    /**
     * Checks if one or both of the chosen nodes are not in the game or has 3 connecting edges
     *
     * @param startNode
     * @param endNode
     * @throws IllegalNodesChosenException if one or both of the chosen nodes are not in the game or has 3 connecting edges
     * @author Thea Birk Berger
     */
    private void checkIfNodesAreEligible(Circle startNode, Circle endNode) throws IllegalNodesChosenException {
        if (!(sproutModel.hasNode(startNode) && sproutModel.hasNode(endNode))) {
            throw new IllegalNodesChosenException("One or both nodes chosen does not exist");
        } else if ((startNode != endNode && (sproutModel.getNumberOfEdgesAtNode(startNode) == 3 || sproutModel.getNumberOfEdgesAtNode(endNode) == 3)) ||
                (startNode == endNode && sproutModel.getNumberOfEdgesAtNode(startNode) > 1)) {
            throw new IllegalNodesChosenException("Nodes cannot have more than 3 connecting edges");
        }
    }

    /**
     * As the creating of en edge is terminated once it is deemed illegal,
     * this method is used to get create the illegal edge,
     * so that it can be drawn (in red) in the canvas.
     * The method wont invoke collision checks.
     *
     * @param startNode Start node chosen
     * @param endNode End node chosen
     * @return the illegal edge
     * @author Thea Birk Berger
     */
    Shape getIllegalEdgeBetweenNodes(Circle startNode, Circle endNode) {
        return sproutModel.getIllegalEdgeBetweenNodes(startNode, endNode);
    }

    /**
     * Retrieves the name of the current player from the GameFlow class
     *
     * @return the name of the current player
     * @author Thea Birk Berger
     */
    String getCurrentPlayerName() {
        return sproutModel.getCurrentPlayerName();
    }

    /**
     * Retrieves node ID given a node shape
     *
     * @param circle : a circle shape belonging to a node
     * @return a node ID
     * @author Thea Birk Berger
     */
    Node getNodeFromCircle(Circle circle) {
        return sproutModel.getNodes().get(sproutModel.findNameOfNode(circle));
    }
}
