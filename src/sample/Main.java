package sample;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import java.util.Scanner;

import Controller.SproutController;

public class Main {
    static SproutController controller = new SproutController();

    public static void main(String[] args) {
        acceptUserInput(new Scanner(System.in));  // uncomment for console driven game
    }

    public static void acceptUserInput(Scanner scanner) {
        boolean successfulInput = false;

        while (!successfulInput && scanner.hasNextInt()) {

            int noOfInitialNodes = scanner.nextInt();

            try {
                controller.attemptInitializeGame(noOfInitialNodes);
                successfulInput = true;
            } catch (NumberOfInitialNodesException e) {
                System.out.println(e.getMessage());
            }
        }

        while (scanner.hasNextInt()) {

            int startNode = scanner.nextInt() - 1;
            int endNode = scanner.nextInt() - 1;

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