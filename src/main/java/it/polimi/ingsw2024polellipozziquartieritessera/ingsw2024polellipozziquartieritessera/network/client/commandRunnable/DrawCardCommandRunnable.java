package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.DrawType;

import java.rmi.RemoteException;

public class DrawCardCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            DrawType drawType;
            try {
                drawType = DrawType.valueOf(messageFromCli[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid draw option, please choose a valid option (SharedGold1 / SharedGold2 / DeckGold / SharedResource1 / SharedResource2 / DeckResource)"));
                return;
            }
            try {
                server.drawCard(clientContainer.getClient(), drawType);
            } catch (RemoteException e) {
                serverDisconnected();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
            return;
        }
    }

    @Override
    public void executeGUI() {

    }
}
