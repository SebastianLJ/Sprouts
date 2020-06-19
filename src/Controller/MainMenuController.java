package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainMenuController extends Controller implements Initializable {

    public VBox mainMenu;
    public TextField player1NameLabel;
    public TextField player2NameLabel;

    public void startClickToDrawGame(ActionEvent event) throws IOException {
        changeScene(event, "ChooseDrawModeAndNumberOfNodesView.fxml");
    }

    public void startDragToDrawGame(ActionEvent event) throws IOException {
        changeScene(event, "ChooseNumberOfNodesView.fxml");
    }

    /**
     * @param event The mouse click on the button.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     * @author Emil Sommer Desler
     * This method is executed when the user decides to simulate a file.
     * This method opens a display where the user can enter the name of the file.txt he want to simulate.
     */
    public void startEnterFileName(ActionEvent event) throws IOException {
        changeScene(event, "EnterFileName.fxml");
    }

    public void changeSettings(ActionEvent event) throws IOException {
        changeScene(event, "GameSettings.fxml");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void player1NameAltered() {
        System.out.println(player1NameLabel.getText());
    }

    public void player2NameAltered() {
        System.out.println(player2NameLabel.getText());
    }
}
