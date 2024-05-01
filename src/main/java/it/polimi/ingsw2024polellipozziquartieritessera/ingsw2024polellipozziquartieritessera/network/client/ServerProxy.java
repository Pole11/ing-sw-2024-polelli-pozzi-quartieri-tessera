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
        output.print("ADDUSER, " + nickname);
        output.flush();
        System.out.println("printed user on buffer");
    }

    @Override
    public void startGame() throws RemoteException {
        output.print("STARTGAME");
        output.flush();
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, String side) throws RemoteException {
        output.print("CHOOSESTARTERSIDE, " + side);
        output.flush();
    }

    @Override
    public void chooseInitialColor(VirtualView client, String color) throws RemoteException {
        output.print("CHOOSEINITIALCOLOR, " + color);
        output.flush();
    }

    @Override
    public void chooseInitialObjective(VirtualView client, String cardId) throws RemoteException {
        output.print("CHOOSEINITIALOBJECTIVE, " + cardId);
        output.flush();
    }

    @Override
    public void placeCard(VirtualView client, String placingCardId, String tableCardId, String tableCornerPos, String placingCardSide) throws RemoteException {
        output.print("PLACECARD, " + placingCardId + tableCardId + tableCornerPos + placingCardSide);
        output.flush();
    }

    @Override
    public void drawCard(VirtualView client, String drawType) throws RemoteException {
        output.print("DRAWCARD, " + drawType);
        output.flush();
    }

    @Override
    public void flipCard(VirtualView client, String cardId) throws RemoteException {
        output.print("FLIPCARD, " + cardId);
        output.flush();
    }

    @Override
    public void openChat() throws RemoteException {
        output.print("OPENCHAT");
        output.flush();
    }

    @Override
    public void addMessage(VirtualView client, String content) throws RemoteException {
        output.print("ADDMESSAGE, " + content);
        output.flush();
    }
}