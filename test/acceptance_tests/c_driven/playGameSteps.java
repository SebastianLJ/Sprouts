//package acceptance_tests.c_driven;
//
//
//import Controller.SproutLauncher;
//import Model.Node;
//import holders.ErrorMessageHolder;
//import io.cucumber.java.en.*;
//import javafx.scene.shape.Circle;
//import javafx.scene.shape.Shape;
//
//
//import java.util.List;
//import java.util.Scanner;
//
//import static org.junit.Assert.*;
//
//public class playGameSteps {
//
//    private SproutLauncher main;
//    private ErrorMessageHolder errorMessageHolder;
//    private int startNode;
//    private int endNode;
//    private int numberOfNodes;
//    private int numberOfEdges;
//    private String scannerInput;
//
//    public playGameSteps(SproutLauncher main, ErrorMessageHolder errorMessageHolder) {
//        this.main = main;
//        this.errorMessageHolder = errorMessageHolder;
//        this.scannerInput = "";
//    }
//
//    @Given("that a game has nodes {int} and {int}")
//    public void thatAGameHasNodesAnd(Integer int1, Integer int2) {
//        main.resetSproutController();
//        this.startNode = int1;
//        this.endNode = int2;
//        numberOfNodes = Math.max(int1, int2);
//        numberOfEdges = 0;
//        scannerInput = numberOfNodes + "\n";
//    }
//
//    @Given("node {int} is connected to node {int} and to node {int}")
//    public void nodeIsConnectedToNodeAndToNode(Integer int1, Integer int2, Integer int3) {
//        scannerInput += int1 + " " + int2 + "\n" + int1 + " " + int3 + "\n";
//        numberOfEdges += 2;
//        numberOfNodes += 2;
//    }
//
//    @Given("the user chooses nodes {int} and {int}")
//    public void theUserChoosesNodesAnd(Integer int1, Integer int2) {
//
//        Scanner scanner = new Scanner(scannerInput + int1 + " " + int2 + "\n");
//        main.acceptUserInput(scanner);
//
//        errorMessageHolder.setErrorMessage(main.getController().getOutputExceptionMessage());
//    }
//
//    @Given("nodes {int} and {int} exist")
//    public void nodesAndExist(Integer int1, Integer int2) {
//        assertTrue(main.getController().getSproutModel().hasNode(int1) && main.getController().getSproutModel().hasNode(int2));
//    }
//
//    @Given("nodes {int} and {int} are valid")
//    public void nodesAndAreValid(Integer int1, Integer int2) {
//
//        if (int1 != int2) {
//            assertTrue(main.getController().getSproutModel().getNumberOfEdgesAtNode(int1) < 3 && main.getController().getSproutModel().getNumberOfEdgesAtNode(int2) < 3);
//        } else {
//            assertTrue(main.getController().getSproutModel().getNumberOfEdgesAtNode(int1) < 2);
//        }
//    }
//
//    @Then("a new node is created on the line between the two nodes")
//    public void aNewNodeIsCreatedOnTheLineBetweenTheTwoNodes() {
//        // Get most recently drawn node
//        List<Node> nodes = main.getController().getSproutModel().getNodes();
//        Node newNode = nodes.get(nodes.size()-1);
//        // Get most recently drawn edge
//        List<Shape> edges = main.getController().getSproutModel().getEdges();
//        Shape newEdge = edges.get(edges.size()-1);
//        // Check that the new node is on the new edge
//        assertTrue(newEdge.contains(newNode.getX(), newNode.getY()));
//    }
//
//    @Then("a new node is created on the circle between the two nodes")
//    public void aNewNodeIsCreatedOnTheCircleBetweenTheTwoNodes() {
//        // Get most recently drawn node
//        List<Node> nodes = main.getController().getSproutModel().getNodes();
//        Node newNode = nodes.get(nodes.size()-1);
//        // Get most recently drawn edge
//        List<Shape> edges = main.getController().getSproutModel().getEdges();
//        Shape newEdge = edges.get(edges.size()-1);
//        double circleRadius = ((Circle) newEdge).getRadius();
//
//        assertTrue(Math.abs(newNode.getX() - ((Circle) newEdge).getCenterX()) == circleRadius ||
//                            Math.abs(newNode.getY() - ((Circle) newEdge).getCenterY()) == circleRadius);
//
//    }
//
//    @Then("the game has been extended by {int} line\\(s) and {int} node\\(s)")
//    public void theGameHasBeenExtendedByLineSAndNodeS(Integer int1, Integer int2) {
//        assertEquals(numberOfEdges + int1, main.getController().getSproutModel().getEdges().size());
//        assertEquals(numberOfNodes + int2, main.getController().getSproutModel().getNodes().size());
//    }
//
//}
