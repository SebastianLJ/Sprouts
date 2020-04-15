package Controller;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.Scanner;

public class SproutLauncher extends Application {

    static SproutController controller = new SproutController();

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Sprouts");

        Parent root = FXMLLoader.load(Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource("MainMenu.fxml")));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
//        acceptUserInput(new Scanner(System.in));  // uncomment for console driven game
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
    // For testing console driven game

    public void resetSproutController() {
        controller = new SproutController();
    }

    public SproutController getController() {
        return controller;
    }
}
