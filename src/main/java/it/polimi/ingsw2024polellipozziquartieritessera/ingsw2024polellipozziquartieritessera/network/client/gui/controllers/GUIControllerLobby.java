package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.AdduserCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.ChooseObjectiveCommandRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;

public class GUIControllerLobby extends GUIController {
    @FXML private TextField nicknameTextField;

    @FXML
    public void handleAddUser(ActionEvent event) {
        AdduserCommandRunnable command = new AdduserCommandRunnable();
        command.setNickname(nicknameTextField.getText());
        addCommand(command, this);
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
        });
    }
}
