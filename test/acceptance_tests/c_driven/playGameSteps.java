package acceptance_tests.c_driven;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import Model.Node;
import holders.ErrorMessageHolder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import sample.Main;

import java.util.List;
import java.util.Scanner;

import static org.junit.Assert.*;

public class playGameSteps {

    private Main main;
    private ErrorMessageHolder errorMessageHolder;
    private int startNode;
    private int endNode;
    private int initialNumberOfNodes;
    private int initialNumberOfEdges;
    private String scannerInput;

    public playGameSteps(Main main, ErrorMessageHolder errorMessageHolder) {
        this.main = main;
        this.errorMessageHolder = errorMessageHolder;
        this.scannerInput = "";
    }

    @Given("that a game has nodes {int} and {int}")
    public void thatAGameHasNodesAnd(Integer int1, Integer int2) {
        main.resetSproutController();
        this.startNode = int1;
        this.endNode = int2;
        initialNumberOfNodes = Math.max(int1, int2);
        initialNumberOfEdges = 0;
        scannerInput = String.valueOf(initialNumberOfNodes) + "\n";
    }

    @Given("node {int} is connected to node {int} and to node {int}")
    public void nodeIsConnectedToNodeAndToNode(Integer int1, Integer int2, Integer int3) {
        scannerInput += int1 + " " + int2 + "\n" + int1 + " " + int3;
        initialNumberOfEdges += 2;
    }

    @Given("the user chooses nodes {int} and {int}")
    public void theUserChoosesNodesAnd(Integer int1, Integer int2) {

        Scanner scanner = new Scanner(scannerInput + int1.toString() + " " + int2.toString());
        try {
            main.acceptUserInput(scanner);
        } catch (NotEnoughInitialNodesException | IllegalNodesChosenException e) {
            errorMessageHolder.setErrorMessage(e.getMessage());
        }
    }

    @Given("nodes {int} and {int} exist")
    public void nodesAndExist(Integer int1, Integer int2) {
        assertTrue(main.getController().getSproutModel().hasNodeWithName(int1) && main.getController().getSproutModel().hasNodeWithName(int2));
    }

    @Given("nodes {int} and {int} are valid")
    public void nodesAndAreValid(Integer int1, Integer int2) {

        if (int1 != int2) {
            assertTrue(main.getController().getSproutModel().getNumberOfEdges(int1) < 3 && main.getController().getSproutModel().getNumberOfEdges(int2) < 3);
        } else {
            assertTrue(main.getController().getSproutModel().getNumberOfEdges(int1) < 2);
        }
    }

    @Then("a new node is created on the line between the two nodes")
    public void aNewNodeIsCreatedOnTheLineBetweenTheTwoNodes() {
        // Get most recently drawn node
        List<Node> nodes = main.getController().getSproutModel().getNodes();
        Node newNode = nodes.get(nodes.size()-1);
        // Get most recently drawn edge
        List<Shape> edges = main.getController().getSproutModel().getEdges();
        Shape newEdge = edges.get(edges.size()-1);
        // Check that the new node is on the new edge
        assertTrue(newEdge.contains(newNode.getX(), newNode.getY()));
    }

    @Then("the game has been extended by {int} line\\(s) and {int} node\\(s)")
    public void theGameHasBeenExtendedByLineSAndNodeS(Integer int1, Integer int2) {
        assertEquals(initialNumberOfEdges + int1, main.getController().getSproutModel().getEdges().size());
        assertEquals(initialNumberOfNodes + int2, main.getController().getSproutModel().getNodes().size());
    }

}
