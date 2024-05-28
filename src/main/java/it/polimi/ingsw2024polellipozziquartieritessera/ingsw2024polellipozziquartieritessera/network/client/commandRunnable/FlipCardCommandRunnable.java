package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class FlipCardCommandRunnable extends CommandRunnable{
    private int cardId;

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    @Override
    public void executeCLI() {
        try {
            int cardId;
            try {
                cardId = Integer.parseInt(messageFromCli[1]);
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid card id\n> ");
                return;
            }
            try {
                server.flipCard(client, cardId);
            } catch (RemoteException e) {
                serverDisconnectedCLI();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
        }
    }

    @Override
    public void executeGUI() {
        try {
            server.flipCard(client, this.cardId);
        } catch (RemoteException e) {
            serverDisconnectedGUI();
        }
    }
}