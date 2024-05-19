package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

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

    @Override
    public void sendMessage(String message) throws RemoteException {
        output.println(Messages.MESSAGE + "; " + message);
        output.flush();
    }

    public void sendError(String error) throws RemoteException {
        output.println(Messages.ERROR + "; " + error);
        output.flush();
    }


    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        output.println(Messages.NICKNAME + "; " + nickname);
        output.flush();
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        output.println(Messages.SENDINDEX + "; " + index);
        output.flush();

    }

    @Override
    public void changePhase(GamePhase nextGamePhase) {
        output.println(Messages.CHANGEPHASE + "; " + nextGamePhase.toString());
        output.flush();
    }

    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {

    }

    @Override
    public void updateAddEnd(int playerIndex, int cardIndex, Side side) throws RemoteException {

    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex, Side side) throws RemoteException {

    }

    @Override
    public void updateBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos) throws RemoteException {

    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {

    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {

    }

    @Override
    public void updateHandSide(int playerIndex, Side side) throws RemoteException {

    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {

    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {

    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {

    }

    @Override
    public void updateStarteCard(int cardId1) throws RemoteException {

    }
}