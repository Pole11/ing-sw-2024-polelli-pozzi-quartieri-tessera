package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.IOException;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;


import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

public class Server implements VirtualServer {
    final Controller controller;
    final ServerSocket listenSocket;
    final Registry registry;

    public Server(ServerSocket listenSocket, Controller controller, Registry registry) {
        this.listenSocket = listenSocket;
        this.controller = controller;
        this.registry = registry;
    }

    public static void main(String[] argv) throws IOException {
        System.out.println("Executing server");

        String host = argv[0];
        int socketport = Integer.parseInt(argv[1]);
        int rmiport = Integer.parseInt(argv[2]);

        startServer(host, socketport, rmiport);
    }

    public static void startServer(String host, int socketport, int rmiport) throws IOException{
        // listen to socket
        ServerSocket listenSocket = new ServerSocket(socketport);

        // listen to rmi
        System.setProperty("java.rmi.server.hostname", host);
        String name = "VirtualServer";
        Registry registry = LocateRegistry.createRegistry(rmiport);

        Server server = new Server(listenSocket, new Controller(), registry);

        //setup gamestate
        GameState gameState = new GameState(server);
        Populate.populate(gameState);

        //FA persistance
        Populate.restoreState(gameState);
        Populate.saveState(gameState);

        server.controller.setGameState(gameState);

        gameState.startThreads();

        //start RMI
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server, 0);
        registry.rebind(name, stub);

        //start socket
        server.runServer();
    }

    public void restart() {
        GameState gameState = new GameState(this);
        try {
            Populate.populate(gameState);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        controller.setGameState(gameState);
        gameState.startThreads();
    }

    private void runServer() throws IOException {
        Socket clientSocket = null;
        while ((clientSocket = this.listenSocket.accept()) != null) {
            InputStreamReader socketRx = new InputStreamReader(clientSocket.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(clientSocket.getOutputStream());

            ClientHandler handler = new ClientHandler(this,  new BufferedReader(socketRx), new BufferedWriter(socketTx));

            new Thread(() -> {
                try {
                    handler.runVirtualView();
                    //controller.manageDisconnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @Override
    public void connectRmi(VirtualView client) throws RemoteException {
        System.out.println("New RMI " + "client connected");
    }

    @Override
    public void addConnectedPlayer(VirtualView client, String nickname) throws RemoteException {
        this.controller.addPlayer(client, nickname);
    }


    @Override
    public void startGame(VirtualView client) throws RemoteException{
        if (!checkConnected(client)) return;
        if (controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE)){
            this.controller.startGame(client);
        } else {
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, Side side) throws RemoteException {
        if (!checkConnected(client)) return;
        if (controller.getGamePhase().equals(GamePhase.CHOOSESTARTERSIDEPHASE)) {
            int playerIndex = getPlayerIndex(client);
            this.controller.chooseInitialStarterSide(playerIndex, side);
        }
        else{
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void chooseInitialColor(VirtualView client, Color color) throws RemoteException {
        if (!checkConnected(client)) return;
        if (controller.getGamePhase().equals(GamePhase.CHOOSECOLORPHASE)) {
            int playerIndex = getPlayerIndex(client);
            this.controller.chooseInitialColor(playerIndex, color);
        }
        else {
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void chooseInitialObjective(VirtualView client, int index) throws RemoteException {
        if (!checkConnected(client)) return;
        if (controller.getGamePhase().equals(GamePhase.CHOOSEOBJECTIVEPHASE)) {
            int playerIndex = getPlayerIndex(client);
            this.controller.chooseInitialObjective(playerIndex, index);
        }
        else{
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void placeCard(VirtualView client, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException {
        if (!checkConnected(client)) return;
        int playerIndex = getPlayerIndex(client);

        //NON FUNZIONA IL CONTROLLO SUL PLAYER
        //NON FUNZIONA IL PARSING A INT SU SOCKEit
        if (
                (controller.getGamePhase().equals(GamePhase.MAINPHASE) || controller.getGamePhase().equals(GamePhase.ENDPHASE)) &&
                        (controller.getTurnPhase().equals(TurnPhase.PLACINGPHASE)) &&
                        (isRightTurn(playerIndex))
        ) {
            //CardIsNotInHand e CardAlreadyPlaced FORSE da rimuovere e gestire sulla view
            try {
                this.controller.placeCard(playerIndex, placingCardId, tableCardId, tableCornerPos, placingCardSide);
            } catch (WrongInstanceTypeException | CardIsNotInHandException | CardAlreadPlacedException | CardAlreadyPresentOnTheCornerException | PlacingOnHiddenCornerException | GoldCardCannotBePlacedException e){
                client.sendError(e.getMessage());
            } catch (WrongPlacingPositionException | CardNotPlacedException e) {
                throw new RuntimeException();
            }
        } else{
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void drawCard(VirtualView client, DrawType drawType) throws RemoteException {
        if (!checkConnected(client)) return;
        int playerIndex = getPlayerIndex(client);


        if (
            (controller.getGamePhase().equals(GamePhase.MAINPHASE) || controller.getGamePhase().equals(GamePhase.ENDPHASE)) &&
            (controller.getTurnPhase().equals(TurnPhase.DRAWPHASE)) &&
            (isRightTurn(playerIndex))
        ){
            try {
                this.controller.drawCard(drawType);
            } catch (InvalidHandException e) {
                client.sendError("Too many cards in hand");
            } catch (EmptyDeckException e) {
                client.sendError("The deck is empty");
            } catch (EmptyMainBoardException e){
                client.sendError("The main board is empty");
            }
        }
        else{
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void flipCard(VirtualView client, int cardId) throws RemoteException {
        if (!checkConnected(client)) return;
        if (controller.getGamePhase().equals(GamePhase.MAINPHASE) || controller.getGamePhase().equals(GamePhase.ENDPHASE)) {
            int playerIndex = getPlayerIndex(client);
            try {
                this.controller.flipCard(playerIndex, cardId);
            } catch (CardIsNotInHandException e) {
                client.sendError("The selected card is not in hand");
            }
        } else {
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void addMessage(VirtualView client, String content) throws RemoteException {
        if (!checkConnected(client)) return;
        this.controller.addMessage(getPlayerIndex(client), content);
    }

    @Override
    public void ping(VirtualView client) throws RemoteException {
        this.controller.ping(client);
    }

    @Override
    public void gameEnded(VirtualView client) throws RemoteException {
        this.controller.gameEnded(client);
    }

    private boolean checkConnected(VirtualView client) throws RemoteException {
        if (!controller.isConnected(client)){
            client.sendError("you disconnected from the game, to reconnect login with ADDUSER <your previous nickname>");
            return false;
        }
        return true;
    }


    private int getPlayerIndex(VirtualView client) {
        return controller.getPlayerIndex(client);
    }

    private boolean isRightTurn(int playerIndex){
        return (controller.getCurrentPlayerIndex() == playerIndex);
    }

}