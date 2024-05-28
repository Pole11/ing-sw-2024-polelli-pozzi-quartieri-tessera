package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class AdduserCommandRunnable extends CommandRunnable{
    private String nickname;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void executeCLI() {
        try {
            try {
                server.addConnectedPlayer(client, messageFromCli[1]);
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
            server.addConnectedPlayer(client, this.nickname);
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }
    }
}
