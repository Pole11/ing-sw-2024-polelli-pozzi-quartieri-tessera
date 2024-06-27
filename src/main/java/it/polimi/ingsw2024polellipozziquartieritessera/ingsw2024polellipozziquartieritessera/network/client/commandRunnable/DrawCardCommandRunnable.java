package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.DrawType;

import java.rmi.RemoteException;

/**
 * Allows a client to draw a card
 */
public class DrawCardCommandRunnable extends CommandRunnable{
    DrawType drawType;

    public void setDrawType(DrawType drawType) {
        this.drawType = drawType;
    }

    @Override
    public void executeCLI() {
        if (messageFromCli.length > 2){
            tooManyArguments();
            return;
        }
        try {
            DrawType drawType;
            try {
                drawType = DrawType.valueOf(messageFromCli[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid draw option, please choose a valid option (SharedGold1 / SharedGold2 / DeckGold / SharedResource1 / SharedResource2 / DeckResource)\n> "));
                return;
            }
            try {
                server.drawCard(client, drawType);
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
            System.out.println("[DEBUG] Drawing Card " + this.drawType);
            server.drawCard(client, this.drawType);
        } catch (RemoteException e) {
            serverDisconnectedGUI();
        }
    }

    @Override
    public void executeHelp() {
        System.out.print("DRAWCARD [ SharedGold1 / SharedGold2 / DeckGold / SharedResource1 / SharedResource2 / DeckResource ]\nthe command allows the current player to draw a card from the main board\n> ");
    }
}
