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
    final HashMap<Integer, VirtualView> clients = new HashMap<>();
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

    private int numberAnswered() {
        int res = 0;
        for (boolean v : this.answered.values()) {
            if (v) { res++; }
        }
        return res;
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
            if (this.clients.size() >= Config.MAX_PLAYERS) {
                client.sendError("The game is full");
                return;
            }
            if (clients.containsValue(client)) {
                client.sendError("You already chose a nickname, you cannot change it");
                return;
            }
            this.clients.put(this.clients.size(), client);
            this.controller.addPlayer(client, nickname);
            System.out.println("New player connected!");
        } else {
            client.sendError("You cannot do this action now");
        }
    }

    //probabilmente da rimuovere prima o poi
    @Override
    public void showNickname(VirtualView client) throws RemoteException{
        if (clients.containsValue(client)){
            if(ping(client)){
                client.sendMessage("Your nickname is " + controller.getPlayerNickname(getPlayerIndex(client)));
            }
        } else {
            if(ping(client)){
                client.sendError("You didn't choose a nickname, use the command ADDUSER [nickname] to do that");
            }
        }
    }

    @Override
    public void startGame(VirtualView client) throws RemoteException{
        if (!clients.values().contains(client)) {
            client.sendError("Please register before starting the game");
            return;
        }
        if (clients.size() >= 2) {
            this.controller.startGame();
            this.controller.setGamePhase(GamePhase.CHOOSESTARTERSIDEPHASE);
        }
        else{
            if (clients.containsValue(client)){
                client.sendError("Number of player insufficient");
            } else {
                client.sendError("You must choose your nickname with adduser first");
            }
        }
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, Side side) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSESTARTERSIDEPHASE)) {
            int playerIndex = getPlayerIndex(client);

            if (this.answered.get(playerIndex)) {
                client.sendError("You alreaedy selected the starter card");
                return;
            }

            this.controller.chooseInitialStarterSide(playerIndex, side);

            try {
                client.sendMessage("Thank you!");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            this.answered.put(playerIndex, true);
            if (numberAnswered() == clients.size()) {
                resetAnswered();
                for (VirtualView clientIterator : this.clients.values()) {
                    try {
                        clientIterator.changePhase(GamePhase.CHOOSECOLORPHASE.toString());
                        clientIterator.sendMessage("Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                this.controller.setGamePhase(GamePhase.CHOOSECOLORPHASE);
            }
        }
        else{
            try {
                client.sendError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void chooseInitialColor(VirtualView client, Color color) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSECOLORPHASE)) {
            int playerIndex = getPlayerIndex(client);

            if (this.answered.get(playerIndex)) {
                client.sendError("You alreaedy selected the color");
                return;
            }

            try {
                this.controller.chooseInitialColor(playerIndex, color);
                client.sendMessage("Thank you for selecting the color!");
                this.answered.put(playerIndex, true);
            } catch (NotUniquePlayerColorException e) {
                client.sendError("The color was already selected by another user, please select a new one ;)");
                return;
            }

            if (numberAnswered() == clients.size()) {
                resetAnswered();
                try {
                    controller.colorChoosed();
                } catch (EmptyDeckException e) {
                    throw new RuntimeException(e);
                }
                try {
                    for (VirtualView clientIterator : this.clients.values()) {
                        getPlayerIndex(clientIterator);
                        clientIterator.changePhase(GamePhase.CHOOSEOBJECTIVEPHASE.toString());
                        clientIterator.sendMessage("Everyone chose his color, now please select one of the objective card from the selection with the command CHOOSEOBJECTIVE [0/1]: " + this.controller.getObjectiveCardOptions(playerIndex)[0].getId() + ", " + this.controller.getObjectiveCardOptions(playerIndex)[1].getId());
                        this.controller.setGamePhase(GamePhase.CHOOSEOBJECTIVEPHASE);
                        this.showHand(clientIterator);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            try {
                client.sendError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void chooseInitialObjective(VirtualView client, int cardId) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSEOBJECTIVEPHASE)) {
            int playerIndex = getPlayerIndex(client);

            if (this.answered.get(playerIndex)) {
                client.sendError("You alreaedy selected the objective card");
                return;
            }

            try {
                this.controller.chooseInitialObjective(playerIndex, cardId);
                client.sendMessage("Thank you!");
                this.answered.put(playerIndex, true);
            } catch (InvalidObjectiveCardException e) {
                client.sendError("The objective card you selected was invalid, please try again");
                return;
            }

            if (numberAnswered() == clients.size()) {
                resetAnswered();
                try {
                    for (VirtualView clientIterator : this.clients.values()) {
                        if (clients.get(controller.getCurrentPlayerIndex()).equals(clientIterator)) {
                            if (ping(clientIterator)){
                                clientIterator.sendMessage("The preparation phase is over, it's your turn to play as first, use the command PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)] to place your card");
                            }
                        } else {
                            clientIterator.sendMessage("The preparation phase is over, it's now the turn of " + controller.getPlayerNickname(controller.getCurrentPlayerIndex()) + " to play as first");
                        }
                        clientIterator.changePhase(GamePhase.MAINPHASE.toString());
                        this.showHand(clientIterator);
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
                this.controller.setGamePhase(GamePhase.MAINPHASE);
            }
        }
        else{
            try {
                client.sendError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void showHand(VirtualView client) throws RemoteException {
        /*if (!controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE) && !controller.getGamePhase().equals(GamePhase.CHOOSECOLORPHASE) && !controller.getGamePhase().equals(GamePhase.CHOOSESTARTERSIDEPHASE)) {
            if (ping(client)) {
                int card1Id = controller.getHandId(getPlayerIndex(client)).get(0);
                Side card1Side = controller.getHandSide(getPlayerIndex(client)).get(0);
                int card2Id = controller.getHandId(getPlayerIndex(client)).get(1);
                Side card2Side = controller.getHandSide(getPlayerIndex(client)).get(1);
                int card3Id = controller.getHandId(getPlayerIndex(client)).get(2);
                Side card3Side = controller.getHandSide(getPlayerIndex(client)).get(2);

                client.printCard(card1Id, card1Side, card2Id, card2Side, card3Id, card3Side);
            }
        } else {
            if (ping(client)) {
                client.sendError("Your hand has not been inizialized");
            }
        }

         */
    }

    @Override
    public void showCommonObjectives(VirtualView client) throws RemoteException {
        if (!controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE) && !controller.getGamePhase().equals(GamePhase.CHOOSECOLORPHASE) && !controller.getGamePhase().equals(GamePhase.CHOOSESTARTERSIDEPHASE)){
            if (ping(client)){
                // TODO: implement method in controller
                // int card1Id = controller.getSharedObjectives().get(0);
                // int card2Id = controller.getSharedObjectives().get(1);

                // client.printCard(card1Id, Side.UP, card2Id, SIde.UP);
            }
        } else{
            if (ping(client)) {
                client.sendError("Your hand has not been inizialized");
            }
        }
    }

    @Override
    public void placeCard(VirtualView client, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException {
        int playerIndex = getPlayerIndex(client);

        if (controller.getGamePhase().equals(GamePhase.MAINPHASE) &&
            (controller.getTurnPhase().equals(TurnPhase.PLACINGPHASE)) &&
            (isRightTurn(playerIndex))){

            try {
                this.controller.placeCard(playerIndex, placingCardId, tableCardId, tableCornerPos, placingCardSide);
                for (VirtualView clientIterator : this.clients.values()) {
                    if (client.equals(clientIterator)) {
                        if (ping(clientIterator)){
                            clientIterator.sendMessage("you placed your card, now you have to draw your card with DRAW [SHAREDGOLD1/SHAREDGOLD2/SHAREDRESOURCE1/SHAREDRESOURCE/DECKGOLD/DECKRESOURCE]");
                        }
                    } else {
                        if (ping(clientIterator)){
                            clientIterator.sendMessage(controller.getPlayerNickname(controller.getCurrentPlayerIndex()) + "placed his card");
                        }
                    }
                }

            } catch (CardNotPlacedException | WrongInstanceTypeException e) {
                throw new RuntimeException(e);
            } catch (CardIsNotInHandException e) {
                client.sendError("Sorry, but the card is not in your hand");
            } catch (WrongPlacingPositionException e) {
                client.sendError("");
            } catch (PlacingOnHiddenCornerException e) {
                client.sendError("The selected corner does not exist, please select an existing corner");
            } catch (CardAlreadyPresentOnTheCornerException e) {
                client.sendError("The selected corner was already connected to another card");
            } catch (GoldCardCannotBePlacedException e) {
                client.sendError("You don't have the elements to place this card");
            } catch (CardAlreadPlacedException e) {
                client.sendError("... You are certainly hacking ...");
            }
        } else {
            try {
                client.sendError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void drawCard(VirtualView client, DrawType drawType) throws RemoteException {
        int playerIndex = getPlayerIndex(client);

        if (controller.getGamePhase().equals(GamePhase.MAINPHASE) &&
            (controller.getTurnPhase().equals(TurnPhase.DRAWPHASE)) &&
            (isRightTurn(playerIndex))){

            try {
                int playing_client_index = controller.getCurrentPlayerIndex();
                this.controller.drawCard(drawType);
                for (VirtualView clientIterator : this.clients.values()) {
                    if (clients.get(playing_client_index).equals(clientIterator)) {
                        if (ping(clientIterator)){
                            clientIterator.sendMessage("you drew your card, now is the turn of " + controller.getPlayerNickname(controller.getCurrentPlayerIndex()));
                        }
                    } else if (clients.get(controller.getCurrentPlayerIndex()).equals(clientIterator)) {
                        if (ping(clientIterator)) {
                            clientIterator.sendMessage(controller.getPlayerNickname(playing_client_index) + "drew his card, now it's your turn to place your card with PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)]");
                        }
                    } else {
                        if (ping(clientIterator)){
                            clientIterator.sendMessage(controller.getPlayerNickname(playing_client_index) + "drew his card, now it's the turn of " + controller.getPlayerNickname(controller.getCurrentPlayerIndex()) + " to place his card");
                        }
                    }
                }
            } catch (InvalidHandException e) {
                client.sendError("Too many cards in hand");
            } catch (EmptyDeckException e) {
                client.sendError("The deck is empty");
            }
        }
        else{
            try {
                client.sendError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
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
            try {
                client.sendError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
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



    private int getPlayerIndex(VirtualView client) {
        for (int i : this.clients.keySet()) {
            if (this.clients.get(i).equals(client)) {
                return i;
            }
        }
        return -1;
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

     */

    private boolean ping(VirtualView client){
        try {
            client.ping("ping");
        } catch (RemoteException e) {
            System.out.println("client" + getPlayerIndex(client) + "is disconnected");
            return false;
        }
        return true;
    }
}