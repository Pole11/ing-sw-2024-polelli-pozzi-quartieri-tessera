package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class StartCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            server.startGame(client);
        } catch (RemoteException e) {
            this.serverDisconnectedCLI();
        }
    }

    @Override
    public void executeGUI() {
        try {
            server.startGame(client);
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }
    }

    @Override
    public void executeHelp() {
        System.out.print("START\nThe command allows a player to start a game if there are more than one player\n> ");
    }
}
