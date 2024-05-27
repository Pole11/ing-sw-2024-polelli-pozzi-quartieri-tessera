package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class PingCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            server.ping(clientContainer.getClient());
        } catch (RemoteException e) {
            serverDisconnected();
        }
    }

    @Override
    public void executeGUI() {

    }
}
