package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class SproutLauncher extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Sprouts");

        Parent root = FXMLLoader.load(Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource("MainMenu.fxml")));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
