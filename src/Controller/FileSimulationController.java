package Controller;

import Exceptions.NumberOfInitialNodesException;
import View.View;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.tools.Tool;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class FileSimulationController implements Initializable {
    public Pane gamePane;
    private SproutController sproutController = new SproutController();
    public ListView<String> moveList;
    private String filename;
    private ArrayList<String> moves = new ArrayList<>();

    private ArrayList<ListCell<String>> cells = new ArrayList<>();

    public final Tooltip toolTip = new Tooltip();

    private View view;

    public FileSimulationController() {
    }

    void setFileName(String filename) {
        this.filename = filename;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view = new View(sproutController.getSproutModel());
        Platform.runLater(() -> {
            sproutController.updateSize(gamePane.getWidth(), gamePane.getHeight());
        });
        moveList.setCellFactory(listView -> {
            ToolTipCell cell = new ToolTipCell();
            cells.add(cell);
            return cell;
        });
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
        //reset game
        sproutController.resetGame();

        // Reset view
        view.resetView(gamePane);

        moveList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<ActionEvent>() {

            private int i = 0;

            @Override
            public void handle(ActionEvent event) {
                //init starting points
                boolean legalGame = true;
                String[] move = {"-1", "-1"};
                int n = Integer.parseInt(moves.get(0));
                try {
                    if (i == 0) {
                        sproutController.attemptInitializeGame(n);
                        view.initializeNodes(gamePane);
                        System.out.println("successfully initialized game");
                    } else {
                        //execute moves
                        moveList.getSelectionModel().select(i - 1);
                        move = moves.get(i).split("\\s");
                        sproutController.attemptDrawEdgeBetweenNodes(Integer.parseInt(move[0]) - 1, Integer.parseInt(move[1]) - 1);
                        view.updateCanvas(gamePane);
                        System.out.println("successfully executed move : from " + move[0] + " to " + move[1]);
                    }
                    i++;
                } catch (NumberOfInitialNodesException e) {
                    prepareTooltip(e.getMessage());
                    System.out.println(e.getMessage());
                    legalGame = false;
                } catch (Exception e) {
                    prepareTooltip("Failed at executing move : from " + move[0] + " to " + move[1] + "\n" + e.getMessage());
                    System.out.println("Failed at executing move : from " + move[0] + " to " + move[1]);
                    System.out.println(e.getMessage());
                    legalGame = false;
                }
                if (legalGame && i == moves.size()) {
                    System.out.println("Legal game - File successfully simulated");
                } else if (i == moves.size()) {
                    System.out.println("Illegal game - File unsuccessfully simulated");
                }
            }

            private void prepareTooltip(String s) {
                toolTip.setText(s);
                cells.get(i).setTooltip(toolTip);
            }
        }));
        timeline.setCycleCount(moves.size());
        timeline.play();


    }
}
