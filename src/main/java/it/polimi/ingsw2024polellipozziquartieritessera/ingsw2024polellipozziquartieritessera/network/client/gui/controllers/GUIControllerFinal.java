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
            String text;
            if (getViewModel().getWinners().size() == 1){
                text = "The winner is " + getViewModel().winners.getFirst();
            } else {
                text = "The winners are ";
                for (int i = 0; i < getViewModel().getWinners().size(); i++) {
                    text += getViewModel().getWinners().get(i) + " ";
                }
            }


            winnerText.setText(text);
        });
    }
}
