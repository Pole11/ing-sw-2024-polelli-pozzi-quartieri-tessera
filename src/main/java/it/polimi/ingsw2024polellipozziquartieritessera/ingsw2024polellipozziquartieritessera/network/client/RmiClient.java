package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.util.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.rmi.*;

public class RmiClient extends UnicastRemoteObject implements VirtualView {
    private final VirtualServer server;

    // TODO: it is public for testing purpose
    public RmiClient(VirtualServer server) throws RemoteException {
        this.server = server;
    }

    public static void execute(String host, String port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
            VirtualServer server = (VirtualServer) registry.lookup("VirtualServer");
            (new RmiClient(server)).run(); // crea la mia copia sul server
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
            System.out.println("An error occurred while executing RmiClient!");
        }
    }

    private void run() {
        try {
            System.out.println("Welcome to CODEX!");

            // CHIEDI SE VUOLE GUI O TUI
            Scanner scan = new Scanner(System.in);
            System.out.print("Do you GUI? [Y/n] ");
            String input = scan.nextLine();
            if (input != null && (input.equals("") || input.equalsIgnoreCase("y"))) {
                Client.runGui(server, this);
            } else if (input.equalsIgnoreCase("n")) {
                this.server.connectRmi(this);
                Client.runCli(server, this);
            } else {
                System.out.println("Please enter a valid input!");
            }
        } catch (RemoteException e) {

        }
    }

    @Override
    public void sendMessage(String msg) throws RemoteException {
        Client.sendMessage(msg);
    }

    @Override
    public void sendError(String msg) throws RemoteException {
        Client.sendError(msg);
    }

    @Override
    public void ping(String ping) throws RemoteException {
        Client.ping(ping);
    }

    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        Client.nicknameUpdate(index, nickname);
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        Client.sendIndex(index);
    }

    @Override
    public void updateGamePhase(GamePhase nextGamePhaseString) {
        Client.changePhase(nextGamePhaseString);
    }

    @Override
    public void updateTurnPhase(TurnPhase nextTurnPhase) throws RemoteException {
        Client.updateTurnPhase(nextTurnPhase);
    }

    @Override
    public void start() throws RemoteException {
        Client.start();
    }

    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        Client.connectionInfo(playerIndex, connected);
    }

    @Override
    public void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        Client.updateAddHand(playerIndex, cardIndex);
    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        Client.updateRemoveHand(playerIndex, cardIndex);
    }

    @Override
    public void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        Client.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {
        Client.updateColor(playerIndex, color);
    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        Client.updateCurrentPlayer(currentPlayerIndex);
    }

    @Override
    public void updateHandSide(int cardIndex, Side side) throws RemoteException {
        Client.updateHandSide(cardIndex, side);
    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {
        Client.updatePoints(playerIndex, points);
    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        Client.updateSecretObjective(objectiveCardId1, objectiveCardId2);
    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        Client.updateSharedObjective(sharedObjectiveCardId1, sharedObjectiveCardId2);
    }

    @Override
    public void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) {
        Client.updateMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firtGoldDeckCard, firstResourceDeckCard);
    }

    @Override
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        Client.updateStarterCard(playerIndex, cardId1, side);
    }

}
