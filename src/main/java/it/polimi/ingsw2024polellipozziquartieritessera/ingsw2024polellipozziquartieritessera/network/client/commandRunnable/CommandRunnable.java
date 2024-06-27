package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIController;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.IOException;

/**
 * Abstract class that represent all command used by the clients
 */
public abstract class CommandRunnable {
    /** The virtual server associated with this command runnable. */
    protected VirtualServer server;

    /** Array containing the message received from the CLI. */
    protected String[] messageFromCli;

    /** The client container associated with this command runnable. */
    protected Client clientContainer;

    /** The GUI controller associated with this command runnable. */
    protected GUIController guiController;

    /** The client view associated with this command runnable. */
    protected VirtualView client;

    /**
     * Sets the virtual server for this command runnable.
     *
     * @param server The virtual server to set.
     */
    public void setServer(VirtualServer server) {
        this.server = server;
    }

    /**
     * Sets the message received from the CLI for this command runnable.
     *
     * @param message The message from CLI to set.
     */
    public void setMessageFromCli(String[] message) {
        this.messageFromCli = message;
    }

    /**
     * Sets the GUI controller for this command runnable.
     *
     * @param guiController The GUI controller to set.
     */
    public void setGuiController(GUIController guiController) {
        this.guiController = guiController;
    }

    /**
     * Sets the client container for this command runnable.
     *
     * @param clientContainer The client to set.
     */
    public void setClientContainer(Client clientContainer) {
        this.clientContainer = clientContainer;
    }

    /**
     * Sets the client view for this command runnable.
     *
     * @param client The client to set.
     */
    public void setClient(VirtualView client) {
        this.client = client;
    }

    /**
     * Executes the command in the CLI context.
     */
    public abstract void executeCLI();

    /**
     * Executes the command in the GUI context.
     */
    public abstract void executeGUI();

    /**
     * Executes the help command.
     */
    public abstract void executeHelp();

    /**
     * Handles server disconnection in the CLI context.
     */
    public void serverDisconnectedCLI() {
        clientContainer.serverDisconnected();
    }

    /**
     * Handles server disconnection in the GUI context.
     */
    public void serverDisconnectedGUI() {
        System.out.println("Disconnection from guicommandrunnable");
        clientContainer.serverDisconnected();
    }

    /**
     * Prints an error message for too many arguments in CLI context.
     */
    public void tooManyArguments() {
        System.err.print("Invalid number of arguments: you put too many arguments\n> ");
    }
}
