package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

public class ClientProxy implements VirtualView {
    final PrintWriter output;

    public ClientProxy(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void ping(String ping) throws RemoteException {
        output.println(Messages.PING + "; " + ping);
        output.flush();
    }

    public void sendError(String error) throws RemoteException {
        output.println(Messages.ERROR + "; " + error);
        output.flush();
    }


    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        output.println(Messages.NICKNAME + "; " + index + "; "+ nickname);
        output.flush();
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        output.println(Messages.SENDINDEX + "; " + index);
        output.flush();

    }

    @Override
    public void updateGamePhase(GamePhase nextGamePhase) {
        output.println(Messages.GAMEPHASE + "; " + nextGamePhase.toString());
        output.flush();
    }

    @Override
    public void updateTurnPhase(TurnPhase nextTurnPhase) throws RemoteException {
        output.println(Messages.TURNPHASE + "; " + nextTurnPhase.toString());
        output.flush();
    }


    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        output.println(Messages.CONNECTIONINFO + "; " + playerIndex + "; " + connected);
        output.flush();
    }

    @Override
    public void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        output.println(Messages.ADDHAND + "; " + playerIndex + "; " + cardIndex);
        output.flush();
    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        output.println(Messages.REMOVEHAND + "; " + playerIndex + "; " + cardIndex);
        output.flush();
    }

    @Override
    public void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        output.println(Messages.UPDATEPLAYERBOARD + "; " + playerIndex + "; " + placingCardId + "; " + tableCardId + "; " + existingCornerPos.toString() + "; " + side.toString());
        output.flush();
    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {
        output.println(Messages.UPDATECOLOR + "; " + playerIndex + "; " + color.toString());
        output.flush();
    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        output.println(Messages.UPDATECURRENTPLAYER + "; " + currentPlayerIndex);
        output.flush();
    }

    @Override
    public void updateHandSide(int cardIndex, Side side) throws RemoteException {
        output.println(Messages.UPDATEHANDSIDE + "; " + cardIndex + "; " + side.toString());
        output.flush();
    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {
        output.println(Messages.UPDATEPOINTS + "; " + playerIndex + "; " + points);
        output.flush();
    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        output.println(Messages.UPDATESECRETOBJECTIVE + "; " + objectiveCardId1 + "; " + objectiveCardId2);
        output.flush();
    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        output.println(Messages.UPDATESHAREDOBJECTIVE + "; " + sharedObjectiveCardId1 + "; " + sharedObjectiveCardId2);
        output.flush();
    }

    @Override
    public void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) throws RemoteException {
        output.println(Messages.UPDATEMAINBOARD + "; " + sharedGoldCard1 + "; " + sharedGoldCard2 + "; " + sharedResourceCard1 + "; " + sharedResourceCard2 + "; " + firtGoldDeckCard + "; " + firstResourceDeckCard);
        output.flush();
    }

    @Override
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        String sideString;
        if (side == null){
            sideString = "null";
        } else {
            sideString = side.toString();
        }
        output.println(Messages.UPDATESTARTER + "; " + playerIndex + "; " + cardId1 + "; " + sideString);
        output.flush();
    }

    @Override
    public void updateWinner(ArrayList<Integer> playerIndexes) throws RemoteException {
        String message = Messages.UPDATEWINNER.toString();
        for (int playerIndex : playerIndexes) {
            message += "; " + playerIndex;
        }
        output.println(message);
        output.flush();
    }

    @Override
    public void updateElement(int playerIndex, Element element, int numberOfElements) throws RemoteException {
        output.println(Messages.UPDATEELEMENT + "; " + playerIndex + "; " + element + "; " + numberOfElements);
        output.flush();
    }
}