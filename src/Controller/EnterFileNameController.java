package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EnterFileNameController implements Initializable {
    @FXML public TextField filenameInputField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void startSimulationOfFile(ActionEvent event) throws IOException {

        String filename = filenameInputField.getText();

        // TODO test if filename is good.
        if (true) {
            FXMLLoader loader = new FXMLLoader(
                    EnterFileNameController.class.getClassLoader().getResource(
                            "GameViewForFileSimulation.fxml")
            );

            Parent fileSimulationParent = loader.load();

            Scene fileSimulationScene = new Scene(fileSimulationParent);

            FileSimulationController controller =
                    loader.getController();
            controller.setFileName(filename);

            //This line gets the Stage information
            Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

            window.setScene(fileSimulationScene);
            window.show();
        } else {
            // Notify user on bad filename
        }
    }
}
