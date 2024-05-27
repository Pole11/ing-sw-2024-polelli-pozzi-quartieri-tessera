package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class AdduserCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            try {
                server.addConnectedPlayer(clientContainer.getClient(), messageFromCli[1]);
            } catch (RemoteException e) {
                serverDisconnected();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
        }
    }

    @Override
    public void executeGUI() {

    }
}
