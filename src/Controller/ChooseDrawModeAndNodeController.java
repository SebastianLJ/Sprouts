package Controller;


import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChooseDrawModeAndNodeController extends Controller implements Initializable {
    public TextField numberOfNodesTextField;
    public ToggleGroup drawMode;

    public void goBack(ActionEvent event) throws IOException {
        changeScene(event, "MainMenu.fxml");
    }

    public void startGame(ActionEvent event) throws IOException {
        String s;
        if (drawMode == null) {
            startGame(event, 1, (s = numberOfNodesTextField.getText()).length() > 0 ? Integer.parseInt(s) : 0, false);
        } else {
            startGame(event, 0, (s = numberOfNodesTextField.getText()).length() > 0 ? Integer.parseInt(s) : 0, (boolean) drawMode.getSelectedToggle().getUserData());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        numberOfNodesTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numberOfNodesTextField.setText(newValue.replaceAll("[^\\d]", ""));
            }
            if (numberOfNodesTextField.getText().length() > 2) {
                numberOfNodesTextField.setText(oldValue);
            }
        });

        if (drawMode != null) {
            drawMode.getToggles().get(0).setUserData(false);
            drawMode.getToggles().get(1).setUserData(true);
        }
    }
}
