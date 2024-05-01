package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.*;

//server che vede il client
public interface VirtualServer extends Remote {
    void connectRmi(VirtualView vv) throws RemoteException;

    // qua metto tutte le chiamate al controller
    // set username
    // set color
    // set starter side
    // set objective
    // flip
    // place
    // draw

    public void addConnectedPlayer(VirtualView client, String nickname) throws RemoteException;

    public void startGame() throws RemoteException;

    public void chooseInitialStarterSide(VirtualView client, String side) throws RemoteException;

    public void chooseInitialColor(VirtualView client, String color) throws RemoteException;

    public void chooseInitialObjective(VirtualView client, String cardId) throws RemoteException;

    public void placeCard(VirtualView client, String placingCardId, String tableCardId, String tableCornerPos, String placingCardSide) throws RemoteException;

    public void drawCard(VirtualView client, String drawType) throws RemoteException;

    public void flipCard(VirtualView client, String cardId) throws RemoteException;

    public void openChat() throws RemoteException;

    public void addMessage(VirtualView client, String content) throws RemoteException;

}
