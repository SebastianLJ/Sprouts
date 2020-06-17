package Controller;

import Exceptions.CollisionException;
import Exceptions.GameOverException;
import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import Utility.TooltipCell;
import View.View;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FileSimulationController extends SproutController implements Initializable {
    public Pane gamePane;
    public Label gameResponseLabel;
    public ListView<String> moveList;
    private String filename;
    private ArrayList<String> moves = new ArrayList<>();

    private ArrayList<ListCell<String>> cells = new ArrayList<>();

    private View view;
    private boolean legalGame;
    private Timeline timeline = createTimeline();
    private int i = 0;

    public FileSimulationController() {
        super();
    }

    void setFileName(String filename) {
        this.filename=filename;
    }

    /**
     * @author Emil Sommer Desler
     * Initialize the scene connecting it you the view class that handles the visual updates to the scene
     * Also sets up the cell factory that allow to create custom cells for the list showing the moves in the simulated files.
     * @param url Required - Not used.
     * @param resourceBundle Required - Not used.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        view = new View(getSproutModel());
        Platform.runLater(() -> {
            updateSize(gamePane.getWidth(), gamePane.getHeight());
        });
        moveList.setCellFactory(listView -> {
            TooltipCell cell = new TooltipCell();
            cells.add(cell);
            return cell;
        });
    }

    public void goToMainMenu(ActionEvent event) throws IOException {
        changeScene(event, "MainMenu.fxml");
    }

    public void goToEnterFileToSimulate(ActionEvent event) throws IOException {
        changeScene(event, "EnterFileName.fxml");
    }

    /**
     * @author Sebastian Lund
     * Checks file for invalid syntax using regex.
     * @return 0 if file is valid / linenumber of invalid syntax if file is invalid.
     * @throws IOException file reader exception.
     */
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

    /**
     * @author Sebastian Lund & Emil Sommer Desler
     * Runs the simulation of the given file when the user clicks the button.
     */
    public void runFile() {
        // Reset model
        resetGame();

        // Reset ListView
        view.resetCells(cells);

        // Reset view
        view.resetGameView(gamePane);

        // Reset GameResponseLabel
        view.setGameResponseLabelText(gameResponseLabel, "");

        // Reset simulator
        timeline.stop();
        legalGame = true;
        setGameOnGoing(false);
        i = 0;
        timeline.setCycleCount(moves.size());
        timeline.play();
    }

    /**
     * @author Sebastian Lund & Emil Sommer Desler
     * Creates the timeline object that handles the simulation of the text file.
     * @return The created timeline
     */
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
                            attemptInitializeGame(n);
                            view.initializeNodes(gamePane);
                            message = "successfully initialized game";
                        } else {
                            //execute moves
                            move = moves.get(i).split("\\s");
                            attemptDrawEdgeBetweenNodes(Integer.parseInt(move[0]) - 1, Integer.parseInt(move[1]) - 1);
                            view.updateCanvasClick(gamePane);
                            message = "successfully executed move : from " + move[0] + " to " + move[1];
                        }
                        color = i % 2 == 0 ? "-fx-background-color: darkgreen": "-fx-background-color: green";
                    } catch (NumberOfInitialNodesException e) {
                        color = "-fx-background-color: red";
                        message = e.getMessage();
                        legalGame = false;
                        view.showGameResponse(gameResponseLabel, e.getMessage());
                    } catch (IllegalNodesChosenException e) {
                        color = "-fx-background-color: red";
                        message = "Failed at executing move : from " + move[0] + " to " + move[1] + "\n" + e.getMessage();
                        legalGame = false;
                        view.showGameResponse(gameResponseLabel, e.getMessage());
                    } catch (GameOverException e) {
                        color = "-fx-background-color: yellow";
                        message = e.getMessage();
                        timeline.stop();
                        view.showGameResponse(gameResponseLabel, e.getMessage());
                    } catch (CollisionException e) {
                        color = "-fx-background-color: red";
                        message = "Failed at executing move : from " + move[0] + " to " + move[1];
                        timeline.stop();
                        view.showGameResponse(gameResponseLabel, e.getMessage());
                    }

                    i++;
                    view.setColorForCell(color, cells.get(i));
                    view.prepareTooltip(message, cells.get(i));

                    if (legalGame && i == moves.size()) {
                        System.out.println("Legal game - File successfully simulated");
                        view.setGameResponseLabelText(gameResponseLabel, "Game is incomplete.");
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
