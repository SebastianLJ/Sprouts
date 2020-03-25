package Controller;

import Exceptions.InvalidFileSyntax;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EnterFileNameController implements Initializable {
    @FXML public TextField filenameInputField;
    @FXML public Label fileResponseLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void startSimulationOfFile(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                EnterFileNameController.class.getClassLoader().getResource(
                        "GameViewForFileSimulation.fxml")
        );

        Parent fileSimulationParent = loader.load();

        Scene fileSimulationScene = new Scene(fileSimulationParent);

        String filename = filenameInputField.getText() + ".txt";

        FileSimulationController fileSimulationController =
                loader.getController();
        fileSimulationController.setFileName(filename);
        try {
            int n = fileSimulationController.validateFile();
            if (n == 0) {
                fileResponseLabel.setText("File syntax is valid");
                System.out.println("File syntax is valid");
                //This line gets the Stage information
                Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

                window.setScene(fileSimulationScene);
                window.show();
            } else {
                //todo handle syntax error in JavaFX
                fileResponseLabel.setText("Syntax error at line " + n);
                System.out.println("Syntax error at line " + n);
            }
        } catch (Exception e) {
            fileResponseLabel.setText("File not found");
        }
    }
}
