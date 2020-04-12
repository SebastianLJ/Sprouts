package sample;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import java.util.Scanner;

import Controller.SproutController;

public class Main {

    static SproutController controller = new SproutController();


    public static void main(String[] args) throws NumberOfInitialNodesException, IllegalNodesChosenException {
        acceptUserInput(new Scanner(System.in));  // uncomment for console driven game
//        launch(args);                             // uncomment for javaFX driven game
    }

    public static void acceptUserInput(Scanner scanner) {

        Scanner stdin = scanner;
        Boolean successfulInput = false;

        while (!successfulInput && stdin.hasNextInt()) {

            int noOfInitialNodes = stdin.nextInt();

            try {
                controller.attemptInitializeGame(noOfInitialNodes);
                successfulInput = true;
            } catch (NumberOfInitialNodesException e) {
                System.out.println(e.getMessage());
            }
        }


        while (stdin.hasNextInt()) {

            int startNode = stdin.nextInt() - 1;
            int endNode = stdin.nextInt() - 1;

            try {
                controller.attemptDrawEdgeBetweenNodes(startNode, endNode);
            } catch (IllegalNodesChosenException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    // -----------------------------------------//
    // for testing:

    public void resetSproutController() {
        controller = new SproutController();
    }

    public SproutController getController() {
        return controller;
    }
}