package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;

import java.rmi.RemoteException;

public class ChooseColorCommandRunnable extends CommandRunnable{
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
