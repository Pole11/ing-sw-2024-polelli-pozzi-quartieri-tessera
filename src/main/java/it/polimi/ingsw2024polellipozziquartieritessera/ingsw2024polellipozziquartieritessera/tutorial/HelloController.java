package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.tutorial;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}