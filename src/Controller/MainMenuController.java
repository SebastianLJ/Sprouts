package Controller;

import Exceptions.NotEnoughInitialNodesException;
import io.cucumber.java.sl.In;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
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

    public void startClickToDrawGame(ActionEvent event) throws IOException {
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

    public void startDragToDrawGame(ActionEvent event) throws IOException {
        if (promptedForClickGame) {
            removeAskForNumberOfStartingNodes();
            promptedForNumberOfStartingNodes = false;
        }
        whichGameType = 1;
        promptedForDragGame = true;
        askForNumberOfStartingNodes();
    }

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

    private void askForNumberOfStartingNodes() {
        if (!promptedForNumberOfStartingNodes) {
            HBox container = new HBox();
            container.setSpacing(4);

            TextField input = new TextField();
            input.setPromptText("# of nodes");
            input.setPadding(new Insets(8));
            input.setMaxWidth(mainMenu.getWidth()/2);

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

            mainMenu.getChildren().add(mainMenuIndex + whichGameType, container);

            promptedForNumberOfStartingNodes = true;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
