package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.DrawType;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
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
        output.println("ADDUSER, " + nickname);
        output.flush();
    }

    @Override
    public void startGame() throws RemoteException {
        output.println("START");
        output.flush();
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, String side) throws RemoteException {
        output.println("CHOOSESTARTER, " + side);
        output.flush();
    }

    @Override
    public void chooseInitialColor(VirtualView client, String color) throws RemoteException {
        output.println("CHOOSECOLOR, " + color);
        output.flush();
    }

    @Override
    public void chooseInitialObjective(VirtualView client, String cardId) throws RemoteException {
        output.println("CHOOSEOBJECTIVE, " + cardId);
        output.flush();
    }

    @Override
    public void placeCard(VirtualView client, String placingCardId, String tableCardId, String tableCornerPos, String placingCardSide) throws RemoteException {
        output.println("PLACECARD, " + placingCardId + tableCardId + tableCornerPos + placingCardSide);
        output.flush();
    }

    @Override
    public void drawCard(VirtualView client, String drawType) throws RemoteException {
        output.println("DRAWCARD, " + drawType);
        output.flush();
    }

    @Override
    public void flipCard(VirtualView client, String cardId) throws RemoteException {
        output.println("FLIPCARD, " + cardId);
        output.flush();
    }

    @Override
    public void openChat() throws RemoteException {
        output.println("OPENCHAT");
        output.flush();
    }

    @Override
    public void addMessage(VirtualView client, String content) throws RemoteException {
        output.println("ADDMESSAGE, " + content);
        output.flush();
    }
}