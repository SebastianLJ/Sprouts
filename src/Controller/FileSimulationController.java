package Controller;

import Exceptions.InvalidFileSyntax;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class FileSimulationController implements Initializable {
    private SproutController sproutController = new SproutController();
    public Label filenameLabel;
    private String filename;
    private long simSpeed = 500; //speed in ms

    public FileSimulationController() {
    }

    void setFileName(String filename) {
        this.filename = filename;
        filenameLabel.setText(filenameLabel.getText() + filename);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    private void goToScene(ActionEvent event, String fxmlToLoad) throws IOException {
        Parent parent = FXMLLoader.load(
                Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource(
                        fxmlToLoad)
                ));

        Scene scene = new Scene(parent);

        //This line gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(scene);
        window.show();
    }

    public void goToMainMenu(ActionEvent event) throws IOException {
        goToScene(event, "MainMenu.fxml");
    }

    public void goToEnterFileToSimulate(ActionEvent event) throws IOException {
        goToScene(event, "EnterFileName.fxml");
    }

    public boolean validateFile() throws Exception {
        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        int lineNumber = 1;
        if (!line.matches("\\d+")) {
            throw new InvalidFileSyntax(lineNumber);
        }
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (!line.matches("\\d+\\s\\d+")) {
                throw new InvalidFileSyntax(lineNumber);
            }
        }
        return true;
    }

    public void runFile(ActionEvent event) throws IOException, InterruptedException {
        File file = new File(filename);
        BufferedReader reader = new BufferedReader((new FileReader(file)));
        String line = reader.readLine();
        int linenumber = 1;

        //init starting points
        int n = Integer.parseInt(line);
        try {
            sproutController.attemptInitializeGame(n);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //execute moves
        String[] move = new String[2];
        while((line = reader.readLine()) != null) {
            move = line.split("\\s");
            Thread.sleep(simSpeed);
            try {
                sproutController.attemptDrawEdgeBetweenNodes(Integer.parseInt(move[0]), Integer.parseInt(move[1]));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
