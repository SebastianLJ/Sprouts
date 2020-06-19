package Controller;

import Exceptions.*;
import Model.SproutModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class SproutController extends Controller {

    // private SproutView sproutView;
    private SproutModel sproutModel;
    private boolean gameOnGoing;
    // For testing
    private String outputExceptionMessage;


    public SproutController() {
        sproutModel = new SproutModel();
        gameOnGoing = false;
        outputExceptionMessage = "";
    }

    public void attemptInitializeGame(int noOfInitialNodes) throws NumberOfInitialNodesException {

        if (gameOnGoing) {
            // TODO
        } else {
            if (noOfInitialNodes > 1 && noOfInitialNodes < 100) {
                sproutModel.addRandomNodes(noOfInitialNodes);
                gameOnGoing = true;
            } else {
                outputExceptionMessage = "You must start the game with at least 2 nodes and at most 99 nodes";
                throw new NumberOfInitialNodesException(outputExceptionMessage);
            }
        }
    }

    public void attemptDrawEdgeBetweenNodes(int startNodeName, int endNodeName) throws IllegalNodesChosenException, GameOverException, CollisionException, NoValidEdgeException, InvalidPath {
        if (!(sproutModel.hasNode(startNodeName) && sproutModel.hasNode(endNodeName))) {
            outputExceptionMessage = "One or both nodes does not exist";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else {
            Circle startNode = sproutModel.getNodeFromId(startNodeName);
            Circle endNode = sproutModel.getNodeFromId(endNodeName);
            attemptDrawEdgeBetweenNodes(startNode, endNode);
            gameOnGoing = false;
        }
    }

    public void attemptDrawSmartEdgeBetweenNodes(int startNodeName, int endNodeName) throws IllegalNodesChosenException, GameOverException, NoValidEdgeException, InvalidPath {
        if (!(sproutModel.hasNode(startNodeName) && sproutModel.hasNode(endNodeName))) {
            outputExceptionMessage = "One or both nodes does not exist";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else {
            Circle startNode = sproutModel.getNodeFromId(startNodeName);
            Circle endNode = sproutModel.getNodeFromId(endNodeName);
            attemptDrawSmartEdgeBetweenNodes(startNode, endNode);
            gameOnGoing = false;
        }
    }


    /**
     * @param mousePressed
     * @author Noah Bastian Christiansen
     */
    public void setupDrawing(MouseEvent mousePressed) throws InvalidNode {
        sproutModel.initializePath(mousePressed);

    }

    /**
     * This method communicates to the model's drawPath method that a drawing has started
     *
     * @param mouseDragged The mousedrag that begins the drawing
     * @author Noah Bastian Christiansen
     */
    public void beginDrawing(MouseEvent mouseDragged) throws PathForcedToEnd, InvalidPath, CollisionException {
        sproutModel.drawPath(mouseDragged);
    }

    /**
     * This method let's the model's finishPath method know that the path should now end.
     *
     * @param mouseEvent The last mouseevent that ends the drawing.
     * @author Noah Bastian Christiansen
     */
    public void completeDrawing(MouseEvent mouseEvent) throws InvalidNode, InvalidPath {
        sproutModel.finishPath(mouseEvent);
    }

    /**
     * This method tells the model to update itself since a new node needs to be generated on the line that was just drawn.
     *
     * @author Noah Bastian Christiansen
     */
    public void addNodeOnValidLineDrag() throws InvalidPath {
        //sproutModel.getNewNodeForPath();
    }

    /**
     * This method let's the gameController know if a collision has occured by letting the sproutController ask the model.
     *
     * @author Noah Bastian Christiansen
     */
    public boolean isCollided() {
        return sproutModel.hasNewestPathCollided();
    }

    public SproutModel getSproutModel() {
        return sproutModel;
    }

    public void updateSize(double width, double height) {
        sproutModel.setWidth(width);
        sproutModel.setHeight(height);
    }

    public void resetGame() {
        sproutModel.resetGame();
    }

    public String getOutputExceptionMessage() {
        return outputExceptionMessage;
    }

    public List<Model.Node> getNodes() {
        return sproutModel.getNodes();
    }

    public void attemptDrawEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, GameOverException, CollisionException, NoValidEdgeException, InvalidPath {
        checkIfNodesAreEligible(startNode, endNode);
        sproutModel.drawEdgeBetweenNodes(startNode, endNode);
        concludeTurn();
    }

    private void concludeTurn() throws GameOverException {
        if (sproutModel.hasNoRemainingLegalMoves()) {
            outputExceptionMessage = "There are no more legal moves. The winner is player " + sproutModel.getCurrentPlayer();
            gameOnGoing = false;
            throw new GameOverException(outputExceptionMessage);
        } else {
            sproutModel.changeTurns();
        }
    }

    private void checkIfNodesAreEligible(Circle startNode, Circle endNode) throws IllegalNodesChosenException {
        if (!(sproutModel.hasNode(startNode) && sproutModel.hasNode(endNode))) {
            outputExceptionMessage = "One or both nodes does not exist";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else if ((startNode != endNode && (sproutModel.getNumberOfEdgesAtNode(startNode) == 3 || sproutModel.getNumberOfEdgesAtNode(endNode) == 3)) ||
                (startNode == endNode && sproutModel.getNumberOfEdgesAtNode(startNode) > 1)) {
            outputExceptionMessage = "Nodes cannot have more than 3 connecting edges";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        }
    }

    public void attemptDrawSmartEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, GameOverException, NoValidEdgeException, InvalidPath {
        checkIfNodesAreEligible(startNode, endNode);
        sproutModel.drawSmartLine(startNode, endNode);
        concludeTurn();
    }

    public Shape getIllegalEdgeBetweenNodes(Circle startNode, Circle endNode) {
        return sproutModel.getIllegalEdgeBetweenNodes(startNode, endNode);
    }

    public boolean isGameOnGoing() {
        return gameOnGoing;
    }

    public void setGameOnGoing(boolean value) {
        gameOnGoing = value;
    }
}
