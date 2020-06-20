package acceptance_tests;

import Controller.FileSimulationController;
import holders.ErrorMessageHolder;
import static org.junit.Assert.*;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.io.IOException;


public class StrictGame {

    private ErrorMessageHolder errorMessageHolder;
    private FileSimulationController fileSimulationController;


    public StrictGame(ErrorMessageHolder errorMessageHolder) {
        this.errorMessageHolder = errorMessageHolder;
    }


    @Given("that the game is reset")
    public void that_the_game_is_reset() {
        fileSimulationController = new FileSimulationController();
    }


    @When("the test file {fileName} is input in the file simulation")
    public void the_test_file_is_input_in_the_file_simulation(String fileName) {
        fileSimulationController.setFileName(fileName);
    }

    @Then("the file is a valid file")
    public void the_file_is_a_valid_file() {
        try {
            fileSimulationController.validateFile();
        } catch (IOException e) {
            errorMessageHolder.setErrorMessage(e.getMessage());
        }
    }

    @Then("the file runs uninterrupted")
    public void the_file_runs_uninterrupted() {
        fileSimulationController.resetAndRunSimulator();
        assertTrue(fileSimulationController.fileRanCompletely());
    }

    @Then("the game response is {expectedResponse}")
    public void the_game_response_is(String expectedResponse) {
        assertEquals(expectedResponse, fileSimulationController.gameResponseLabel.getText());
    }

}
