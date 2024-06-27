package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.application.Platform;

import java.io.*;
import java.net.*;
import java.net.ConnectException;
import java.rmi.*;
import java.util.*;

/**
 * Represents a socket client implementation that communicates with a VirtualServer.
 */
public class SocketClient implements VirtualView {
    private final  BufferedReader input;
    private final ServerProxy server;
    private final Client clientContainer;

    /**
     * Constructs a socket client with the specified input reader, server proxy, and client container.
     *
     * @param input The BufferedReader for reading input messages from the server.
     * @param server The ServerProxy instance to communicate with.
     * @param clientContainer The client container which manages the client's behavior.
     */
    public SocketClient(BufferedReader input, VirtualServer server, Client clientContainer) {
        this.input = input;
        this.server = (ServerProxy) server;
        this.clientContainer = clientContainer;
    }

    /**
     * Runs the socket client logic. Starts a new thread for running the virtual server interaction.
     * Handles user interaction based on whether the client is currently running or not.
     *
     * @throws RemoteException If there is an issue with RMI communication.
     */
    public void run() throws RemoteException {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (ConnectException e) {
                clientContainer.serverDisconnected();
            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();

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

    }

    /**
     * Handles incoming messages from the virtual server.
     *
     * @throws IOException If there is an issue reading from the input stream.
     */
    private void runVirtualServer() throws IOException {
        String line;
        // Read message type
        while ((line = input.readLine()) != null) {
            String[] messageString = line.split("; ");
            Messages message = Messages.valueOf(messageString[0]);
            switch (message) {
                case Messages.GAMEPHASE:
                    this.updateGamePhase(GamePhase.valueOf(messageString[1]));
                    break;
                case Messages.TURNPHASE:
                    this.updateTurnPhase(TurnPhase.valueOf(messageString[1]));
                    break;
                case Messages.CONNECTIONINFO:
                    this.connectionInfo(Integer.parseInt(messageString[1]), Boolean.parseBoolean(messageString[2]));
                    break;
                case Messages.ERROR:
                    this.sendError(messageString[1]);
                    break;
                case Messages.NICKNAME:
                    this.nicknameUpdate(Integer.parseInt(messageString[1]),messageString[2]);
                    break;
                case Messages.PING:
                    this.ping(messageString[1]);
                    break;
                case Messages.SENDINDEX:
                    this.sendIndex(Integer.parseInt(messageString[1]));
                    break;
                case Messages.ADDHAND:
                    this.updateAddHand(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]));
                    break;
                case Messages.REMOVEHAND:
                    this.updateRemoveHand(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]));
                    break;
                case Messages.UPDATEPLAYERBOARD:
                    this.updatePlayerBoard(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]), Integer.parseInt(messageString[3]), CornerPos.valueOf(messageString[4]), Side.valueOf(messageString[5]));
                    break;
                case Messages.UPDATEMAINBOARD:
                    this.updateMainBoard(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]), Integer.parseInt(messageString[3]), Integer.parseInt(messageString[4]), Integer.parseInt(messageString[5]), Integer.parseInt(messageString[6]));
                    break;
                case Messages.UPDATECOLOR:
                    this.updateColor(Integer.parseInt(messageString[1]), Color.valueOf(messageString[2]));
                    break;
                case Messages.UPDATECURRENTPLAYER:
                    this.updateCurrentPlayer(Integer.parseInt(messageString[1]));
                    break;
                case Messages.UPDATEHANDSIDE:
                    this.updateHandSide(Integer.parseInt(messageString[1]), Side.valueOf(messageString[2]));
                    break;
                case Messages.UPDATEPOINTS:
                    this.updatePoints(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]));
                    break;
                case Messages.UPDATESECRETOBJECTIVE:
                    this.updateSecretObjective(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]));
                    break;
                case Messages.UPDATESHAREDOBJECTIVE:
                    this.updateSharedObjective(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]));
                    break;
                case Messages.UPDATESTARTER:
                    Side side;
                    if (messageString[3].equals("null")) { // if NULL is passed as Side parameter, t
                        side = null;
                    } else {
                        side = Side.valueOf(messageString[3]);
                    }
                    this.updateStarterCard(Integer.parseInt(messageString[1]), Integer.parseInt(messageString[2]), side);
                    break;
                case Messages.UPDATEWINNER:
                    ArrayList<Integer> winners = new ArrayList<>();
                    for (int i = 1; i < messageString.length; i++){
                        winners.add(Integer.parseInt(messageString[i]));
                    }
                    this.updateWinner(winners);
                    break;
                case Messages.UPDATEELEMENT:
                    this.updateElement(Integer.parseInt(messageString[1]), Element.valueOf(messageString[2]), Integer.parseInt(messageString[3]));
                    break;
                case Messages.UPDATECHAT:
                    this.updateChat(Integer.parseInt(messageString[1]), messageString[2].replace("~", " "));
                    break;
                case Messages.REDIRECTOUT:
                    this.redirectOut(Boolean.parseBoolean(messageString[1]));
                    break;
                case Messages.PONG:
                    this.pong();
                    break;
                default:
                    System.err.println("[5xx INVALID MESSAGE FROM SERVER]");
                    break;
            }
        }
        //clientContainer.serverDisconnected();
        //TODO: controllare se funziona anche se il server perde la connesisone e non solo se crasha
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
    public void pong() throws RemoteException {
        clientContainer.pong();
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