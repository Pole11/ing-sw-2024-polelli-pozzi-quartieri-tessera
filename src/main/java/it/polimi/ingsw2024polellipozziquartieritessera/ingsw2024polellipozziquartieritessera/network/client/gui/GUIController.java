package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.*;

import java.io.IOException;
import java.rmi.RemoteException;

public class GUIController {
    /*
    @FXML
    private Label serverMessageLabel;
    @FXML
    private Label serverErrorLabel;
    @FXML
    private TextField nicknameTextField;

    private VirtualView client;
    private VirtualServer server;

    public GUIController(VirtualView client, VirtualServer server) {
        this.client = client;
        this.server = server;
    }

    @FXML
    public void handleAddUser(ActionEvent event) {
        String message = Command.ADDUSER + " " + nicknameTextField.getText();
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while adding a user");
        }
    }

    @FXML
    public void handleStartGame(ActionEvent event) {
        String message = Command.START.toString();
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while adding a user");
        }
        // check if the user has logged in
        // check if the game is startable
    }

    @FXML
    public void chooseFrontStarter(ActionEvent event) {
        chooseSideStarter(Side.FRONT);
    }

    @FXML
    public void chooseBackStarter(ActionEvent event) {
        chooseSideStarter(Side.BACK);
    }

    private void chooseSideStarter(Side side) {
        String message = Command.CHOOSESTARTER + " " + side;
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while choosing the starter side");
        }
    }

    @FXML
    private void chooseBlueColor(ActionEvent event) {
        chooseColor(Color.BLUE);
    }

    @FXML
    private void chooseGreenColor(ActionEvent event) {
        chooseColor(Color.GREEN);
    }

    @FXML
    private void chooseYellowColor(ActionEvent event) {
        chooseColor(Color.YELLOW);
    }

    @FXML
    private void chooseRedColor(ActionEvent event) {
        chooseColor(Color.RED);
    }

    private void chooseColor(Color color) {
        String message = Command.CHOOSECOLOR + " " + color;
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while adding a user");
        }
    }

    public void goToScene(String fxml) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                try {
                    GUIApplication.changeScene(fxml);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    public void chooseFirstObjective(ActionEvent event) {
        chooseObjective(0);
    }

    @FXML
    public void chooseSecondObjective(ActionEvent event) {
        chooseObjective(1);
    }

    public void chooseObjective(int index) {
        String message = Command.CHOOSEOBJECTIVE + " " + index;
        try {
            Client.manageInputCli(this.server, message.split(" "), this.client);
        } catch (RemoteException e) {
            e.printStackTrace(); // TODO: remove on prod
            serverMessageLabel.setText("There was an error while adding a user");
        }
    }

    @FXML
    public void setServerMessage(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                System.out.println("[DEBUG] Rendering server message: " + serverMessage);
                serverMessageLabel.setText("INFO FROM SERVER: " + serverMessage);
            }
        });
    }

    @FXML
    public void setServerError(String serverMessage) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                System.err.println("[DEBUG] Rendering server error: " + serverMessage);
                serverErrorLabel.setText("ERROR FROM SERVER: " + serverMessage);
            }
        });
    }
     */
}
