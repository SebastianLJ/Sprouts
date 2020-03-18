package acceptance_tests.c_driven;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import holders.ErrorMessageHolder;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import sample.Main;
import Model.Point;

import java.util.Scanner;

import static org.junit.Assert.*;

public class startGameSteps {

    private Main main;
    private ErrorMessageHolder errorMessageHolder;

    public startGameSteps(Main main, ErrorMessageHolder errorMessageHolder) {
        this.main = main;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Given("that no game is being played")
    public void thatNoGameIsBeingPlayed() {
        main.resetSproutController();
        assertFalse(main.getController().isGameOnGoing());
    }

    @Given("the user inputs {int} initial nodes")
    public void theUserInputsInitialNodes(Integer int1) {
        Scanner scanner = new Scanner(int1.toString());
        main.acceptUserInput(scanner);
    }

    @Then("a new game is created")
    public void aNewGameIsCreated() {
        assertTrue(main.getController().isGameOnGoing());
    }

    @Then("the game has {int} nodes and {int} lines drawn")
    public void theGameHasNodesAndLinesDrawn(int int1, int int2) {
        assertEquals(int1, main.getController().getSproutModel().getNodes().size());
        assertEquals(int2, main.getController().getSproutModel().getEdges().size());
    }

    @Then("no game is being played")
    public void noGameIsBeingPlayed() {
        assertFalse(main.getController().isGameOnGoing());
    }

    @Then("a message with the text {string} is given to the user")
    public void aMessageWithTheTextIsGivenToTheUser(String string) {
        assertEquals(string, errorMessageHolder.getErrorMessage());
    }

}
