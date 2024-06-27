package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

/**
 * Allows a client to send a pong
 */
public class PongCommandRunnable extends CommandRunnable {
    @Override
    public void executeCLI() {
        try {
            server.pong(client);

        } catch (RemoteException e) {
            serverDisconnectedCLI();
        }
    }

    @Override
    public void executeGUI() {
        try {
            System.out.println(clientContainer.getGuiApplication().getGUIController().getServerThread());
            server.pong(client);
        } catch (RemoteException e) {
            serverDisconnectedGUI();
        }
    }

    @Override
    public void executeHelp() {
        System.out.println("AMIALIVE");
    }
}
