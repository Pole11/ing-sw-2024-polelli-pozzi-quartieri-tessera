package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

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
