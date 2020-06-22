package Controller;

import Exceptions.*;
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
import javafx.scene.shape.Circle;
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
    public ToggleGroup drawMode;
    public Label currentPlayerNameLabel;

    public ListView<String> moveList;
    private String filename;
    private ArrayList<String> moves = new ArrayList<>();

    private ArrayList<ListCell<String>> cells = new ArrayList<>();

    private View view;
    private boolean legalGame;
    private Timeline timeline = createTimeline();
    private int i = 0;

    private boolean smartGame;

    public FileSimulationController() {
        super();
    }

    public void setFileName(String filename) {
        this.filename=filename;
    }

    /**
     * Initialize the scene connecting it you the view class that handles the visual updates to the scene
     * Also sets up the cell factory that allow to create custom cells for the list showing the moves in the simulated files.
     * @author Emil Sommer Desler
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

        drawMode.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> {
            smartGame = !smartGame;
            runFile();
        });
    }

    public void goToMainMenu(ActionEvent event) throws IOException {
        changeScene(event, "MainMenu.fxml");
    }

    public void goToEnterFileToSimulate(ActionEvent event) throws IOException {
        changeScene(event, "EnterFileName.fxml");
    }

    /**
     * Checks file for invalid syntax using regex.
     * @author Sebastian Lund
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
     * Runs the simulation of the given file when the user clicks the button.
     * @author Sebastian Lund & Emil Sommer Desler
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

        // Reset player name display
        view.showCurrentPlayerName(currentPlayerNameLabel, "");

        // Reset simulator
        resetSimulator();
    }

    public void resetSimulator() {
        timeline.stop();
        legalGame = true;
        i = 0;
        timeline.setCycleCount(moves.size());
        timeline.play();
    }

    /**
     * Creates the timeline object that handles the simulation of the text file.
     * @author Sebastian Lund & Emil Sommer Desler
     * @return The created timeline
     */
    private Timeline createTimeline() {
        return new Timeline(new KeyFrame(Duration.seconds(1), new EventHandler<>() {

            String gameResponse, toolTipMessage;
            String color;
            int startNodeName, endNodeName;

            @Override
            public void handle(ActionEvent event) {
                if (legalGame) {
                    //init starting points
                    String[] move = {"-1", "-1"};
                    try {
                        if (i == 0) {
                            int n = Integer.parseInt(moves.get(0));
                            attemptInitializeGame(n);
                            view.setGameResponseLabelText(gameResponseLabel, "");
                            view.initializeNodes(gamePane);
                        } else {
                            //execute moves
                            move = moves.get(i).split("\\s");

                            startNodeName = Integer.parseInt(move[0]) - 1;
                            endNodeName = Integer.parseInt(move[1]) - 1;
                            view.showCurrentPlayerName(currentPlayerNameLabel, getCurrentPlayerName());

                            if (smartGame) {
                                attemptDrawSmartEdgeBetweenNodes(startNodeName, endNodeName);
                            } else {
                                attemptDrawEdgeBetweenNodes(startNodeName, endNodeName);
                            }
                            view.updateCanvasClick(gamePane);
                            toolTipMessage = "successfully executed move : from " + move[0] + " to " + move[1];
                        }
                        color = i % 2 == 0 ? "-fx-background-color: darkgreen": "-fx-background-color: green";
                    } catch (NumberOfInitialNodesException e) {
                        color = "-fx-background-color: red";
                        legalGame = false;
                        view.prepareTooltip(e.getMessage(), cells.get(i+1));
                    } catch (IllegalNodesChosenException e) {
                        color = "-fx-background-color: red";
                        gameResponse = e.getMessage();
                        toolTipMessage = "Failed to execute move from " + move[0] + " to " + move[1] + "\n" + e.getMessage();
                        legalGame = false;
                    } catch (CollisionException e) {
                        Circle startNode = getSproutModel().getNodeFromId(startNodeName);
                        Circle endNode = getSproutModel().getNodeFromId(endNodeName);
                        color = "-fx-background-color: red";
                        view.illegalEdgeAnimation(gamePane, getIllegalEdgeBetweenNodes(startNode, endNode));
                        gameResponse = e.getMessage();
                        toolTipMessage = "Failed to execute move from " + move[0] + " to " + move[1];
                        timeline.stop();
                    } catch (GameEndedException e) {
                        color = color.equals("-fx-background-color: green") ? "-fx-background-color: darkgreen" : "-fx-background-color: green";
                        gameResponse = e.getMessage();
                        toolTipMessage = "Successfully executed move from " + move[0] + " to " + move[1];
                        view.updateCanvasClick(gamePane);
                        timeline.stop();
                    } catch (InvalidPath invalidPath) {
                        color = "-fx-background-color: red";
                        gameResponse = invalidPath.getMessage();
                        toolTipMessage = "Failed to execute move from " + move[0] + " to " + move[1];
                        view.illegalPath(gamePane, invalidPath.getPath());
                        timeline.stop();
                    } catch (NoValidEdgeException e) {
                        color = "-fx-background-color: red";
                        gameResponse = e.getMessage();
                        toolTipMessage = "Failed to execute move from " + move[0] + " to " + move[1];
                        timeline.stop();
                    }
                    i++;
                    view.setColorForCell(color, cells.get(i));
                    view.prepareTooltip(toolTipMessage,cells.get(i));
                    view.setGameResponseLabelText(gameResponseLabel, gameResponse);

                    if (legalGame && i == moves.size()) {
                        System.out.println("Legal game - File successfully simulated");
                        view.setGameResponseLabelText(gameResponseLabel, "The game is incomplete");
                    } else if (!legalGame) {
                        System.out.println("Illegal game - File unsuccessfully simulated");
                        timeline.stop();
                    }
                }
            }
        }));
    }

}
