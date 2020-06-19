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


    public SproutController() {
        sproutModel = new SproutModel();
    }

    public void attemptInitializeGame(int noOfInitialNodes) throws NumberOfInitialNodesException {
        if (noOfInitialNodes > 1 && noOfInitialNodes < 100) {
            sproutModel.addRandomNodes(noOfInitialNodes);
            sproutModel.setPlayerNames(player1Name,player2Name);
        } else {
            throw new NumberOfInitialNodesException("You must start the game with at least 2 nodes and at most 99 nodes");
        }
    }

    public void attemptDrawEdgeBetweenNodes(int startNodeName, int endNodeName) throws IllegalNodesChosenException, CollisionException, NoValidEdgeException, InvalidPath, GameEndedException {
        if (!(sproutModel.hasNode(startNodeName) && sproutModel.hasNode(endNodeName))) {
            throw new IllegalNodesChosenException("One or both nodes chosen does not exist");
        } else {
            Circle startNode = sproutModel.getNodeFromId(startNodeName);
            Circle endNode = sproutModel.getNodeFromId(endNodeName);
            attemptDrawEdgeBetweenNodes(startNode, endNode);
        }
    }

    public void attemptDrawSmartEdgeBetweenNodes(int startNodeName, int endNodeName) throws IllegalNodesChosenException, NoValidEdgeException, InvalidPath, GameEndedException {
        if (!(sproutModel.hasNode(startNodeName) && sproutModel.hasNode(endNodeName))) {
            throw new IllegalNodesChosenException("One or both nodes chosen does not exist");
        } else {
            Circle startNode = sproutModel.getNodeFromId(startNodeName);
            Circle endNode = sproutModel.getNodeFromId(endNodeName);
            attemptDrawSmartEdgeBetweenNodes(startNode, endNode);
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
     * @param mouseDragged The mousedrag that begins the drawing
     * @author Noah Bastian Christiansen
     */
    public void beginDrawing(MouseEvent mouseDragged) throws InvalidPath, CollisionException, GameEndedException, PathForcedToEnd {
         sproutModel.drawPath(mouseDragged);
    }

    /**
     * This method let's the model's finishPath method know that the path should now end.
     * @param mouseEvent The last mouseevent that ends the drawing.
     * @author Noah Bastian Christiansen
     */
    public void completeDrawing(MouseEvent mouseEvent) throws InvalidNode, InvalidPath, GameEndedException {
        sproutModel.finishPath(mouseEvent);
        sproutModel.updateGameState(true);
    }

    /**
     * This method tells the model to update itself since a new node needs to be generated on the line that was just drawn.
     * @author Noah Bastian Christiansen
     */
    public void addNodeOnValidLineDrag() throws InvalidPath {
        sproutModel.getNewNodeForPath();
    }

    /**
     * This method let's the gameController know if a collision has occured by letting the sproutController ask the model.
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

    public List<Model.Node> getNodes() {
        return sproutModel.getNodes();
    }

    public void attemptDrawEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, CollisionException, NoValidEdgeException, InvalidPath, GameEndedException {
        checkIfNodesAreEligible(startNode, endNode);
        sproutModel.drawEdgeBetweenNodes(startNode, endNode, false);
        sproutModel.updateGameState(false);
    }

    public void attemptDrawSmartEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, NoValidEdgeException, InvalidPath, GameEndedException {
        checkIfNodesAreEligible(startNode, endNode);
        sproutModel.drawSmartEdge(startNode, endNode, false);
        sproutModel.updateGameState(true);
    }

    private void checkIfNodesAreEligible(Circle startNode, Circle endNode) throws IllegalNodesChosenException {
        if (!(sproutModel.hasNode(startNode) && sproutModel.hasNode(endNode))) {
            throw new IllegalNodesChosenException("One or both nodes chosen does not exist");
        } else if ((startNode != endNode && (sproutModel.getNumberOfEdgesAtNode(startNode) == 3 || sproutModel.getNumberOfEdgesAtNode(endNode) == 3)) ||
                (startNode == endNode && sproutModel.getNumberOfEdgesAtNode(startNode) > 1)) {
            throw new IllegalNodesChosenException("Nodes cannot have more than 3 connecting edges");
        }
    }

    public Shape getIllegalEdgeBetweenNodes(Circle startNode, Circle endNode) {
        return sproutModel.getIllegalEdgeBetweenNodes(startNode, endNode);
    }

    String getCurrentPlayerName() {
        return sproutModel.getCurrentPlayerName();
    }

    String[] getPlayerNames() {
        return sproutModel.getPlayerNames();
    }
}
