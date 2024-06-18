package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class GUIControllerFinal extends GUIController {
    @FXML
    Text winnerText;

    @Override
    public void update() {
        Platform.runLater(() -> {
            winnerText.setText("The winner is/are ...");
        });
    }
}
