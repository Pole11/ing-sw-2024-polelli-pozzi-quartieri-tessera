package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.CommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.StartCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.rmi.RemoteException;
import java.util.ArrayDeque;

public class GUIControllerStartGame extends GUIController {

    @FXML
    public void handleStartGame(ActionEvent event) {
        /*try {
            getServer().startGame(getClient());
        } catch (RemoteException e) {
            Platform.runLater(() -> {
                setServerError("Unfortunatly we could not start the game, please try again :/");
            });
        }*/
        addCommand(new StartCommandRunnable(), this);
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            System.out.println("[DEBUG] Updating start game controller");
            String message = "You successfully entered the game with the nickname " + getViewModel().getNickname(getViewModel().getPlayerIndex()) + ". ";
            message += "The players currently connected are: | ";
            for (int i = 0; i < getViewModel().getPlayersSize(); i++) {
                message += getViewModel().getNickname(i) + " | ";
            }
            setServerMessage(message);
        });
    }
}
