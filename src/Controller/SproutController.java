package Controller;

import Exceptions.GameOverException;
import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import Model.SproutModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

import java.util.List;

public class SproutController {

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

    public void attemptDrawEdgeBetweenNodes(int startNodeName, int endNodeName) throws IllegalNodesChosenException, GameOverException {

        if (!(sproutModel.hasNodeWithName(startNodeName) && sproutModel.hasNodeWithName(endNodeName))) {
            outputExceptionMessage = "One or both nodes does not exist";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else {
            Circle startNode = sproutModel.getNodeWithName(startNodeName);
            Circle endNode = sproutModel.getNodeWithName(endNodeName);
            attemptDrawEdgeBetweenNodes(startNode, endNode);
            gameOnGoing = false;
        }
    }

    public void setupDrawing(MouseEvent mousePressed){
        sproutModel.initializePath(mousePressed);
    }

    public void beginDrawing(MouseEvent mouseDragged){
        sproutModel.drawPath(mouseDragged);
    }

    public void completeDrawing(){
        sproutModel.finishPath();
    }

    public void addNodeOnValidLineDrag(){
        sproutModel.addNodeOnLineDrag();
    }

    public boolean isCollided(){
        return sproutModel.getIsCollided();
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

    public void attemptDrawEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, GameOverException {

        if (!(sproutModel.hasNodeWithName(startNode) && sproutModel.hasNodeWithName(endNode))) {
            outputExceptionMessage = "One or both nodes does not exist";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else if ((startNode != endNode && (sproutModel.getNumberOfEdges(startNode) == 3 || sproutModel.getNumberOfEdges(endNode) == 3)) ||
                (startNode == endNode && sproutModel.getNumberOfEdges(startNode) > 1)) {
            outputExceptionMessage = "Nodes cannot have more than 3 connecting edges";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else {
            sproutModel.drawEdgeBetweenNodes(startNode, endNode);

            if (sproutModel.hasNoRemainingLegalMoves()) {
                outputExceptionMessage = "There are no more legal moves. The winner is player " + sproutModel.getCurrentPlayer();
                gameOnGoing = false;
                throw new GameOverException(outputExceptionMessage);
            } else {
                sproutModel.changeTurns();
            }
        }
    }

    public Shape createEdge(Circle startNode, Circle endNode) {
        return sproutModel.createEdgeBetweenNodes(startNode, endNode);
    }

    public boolean isGameOnGoing() {
        return gameOnGoing;
    }
}
