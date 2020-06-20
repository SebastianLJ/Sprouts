//package acceptance_tests;
//
//import Controller.FileSimulationController;
//import holders.ErrorMessageHolder;
//import static org.junit.Assert.*;
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.When;
//import io.cucumber.java.en.Then;
//
//import java.io.File;
//import java.io.IOException;
//
//
//public class SmartGame {
//
//    private ErrorMessageHolder errorMessageHolder;
//    private FileSimulationController fileSimulationController;
//
//
//    public SmartGame(ErrorMessageHolder errorMessageHolder) {
//        this.errorMessageHolder = errorMessageHolder;
//    }
//
//    @Given("that the game is reset")
//    public void that_the_game_is_reset() {
//        fileSimulationController = new FileSimulationController();
//    }
//
//    @Given("the game mode is smart")
//    public void theGameModeIsSmart() {
//        fileSimulationController.setGameMode(true);
//    }
//
//    @When("the test file {string} is input in the file simulation")
//    public void the_test_file_is_input_in_the_file_simulation(String fileName) {
//
//        String userDirectory = System.getProperty("user.dir");
//        String fullFilename = userDirectory + File.separator + "gameTestFiles" + File.separator + fileName;
//        fileSimulationController.setFileName(fullFilename);
//    }
//
//    @Then("the file is a valid file")
//    public void the_file_is_a_valid_file() throws IOException {
//        assertEquals(0, fileSimulationController.validateFileTest());
//    }
//
//    @When("the file moves are evaluated")
//    public void theFileMovesAreEvaluated() {
//        fileSimulationController.resetSimulator();
//        fileSimulationController.evaluateFileMoves();
//    }
//
//    @Then("the file runs uninterrupted")
//    public void the_file_runs_uninterrupted() {
//        assertTrue(fileSimulationController.fileRanCompletely());
//    }
//
//    @Then("the file run is interrupted")
//    public void theFileRunIsInterrupted() {
//        assertFalse(fileSimulationController.fileRanCompletely());
//    }
//
//    @Then("the game response is {string}")
//    public void the_game_response_is(String expectedResponse) {
//        assertEquals(expectedResponse, fileSimulationController.getGameResponse());
//    }
//
//    @Then("the tooltip response is {string}")
//    public void theTooltipResponseIs(String expectedResponse) {
//        assertEquals(expectedResponse, fileSimulationController.getToolTipMessage());
//
//    }
//
//    @Given("all")
//    public void all() throws IOException {
//        this.fileSimulationController = new FileSimulationController();
//        fileSimulationController.validateFile();
//        fileSimulationController.initializeToolTipCells();
//        fileSimulationController.resetSimulator();
//        String userDirectory = System.getProperty("user.dir");
//        String fullFilename = userDirectory + File.separator + "gameTestFiles" + File.separator + "exceedInit.txt";
//        fileSimulationController.setFileName(fullFilename);
//        fileSimulationController.validateFileTest();
//        fileSimulationController.evaluateFileMoves();
//
//    }
//
//}
