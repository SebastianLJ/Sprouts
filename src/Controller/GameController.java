package Controller;

import Exceptions.NotEnoughInitialNodesException;
import Model.Point;
import View.View;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    // Must be public
    public Pane gamePane;
    private SproutController sproutController;
    private int gameType; // 0 is clickToDraw and 1 is dragToDraw
    private int numberOfInitialNodes;
    private final int CLICK_TO_DRAW_MODE = 0;
    private final int DRAG_TO_DRAW_MODE = 1;
    private View view;

    void setGameType(int whichGameType) {
        gameType = whichGameType;
    }

    // Is used
    public void goToMainMenu(ActionEvent event) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(
                Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource(
                        "MainMenu.fxml")
                ));

        Scene mainMenuScene = new Scene(mainMenuParent);

        //This line gets the Stage information
        Stage window = (Stage)((Node) event.getSource()).getScene().getWindow();

        window.setScene(mainMenuScene);
        window.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sproutController = new SproutController();
        view = new View(sproutController.getSproutModel());
        try {
            sproutController.attemptInitializeGame(numberOfInitialNodes);
        } catch (NotEnoughInitialNodesException e) {
            e.printStackTrace();
        }
        view.updateCanvas(gamePane);
    }

    // Is used
    public void mouseDraggedHandler(MouseEvent mouseDragged) {
        if (gameType == DRAG_TO_DRAW_MODE) {
            sproutController.beginDrawing(mouseDragged);
            if(sproutController.isCollided()) {view.setUpCollisionSettings(mouseDragged);}
    }}

    // Is used
    public void mousePressedHandler(MouseEvent mousePressed) {
        if (gameType == DRAG_TO_DRAW_MODE) {
            sproutController.setupDrawing(mousePressed);
            view.setUpDrawingSettings(mousePressed, gamePane);
        }
    }

    // Is used
    public void mouseReleasedHandler(MouseEvent mouseReleased) {
        if (gameType == DRAG_TO_DRAW_MODE) {
           sproutController.completeDrawing();
            view.setUpSuccessfulPathSettings(mouseReleased);
        }


        }

    public View getView() {
        return view;
    }

    public void setNumberOfInitialNodes(int numberOfInitialNodes) {
        this.numberOfInitialNodes = numberOfInitialNodes;
    }
}
