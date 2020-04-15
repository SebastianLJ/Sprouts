package Controller;

import Exceptions.IllegalNodesChosenException;
import Exceptions.NumberOfInitialNodesException;
import View.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class GameController implements Initializable {
    @SuppressWarnings("WeakerAccess")
    public Pane gamePane;
    public AnchorPane anchorPane;

    private SproutController sproutController;
    private int gameType; // 0 is clickToDraw and 1 is dragToDraw
    private int numberOfInitialNodes;
    private final int CLICK_TO_DRAW_MODE = 0;
    private final int DRAG_TO_DRAW_MODE = 1;
    private View view;
    private boolean nodeIsPrimed;
    private Circle primedNode;

    void setGameType(int whichGameType) {
        gameType = whichGameType;
    }

    @SuppressWarnings("unused")
    public void goToMainMenu(ActionEvent event) throws IOException {
        Parent mainMenuParent = FXMLLoader.load(
                Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource(
                        "MainMenu.fxml")
                ));

        Scene mainMenuScene = new Scene(mainMenuParent);

        //This line gets the Stage information
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();

        window.setScene(mainMenuScene);
        window.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create connection to the sproutController that controls the model
        sproutController = new SproutController();

        // Create connection to view that updates view with information from the model
        view = new View(sproutController.getSproutModel());

        Platform.runLater(() -> {
            // Tell the model how big the game is
            sproutController.updateSize(gamePane.getWidth(), gamePane.getHeight());

            try {
                sproutController.attemptInitializeGame(numberOfInitialNodes);
            } catch (NumberOfInitialNodesException e) {
                try {
                    sproutController.attemptInitializeGame(2);
                } catch (NumberOfInitialNodesException ex) {
                    ex.printStackTrace();
                }
            }

            view.initializeNodes(gamePane);
            initializeListenerForStartingNodes();
        });
    }

    private void initializeListenerForStartingNodes() {
        for (Model.Node node : sproutController.getNodes()) {
            if (gameType == CLICK_TO_DRAW_MODE) {
                node.getShape().setOnMouseClicked(this::clickToDraw);
            } else {
                // Another method for drag to draw game mode
            }
        }
    }

    private void clickToDraw(MouseEvent mouseEvent) {
        if (!nodeIsPrimed) {
            primeNodeToDrawEdgeFrom((Circle) mouseEvent.getSource());
        } else {
            try {
                attemptDrawEdgeBetweenNodes(primedNode, (Circle) mouseEvent.getSource());
                updateCanvas();
            } catch (IllegalNodesChosenException e) {
                view.illegalEdgeAnimation(gamePane, sproutController.createEdge(primedNode, (Circle) mouseEvent.getSource()));
                view.unPrimeNode(primedNode);
                nodeIsPrimed = false;
            }
        }
    }

    @SuppressWarnings("unused")
    public void onMouseClicked(MouseEvent mouseClicked) {
        if (!(mouseClicked.getTarget() instanceof Circle) && primedNode != null) {
            view.unPrimeNode(primedNode);
            nodeIsPrimed = false;
        }
    }

    private void updateCanvas() {
        Circle newNode = view.updateCanvas(gamePane); // update canvas and get newNode created
        newNode.setOnMouseClicked(this::clickToDraw); // Add listener to the new node
    }

    private void attemptDrawEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException {
        sproutController.attemptDrawEdgeBetweenNodes(startNode, endNode);
        view.unPrimeNode(startNode);
        nodeIsPrimed = false; // A edge has been drawn and the node i no longer primed
    }

    /**
     * Save the clickedNode and prime it for a draw
     *
     * @param clickedNode the node the user has clicked
     */
    private void primeNodeToDrawEdgeFrom(Circle clickedNode) {
        view.primeNode(clickedNode);
        primedNode = clickedNode;
        nodeIsPrimed = true;
    }

    @SuppressWarnings("unused")
    public void mouseDraggedHandler(MouseEvent mouseDragged) {
        if (gameType == DRAG_TO_DRAW_MODE) {
            sproutController.beginDrawing(mouseDragged);
            if (sproutController.isCollided()) {
                view.setUpCollisionSettings(mouseDragged);
            }
        }
    }

    @SuppressWarnings("unused")
    public void mousePressedHandler(MouseEvent mousePressed) {
        if (gameType == DRAG_TO_DRAW_MODE) {
            sproutController.setupDrawing(mousePressed);
            view.setUpDrawingSettings(mousePressed, gamePane);
        }
    }

    @SuppressWarnings("unused")
    public void mouseReleasedHandler(MouseEvent mouseReleased) {
        if (gameType == DRAG_TO_DRAW_MODE) {
            sproutController.completeDrawing();
            view.setUpSuccessfulPathSettings(mouseReleased);
        }


    }

    void setNumberOfInitialNodes(int numberOfInitialNodes) {
        this.numberOfInitialNodes = numberOfInitialNodes;
    }
}
