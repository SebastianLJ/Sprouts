package Controller;

import Exceptions.*;
import View.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController extends SproutController implements Initializable {
    @FXML
    public Pane gamePane;

    private int gameMode; // 0 is clickToDraw and 1 is dragToDraw
    private int numberOfInitialNodes;
    private final int CLICK_TO_DRAW_MODE = 0;
    private final int DRAG_TO_DRAW_MODE = 1;
    private View view;
    private boolean theUserHasSelectedANode;
    private Circle selectedNode;
    private boolean dragged;
    private boolean isPathInit = false;
    private Parent mainMenuParent;
    @FXML public BorderPane borderPane;

    public GameController () {
        super();
    }

    void setGameMode(int whichGameType) {
        gameMode = whichGameType;
    }

    @SuppressWarnings("unused")
    public void goToMainMenu(ActionEvent event) throws IOException {
        changeScene(event, "MainMenu.fxml");
    }

    /**
     * @param url            Required - Not used.
     * @param resourceBundle Required - Not used.
     * @author Emil Sommer Desler
     * Connects this class to the gamecontroller called sproutController and the view that handles all visual updates of the game.
     * Tells the model the size of the game and initializes the game with the given amount of starting nodes.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Create connection to view that updates view with information from the model
        view = new View(getSproutModel());


        Platform.runLater(() -> {
            Stage stage= (Stage) gamePane.getScene().getWindow();
            stage.setWidth(SettingsController.width);
            stage.setHeight(SettingsController.height);

            // Tell the model how big the game is
            //borderPane.setPrefWidth(SettingsController.width);
            //borderPane.setPrefHeight(SettingsController.height);

            System.out.println("stage height, width: " + stage.getHeight() + ", " + stage.getWidth());
           // System.out.println("widthGameController: " + width);
           // System.out.println("heightGameController: " + height);
            stage.setResizable(false);
            updateSize(gamePane.getWidth(), gamePane.getHeight());


            try {
                attemptInitializeGame(numberOfInitialNodes);
            } catch (NumberOfInitialNodesException e) {
                try {
                    attemptInitializeGame(2);
                } catch (NumberOfInitialNodesException ex) {
                    ex.printStackTrace();
                }
            }

            view.initializeNodes(gamePane);
            initializeListenerForStackPane();
        });
    }

    /**
     * @author Noah Bastian Christiansen
     *
     *
     */
    private void initializeListenerForStackPane(){
        if(gameMode==CLICK_TO_DRAW_MODE) {
            for (Node stackPane : gamePane.getChildren()) {
                if (stackPane instanceof StackPane) {
                    stackPane.setOnMouseClicked(this::clickToDraw);
                }
            }
        }
    }

    /**
     * @param mouseEvent The mouse click the user performs.
     * @author Emil Sommer Desler & Noah Bastian Christiansen
     * This method handles the game where the user clicks nodes in order to draw edges between them.
     * By clicking a node the user primes that node for drawing and when clicking another node a line is drawn between the nodes (if the line is legal).
     */
    private void clickToDraw(MouseEvent mouseEvent) {
        StackPane test;
        Circle cirkel = new Circle();
        if(mouseEvent.getSource() instanceof StackPane){
        test = (StackPane) mouseEvent.getSource();
        cirkel = (Circle) test.getChildren().get(0);
        }
        else{
            onMouseClicked(mouseEvent);
        }

        if (!theUserHasSelectedANode) {
            primeNodeToDrawEdgeFrom(cirkel);
        } else {
            try {
                attemptDrawEdgeBetweenNodes(selectedNode, cirkel);
                updateCanvasClick();
            } catch (IllegalNodesChosenException e) {
                view.illegalEdgeAnimation(gamePane, createEdge(selectedNode, cirkel));
                view.deselectNode(selectedNode);
                theUserHasSelectedANode = false;
            } catch (GameOverException e) {
                updateCanvasClick();
                System.out.println("Game Over!");
            } catch (CollisionException e) {
               // view.illegalEdgeAnimation(gamePane, createEdge(selectedNode, (Circle) mouseEvent.getSource()));
                view.illegalEdgeAnimation(gamePane, createEdge(selectedNode, cirkel));
                view.deselectNode(selectedNode);
                theUserHasSelectedANode = false;
                System.out.println("Collision!");
            }
        }
    }


    /**
     * @param mouseClick The mouse click the user performs.
     * @author Emil Sommer Desler & Noah Bastian Christiansen
     * If the user has selected a node and clicks on something else than a node the selected node is deselected
     * and the user is free to select a new node.
     */
    @SuppressWarnings("unused")
    public void onMouseClicked(MouseEvent mouseClick) {
        if (!(mouseClick.getSource() instanceof StackPane) && selectedNode != null) {
            view.deselectNode(selectedNode);
            theUserHasSelectedANode = false;
        }
    }

    private void updateCanvasClick() {
        StackPane newNode = view.updateCanvasClick(gamePane); // update canvas and get newNode created
        newNode.setOnMouseClicked(this::clickToDraw); // Add listener to the new node
    }

    /**
     * @author Noah Bastian Christiansen
     * This method is called when the user finishes his/her move  in drag to draw.
     * If the user had no collisions and drew a valid line this method will call upon the view to display the newly generated node.
     */
    private void updateCanvasDrag() {
        view.updateCanvasDrag(gamePane);
    }

    public void attemptDrawEdgeBetweenNodes(Circle startNode, Circle endNode) throws IllegalNodesChosenException, GameOverException, CollisionException {
        super.attemptDrawEdgeBetweenNodes(startNode, endNode);
        view.deselectNode(startNode);
        theUserHasSelectedANode = false; // A edge has been drawn and the node i no longer primed
    }

    /**
     * @param selectedNode the node the user has selected
     * @author Emil Sommer Desler
     * Save the selectedNode and select it to draw from
     */
    private void primeNodeToDrawEdgeFrom(Circle selectedNode) {
        view.selectNode(selectedNode);
        this.selectedNode = selectedNode;
        theUserHasSelectedANode = true;
    }

    /**
     * @param mousePressed The mouse press the user performs.
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * This method is called when the user presses on a node in the gamemode drag to draw.
     * It takes a mouseEvent and sets up the model and the view
     * A path is initialized only if it starts from a node
     */
    @SuppressWarnings("unused")
    public void mousePressedHandler(MouseEvent mousePressed) {
        System.out.println("width: " + gamePane.getWidth());
        System.out.println("height: " + gamePane.getHeight());

        if (mousePressed.getButton() == MouseButton.PRIMARY) {
            isPathInit = false;
            if (gameMode == DRAG_TO_DRAW_MODE) {
                try {
                    setupDrawing(mousePressed);
                    view.setUpDrawingSettings(mousePressed, gamePane);
                    isPathInit = true;
                } catch (InvalidPath invalidPath) {
                    System.out.println(invalidPath.getMessage());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    /**
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * Repeatedly called when the user is dragging his mouse in order to draw.
     * Calls the model's method that draws path to mousevent's coordinates and the method that checks for intersections/collisions
     * @param mouseDragged the mouse drag the user performs. This MouseEvent contains coordinates.
     */
    public void mouseDraggedHandler(MouseEvent mouseDragged) throws InvalidPath {
        if (mouseDragged.getButton() == MouseButton.PRIMARY) {
            if (isPathInit) {
                dragged = true;

                if (gameMode == DRAG_TO_DRAW_MODE) {
                    try {
                        beginDrawing(mouseDragged);
                    } catch (PathForcedToEnd | InvalidPath e) {
                        finishPathHelper(mouseDragged);
                    }
                    if (isCollided()) {
                        view.setUpCollisionSettings(mouseDragged);
                        isPathInit = false;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    /**
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     * This method is called when the user finishes a drawing in drag to draw. A path is finished, only if it ends in a node.
     * If the user had no collisions the path can be added to list of valid lines and a new node can be generated on the path.
     * @param mouseReleased The mouse release the user performs.
     */
    public void mouseReleasedHandler(MouseEvent mouseReleased) {
        if (mouseReleased.getButton() == MouseButton.PRIMARY) {
            if (gameMode == DRAG_TO_DRAW_MODE && !getSproutModel().getIsCollided() && dragged && isPathInit) {
                finishPathHelper(mouseReleased);

            }
            view.setUpSuccessfulPathSettings(mouseReleased);
        }
    }

    private void finishPathHelper(MouseEvent mouseEvent) {
        try {
            completeDrawing(mouseEvent);
            addNodeOnValidLineDrag();
            updateCanvasDrag();
            dragged = false;
            isPathInit = false;
            view.setUpSuccessfulPathSettings(mouseEvent);
        } catch (InvalidPath e) {
            dragged = false;
            isPathInit = false;
            System.out.println(e.getMessage());
        }
    }

    void setNumberOfInitialNodes(int numberOfInitialNodes) {
        this.numberOfInitialNodes = numberOfInitialNodes;
    }


}
