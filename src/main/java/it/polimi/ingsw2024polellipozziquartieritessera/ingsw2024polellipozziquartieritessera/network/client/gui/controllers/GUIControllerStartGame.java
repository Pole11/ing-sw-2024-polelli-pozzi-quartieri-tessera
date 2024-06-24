package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.CommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.StartCommandRunnable;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.rmi.RemoteException;
import java.util.ArrayDeque;

public class GUIControllerStartGame extends GUIController {
    @FXML
    ListView playersConnectedListView;

    @FXML
    public void handleStartGame(ActionEvent event) {
        addCommand(new StartCommandRunnable(), this);
    }

    public void showNicknamesConnected() {
        playersConnectedListView.getItems().clear();
        for (int i = 0; i < getViewModel().getPlayersSize(); i++) {
            playersConnectedListView.getItems().add(getViewModel().getNickname(i));
        }
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            //System.out.println("[DEBUG] Updating start game controller");
            String message = "You successfully entered the game with the nickname " + getViewModel().getNickname(getViewModel().getPlayerIndex()) + ". ";
            showNicknamesConnected();
            setServerMessage(message);
        });
    }
}
