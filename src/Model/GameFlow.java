package Model;

public class GameFlow {

    private boolean player1sTurn;
    private String player1Name;
    private String player2Name;

    public GameFlow() {
        this.player1sTurn = true;
        player1Name = "Player 1HH";
        player2Name = "Player 2";
    }

    public void changeTurn() {
        this.player1sTurn = !player1sTurn;
    }

    public void restartWithCurrentPlayerNames() {
        player1sTurn = true;
    }

    public void restartWithDefaultPlayerNames() {
        player1sTurn = true;
        player1Name = "Player 1";
        player2Name = "Player 2";
    }

    public String getGameResponseText() {
        return "There are no more legal moves.\n" + getCurrentPlayer() + " is the winner!";
    }


    public String getCurrentPlayer() {
        return  player1sTurn ? player1Name : player2Name;
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    void setPlayerNames(String player1Name, String player2Name) {
        this.player1Name = player1Name == null ? "Player 1dfasdfsd" : player1Name;
        this.player2Name = player2Name == null ? "Player 2" : player2Name;
    }
}
