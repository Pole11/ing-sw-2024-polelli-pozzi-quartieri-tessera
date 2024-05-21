package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.rmi.RemoteException;

public class GUIControllerStart extends GUIController {
    @FXML
    public void handleStartGame(ActionEvent event) {
        String message = Command.START.toString();
        //Client.manageInputCli(getServer(), message.split(" "), getClient());

        // check if the user has logged in
        // check if the game is startable
    }
}
