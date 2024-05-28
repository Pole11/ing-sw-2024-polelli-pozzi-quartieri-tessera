package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class ChooseObjectiveCommandRunnable extends CommandRunnable{
    private int index;

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void executeCLI() {
        try {
            int index;
            try {
                index = Integer.parseInt(messageFromCli[1]);
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid card id, please insert a valid card id\n> "));
                return;
            }
            try {
                server.chooseInitialObjective(client, index);
            } catch (RemoteException e) {
                serverDisconnectedCLI();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
        }
    }

    public void executeGUI() {
        try {
            server.chooseInitialObjective(client, index);
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }
    }
}