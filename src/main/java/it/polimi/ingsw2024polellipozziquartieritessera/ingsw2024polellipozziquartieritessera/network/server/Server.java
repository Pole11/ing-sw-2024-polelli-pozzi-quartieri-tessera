package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.IOException;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;


import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;


import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

public class Server implements VirtualServer {
    final Controller controller;
    final ServerSocket listenSocket;
    final HashMap<Integer, Boolean> answered;

    public Server(ServerSocket listenSocket, Controller controller) {
        this.listenSocket = listenSocket;
        this.controller = controller;
        this.answered = new HashMap<>();
        resetAnswered();
    }

    public static void main(String[] argv) throws IOException, WrongStructureConfigurationSizeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException {
        String host = argv[0];
        int socketport = Integer.parseInt(argv[1]);
        int rmiport = Integer.parseInt(argv[2]);

        startServer(host, socketport, rmiport);
    }

    private void resetAnswered() {
        this.answered.put(0, false);
        this.answered.put(1, false);
        this.answered.put(2, false);
        this.answered.put(3, false);
    }

    public static void startServer(String host, int socketport, int rmiport) throws IOException, WrongStructureConfigurationSizeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException{
        //setup gamestate
        GameState gameState = null;
        boolean store = Populate.existStore(); //FA persistance
        if (store){
            // takes data from store
        } else {
            gameState = Populate.populate();
        }

        // listen to socket
        ServerSocket listenSocket = new ServerSocket(socketport);
        Server server = new Server(listenSocket, new Controller(gameState));

        // listen to rmi
        System.setProperty("java.rmi.server.hostname", host);
        String name = "VirtualServer";
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server, 0);
        Registry registry = LocateRegistry.createRegistry(rmiport);
        registry.rebind(name, stub);
        /*
        new Thread(() -> {
            try {
                server.periodicPing();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

         */

        //listen to socket
        server.runServer();
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
                    System.out.println("Client disconnected");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }

    @Override
    public void connectRmi(VirtualView client) throws RemoteException {
        System.out.println("New RMI client connected");
    }

    @Override
    public void addConnectedPlayer(VirtualView client, String nickname) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE) || controller.getGamePhase().equals(GamePhase.TIMEOUT)){
            System.out.println("New player tried connecting!");
            this.controller.addPlayer(client, nickname);
        } else {
            client.sendError("You cannot do this action now");
        }
    }


    @Override
    public void startGame(VirtualView client) throws RemoteException{
        if (controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE)){
            this.controller.startGame(client);
        } else {
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, Side side) throws RemoteException {
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
        int playerIndex = getPlayerIndex(client);

        //NON FUNZIONA IL CONTROLLO SUL PLAYER
        //NON FUNZIONA IL PARSING A INT SU SOCKEit
        if (
            controller.getGamePhase().equals(GamePhase.MAINPHASE) &&
            (controller.getTurnPhase().equals(TurnPhase.PLACINGPHASE)) &&
            (isRightTurn(playerIndex))
        ){

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
        int playerIndex = getPlayerIndex(client);

        if (
            controller.getGamePhase().equals(GamePhase.MAINPHASE) &&
            (controller.getTurnPhase().equals(TurnPhase.DRAWPHASE)) &&
            (isRightTurn(playerIndex))
        ){
            try {
                this.controller.drawCard(drawType);
            } catch (InvalidHandException e) {
                client.sendError("Too many cards in hand");
            } catch (EmptyDeckException e) {
                client.sendError("The deck is empty");
            }
        }
        else{
            client.sendError("You cannot do this action now");
        }
    }

    @Override
    public void flipCard(VirtualView client, int cardId) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.MAINPHASE)) {
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
    public void openChat() {
        this.controller.openChat();
    }

    @Override
    public void addMessage(VirtualView client, String content) {
        this.controller.addMessage(getPlayerIndex(client), content);
    }

    @Override
    public void ping(VirtualView client) throws RemoteException {
        this.controller.ping(client);
    }


    private int getPlayerIndex(VirtualView client) {
        return controller.getPlayerIndex(client);
    }

    private boolean isRightTurn(int playerIndex){
        return (controller.getCurrentPlayerIndex() == playerIndex);
    }

    //-----pinging-------

    /*
    private void periodicPing() throws InterruptedException {
        while (true){
            if (controller.getGamePhase().equals(GamePhase.TIMEOUT)){
                if (!controller.timeoutThread.isAlive()){
                    //il bro ha vinto
                }
            } else {
                updatePlayersConnected();
                Thread.sleep(3000);
            }
        }
    }

     */

    /*
    private void updatePlayersConnected(){
        AtomicInteger index = new AtomicInteger();
        AtomicInteger numberConnected = new AtomicInteger();
        clients.values().stream().forEach(e->{
            boolean connected = false;
            if (ping(e)){
                connected = true;
                numberConnected.getAndIncrement();
            }
            controller.setConnected(index.get(), connected);
            index.getAndIncrement();
        });
        if (!controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE)) {
            if (numberConnected.get() == 0){
                //chiedere specifica
            } else if (numberConnected.get() == 1){
                controller.setGamePhase(GamePhase.TIMEOUT);
                controller.timeoutThread =  new Thread(() -> {
                    try {
                        controller.startTimeout();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });
                controller.timeoutThread.start();
            }
        }
    }



    private boolean ping(VirtualView client){
        try {
            client.ping("ping");
        } catch (RemoteException e) {
            System.out.println("client" + getPlayerIndex(client) + "is disconnected");
            return false;
        }
        return true;
    }GUI

     */
}