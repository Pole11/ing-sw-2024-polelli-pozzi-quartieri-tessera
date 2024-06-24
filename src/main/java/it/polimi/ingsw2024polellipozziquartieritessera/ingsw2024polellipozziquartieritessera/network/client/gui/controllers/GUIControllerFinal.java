package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.HashMap;

public class GUIControllerFinal extends GUIController {
    @FXML
    Text winnerText;

    public GUIControllerFinal() {
        //this.update();
    }

    @FXML
    public void handleBackToLogin(ActionEvent event) {
        goToScene("/fxml/lobby.fxml");
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            HashMap<String, Object> params = getParamsMap();
            ArrayList<String> playerIndexes = (ArrayList<String>) params.get("playerIndexes");

            String text;
            System.out.println(playerIndexes);
            if (playerIndexes.size() == 1){
                text = "The winner is " + playerIndexes.getFirst();
            } else {
                text = "The winners are ";
                for (int i = 0; i < playerIndexes.size(); i++) {
                    text += playerIndexes.get(i) + " ";
                }
            }

            winnerText.setText(text);
        });
    }
}
