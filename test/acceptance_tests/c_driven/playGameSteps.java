package acceptance_tests.c_driven;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NotEnoughInitialNodesException;
import Model.Point;
import cucumber.api.java.en.*;
import holders.ErrorMessageHolder;
import sample.Main;

import java.util.Scanner;

import static org.junit.Assert.*;

public class playGameSteps {

    private Main main;
    private ErrorMessageHolder errorMessageHolder;
    int noOfNodes;
    int numberOfEdges;
    int startNodeName;
    int endNodeName;

    public playGameSteps(Main main, ErrorMessageHolder errorMessageHolder) {
        this.main = main;
        this.errorMessageHolder = errorMessageHolder;
    }

//    @Given("the user chooses nodes {int} and {int}")
//    public void theUserChoosesNodesAnd(Integer int1, Integer int2) throws NotEnoughInitialNodesException, IllegalNodesChosenException {
//        startNodeName = int1;
//        endNodeName = int2;
//        noOfNodes = Math.max(Math.max(int1, int2), 3);
//        numberOfEdges = 0;
//        Scanner scanner = new Scanner(String.valueOf(noOfNodes) + "\n" + int1.toString() + "\n" + int2.toString());
//        main.acceptUserInput(scanner);
//    }
//
//    @Given("nodes {int} and {int} exist")
//    public void nodesAndExist(Integer int1, Integer int2) {
//        assertTrue(main.getController().getSproutModel().hasNodeWithName(int1) && main.getController().getSproutModel().hasNodeWithName(int2));
//    }
//
//    @Given("nodes {int} and {int} are valid")
//    public void nodesAndAreValid(Integer int1, Integer int2) {
//        assertTrue(main.getController().getSproutModel().hasMaxNumberOfEdges(int1) && main.getController().getSproutModel().hasMaxNumberOfEdges(int2));
//    }
//
//    @Then("a new node is created on the line between the two nodes")
//    public void aNewNodeIsCreatedOnTheLineBetweenTheTwoNodes() {
//        Point startNode = main.getController().getSproutModel().nodeCoordinates(startNodeName);
//        Point endNode = main.getController().getSproutModel().nodeCoordinates(endNodeName);
//        Point newNode = main.getController().getSproutModel().nodeCoordinates(noOfNodes);
//        int xUpperBound = Math.max(startNode.getX(), endNode.getX());
//        int xLowerBound = Math.min(startNode.getX(), endNode.getX());
//        int yUpperBound = Math.max(startNode.getY(), endNode.getY());
//        int yLowerBound = Math.min(startNode.getY(), endNode.getY());
//        assertTrue(newNode.getX() < xUpperBound &&
//                            newNode.getX() > xLowerBound &&
//                            newNode.getY() < yUpperBound &&
//                            newNode.getY() > yLowerBound);
//    }
//
//    @Then("the game has been extended by one line and one node")
//    public void theGameHasBeenExtendedByOneLineAndOneNode() {
//        assertEquals(noOfNodes + 1, main.getController().getSproutModel().getNumberOfNodes());
//        assertEquals(numberOfEdges + 1, main.getController().getSproutModel().getNumberOfEdges());
//    }

}
