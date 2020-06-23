package Utility.Exceptions;

public class GameEndedException extends Exception {

    String winner;

    public GameEndedException(String message) {
        super(message);
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public String getWinner() {
        return winner;
    }
}
