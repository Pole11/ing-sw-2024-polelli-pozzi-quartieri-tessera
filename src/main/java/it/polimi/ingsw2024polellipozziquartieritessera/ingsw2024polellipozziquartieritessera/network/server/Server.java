package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.IOException;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


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
    int numberAnswered = 0;
    
    public Server(ServerSocket listenSocket, Controller controller) {
        this.listenSocket = listenSocket;
        this.controller = controller;
    }

    public static void main(String[] argv) throws IOException, WrongStructureConfigurationSizeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException {
        String host = argv[0];
        int socketport = Integer.parseInt(argv[1]);
        int rmiport = Integer.parseInt(argv[2]);

        startServer(host, socketport, rmiport);
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

        new Thread(() -> {
            try {
                server.periodicPing();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

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
    public void addConnectedPlayer(VirtualView client, String nickname) {
        if (controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE)){
            try {
                if (this.clients.size() >= Config.MAX_PLAYERS) {
                    client.printError("The game is full");
                    return;
                }
                if (clients.containsValue(client)){
                    client.printError("You already chose a nickname, you cannot change it");
                    return;
                }

                this.controller.addPlayer(nickname);
                this.clients.put(this.clients.size(), client);
                System.out.println("New player connected!");
                if (clients.size()==1){
                    if(ping(client)){
                        client.printMessage("you successfully entered the game with the nickname " + nickname + ", wait for at least two players to start the game");
                    }
                } else {
                    for (VirtualView clientIterator : this.clients.values()) {
                        if (clientIterator.equals(client)){
                            if(ping(clientIterator)){
                                clientIterator.printMessage("you successfully entered the game with the nickname " + nickname + ", there are " + clients.size() + " players connected, to start the game type START");
                            }
                        } else {
                            if (ping(clientIterator)){
                                clientIterator.printMessage("User " + nickname + " has ben added to game, there are " + clients.size() + " players connected, to start the game type START");
                            }
                        }
                    }
                }

            } catch (NotUniquePlayerNicknameException e) {
                try {
                    //it is not safe because when someone is disconnected, it can be replaced by
                    //another person, but without password it is the only way
                    if (ping(client)){
                        client.printError("The nickname already exists, please enter a new one");
                    } else {
                        client.printMessage("You successfully re-entered the game with the nickname " + nickname);
                        if (controller.getGamePhase().equals(GamePhase.NICKNAMEPHASE)){
                            controller.timeoutThread.interrupt();
                            controller.setGamePhase(controller.prevGamePhase);
                            controller.prevGamePhase = null;
                        }
                        updatePlayersConnected();
                    }
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                } finally {
                    return;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (EmptyDeckException e) {
                try {
                    client.printError("The deck is empty");
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                } finally {
                    return;
                }
            }
        } else {
            try {
                client.printError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void showNickname(VirtualView client) throws RemoteException{
        if (clients.containsValue(client)){
            if(ping(client)){
                client.printMessage("Your nickname is " + controller.getPlayerNickname(getPlayerIndex(client)));
            }
        } else {
            if(ping(client)){
                client.printError("You didn't choose a nickname, use the command ADDUSER [nickname] to do that");
            }
        }
    }

    @Override
    public void startGame(VirtualView client) throws RemoteException{
        if (clients.size() >= 2) {
            System.out.println("Starting game");
            for (VirtualView clientIterator : this.clients.values()) {
                if (ping(clientIterator)){
                    clientIterator.printMessage("The game has begun. Please choose the side of your starter card with the command " + Command.CHOOSESTARTER + " [Front / Back]");
                }
            }
            try {
                this.controller.startGame();
            } catch (EmptyDeckException e) {
                client.printError("The deck is empty");
            }
            this.controller.setGamePhase(GamePhase.CHOOSESTARTERSIDEPHASE);
        }
        else{
            if (clients.containsValue(client)){
                if (ping(client)){
                    client.printError("Number of player insufficient");
                }
            } else {
                if (ping(client)){
                    client.printError("You must choose your nickname with adduser first");
                }
            }

        }
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, Side side) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSESTARTERSIDEPHASE)) {
            int playerIndex = getPlayerIndex(client);

            this.controller.chooseInitialStarterSide(playerIndex, side);
            this.numberAnswered++;
            try {
                client.printMessage("Thank you!");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            if (numberAnswered == clients.size()) {
                numberAnswered = 0;
                for (VirtualView clientIterator : this.clients.values()) {
                    try {
                        clientIterator.printMessage("Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                this.controller.setGamePhase(GamePhase.CHOOSECOLORPHASE);
            }
        }
        else{
            try {
                client.printError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void chooseInitialColor(VirtualView client, Color color) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSECOLORPHASE)) {
            int playerIndex = getPlayerIndex(client);

            try {
                this.controller.chooseInitialColor(playerIndex, color);
                client.printMessage("Thank you!");
                numberAnswered ++;
            } catch (NotUniquePlayerColorException e) {
                client.printError("The color was already selected by another user, please select a new one ;)");
                return;
            }

            if (numberAnswered == clients.size()) {
                numberAnswered = 0;
                try {
                    controller.colorChoosed();
                } catch (EmptyDeckException e) {
                    throw new RuntimeException(e);
                }
                try {
                    for (VirtualView clientIterator : this.clients.values()) {
                        getPlayerIndex(clientIterator);
                        clientIterator.printMessage("Everyone chose his color, now please select one of the objective card from the selection with the command CHOOSEOBJECTIVE [0/1]: " + this.controller.getObjectiveCardOptions(playerIndex)[0].getId() + ", " + this.controller.getObjectiveCardOptions(playerIndex)[1].getId());
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
                client.printError("You cannot do this action now");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void chooseInitialObjective(VirtualView client, int cardId) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSEOBJECTIVEPHASE)) {
            int playerIndex = getPlayerIndex(client);

            try {
                this.controller.chooseInitialObjective(playerIndex, cardId);
                client.printMessage("Thank you!");
                numberAnswered++;
            } catch (InvalidObjectiveCardException e) {
                client.printError("The objective card you selected was invalid, please try again");
                return;
            }

            if (numberAnswered == clients.size()) {
                numberAnswered = 0;
                try {
                    for (VirtualView clientIterator : this.clients.values()) {
                        if (clients.get(controller.getCurrentPlayerIndex()).equals(clientIterator)) {
                            if (ping(clientIterator)){
                                clientIterator.printMessage("The preparation phase is over, it's your turn to play as first, use the command PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)] to place your card");
                            }
                        } else {
                            clientIterator.printMessage("The preparation phase is over, it's now the turn of " + controller.getPlayerNickname(controller.getCurrentPlayerIndex()) + " to play as first");
                        }
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
                client.printError("You cannot do this action now");
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
                client.printError("Your hand has not been inizialized");
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
                client.printError("Your hand has not been inizialized");
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
                            clientIterator.printMessage("you placed your card, now you have to draw your card with DRAW [SHAREDGOLD1/SHAREDGOLD2/SHAREDRESOURCE1/SHAREDRESOURCE/DECKGOLD/DECKRESOURCE]");
                        }
                    } else {
                        if (ping(clientIterator)){
                            clientIterator.printMessage(controller.getPlayerNickname(controller.getCurrentPlayerIndex()) + "placed his card");
                        }
                    }
                }

            } catch (CardNotPlacedException | WrongInstanceTypeException e) {
                throw new RuntimeException(e);
            } catch (CardIsNotInHandException e) {
                client.printError("Sorry, but the card is not in your hand");
            } catch (WrongPlacingPositionException e) {
                client.printError("");
            } catch (PlacingOnHiddenCornerException e) {
                client.printError("The selected corner does not exist, please select an existing corner");
            } catch (CardAlreadyPresentOnTheCornerException e) {
                client.printError("The selected corner was already connected to another card");
            } catch (GoldCardCannotBePlacedException e) {
                client.printError("You don't have the elements to place this card");
            } catch (CardAlreadPlacedException e) {
                client.printError("... You are certainly hacking ...");
            }
        } else {
            try {
                client.printError("You cannot do this action now");
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
                            clientIterator.printMessage("you drew your card, now is the turn of " + controller.getPlayerNickname(controller.getCurrentPlayerIndex()));
                        }
                    } else if (clients.get(controller.getCurrentPlayerIndex()).equals(clientIterator)) {
                        if (ping(clientIterator)) {
                            clientIterator.printMessage(controller.getPlayerNickname(playing_client_index) + "drew his card, now it's your turn to place your card with PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)]");
                        }
                    } else {
                        if (ping(clientIterator)){
                            clientIterator.printMessage(controller.getPlayerNickname(playing_client_index) + "drew his card, now it's the turn of " + controller.getPlayerNickname(controller.getCurrentPlayerIndex()) + " to place his card");
                        }
                    }
                }
            } catch (InvalidHandException e) {
                client.printError("Too many cards in hand");
            } catch (EmptyDeckException e) {
                client.printError("The deck is empty");
            }
        }
        else{
            try {
                client.printError("You cannot do this action now");
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
                client.printError("The selected card is not in hand");
            }
        } else {
            try {
                client.printError("You cannot do this action now");
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

    private void updatePlayersConnected(){
        AtomicInteger index = new AtomicInteger();
        AtomicInteger numberConnected = new AtomicInteger();
        clients.values().stream().forEach(e->{
            index.getAndIncrement();
            boolean connected = false;
            if (ping(e)){
                connected = true;
                numberConnected.getAndIncrement();
            }
            controller.setConnected(index.get(), connected);
        });
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