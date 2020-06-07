package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class EnterFileNameController extends Controller implements Initializable {
    @FXML public TextField filenameInputField;
    @FXML public Label fileResponseLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    /**
     * @author Sebastian Lund
     * Gets filename from user input and performs syntax check on the given file.
     * Sets fileResponseLabel to "Syntax error at line n" / "File syntax is valid".
     * @param event user event for proceeding to next stage.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     */
    public void startSimulationOfFile(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                EnterFileNameController.class.getClassLoader().getResource(
                        "GameViewForFileSimulation.fxml")
        );

        Parent fileSimulationParent = loader.load();

        Scene fileSimulationScene = new Scene(fileSimulationParent);

        String filename = filenameInputField.getText();

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
        } catch (Exception e) {;
            if (e.getMessage() == null) {
                fileResponseLabel.setText("File is empty");
            } else {
                fileResponseLabel.setText("File not found");
            }
        }
    }

    /**
     * @author Noah Bastian Christiansen
     * Let's user choose a file to simulate by clicking the button choose file.
     * Sets textfield to be the path of the chosen file.
     * @param event
     */

    public void chooseFile(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();
        File chosenFile = fileChooser.showOpenDialog(window);
        try {
            filenameInputField.setText(chosenFile.getAbsolutePath());
        }
        catch (NullPointerException e){
            System.out.println("No file chosen");
        }
    }

    /**
     * @author Emil Sommer Desler
     * Return the user to the main menu when clicking the button "Main Menu" in the application.
     * @param event The mouse click on the button.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     */
    public void goToMainMenu(ActionEvent event) throws IOException {
        changeScene(event, "MainMenu.fxml");
    }
}
