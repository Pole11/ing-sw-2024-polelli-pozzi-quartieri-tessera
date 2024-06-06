package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.rmi.*;
import java.util.ArrayList;

//clint che vede il server
public interface VirtualView extends Remote {
    void ping(String ping) throws RemoteException;
    void sendError(String error) throws RemoteException;
    void sendIndex(int index) throws RemoteException;
    void nicknameUpdate(int index, String nickname) throws RemoteException;
    void updateGamePhase(GamePhase nextGamePhase) throws RemoteException;
    void updateTurnPhase(TurnPhase nextTurnPhase) throws RemoteException;
    //FORSE da togliere
    void start() throws RemoteException;
    void connectionInfo(int playerIndex, boolean connected) throws RemoteException;
    void updateAddHand(int playerIndex, int cardIndex) throws RemoteException;
    void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException;
    void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException;
    void updateColor(int playerIndex, Color color) throws RemoteException;
    void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException;
    void updateHandSide(int cardIndex, Side side) throws RemoteException;
    void updatePoints(int playerIndex, int points) throws RemoteException;
    void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException;
    void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException;
    void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) throws RemoteException;
    //side is null if is not decided
    void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException;
    void updateWinner(ArrayList<Integer> playerIndexes) throws RemoteException;

}