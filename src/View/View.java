package View;

import Model.Node;
import Model.SproutModel;
import javafx.animation.*;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.ArrayList;


public class View {
    private SproutModel model;


    public View(SproutModel model) {
        this.model = model;
    }

    /**
     * Generates numbers on the nodes upon game start.
     *
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     */
    public void initializeNodes(Pane gamePane) {
        for (Node node : model.getNodes()) {
            gamePane.getChildren().add(addNumberOnNode(node));
        }
    }

    /**
     * Adds number on nodes in click-to-draw
     * @return The stack pane which contains the node and the number.
     *
     * @author Noah Bastian Christiansen
     */
    public StackPane updateCanvasClick(Pane gamePane) {
        // Get edge
        Shape newEdge = model.getNewestEdge();
        // Get node
        Node newNode = model.getNewestNode();
        // Animate edge
        legalEdgeAnimation(gamePane, newEdge);
        // Add new node to view
        StackPane newStackPane = addNumberOnNode(newNode);
        gamePane.getChildren().add(newStackPane);
        return newStackPane;
    }

    /**
     * Adds the number on the node in case of a successful drawing.
     *
     * @author Noah Bastian Christiansen & Sebastian Lund Jensen
     */
    public void updateCanvasDrag(Pane gamePane){
        Node newNode = model.getNewestNode();
        gamePane.getChildren().add(addNumberOnNode(newNode));
    }

    /**
     * Adds the path to the gamepane so it is visible and changes the cursor to a crosshair to indicate that the user has begun drawing.
     *
     * @author Noah Bastian Christiansen
     */
    public void setUpDrawingSettings(MouseEvent mousePressed, Pane gamePane) {
        Scene scene = ((javafx.scene.Node) mousePressed.getSource()).getScene();
        scene.setCursor(Cursor.CROSSHAIR);
        gamePane.getChildren().add(model.getMostRecentlyDrawnPath());
    }

    /**
     * Changes the cursor back to default when the user collides with a line.
     *
     * @author Noah Bastian Christiansen
     */
    public void setUpCollisionSettings(MouseEvent mouseDragged) {
        Scene scene = ((javafx.scene.Node) mouseDragged.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }
    /**
     * Changes the cursor from a cross hair to a normal cursor to indicate that the user has finished his drawing successfully.
     *
     * @author Noah Bastian Christiansen
     */
    public void setUpSuccessfulPathSettings(MouseEvent mouseReleased) {
        Scene scene = ((javafx.scene.Node) mouseReleased.getSource()).getScene(); //perhaps set scene somewhere in here.
        scene.setCursor(Cursor.DEFAULT);
    }

    public void deselectNode(Circle primedNode) {
        primedNode.setStrokeWidth(0);
        primedNode.setStroke(Color.BLACK);
    }

    public void selectNode(Circle primedNode) {
        primedNode.setStrokeWidth(2.0);
        primedNode.setStroke(Color.GREEN);
    }

    public void illegalEdgeAnimation(Pane gamePane, Shape edge) {
        edge.setStroke(Color.RED);
        gamePane.getChildren().add(0, edge);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), edge);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.4);
        fadeTransition.setCycleCount(4);
        fadeTransition.setOnFinished(e -> gamePane.getChildren().remove(edge));
        fadeTransition.play();
    }

    public void legalEdgeAnimation(Pane gamePane, Shape shape) {
        if (shape instanceof Line) {
            double x = ((Line) shape).getStartX();
            double y = ((Line) shape).getStartY();
            Line lineAnimation = new Line(x, y, x, y);
            gamePane.getChildren().add(0, lineAnimation);
            Timeline timeline = new Timeline();
            KeyValue kvX = new KeyValue(lineAnimation.endXProperty(), ((Line) shape).getEndX());
            KeyValue kvY = new KeyValue(lineAnimation.endYProperty(), ((Line) shape).getEndY());
            KeyFrame kf = new KeyFrame(Duration.millis(500), kvX, kvY);
            timeline.getKeyFrames().add(kf);
            timeline.play();
        } else {
            gamePane.getChildren().add(shape);
        }
    }

    public void illegalNode(Circle circle) {
        circle.setStrokeWidth(2.0);
        circle.setStrokeType(StrokeType.INSIDE);
        circle.setStroke(Color.RED);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), circle);
        blinkAnimation(fadeTransition, 4);
        fadeTransition.setOnFinished(e -> { circle.setStrokeWidth(0.0);
                                            circle.setStroke(Color.BLACK);
                                            circle.setOpacity(1.0);
        });
        fadeTransition.play();
    }

    public void illegalPath(Pane gamePane, Path path) {
        path.setStroke(Color.RED);
        gamePane.getChildren().add(path);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.2), path);
        blinkAnimation(fadeTransition, 4);

        fadeTransition.setOnFinished(e -> gamePane.getChildren().remove(path));
        fadeTransition.play();
    }

    /**
     * Shows the game response text on the bottom of the screen.
     * Game response includes reasons for collision and winner announcement.
     * Belongs to the GameView.
     *
     * @param gameResponseLabel : Game response label
     * @param text : Game response text
     * @author Thea Birk Berger
     */
    public void showGameResponse(Label gameResponseLabel, String text) {
        gameResponseLabel.setText(text);
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), gameResponseLabel);
        blinkAnimation(fadeTransition, 2);
        fadeTransition.setOnFinished(e -> gameResponseLabel.setText(""));
        fadeTransition.play();
    }

    private void blinkAnimation(FadeTransition fadeTransition, int times) {
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.4);
        fadeTransition.setCycleCount(times);
        fadeTransition.setAutoReverse(true);
    }

    /**
     * Shows the current player name on top of the screen
     *
     * @param playerNameLabel : The player name label
     * @param currentPlayerName : The current player name
     * @author Thea Birk Berger
     */
    public void showWinnerAnimation(Label playerNameLabel, String currentPlayerName) {
        playerNameLabel.setText(currentPlayerName);
    }

    public void resetGameView(Pane gamePane) {
        gamePane.getChildren().clear();
    }

    public void resetCells(ArrayList<ListCell<String>> cells) {
        int i = 0;
        for (ListCell<String> cell : cells) {
            cells.get(i).setTooltip(null);
            cell.setStyle(i++ % 2 == 0 ? "-fx-background-color:#f9f9f9;" : "-fx-background-color:#FFFFFF;");
        }
    }

    public void setColorForCell(String s, ListCell<String> cell) {
        cell.setStyle(s);
    }

    public void prepareTooltip(String message, ListCell<String> cell) {
        Tooltip toolTip = new Tooltip();
        setUpTooltipPreferences(toolTip);
        toolTip.setText(message);
        cell.setTooltip(toolTip);
    }

    private void setUpTooltipPreferences(Tooltip toolTip) {
        toolTip.setShowDelay(Duration.ZERO);
        toolTip.setShowDuration(Duration.INDEFINITE);
        toolTip.setHideDelay(Duration.ZERO);
    }

    public void showCurrentPlayerName(Label playerNameLabel, String playerName) {
        playerNameLabel.setText("Now playing:\t" + playerName);
    }

    /**
     *
     * This method numerates the nodes but creating a stack pane with the node's shape (a circle) and some text (the node's number) on it.
     *
     * @return A stackpane consisting of the node's shape (a circle) and some text indicating the node's number.
     * @param node The node which needs an number added to it.
     * @author Noah Bastian Christiansen
     * @author Sebastian Lund Jensen
     */
    private StackPane addNumberOnNode(Node node){
        final Text text = new Text(""+node.getId());
        text.setFill(Color.WHITE);
        text.setStyle("-fx-font-weight: 900" + ";-fx-font-size:" + (1.4*node.getNodeRadius()));
        final StackPane stack = new StackPane();
        stack.getChildren().addAll(node.getShape(), text);
        stack.relocate(node.getX()-node.getNodeRadius(),node.getY()-node.getNodeRadius());
        return stack;
    }
    /**
     * Shows the game response text on the bottom of the screen.
     * Game response includes reasons for collision and winner announcement.
     * Belongs to the GameViewForFileSimulation.
     *
     * @param gameResponseLabel : Game response label
     * @param s : Game response text
     * @author Thea Birk Berger
     */
    public void setGameResponseLabelText(Label gameResponseLabel, String s) {
        gameResponseLabel.setText(s);
    }
}
