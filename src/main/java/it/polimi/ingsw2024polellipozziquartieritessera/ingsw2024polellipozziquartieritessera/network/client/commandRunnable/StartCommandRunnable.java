package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class StartCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            server.startGame(clientContainer.getClient());
        } catch (RemoteException e) {
            this.serverDisconnected();
        }
    }

    @Override
    public void executeGUI() {

    }
}
