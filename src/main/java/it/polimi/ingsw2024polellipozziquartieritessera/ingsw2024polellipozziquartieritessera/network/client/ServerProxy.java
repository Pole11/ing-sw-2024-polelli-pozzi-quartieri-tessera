package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

public class ServerProxy implements VirtualServer {
    final PrintWriter output;

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
    public void showNickname(VirtualView client) throws RemoteException {
        output.println(Command.SHOWNICKNAME);
        output.flush();
    }

    @Override
    public void startGame(VirtualView client) throws RemoteException {
        output.println(Command.START);
        output.flush();
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, String side) throws RemoteException {
        output.println(Command.CHOOSESTARTER + "; " + side);
        output.flush();
    }

    @Override
    public void chooseInitialColor(VirtualView client, String color) throws RemoteException {
        output.println(Command.CHOOSECOLOR + "; " + color);
        output.flush();
    }

    @Override
    public void chooseInitialObjective(VirtualView client, String cardId) throws RemoteException {
        output.println(Command.CHOOSEOBJECTIVE +"; " + cardId);
        output.flush();
    }

    @Override
    public void showHand(VirtualView client) {
        output.println(Command.SHOWHAND);
        output.flush();
    }

    public void showCommonObjectives(VirtualView client) {
        output.println(Command.SHOWOBJECTIVES);
        output.flush();
    }

    @Override
    public void placeCard(VirtualView client, String placingCardId, String tableCardId, String tableCornerPos, String placingCardSide) throws RemoteException {
        output.println(Command.PLACECARD + "; " + "; " + placingCardId + "; " + tableCardId + "; " + tableCornerPos + "; " + placingCardSide);
        output.flush();
    }

    @Override
    public void drawCard(VirtualView client, String drawType) throws RemoteException {
        output.println(Command.DRAWCARD + "; " + drawType);
        output.flush();
    }

    @Override
    public void flipCard(VirtualView client, String cardId) throws RemoteException {
        output.println(Command.FLIPCARD + "; " + cardId);
        output.flush();
    }

    @Override
    public void openChat() throws RemoteException {
        output.println(Command.OPENCHAT);
        output.flush();
    }

    @Override
    public void addMessage(VirtualView client, String content) throws RemoteException {
        output.println(Command.ADDMESSAGE + "; " + content);
        output.flush();
    }
}