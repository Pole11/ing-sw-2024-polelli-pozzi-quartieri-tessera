package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class ChooseObjectiveCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            int cardId;
            try {
                cardId = Integer.parseInt(messageFromCli[1]);
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid card id, please insert a valid card id\n> "));
                return;
            }
            try {
                server.chooseInitialObjective(client, cardId);
            } catch (RemoteException e) {
                serverDisconnected();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
            return;
        }
    }

    public void executeGUI(){

    }
}
