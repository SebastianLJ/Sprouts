package sample;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import Model.Point;
import javafx.application.Application;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import Controller.SproutController;

public abstract class Main extends Application {
    static SproutController controller = new SproutController();


    public static void main(String[] args) throws NotEnoughInitialNodesException, IllegalNodesChosenException {
        acceptUserInput(new Scanner(System.in));  // uncomment for console driven game
/*//        launch(args);                             // uncomment for javaFX driven game*/
    }

    public static void acceptUserInput(Scanner scanner) {

        Scanner stdin = scanner;

        int noOfInitialNodes = stdin.nextInt();

        try {
            controller.attemptInitializeGame(noOfInitialNodes);
        } catch (NotEnoughInitialNodesException e) {
            System.out.println(e.getMessage());
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