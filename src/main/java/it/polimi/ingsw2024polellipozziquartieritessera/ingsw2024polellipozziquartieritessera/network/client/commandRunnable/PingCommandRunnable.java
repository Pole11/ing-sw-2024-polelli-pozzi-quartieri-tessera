package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

/**
 * Allowa a client to send a ping
 */
public class PingCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            server.ping(client);
        } catch (RemoteException e) {
            serverDisconnectedCLI();
        }
    }

    @Override
    public void executeGUI() {
        try {
            server.ping(client);
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }
    }

    @Override
    public void executeHelp() {

    }
}
