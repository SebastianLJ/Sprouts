package View;

import Model.Node;
import Model.SproutModel;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class View {
    private SproutModel model;

    public View(SproutModel model) {
        this.model = model;
    }

    public void updateCanvas(Pane gamePane) {
        for (Node node: model.getNodes()) {
            gamePane.getChildren().add(node.getShape());
        }
    }
}
