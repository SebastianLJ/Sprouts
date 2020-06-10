package Controller;

import Exceptions.*;
import View.View;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.Initializable;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class GameController extends SproutController implements Initializable {
    @FXML
    public Pane gamePane;

    private int gameType; // 0 is clickToDraw and 1 is dragToDraw
    private int numberOfInitialNodes;
    private final int CLICK_TO_DRAW_MODE = 0;
    private final int DRAG_TO_DRAW_MODE = 1;
    private View view;
    private boolean theUserHasSelectedANode;
    private Circle selectedNode;
    private boolean dragged;
    private boolean isPathInit = false;

    public GameController () {
        super();
    }

    void setGameType(int whichGameType) {
        gameType = whichGameType;
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
            // Tell the model how big the game is
            updateSize(0.0,0.0);
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
            initializeListenerForStartingNodes();
        });
    }

    /**
     * @author Emil Sommer Desler
     * Listeners makes us able to tell which nodes the user selects and
     * that way update the model accordingly to the moves the player does.
     */
    private void initializeListenerForStartingNodes() {
        for (Model.Node node : getNodes()) {
            if (gameType == CLICK_TO_DRAW_MODE) {
                node.getShape().setOnMouseClicked(this::clickToDraw);
            } else {
                //todo highlight selected node in drag to draw
            }
        }
    }

    /**
     * @param mouseEvent The mouse click the user performs.
     * @author Emil Sommer Desler
     * This method handles the game where the user clicks nodes in order to draw edges between them.
     * By clicking a node the user primes that node for drawing and when clicking another node a line is drawn between the nodes (if the line is legal).
     */
    private void clickToDraw(MouseEvent mouseEvent) {
        if (!theUserHasSelectedANode) {
            primeNodeToDrawEdgeFrom((Circle) mouseEvent.getSource());
        } else {
            try {
                attemptDrawEdgeBetweenNodes(selectedNode, (Circle) mouseEvent.getSource());
                updateCanvasClick();
            } catch (IllegalNodesChosenException e) {
                view.illegalEdgeAnimation(gamePane, createEdge(selectedNode, (Circle) mouseEvent.getSource()));
                view.deselectNode(selectedNode);
                theUserHasSelectedANode = false;
            } catch (GameOverException e) {
                updateCanvasClick();
                System.out.println("Game Over!");
            } catch (CollisionException e) {
                view.illegalEdgeAnimation(gamePane, createEdge(selectedNode, (Circle) mouseEvent.getSource()));
                view.deselectNode(selectedNode);
                theUserHasSelectedANode = false;
                System.out.println("Collision!");
            }
        }
    }


    /**
     * @param mouseClick The mouse click the user performs.
     * @author Emil Sommer Desler
     * If the user has selected a node and clicks on something else than a node the selected node is deselected
     * and the user is free to select a new node.
     */
    @SuppressWarnings("unused")
    public void onMouseClicked(MouseEvent mouseClick) {
        if (!(mouseClick.getTarget() instanceof Circle) && selectedNode != null) {
            view.deselectNode(selectedNode);
            theUserHasSelectedANode = false;
        }
    }

    private void updateCanvasClick() {
        Circle newNode = view.updateCanvasClick(gamePane); // update canvas and get newNode created
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
        if (mousePressed.getButton() == MouseButton.PRIMARY) {
            isPathInit = false;
            if (gameType == DRAG_TO_DRAW_MODE) {
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
            //left bottom corner (-17,232)
            //upper left corner (-17, -17)
            //bottom right corner (472,232)
            //top right corner (472,-17)
            if (isPathInit) {
            /*System.out.println("width: " + gamePane.getWidth());
            System.out.println("height: " + gamePane.getHeight());
            System.out.println("boundsInLocal: " + gamePane.getBoundsInLocal());
            System.out.println("x: " + mouseDragged.getX());
            System.out.println("y: " + mouseDragged.getY());*/
                dragged = true;

                if (gameType == DRAG_TO_DRAW_MODE) {
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
            if (gameType == DRAG_TO_DRAW_MODE && !getSproutModel().getIsCollided() && dragged && isPathInit) {
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
