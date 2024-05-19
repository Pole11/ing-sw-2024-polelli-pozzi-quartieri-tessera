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
            String[] message = line.split("; ");
            switch (message[0]) {
                case "MESSAGE":
                    this.sendMessage(message[1]);
                    break;
                case "ERROR":
                    this.sendError(message[1]);
                    break;
                case "PING":
                    this.ping(message[1]);
                    break;
                case "PHASE":
                    this.changePhase(GamePhase.valueOf(message[1]));
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

    }

    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        Client.nicknameUpdate(index, nickname);
    }

    @Override
    public void sendIndex(int index) throws RemoteException {

    }

    @Override
    public void changePhase(GamePhase nextGamePhaseString) {
        Client.changePhase(nextGamePhaseString);
        /*
        try {
            System.out.println(nextGamePhaseString);
            GamePhase nextGamePhase = GamePhase.valueOf(nextGamePhaseString);
            Client.changePhase(nextGamePhase);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Invalid game phase");
        }*/
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