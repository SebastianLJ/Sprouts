package Controller;

import Exceptions.InvalidFileSyntax;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class FileSimulationController implements Initializable {
    private SproutController sproutController = new SproutController();
    public Label filenameLabel;
    public ListView moveList;
    private String filename;
    private long simSpeed = 500; //speed in ms
    private ArrayList<String> moves = new ArrayList<>();

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

    //returns linenumber of syntax error or 0 if no error
    public int validateFile() throws IOException {
        File file = new File(filename);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        int lineNumber = 1;
        if (!line.matches("\\d+")) {
            moves = new ArrayList<>();
            return lineNumber;
        }
        moves.add(line);
        while ((line = reader.readLine()) != null) {
            lineNumber++;
            if (!line.matches("\\d+\\s\\d+")) {
                moves = new ArrayList<>();
                return lineNumber;
            }
            moves.add(line);
            moveList.getItems().add(line);
        }
        return 0;
    }

    public void runFile(ActionEvent event) throws IOException, InterruptedException {
        //init starting points
        int n = Integer.parseInt(moves.get(0));
        try {
            sproutController.attemptInitializeGame(n);
            System.out.println("successfully initialized game");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //execute moves
        String[] move;
        boolean legalGame = true;
        for (int i = 1; i < moves.size(); i++) {
            move = moves.get(i).split("\\s");
            Thread.sleep(simSpeed);
            try {
                sproutController.attemptDrawEdgeBetweenNodes(Integer.parseInt(move[0]) - 1, Integer.parseInt(move[1]) - 1);
                System.out.println("successfully executed move : from " + move[0] + " to " + move[1]);
            } catch (Exception e) {
                System.out.println("Failed at executing move : from " + move[0] + " to " + move[1]);
                System.out.println(e.getMessage());
                legalGame = false;

            }
        }
        if (legalGame) {
            System.out.println("Legal game - File successfully simulated");
        } else {
            System.out.println("Illegal game - File unsuccessfully simulated");
        }
    }
}
