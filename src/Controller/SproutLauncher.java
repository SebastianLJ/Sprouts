package Controller;

import Exceptions.GameOverException;
import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class SproutLauncher extends Application {

    static SproutController controller = new SproutController();
    private DoubleProperty fontSize = new SimpleDoubleProperty(10);

    /**
     * @author Emil Sommer Desler
     * Starts the initial stage that is used in the entire program to show different scenes to the user.
     * @param stage The stage used to show scenes to the user.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Sprouts");

        // Gets the screen size of the users computer
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource("MainMenu.fxml")));

        Parent root = fxmlLoader.load();

        // Sets the scene to half of the width and height of the screen size
        Scene scene = new Scene(root, screenBounds.getWidth()/2, screenBounds.getHeight()/2);

        // TODO Make things scale-able
        fontSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(100));
        VBox mainMenu = (VBox) fxmlLoader.getNamespace().get("mainMenu");
        mainMenu.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));

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
            } catch (Exception e) {
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
