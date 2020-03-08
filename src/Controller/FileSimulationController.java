package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FileSimulationController implements Initializable {
    public Label filenameLabel;
    private String filename;

    void setFileName(String filename) {
        this.filename = filename;
        filenameLabel.setText(filenameLabel.getText() + filename);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void goToMainMenu(ActionEvent event) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(
                Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource(
                        "MainMenu.fxml")
                ));

        Scene mainMenuScene = new Scene(mainMenuParent);

        //This line gets the Stage information
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

        window.setScene(mainMenuScene);
        window.show();
    }

    public void goToEnterFileToSimulate(ActionEvent event) throws IOException {
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
}
