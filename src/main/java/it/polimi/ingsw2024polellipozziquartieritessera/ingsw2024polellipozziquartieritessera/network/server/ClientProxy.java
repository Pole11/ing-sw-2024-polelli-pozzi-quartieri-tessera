package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

public class ClientProxy implements VirtualView {
    final PrintWriter output;

    public ClientProxy(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        output.println("MESSAGE; " + message);
        output.flush();
    }

    public void sendError(String error) throws RemoteException {
        output.println("ERROR; " + error);
        output.flush();
    }

    @Override
    public void ping(String ping) throws RemoteException {
        output.println("PING; " + ping);
        output.flush();
    }

    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {

    }

    @Override
    public void sendIndex(int index) throws RemoteException {

    }

    @Override
    public void printCard(int id1, Side side1, int id2, Side side2, int id3, Side side3) throws RemoteException {
    }
    @Override
    public void printCard(int id1, Side side1, int id2, Side side2) throws RemoteException {
    }
    @Override
    public void printCard(int id, Side side) throws RemoteException {
    }

    @Override
    public void changePhase(String nextGamePhaseString) {
        output.println("PHASE; " + nextGamePhaseString);
    }
}