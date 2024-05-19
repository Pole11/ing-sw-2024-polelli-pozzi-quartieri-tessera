package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;

import java.rmi.*;

//clint che vede il server
public interface VirtualView extends Remote {
    void ping(String ping) throws RemoteException;
    void sendMessage(String message) throws RemoteException;
    void sendError(String error) throws RemoteException;
    void sendIndex(int index) throws RemoteException;
    void nicknameUpdate(int index, String nickname) throws RemoteException;
    void changePhase(GamePhase nextGamePhase) throws RemoteException;
    void connectionInfo(int playerIndex, boolean connected) throws RemoteException;
    void updateAddEnd(int playerIndex, int cardIndex, Side side) throws RemoteException;
    void updateRemoveHand(int playerIndex, int cardIndex, Side side) throws RemoteException;
    void updateBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos) throws RemoteException;
    void updateColor(int playerIndex, Color color) throws RemoteException;
    void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException;
    void updateHandSide(int playerIndex, Side side) throws RemoteException;
    void updatePoints(int playerIndex, int points) throws RemoteException;
    void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException;
    void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException;
    void updateStarteCard(int cardId1) throws RemoteException;
}