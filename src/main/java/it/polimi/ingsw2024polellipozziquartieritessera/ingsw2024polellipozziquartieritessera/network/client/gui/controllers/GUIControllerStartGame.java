package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.rmi.RemoteException;

public class GUIControllerStartGame extends GUIController {

    @FXML
    public void handleStartGame(ActionEvent event) {
        try {
            getServer().startGame(getClient());
        } catch (RemoteException e) {
            Platform.runLater(() -> {
                setServerError("Unfortunatly we could not start the game, please try again :/");
            });
        }
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            System.out.println("[DEBUG] Updating start game controller");
            String message = "You successfully entered the game with the nickname " + getTempViewModel().getNickname(getTempViewModel().getPlayerIndex()) + ". ";
            message += "The players currently connected are: | ";
            for (int i = 0; i < getTempViewModel().getPlayersSize(); i++) {
                message += getTempViewModel().getNickname(i) + " | ";
            }
            setServerMessage(message);
        });
    }
}
