package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class OpenChatCommandRunnable extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            server.openChat();
        } catch (RemoteException e) {
            serverDisconnected();
        }
    }

    @Override
    public void executeGUI() {

    }
}
