package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.io.*;
import java.net.*;
import java.rmi.*;
import java.util.*;



public class SocketClient implements VirtualView {
    final BufferedReader input;
    final ServerProxy server;

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
                case Messages.MESSAGE:
                    this.sendMessage(messageString[1]);
                    break;
                case Messages.ERROR:
                    this.sendError(messageString[1]);
                    break;
                case Messages.PING:
                    this.ping(messageString[1]);
                    break;
                case Messages.CHANGEPHASE:
                    this.changePhase(GamePhase.valueOf(messageString[1]));
                    break;

                default:
                    System.err.println("[5xx INVALID MESSAGE FROM SERVER]");
                    break;
            }
        }
    }

    @Override
    public void sendMessage(String msg) {
        Client.printMessage(msg);
    }

    @Override
    public void sendError(String msg) throws RemoteException {
        Client.printError(msg);
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
    public void changePhase(GamePhase nextGamePhaseString) {
        Client.changePhase(nextGamePhaseString);
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
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        Client.updateStarterCard(playerIndex, cardId1, side);
    }

}