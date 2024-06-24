package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class GameEndedCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            server.gameEnded(client);
        } catch (RemoteException e) {
            serverDisconnectedCLI();
        }
    }

    @Override
    public void executeGUI() {
        try {
            server.gameEnded(client);
        } catch (RemoteException e) {
            serverDisconnectedCLI();
        }
    }

    @Override
    public void executeHelp() {
        System.out.println("this method cannot be called manually");
    }
}
