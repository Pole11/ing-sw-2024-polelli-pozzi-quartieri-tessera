package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.util.*;



public class SocketClient implements VirtualView {
    private final  BufferedReader input;
    private final ServerProxy server;

    public SocketClient(BufferedReader input, BufferedWriter output) {
        this.input = input;
        this.server = new ServerProxy(output);
    }

    public static void execute(String host, String portString) throws IOException {
        int port = Integer.parseInt(portString);
        Socket socketToServer = new Socket(host, port);

        InputStreamReader socketRx = new InputStreamReader(socketToServer.getInputStream());
        OutputStreamWriter socketTx = new OutputStreamWriter(socketToServer.getOutputStream());

        new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx)).run();
    }

    private void run() throws RemoteException {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

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
    }

    private void runVirtualServer() throws IOException {
        System.out.println("Welcome to CODEX!");

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
                case Messages.MESSAGE:
                    this.sendMessage(messageString[1]);
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
                case Messages.START:
                    this.start();
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
                default:
                    System.err.println("[5xx INVALID MESSAGE FROM SERVER]");
                    break;
            }
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
    public void
    updateTurnPhase(TurnPhase nextTurnPhase) throws RemoteException {
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