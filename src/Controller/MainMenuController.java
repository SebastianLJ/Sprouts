package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    private void startGame(ActionEvent event, int whichGameType) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SproutLauncher.class.getClassLoader().getResource(
                        "GameView.fxml")
        );

        Parent gameViewParent = loader.load();

        Scene gameViewScene = new Scene(gameViewParent);

        GameController controller =
                loader.getController();
        controller.setGameType(whichGameType);

        //This line gets the Stage information
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

        window.setScene(gameViewScene);
        window.show();
    }

    public void startClickToDrawGame(ActionEvent event) throws IOException {
        startGame(event, 0);
    }

    public void startDragToDrawGame(ActionEvent event) throws IOException {
        startGame(event, 1);
    }

    public void startEnterFileName(ActionEvent event) throws IOException {
        Parent enterFileNameParent = FXMLLoader.load(
                Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource(
                        "EnterFileName.fxml")
                ));

        Scene enterFileNameScene = new Scene(enterFileNameParent);

        //This line gets the Stage information
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

        window.setScene(enterFileNameScene);
        window.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
