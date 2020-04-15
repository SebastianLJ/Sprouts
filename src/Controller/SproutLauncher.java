package Controller;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

public class SproutLauncher extends Application {

    private DoubleProperty fontSize = new SimpleDoubleProperty(10);

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Sprouts");

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource("MainMenu.fxml")));

        Parent root = fxmlLoader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        Scene scene = new Scene(root, screenBounds.getWidth()/2, screenBounds.getHeight()/2);

        fontSize.bind(scene.widthProperty().add(scene.heightProperty()).divide(50));
        VBox mainMenu = (VBox) fxmlLoader.getNamespace().get("mainMenu");
        mainMenu.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSize.asString()));

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
