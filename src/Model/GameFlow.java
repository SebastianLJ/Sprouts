package Model;

public class GameFlow {

    private boolean player1sTurn;
    private String player1Name;
    private String player2Name;

    /**
     * Initializes Player 1 as the first player to make a move in the game.
     * Sets default player names, if not stated in the main menu.
     *
     * @author Thea Birk Berger
     */
    public GameFlow() {
        this.player1sTurn = true;
        player1Name = "Player 1";
        player2Name = "Player 2";
    }

    /**
     * Registers that the turn changes between the two players.
     *
     * @author Thea Birk Berger
     */
    public void changeTurn() {
        this.player1sTurn = !player1sTurn;
    }

    /**
     * Restarts the game flow, hence giving the turn to Player 1.
     *
     * @author Thea Birk Berger
     */
    public void restartWithCurrentPlayerNames() {
        player1sTurn = true;
    }

    /**
     * Provides the game response (the winner announcement) to be displayed in the UI
     *
     * @return the game response
     * @author Thea Birk Berger
     */
    public String getGameResponseText() {
        return "There are no more legal moves.\n" + getCurrentPlayer() + " is the winner!";
    }


    /**
     * @return the name of the current player
     * @author Thea Birk Berger
     */
    public String getCurrentPlayer() {
        return  player1sTurn ? player1Name : player2Name;
    }

    /**
     * Used by the view to set the player names.
     *
     * @param player1Name : Name of Player 1 as set in main menu
     * @param player2Name : Name of Player 2 as set in main menu
     * @author Thea Birk Berger
     */
    void setPlayerNames(String player1Name, String player2Name) {
        this.player1Name = player1Name == null ? "Player 1" : player1Name;
        this.player2Name = player2Name == null ? "Player 2" : player2Name;
    }
}
