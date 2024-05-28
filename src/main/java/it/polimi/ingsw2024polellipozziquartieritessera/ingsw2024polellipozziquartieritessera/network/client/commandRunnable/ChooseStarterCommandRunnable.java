package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;

import java.rmi.RemoteException;

public class ChooseStarterCommandRunnable extends CommandRunnable {
    Side side;

    public void setSide(Side side) {
        this.side = side;
    }

    @Override
    public void executeCLI() {
        try {
            Side side;
            try {
                side = Side.valueOf(messageFromCli[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                System.err.print(("Invalid side, please enter a valid side (Front / Back)\n> "));
                return;
            }
            try {
                server.chooseInitialStarterSide(client, side);
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
            server.chooseInitialStarterSide(client, this.side);
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }

    }
}
