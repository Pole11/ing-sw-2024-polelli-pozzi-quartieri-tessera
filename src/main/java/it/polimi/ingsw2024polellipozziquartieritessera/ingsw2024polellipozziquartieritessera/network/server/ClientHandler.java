package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.CommandRunnable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Class that resides in the server and handles the clients messages for the server
 */
public class ClientHandler implements VirtualView {
    final Server server;
    final BufferedReader input;
    final VirtualView view;

    /**
     * Constructor for the ClientHandler class.
     *
     * @param server The server instance this handler is associated with.
     * @param input The BufferedReader to read input from the client.
     * @param output The BufferedWriter to send output to the client.
     */
    public ClientHandler(Server server, BufferedReader input, BufferedWriter output) {
        this.server = server;
        this.input = input;
        this.view = new ClientProxy(output);
    }

    /**
     * Runs the virtual client, continuously reading input from the client and handling commands.
     *
     * @throws IOException If an I/O error occurs while reading input.
     */
    public void runVirtualView() throws IOException {
        String line;
        System.out.println("New Socket client connected");
        while ((line = input.readLine()) != null) {
            String[] message = line.split("; ");
            Command command = null;
            //is thw ack for the ending of the game, it is a unsual way to send data because some of structures are not in the usual behaviour
            if (message[0].equals(Command.GAMEENDED.toString())) {
                server.gameEnded(this);
            } else {
                try {
                    command = Command.valueOf(message[0].toUpperCase());
                } catch(IllegalArgumentException e) {
                    System.out.println("Invalid command: " + message[0]);
                    continue;
                }

                if (Command.valueOf(message[0]).getType().equals("Local")){
                    System.err.println("message arrived to server that should have been managed in client");
                } else {
                    Command.valueOf(message[0]).getCommandRunnable(message, server, null, this).executeCLI();
                }
            }

        }
    }

    @Override
    public void ping(String ping) throws RemoteException {
        view.ping(ping);
    }

    @Override
    public void pong() throws RemoteException {
        view.pong();
    }

    @Override
    public void sendError(String error) throws RemoteException {
        view.sendError(error);
    }

    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        view.nicknameUpdate(index, nickname);
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        view.sendIndex(index);
    }

    @Override
    public void updateGamePhase(GamePhase nextGamePhase) throws RemoteException {
        view.updateGamePhase(nextGamePhase);
    }

    @Override
    public void updateTurnPhase(TurnPhase nextTurnPhase) throws RemoteException {
        view.updateTurnPhase(nextTurnPhase);
    }

    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        view.connectionInfo(playerIndex, connected);
    }

    @Override
    public void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        view.updateAddHand(playerIndex, cardIndex);
    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        view.updateRemoveHand(playerIndex, cardIndex);
    }

    @Override
    public void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        view.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {
        view.updateColor(playerIndex, color);
    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        view.updateCurrentPlayer(currentPlayerIndex);
    }

    @Override
    public void updateHandSide(int cardIndex, Side side) throws RemoteException {
        view.updateHandSide(cardIndex, side);
    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {
        view.updatePoints(playerIndex, points);
    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        view.updateSecretObjective(objectiveCardId1, objectiveCardId2);
    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        view.updateSharedObjective(sharedObjectiveCardId1, sharedObjectiveCardId2);
    }

    @Override
    public void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) throws RemoteException {
        view.updateMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firtGoldDeckCard, firstResourceDeckCard);
    }

    @Override
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        view.updateStarterCard(playerIndex, cardId1, side);
    }

    @Override
    public void updateWinner(ArrayList<Integer> playerIndexes) throws RemoteException {
        view.updateWinner(playerIndexes);
    }

    @Override
    public void updateElement(int playerIndex, Element element, int numberOfElements) throws RemoteException {
        view.updateElement(playerIndex, element, numberOfElements);
    }

    @Override
    public void updateChat(int playerIndex, String content) throws RemoteException {
        view.updateChat(playerIndex, content);
    }
    public void redirectOut(boolean bool) throws RemoteException {
        view.redirectOut(bool);
    }
}
