package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Controller {

    String player1Name;
    String player2Name;

    void changeScene(ActionEvent event, String fxmlFile) throws IOException {
        Parent enterFileNameParent = FXMLLoader.load(
                Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource(
                        fxmlFile)
                ));

        Scene enterFileNameScene = new Scene(enterFileNameParent);

        //This line gets the Stage information
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();
        window.setResizable(false);

        window.setScene(enterFileNameScene);
        window.show();
    }

    /**
     * @param event         The mouse click on the button.
     * @param whichGameType Which game mode
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     * @author Emil Sommer Desler
     * @author Noah Bastian Christiansen
     * This method in run on a button click and starts either a click to draw game or a drag to draw game.
     */
    void startGame(ActionEvent event, int whichGameType, int numberOfInitialNodes, boolean smartGame) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SproutLauncher.class.getClassLoader().getResource(
                        "GameView.fxml")
        );
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        Parent gameViewParent = loader.load();
        GameController controller = loader.getController();
        Scene gameViewScene = new Scene(gameViewParent, SettingsController.width, SettingsController.height);


        window.setScene(gameViewScene);
        window.setResizable(false);
        controller.setGameMode(whichGameType);
        controller.setNumberOfInitialNodes(numberOfInitialNodes);
        controller.setSmartGame(smartGame);

        window.show();
    }

}
