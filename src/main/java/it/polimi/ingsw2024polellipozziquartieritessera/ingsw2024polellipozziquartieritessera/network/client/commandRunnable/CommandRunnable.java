package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIController;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.IOException;

public abstract class CommandRunnable {
    VirtualServer server;
    String[] messageFromCli;
    Client clientContainer;
    GUIController guiController;
    VirtualView client;

    public void setServer(VirtualServer server) {
        this.server = server;
    }

    public void setMessageFromCli(String[] message) {
        this.messageFromCli = message;
    }

    public void setGuiController(GUIController guiController) {
        this.guiController = guiController;
    }

    public void setClientContainer(Client clientContainer) {
        this.clientContainer = clientContainer;
    }

    public void setClient(VirtualView client) {
        this.client = client;
    }

    public abstract void executeCLI();

    public abstract void executeGUI();

    public abstract void executeHelp();

    public void serverDisconnectedCLI() {
        clientContainer.serverDisconnected();
    }

    public void serverDisconnectedGUI() {
        clientContainer.serverDisconnected();
    }
}
