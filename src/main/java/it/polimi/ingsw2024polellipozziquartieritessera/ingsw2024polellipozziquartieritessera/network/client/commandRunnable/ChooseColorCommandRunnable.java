package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;

import java.rmi.RemoteException;

public class ChooseColorCommandRunnable extends CommandRunnable{
    Color color;

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void executeCLI() {
        try {
            Color color;
            try {
                color = Color.valueOf(messageFromCli[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid color, please enter a valid color (Blue / Green / Yellow / Red)\n> "));
                return;
            }
            try {
                server.chooseInitialColor(client, color);
            } catch (RemoteException e) {
                serverDisconnectedCLI();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
            return;
        }
    }


    @Override
    public void executeGUI() {
        try {
            server.chooseInitialColor(client, this.color);
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }

    }

    @Override
    public void executeHelp() {
        System.out.print("CHOOSECOLOR [ Blue / Green / Yellow / Red ]\n> ");
    }
}
