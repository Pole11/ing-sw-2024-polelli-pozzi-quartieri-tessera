package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

/**
 * Represents a proxy for interacting with a server via output streams.
 */
public class ServerProxy implements VirtualServer {
    final PrintWriter output;

    /**
     * Costructor for the ServerProxy.
     *
     * @param output The BufferedWriter used to send output to the server.
     */
    public ServerProxy(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void connectRmi(VirtualView vv) throws RemoteException { // useless, just for implementing all the interface

    }

    @Override
    public void addConnectedPlayer(VirtualView client, String nickname) throws RemoteException {
        output.println(Command.ADDUSER +"; " + nickname);
        output.flush();
    }

    @Override
    public void startGame(VirtualView client) throws RemoteException {
        output.println(Command.START);
        output.flush();
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, Side side) throws RemoteException {
        output.println(Command.CHOOSESTARTER + "; " + side.toString());
        output.flush();
    }

    @Override
    public void chooseInitialColor(VirtualView client, Color color) throws RemoteException {
        output.println(Command.CHOOSECOLOR + "; " + color.toString());
        output.flush();
    }

    @Override
    public void chooseInitialObjective(VirtualView client, int cardId) throws RemoteException {
        output.println(Command.CHOOSEOBJECTIVE +"; " + cardId);
        output.flush();
    }


    @Override
    public void placeCard(VirtualView client, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException {
        output.println(Command.PLACECARD + "; " + placingCardId + "; " + tableCardId + "; " + tableCornerPos.toString() + "; " + placingCardSide.toString());
        output.flush();
    }

    @Override
    public void drawCard(VirtualView client, DrawType drawType) throws RemoteException {
        output.println(Command.DRAWCARD + "; " + drawType);
        output.flush();
    }

    @Override
    public void flipCard(VirtualView client, int cardId) throws RemoteException {
        output.println(Command.FLIPCARD + "; " + cardId);
        output.flush();
    }

    @Override
    public void addMessage(VirtualView client, String content) throws RemoteException {
        output.println(Command.ADDMESSAGE + "; " + content);
        output.flush();
    }

    @Override
    public void ping(VirtualView client) throws RemoteException {
        output.println(Command.PING);
        output.flush();
    }

    public void pong(VirtualView client) throws RemoteException {
        output.println(Command.PONG);
        output.flush();
    }

    @Override
    public void gameEnded(VirtualView client) throws RemoteException {
        output.println(Command.GAMEENDED);
        output.flush();
    }
}