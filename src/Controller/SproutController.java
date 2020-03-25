package Controller;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import Model.SproutModel;
import javafx.scene.input.MouseEvent;

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

    public boolean isGameOnGoing() {
        return gameOnGoing;
    }

    public void attemptDrawEdgeBetweenNodes(int startNode, int endNode) throws IllegalNodesChosenException {

        if (!(sproutModel.hasNodeWithName(startNode) && sproutModel.hasNodeWithName(endNode))) {
            outputExceptionMessage = "One or both nodes chosen does not exist";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else if ((startNode != endNode && (sproutModel.getNumberOfEdges(startNode) == 3 || sproutModel.getNumberOfEdges(endNode) == 3)) ||
                   (startNode == endNode && sproutModel.getNumberOfEdges(startNode) > 1)) {
            outputExceptionMessage = "Nodes cannot have more than 3 connecting edges";
            throw new IllegalNodesChosenException(outputExceptionMessage);
        } else {
            sproutModel.drawEdgeBetweenNodes(startNode, endNode);
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

    public boolean isCollided(){
        return sproutModel.getIsCollided();
    }

    public SproutModel getSproutModel() {
        return sproutModel;
    }

    public void updateSize(double width, double height) {
        sproutModel.setWidth((int) width);
        sproutModel.setHeight((int) height);
    }

    public void resetGame() {
        sproutModel.resetGame();
        gameOnGoing = false;
    }

    public String getOutputExceptionMessage() {
        return outputExceptionMessage;
    }
}
