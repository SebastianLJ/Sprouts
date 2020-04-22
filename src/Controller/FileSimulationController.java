package Controller;

import Exceptions.GameOverException;
import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import View.View;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.animation.Animation.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;

public class FileSimulationController implements Initializable {
    public Pane gamePane;
    public Label gameResponseLabel;
    private SproutController sproutController = new SproutController();
    public ListView<String> moveList;
    private String filename;
    private ArrayList<String> moves = new ArrayList<>();

    private ArrayList<ListCell<String>> cells = new ArrayList<>();

    private View view;
    private boolean legalGame;
    private Timeline timeline = createTimeline();
    private int i = 0;

    public FileSimulationController() {
    }

    void setFileName(String filename) {
        String userDirectory = System.getProperty("user.dir");
        this.filename = userDirectory + File.separator + "gameTestFiles" + File.separator + filename;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view = new View(sproutController.getSproutModel());
        Platform.runLater(() -> {
            sproutController.updateSize(gamePane.getWidth(), gamePane.getHeight());
        });
        moveList.setCellFactory(listView -> {
            TooltipCell cell = new TooltipCell();
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
        moveList.getItems().add(line);
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

    public void runFile(ActionEvent event) {
        // Reset model
        sproutController.resetGame();

        // Reset ListView
        view.resetCells(cells);

        // Reset view
        view.resetGameView(gamePane);

        // Reset GameResponseLabel
        view.setGameResponseLabelText(gameResponseLabel, "");

        timeline.stop();

        legalGame = true;
        i = 0;
        timeline.setCycleCount(moves.size());
        timeline.play();
    }

    private Timeline createTimeline() {
        return new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<>() {

            String message;
            String color;

            @Override
            public void handle(ActionEvent event) {
                if (legalGame) {
                    //init starting points
                    String[] move = {"-1", "-1"};
                    try {
                        if (i == 0) {
                            int n = Integer.parseInt(moves.get(0));
                            sproutController.attemptInitializeGame(n);
                            view.initializeNodes(gamePane);
                            message = "successfully initialized game";
                        } else {
                            //execute moves
                            move = moves.get(i).split("\\s");
                            sproutController.attemptDrawEdgeBetweenNodes(Integer.parseInt(move[0]) - 1, Integer.parseInt(move[1]) - 1);
                            view.updateCanvas(gamePane);
                            message = "successfully executed move : from " + move[0] + " to " + move[1];
                        }
                        color = i % 2 == 0 ? "-fx-background-color: darkgreen": "-fx-background-color: green";
                    } catch (NumberOfInitialNodesException e) {
                        color = "-fx-background-color: red";
                        message = e.getMessage();
                        legalGame = false;
                    } catch (IllegalNodesChosenException e) {
                        color = "-fx-background-color: red";
                        message = "Failed at executing move : from " + move[0] + " to " + move[1] + "\n" + e.getMessage();
                        legalGame = false;
                    } catch (GameOverException e) {
                        color = "-fx-background-color: yellow";
                        message = e.getMessage();
                        gameResponseLabel.setText(e.getMessage());
                        timeline.stop();
                    }

                    i++;
                    view.setColorForCell(color, cells.get(i));
                    view.prepareTooltip(message, cells.get(i));

                    if (legalGame && i == moves.size()) {
                        System.out.println("Legal game - File successfully simulated");
                        view.setGameResponseLabelText(gameResponseLabel, "Game is incomplete.");
                        // Should timeline.stop(); not be here ? Nej for den kører kun til moves.size() se condition for if statement

                    } else if (!legalGame) {
                        System.out.println("Illegal game - File unsuccessfully simulated");
                        view.setGameResponseLabelText(gameResponseLabel, "Game stopped prematurely.");
                        timeline.stop();
                    }
                }
            }
        }));
    }
}
