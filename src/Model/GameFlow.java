package Model;

import java.util.List;

public class GameFlow {

    private boolean player1sTurn;

    public GameFlow() {
        this.player1sTurn = true;
    }

    public void changeTurn() {
        this.player1sTurn = !player1sTurn;
    }

    public boolean noRemainingLegalMovesSimpleGame(List<Node> nodes) {

        int doesNotHaveMaxEdges = 0;
        for (Node node : nodes) {
            doesNotHaveMaxEdges += node.getNumberOfConnectingEdges() < 3 ? 1 : 0;
        }
        return doesNotHaveMaxEdges <= 1;
    }

    public boolean noRemainingLegalMovesAdvancedGame() {
        return true;
    }


        public int getCurrentPlayer() {
        return player1sTurn ? 1 : 2;
    }

}
