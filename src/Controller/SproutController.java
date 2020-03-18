package Controller;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import Model.SproutModel;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;

public class SproutController {

    //    private SproutView sproutView;
    private SproutModel sproutModel;
    private boolean gameOnGoing;


    public SproutController() {
        sproutModel = new SproutModel();
        gameOnGoing = false;
    }

    public void attemptInitializeGame(int noOfInitialNodes) throws NotEnoughInitialNodesException {

        if (gameOnGoing) {
            // TODO
        } else {
            if (noOfInitialNodes > 0) {
                sproutModel.addRandomNodes(noOfInitialNodes);
                gameOnGoing = true;
            } else {
                throw new NotEnoughInitialNodesException("You must start the game with at least 2 nodes");
            }
        }
    }

    public boolean isGameOnGoing() {
        return gameOnGoing;
    }

    public void attemptDrawEdgeBetweenNodes(int startNode, int endNode) throws IllegalNodesChosenException {
        if (!sproutModel.hasNodeWithName(startNode) || !sproutModel.hasNodeWithName(endNode)) {
            throw new IllegalNodesChosenException("One or both nodes chosen does not exist");
        } else if ((startNode != endNode && (sproutModel.getNumberOfEdges(startNode) == 3 || sproutModel.getNumberOfEdges(endNode) == 3)) ||
                   (startNode == endNode && sproutModel.getNumberOfEdges(startNode) > 1)) {
            throw new IllegalNodesChosenException("Nodes cannot have more than 3 connecting edges");
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
}
