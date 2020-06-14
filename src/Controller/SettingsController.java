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
    private Parent mainMenuParent;
    public static int width = 800;
    public static int height = 600;


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

    public void applyResolutionChange() throws IOException {

        String chosenResolution = resolutions.getValue();
        String resArr[] = chosenResolution.split("x");
        width=Integer.parseInt(resArr[0]);
        height=Integer.parseInt(resArr[1]);

    }
    public void goToMainMenu() throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SproutLauncher.class.getClassLoader().getResource(
                        "MainMenu.fxml")
        );
        mainMenuParent = loader.load();
        Scene mainMenu = new Scene(mainMenuParent);
        Stage window = (Stage) resolutions.getScene().getWindow();
        window.setScene(mainMenu);
        window.show();
    }

}
