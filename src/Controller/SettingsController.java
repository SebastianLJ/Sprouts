package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController extends Controller implements Initializable {

    @FXML
    private ChoiceBox<String> resolutions = new ChoiceBox<>();
    public static int width = 800;
    public static int height = 600;

    /**
     * Sets up a choicebox with a list of resolutions that the user can choose from.
     * @author Noah Bastian Christiansen
     * @param url   Required - Not used.
     * @param resourceBundle    Required - Not used.
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        resolutions.getItems().add("800x600");
        resolutions.getItems().add("1024x768");
        resolutions.getItems().add("1280x960");
        resolutions.getItems().add("1366x768");
        resolutions.getItems().add("1600x900");
        resolutions.getItems().add("1920x1080");
        resolutions.setValue(width+"x"+height);

    }

    /**
     * When the user presses apply this method is called.
     * This method ensures that the width and height of the game gets set.
     * @author Noah Bastian Christiansen
     */
    public void applyResolutionChange() {

        String chosenResolution = resolutions.getValue();
        String[] resArr = chosenResolution.split("x");
        width=Integer.parseInt(resArr[0]);
        height=Integer.parseInt(resArr[1]);

    }

    /**
     * When the user presses the back button this method is called.
     * This method returns the user to the main menu.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     * @author Noah Bastian Christiansen
     */
    public void goToMainMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SproutLauncher.class.getClassLoader().getResource(
                        "MainMenu.fxml")
        );
        Parent mainMenuParent = loader.load();
        Scene mainMenu = new Scene(mainMenuParent);
        Stage window = (Stage) resolutions.getScene().getWindow();
        window.setScene(mainMenu);
        window.show();
    }

}
