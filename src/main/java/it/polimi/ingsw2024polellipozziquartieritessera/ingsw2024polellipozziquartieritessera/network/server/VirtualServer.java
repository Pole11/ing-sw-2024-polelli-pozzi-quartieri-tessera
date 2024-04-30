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

    public void chooseInitialStarterSide(int playerIndex, Side side) throws RemoteException;

    public void chooseInitialColor(int playerIndex, Color color) throws RemoteException;

    public void chooseInitialObjective(int playerIndex, int cardId) throws RemoteException;

    public void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException;

    public void drawCard(DrawType drawType) throws RemoteException;

    public void flipCard(int playerIndex, int cardId) throws RemoteException;

    public void openChat() throws RemoteException;

    public void addMessage(Player player, String content) throws RemoteException;

}
