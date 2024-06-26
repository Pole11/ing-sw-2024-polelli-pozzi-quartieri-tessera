package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.Platform;

import java.util.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.rmi.*;

public class RmiClient extends UnicastRemoteObject implements VirtualView {
    private final VirtualServer server;
    private final Client clientContainer;

    // TODO: it is public for testing purpose
    public RmiClient(VirtualServer server, Client clientContainer) throws RemoteException {
        this.server = server;
        this.clientContainer = clientContainer;
    }

    public void run() {
        try {
            if (!clientContainer.isRunning()){
                System.out.println("Welcome to CODEX!");
                Scanner scan = new Scanner(System.in);
                System.out.print("Do you GUI? [Y/n] ");
                String input = scan.nextLine();
                if (input != null && (input.equals("") || input.equalsIgnoreCase("y"))) {
                    clientContainer.runGui();
                } else if (input.equalsIgnoreCase("n")) {
                    this.server.connectRmi(this);
                    clientContainer.runCli();
                } else {
                    System.out.println("Please enter a valid input!");
                }
            } else {
                if (clientContainer.isMeDoGui()){
                    clientContainer.getGuiApplication().setClientServer(this, server);
                    clientContainer.getGuiApplication().changeScene("/fxml/lobby.fxml");
                    clientContainer.getGuiApplication().getGUIController().restartPong(server, this, clientContainer);

                } else {
                    //clientContainer.runCli();
                    clientContainer.getCliController().restartPong(server, this, clientContainer);
                    System.out.println("the server is now available, try to re-connect with command ADDUSER");
                }
            }
        } catch (RemoteException e) {

        }
    }

    public Client getClientContainer(){
        return clientContainer;
    }

    @Override
    public void sendError(String msg) throws RemoteException {
        clientContainer.sendError(msg);
    }

    @Override
    public void ping(String ping) throws RemoteException {
        clientContainer.ping(ping);
    }

    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        clientContainer.nicknameUpdate(index, nickname);
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        clientContainer.sendIndex(index);
    }

    @Override
    public void updateGamePhase(GamePhase nextGamePhaseString) {
        clientContainer.updateGamePhase(nextGamePhaseString);
    }

    @Override
    public void updateTurnPhase(TurnPhase nextTurnPhase) throws RemoteException {
        clientContainer.updateTurnPhase(nextTurnPhase);
    }

    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        clientContainer.connectionInfo(playerIndex, connected);
    }

    @Override
    public void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        clientContainer.updateAddHand(playerIndex, cardIndex);
    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        clientContainer.updateRemoveHand(playerIndex, cardIndex);
    }

    @Override
    public void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        clientContainer.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {
        clientContainer.updateColor(playerIndex, color);
    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        clientContainer.updateCurrentPlayer(currentPlayerIndex);
    }

    @Override
    public void updateHandSide(int cardIndex, Side side) throws RemoteException {
        clientContainer.updateHandSide(cardIndex, side);
    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {
        clientContainer.updatePoints(playerIndex, points);
    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        clientContainer.updateSecretObjective(objectiveCardId1, objectiveCardId2);
    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        clientContainer.updateSharedObjective(sharedObjectiveCardId1, sharedObjectiveCardId2);
    }

    @Override
    public void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) {
        clientContainer.updateMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firtGoldDeckCard, firstResourceDeckCard);
    }

    @Override
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        clientContainer.updateStarterCard(playerIndex, cardId1, side);
    }

    @Override
    public void updateWinner(ArrayList<Integer> playerIndexes) throws RemoteException {
        clientContainer.updateWinner(playerIndexes);
    }

    @Override
    public void updateElement(int playerIndex, Element element, int numberOfElements) throws RemoteException {
        clientContainer.updateElement(playerIndex, element, numberOfElements);
    }

    @Override
    public void updateChat(int playerIndex, String content) throws RemoteException {
        clientContainer.updateChat(playerIndex, content);
    }

    @Override
    public void redirectOut(boolean bool) throws RemoteException {
        clientContainer.redirectOut(bool);
    }
}
