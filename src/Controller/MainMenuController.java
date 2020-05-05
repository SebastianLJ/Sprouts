package Controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainMenuController implements Initializable {

    public VBox mainMenu;

    private final int mainMenuIndex = 2;

    private int whichGameType;
    private boolean promptedForNumberOfStartingNodes;
    private boolean promptedForDragGame;
    private boolean promptedForClickGame;

    private DoubleProperty fontSize = new SimpleDoubleProperty(10);

    /**
     * @author Emil Sommer Desler
     * This method in run on a button click and starts either a click to draw game or a drag to draw game.
     * @param event The mouse click on the button.
     * @param numberOfInitialNodes User inputs how many nodes the game must start with.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     */
    private void startGame(MouseEvent event, int numberOfInitialNodes) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SproutLauncher.class.getClassLoader().getResource(
                        "GameView.fxml")
        );

        GameController controller = new GameController();
        controller.setGameType(whichGameType);
        controller.setNumberOfInitialNodes(numberOfInitialNodes);

        loader.setController(controller);

        Parent gameViewParent = loader.load();

        Scene gameViewScene = new Scene(gameViewParent);

        //This line gets the Stage information
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

        window.setScene(gameViewScene);
        window.show();
    }

    public void startClickToDrawGame() {
        if (promptedForDragGame) {
            removeAskForNumberOfStartingNodes();
            promptedForNumberOfStartingNodes = false;
        }
        whichGameType = 0;
        promptedForClickGame = true;
        askForNumberOfStartingNodes();
    }

    private void removeAskForNumberOfStartingNodes() {
        mainMenu.getChildren().remove(mainMenuIndex + whichGameType);
    }

    public void startDragToDrawGame() {
        if (promptedForClickGame) {
            removeAskForNumberOfStartingNodes();
            promptedForNumberOfStartingNodes = false;
        }
        whichGameType = 1;
        promptedForDragGame = true;
        askForNumberOfStartingNodes();
    }

    /**
     * @author Emil Sommer Desler
     * This method is executed when the user decides to simulate a file.
     * This method opens a display where the user can enter the name of the file.txt he want to simulate.
     * @param event The mouse click on the button.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     */
    public void startEnterFileName(ActionEvent event) throws IOException {
        Parent enterFileNameParent = FXMLLoader.load(
                Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource(
                        "EnterFileName.fxml")
                ));

        Scene enterFileNameScene = new Scene(enterFileNameParent);

        //This line gets the Stage information
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

        window.setScene(enterFileNameScene);
        window.show();
    }

    /**
     * @author Emil Sommer Desler
     * Creates the dialog that ask the number of how many starting nodes
     * to begin the game with.
     */
    private void askForNumberOfStartingNodes() {
        if (!promptedForNumberOfStartingNodes) {
            HBox container = new HBox();
            container.setSpacing(4);

            TextField input = new TextField();
            input.setPromptText("# of nodes");
            input.setPadding(new Insets(8));

            // Watches the user input is only numbers and no more than 99 starting nodes.
            input.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    input.setText(newValue.replaceAll("[^\\d]", ""));
                }
                if (input.getText().length() > 2) {
                    input.setText(oldValue);
                }
            });

            Button startGameButton = new Button("Start Game");
            startGameButton.setPadding(new Insets(8));
            startGameButton.setWrapText(true);

            startGameButton.setOnMouseClicked(event -> {
                try {
                    if (!input.getText().isEmpty()) {
                        startGame(event, Integer.parseInt(input.getText()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            container.getChildren().addAll(input, startGameButton);
            container.setAlignment(Pos.CENTER);
            HBox.setHgrow(input, Priority.ALWAYS);

            mainMenu.getChildren().add(mainMenuIndex + whichGameType, container);

            promptedForNumberOfStartingNodes = true;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
}
