package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.*;

//server che vede il client
public interface VirtualServer extends Remote {
    public void connectRmi(VirtualView vv) throws RemoteException;

    public void addConnectedPlayer(VirtualView client, String nickname) throws RemoteException;
    public void showNickname(VirtualView client) throws RemoteException;

    public void startGame(VirtualView client) throws RemoteException;

    public void chooseInitialStarterSide(VirtualView client, Side side) throws RemoteException;

    public void chooseInitialColor(VirtualView client, Color color) throws RemoteException;

    public void chooseInitialObjective(VirtualView client, int cardId) throws RemoteException;

    public void showHand(VirtualView client) throws RemoteException;

    public void showCommonObjectives(VirtualView client) throws RemoteException;// show common objectives
    //public void showObjectives(VirtualView client); // show two objective card (for starting choice)
    //public void showBoard(VirtualView client); // show current board
    //public void showSharedCards(VirtualView client);// show shared cards
    //public void showOtherPlayersBoard(VirtualView client);// show other player board?
    //public void showChat(VirtualView client); // show chat
    //public void showDeckQty(); // show number of cards of a deck

    public void placeCard(VirtualView client, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException;

    public void drawCard(VirtualView client, DrawType drawType) throws RemoteException;

    public void flipCard(VirtualView client, int cardId) throws RemoteException;

    public void openChat() throws RemoteException;

    public void addMessage(VirtualView client, String content) throws RemoteException;

}