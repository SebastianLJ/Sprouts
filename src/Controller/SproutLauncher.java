package Controller;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SproutLauncher extends Application {

    static SproutController controller = new SproutController();

    /**
     * @author Emil Sommer Desler
     * Starts the initial stage that is used in the entire program to show different scenes to the user.
     * @param stage The stage used to show scenes to the user.
     * @throws IOException Thrown by the FXMLLoader if the fxml document is not present.
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Sprouts");

        FXMLLoader fxmlLoader = new FXMLLoader(Objects.requireNonNull(SproutLauncher.class.getClassLoader().getResource("MainMenu.fxml")));

        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800,600);

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
