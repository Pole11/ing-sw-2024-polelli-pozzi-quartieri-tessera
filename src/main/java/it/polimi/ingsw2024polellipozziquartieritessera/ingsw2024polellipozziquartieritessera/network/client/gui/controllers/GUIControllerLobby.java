package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.rmi.RemoteException;

public class GUIControllerLobby extends GUIController {
    @FXML private TextField nicknameTextField;

    @FXML
    public void handleAddUser(ActionEvent event) {
        String message = Command.ADDUSER + " " + nicknameTextField.getText();
        //Client.manageInputCli(getServer(), message.split(" "), getClient());
        try {
            getServer().addConnectedPlayer(getClient(), nicknameTextField.getText());
        } catch (RemoteException e) {
            setServerError("There was an error while connecting to the server, please try again");
        }
    }

    @Override
    public void update(ViewModel viewModel) {
        Platform.runLater(() -> {
            System.out.println("[DEBUG] Updating lobby controller");
        });
    }
}
