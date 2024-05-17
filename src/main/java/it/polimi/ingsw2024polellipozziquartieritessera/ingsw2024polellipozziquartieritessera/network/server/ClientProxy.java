package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Messages;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

public class ClientProxy implements VirtualView {
    final PrintWriter output;

    public ClientProxy(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void ping(String ping) throws RemoteException {
        output.println(Messages.PING + "; " + ping);
        output.flush();
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        output.println(Messages.MESSAGE + "; " + message);
        output.flush();
    }

    public void sendError(String error) throws RemoteException {
        output.println(Messages.ERROR + "; " + error);
        output.flush();
    }


    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        output.println(Messages.NICKNAME + "; " + nickname);
        output.flush();
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        output.println(Messages.SENDINDEX + "; " + index);
        output.flush();

    }

    @Override
    public void changePhase(String nextGamePhaseString) {
        output.println(Messages.CHANGEPHASE + "; " + nextGamePhaseString);
        output.flush();
    }
}