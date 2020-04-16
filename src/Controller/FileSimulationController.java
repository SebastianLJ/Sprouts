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

    public final Tooltip toolTip = new Tooltip();

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
        toolTip.setShowDelay(Duration.ZERO);
        toolTip.setShowDuration(Duration.INDEFINITE);
        toolTip.setHideDelay(Duration.ZERO);
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

        if (timeline.getStatus() == Status.STOPPED) {

            // Reset model
            sproutController.resetGame();

            // Reset frame pane: Table and response labels
            resetFramePane();

            // Reset game pane
            view.resetView(gamePane);

            timeline.stop();

            legalGame = true;
            i = 0;
            timeline.setCycleCount(moves.size());
            timeline.play();
        }
    }

    private Timeline createTimeline() {
        return new Timeline(new KeyFrame(Duration.seconds(0.5), new EventHandler<>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println(legalGame);
                if (legalGame) {
                    //init starting points
                    String[] move = {"-1", "-1"};
                    int n = Integer.parseInt(moves.get(0));
                    try {
                        if (i == 0) {
                            setColorForCell("-fx-background-color: green");
                            sproutController.attemptInitializeGame(n);
                            view.initializeNodes(gamePane);
                            System.out.println("successfully initialized game with " + n + " nodes");
                        } else {
                            //execute moves
                            setColorForCell("-fx-background-color: green");
                            move = moves.get(i).split("\\s");
                            sproutController.attemptDrawEdgeBetweenNodes(Integer.parseInt(move[0]) - 1, Integer.parseInt(move[1]) - 1);
                            view.updateCanvas(gamePane);
                            System.out.println("successfully executed move : from " + move[0] + " to " + move[1]);
                        }
                        i++;
                    } catch (Exception e) {
                        if (e instanceof NumberOfInitialNodesException) {
                            setColorForCell("-fx-background-color: red");
                            setText(String.valueOf(n),"");
                            prepareTooltip(e.getMessage());
                            legalGame = false;
                        } else if (e instanceof IllegalNodesChosenException) {
                            setColorForCell("-fx-background-color: red");
                            setText(move[0], move[1]);
                            prepareTooltip("Failed at executing move : from " + move[0] + " to " + move[1] + "\n" + e.getMessage());
                            legalGame = false;
                        } else if (e instanceof GameOverException) {
                            gameResponseLabel.setText(e.getMessage());
                            timeline.stop();
                        }
                    }
                    if (legalGame && i == moves.size()) {
                        System.out.println("Legal game - File successfully simulated");
                        gameResponseLabel.setText("Game is incomplete.");
                        timeline.stop();
                        // Should timeline.stop(); not be here ?

                    } else if (!legalGame) {
                        System.out.println("Illegal game - File unsuccessfully simulated");
                        gameResponseLabel.setText("Game stopped prematurely.");
                        timeline.stop();
                    }
                }
            }

            private void setText(String n1, String n2) {
                cells.get(i+1).setText(n1 + " " + n2 + " (Hover to learn more)");
            }

            private void setColorForCell(String s) {
                if (i % 2 == 0 && s.contains("green")) {
                    cells.get(i+1).setStyle("-fx-background-color: darkgreen"); //TODO +1?
                } else {
                    cells.get(i+1).setStyle(s);
                }
            }

            private void prepareTooltip(String s) {
                toolTip.setText(s);
                cells.get(i+1).setTooltip(toolTip);
            }
        }));
    }

    private void resetFramePane() {

        gameResponseLabel.setText("");

        int i = 0;
        for (ListCell<String> cell : cells) {
            // reset cell error bar
            cell.setTooltip(null);
            // reset cell hover text
            if (cell.getText() != null) cell.setText(cell.getText().replace(" (Hover to learn more)", ""));
            // reset cell color
            cell.setStyle(i++ % 2 == 0 ? "-fx-background-color: white;" : "-fx-background-color: GHOSTWHITE;");
        }
    }
}
