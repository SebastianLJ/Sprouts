package Controller;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SettingsController extends Controller implements Initializable {

    @FXML
    private ChoiceBox<String> resolutions = new ChoiceBox<>();
    private double width;
    private double height;
    //@FXML private BorderPane borderPane;
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        resolutions.getItems().add("640x480");
        resolutions.getItems().add("800x600");
        resolutions.getItems().add("1024x768");
        resolutions.getItems().add("1280x960");
        resolutions.getItems().add("1366x768");
        resolutions.getItems().add("1440x1080");
        resolutions.getItems().add("1600x900");
        resolutions.getItems().add("1920x1080");
        stage=(Stage) resolutions.getScene().getWindow();

    }


    public void applyResolutionChange(){
        String chosenResolution = resolutions.getValue();
        String resArr[] = chosenResolution.split("x");
        width = Double.parseDouble(resArr[0]);
        height = Double.parseDouble(resArr[1]);
        stage.setWidth(width);
        stage.setHeight(height);
/*
Are we allowed to communicate with SproutLauncher or do we have to go through SproutController? (read report)
        SproutLauncher.stage.setWidth(width);
        SproutLauncher.stage.setHeight(height);
*/

    }
    public void goToMainMenu(ActionEvent event) throws IOException {
        changeScene(event, "MainMenu.fxml");
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

}
