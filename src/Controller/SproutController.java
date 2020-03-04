package Controller;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import Model.SproutModel;

public class SproutController {

    //    private SproutView sproutView;
    private SproutModel sproutModel;
    private boolean gameOnGoing;


    public SproutController() {
        gameOnGoing = false;
    };

    public void initializeGame(int noOfInitialNodes) throws NotEnoughInitialNodesException {

        if (gameOnGoing) {
            // TODO
        } else {
            if (noOfInitialNodes > 0) {
                sproutModel = new SproutModel();
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
        } else if (sproutModel.hasMaxNumberOfEdges(startNode) || sproutModel.hasMaxNumberOfEdges(endNode)) {
            throw new IllegalNodesChosenException("Nodes cannot have more than 3 connecting edges");
        } else {
            sproutModel.drawEdgeBetweenNodes(startNode, endNode);
        }
    }

    public SproutModel getSproutModel() {
        return sproutModel;
    }
}
