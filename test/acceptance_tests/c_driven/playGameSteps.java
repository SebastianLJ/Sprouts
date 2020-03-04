package acceptance_tests.c_driven;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import Model.Node;
import holders.ErrorMessageHolder;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import javafx.scene.shape.Line;
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

    public playGameSteps(Main main, ErrorMessageHolder errorMessageHolder) {
        this.main = main;
        this.errorMessageHolder = errorMessageHolder;
    }

    @Given("the user chooses nodes {int} and {int}")
    public void theUserChoosesNodesAnd(Integer int1, Integer int2) throws NotEnoughInitialNodesException, IllegalNodesChosenException {
        this.startNode = int1;
        this.endNode = int2;
        initialNumberOfNodes = 2;
        initialNumberOfEdges = 0;
        int initialNumberOfNodes = Math.max(int1, int2);
        Scanner scanner = new Scanner(String.valueOf(initialNumberOfNodes) + "\n" + int1.toString() + " " + int2.toString());
        main.acceptUserInput(scanner);
    }

    @Given("nodes {int} and {int} exist")
    public void nodesAndExist(Integer int1, Integer int2) {
        assertTrue(main.getController().getSproutModel().hasNodeWithName(int1) && main.getController().getSproutModel().hasNodeWithName(int2));
    }

    @Given("nodes {int} and {int} are valid")
    public void nodesAndAreValid(Integer int1, Integer int2) {
        assertFalse(main.getController().getSproutModel().hasMaxNumberOfEdges(int1) && main.getController().getSproutModel().hasMaxNumberOfEdges(int2));
    }

    @Then("a new node is created on the line between the two nodes")
    public void aNewNodeIsCreatedOnTheLineBetweenTheTwoNodes() {
        List<Node> nodes = main.getController().getSproutModel().getNodes();
        Node newNode = nodes.get(nodes.size()-1);
        List<Line> edges = main.getController().getSproutModel().getEdges();
        Line newEdge = edges.get(edges.size()-1);
        assertTrue(newEdge.contains(newNode.getX(), newNode.getY()));
    }

    @Given("the game has been extended by {int} line and {int} node")
    public void theGameHasBeenExtendedByLineAndNode(Integer int1, Integer int2) {
        assertEquals(initialNumberOfEdges + int1, main.getController().getSproutModel().getEdges().size());
        assertEquals(initialNumberOfNodes + int2, main.getController().getSproutModel().getNodes().size());

    }

}
