package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.*;

import java.rmi.RemoteException;

public class GUIController {
    @FXML
    private Label serverMessageLabel;
    @FXML
    private TextField nicknameTextField;

    private VirtualView client;
    private VirtualServer server;

    public void setClient(VirtualView client) {
        this.client = client;
    }

    public void setServer(VirtualServer server) {
        this.server = server;
    }

    @FXML
    public void handleAddUser(ActionEvent event) {
        String message = Command.ADDUSER + " " + nicknameTextField.getText();
        try {
            Client.manageInput(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while adding a user");
        }
    }

    @FXML
    public void setServerMessage(String serverMessage) {
        System.out.println("[DEBUG] Rendering server message: " + serverMessage);
        serverMessageLabel.setText(serverMessage);
    }
}
