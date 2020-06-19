package Model;

import Exceptions.CollisionException;
import Exceptions.GameEndedException;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.List;

public class GameFlow {

    private boolean player1sTurn;
    private String player1Name;
    private String player2Name;

    public GameFlow() {
        this.player1sTurn = true;
    }

    public void changeTurn() {
        this.player1sTurn = !player1sTurn;
    }


    public boolean atLeastOneLegalMoveDynamicGame() {



        return true;
    }

    public String getGameResponseText() {
        return "There are no more legal moves.\n" + getCurrentPlayer() + " is the winner!";
    }


    public String getCurrentPlayer() {
        return  player1sTurn ?
                (player1Name == null ? "Player 1" : player1Name) :
                (player2Name == null ? "Player 2" : player2Name);
    }

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }
}
