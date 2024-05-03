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

    public static void main(String[] argv) throws IOException, WrongStructureConfigurationSizeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException {
        //setup gamestate
        GameState gameState = null;
        boolean store = Populate.existStore(); //FA persistance
        if (store){
            // takes data from store
        } else {
            gameState = Populate.populate();
        }
        String host = argv[0];
        int socketport = Integer.parseInt(argv[1]);
        int rmiport = Integer.parseInt(argv[2]);

        // listen to socket
        ServerSocket listenSocket = new ServerSocket(socketport);
        Server engine = new Server(listenSocket, new Controller(gameState));

        // listen to rmi
        System.setProperty("java.rmi.server.hostname", host);
        String name = "VirtualServer";
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(engine, 0);
        Registry registry = LocateRegistry.createRegistry(rmiport);
        registry.rebind(name, stub);

        //listen to socket
        engine.runServer();
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
                this.controller.addPlayer(nickname);
                this.clients.put(this.clients.size(), client);
                System.out.println("New player connected!");
                for (VirtualView clientIterator : this.clients.values()) {
                    if (ping(clientIterator)){
                        clientIterator.printMessage("User " + nickname + " has ben added to game!");
                    }
                }
            } catch (NotUniquePlayerNicknameException e) {
                try {
                    client.printError("The nickname already exists :/\nPlease enter a new one");
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
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
    public void startGame() {
        if (clients.size() >= 2) {
            System.out.println("Starting game");
            for (VirtualView client : this.clients.values()) {
                try {
                    client.printMessage("Please choose your starter side [Front / Back]");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            this.controller.startGame();
            this.controller.setGamePhase(GamePhase.CHOOSESTARTERSIDEPHASE);
        }
        else{
            for (VirtualView client : this.clients.values()) {
                try {
                    client.printMessage("Number of player insufficient");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void chooseInitialStarterSide(VirtualView client, String sideValue) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSESTARTERSIDEPHASE)) {
            int playerIndex = getPlayerIndex(client);
            Side side = null;

            try {
                side = Side.valueOf(sideValue.toUpperCase());
            } catch (IllegalArgumentException e) {
                client.printError("Invalid side, please enter a valid side (Front / Back)");
                return;
            }

            this.controller.chooseInitialStarterSide(playerIndex, side);
            this.numberAnswered++;
            try {
                client.printMessage("Thank you!!!");
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

            if (numberAnswered == clients.size()) {
                numberAnswered = 0;
                for (VirtualView clientIterator : this.clients.values()) {
                    try {
                        clientIterator.printMessage("Please select a valid color from one of the lists [Blue, Green, Yellow, Red]");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
                this.controller.setGamePhase(GamePhase.COLORPHASE);
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
    public void chooseInitialColor(VirtualView client, String colorValue) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.COLORPHASE)) {
            int playerIndex = getPlayerIndex(client);
            Color color;

            try {
                color = Color.valueOf(colorValue.toUpperCase());
            } catch (IllegalArgumentException e) {
                client.printMessage("Please select a valid color from one of the lists [Blue, Green, Yellow, Red]");
                return;
            }

            try {
                this.controller.chooseInitialColor(playerIndex, color);
                numberAnswered ++;
            } catch (NotUniquePlayerColorException e) {
                client.printError("The color was already selected by another user, please select a new one ;)");
                return;
            }

            if (numberAnswered == clients.size()) {
                numberAnswered = 0;
                controller.colorChoosed();
                try {
                    for (VirtualView clientIterator : this.clients.values()) {
                        getPlayerIndex(clientIterator);
                        clientIterator.printMessage("Select one of the objective card from the selection [0, 1]: " + this.controller.getObjectiveCardOptions(playerIndex)[0].getId() + ", " + this.controller.getObjectiveCardOptions(playerIndex)[1].getId());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                this.controller.setGamePhase(GamePhase.CHOOSEOBJECTIVEPHASE);
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
    public void chooseInitialObjective(VirtualView client, String cardIdString) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.CHOOSEOBJECTIVEPHASE)) {
            int playerIndex = getPlayerIndex(client);
            int cardId;

            try {
                cardId = Integer.parseInt(cardIdString);
            } catch (NumberFormatException e) {
                client.printError("Please enter a valid card ID");
                return;
            }

            try {
                this.controller.chooseInitialObjective(playerIndex, cardId);
                numberAnswered++;
            } catch (InvalidObjectiveCardException e) {
                client.printError("The objective card you selected was invalid, please try again");
                return;
            }

            if (numberAnswered == clients.size()) {
                numberAnswered = 0;
                for (VirtualView clientIterator : this.clients.values()) {
                    try {
                        clientIterator.printMessage("The preparation phase is over, the first player can now play is turn");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
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
    public void placeCard(VirtualView client, String placingCardIdString, String tableCardIdString, String tableCornerPos, String placingCardSideValue) throws RemoteException {
        int playerIndex = getPlayerIndex(client);

        if (controller.getGamePhase().equals(GamePhase.MAINPHASE) &&
            (controller.getTurnPhase().equals(TurnPhase.PLACINGPHASE)) &&
            (isRightTurn(playerIndex))){
            int placingCardId;
            int tableCardId;
            Side placingCardSide;

            try{
                placingCardId = Integer.parseInt(placingCardIdString);
                tableCardId = Integer.parseInt(tableCardIdString);
            } catch (NumberFormatException e) {
                client.printError("Please enter valid card ids");
                return;
            }

            try{
                placingCardSide = Side.valueOf(placingCardSideValue.toUpperCase());
            } catch (IllegalArgumentException e) {
                client.printError("Please enter a valid side (Front / Back)");
                return;
            }

            try {
                this.controller.placeCard(playerIndex, placingCardId, tableCardId, CornerPos.valueOf(tableCornerPos), placingCardSide);
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
                client.printError("... You are certainly hackering ...");
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
    public void drawCard(VirtualView client, String drawTypeValue) throws RemoteException {
        int playerIndex = getPlayerIndex(client);

        if (controller.getGamePhase().equals(GamePhase.MAINPHASE) &&
            (controller.getTurnPhase().equals(TurnPhase.DRAWPHASE)) &&
            (isRightTurn(playerIndex))){
            DrawType drawType;

            try{
                drawType = DrawType.valueOf(drawTypeValue.toUpperCase());
            } catch (IllegalArgumentException e){
                client.printError("Please enter a valid draw type");
                return;
            }

            try {
                this.controller.drawCard(drawType);
            } catch (InvalidHandException e) {
                client.printError("Too many cards in hand");
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
    public void flipCard(VirtualView client, String cardIdString) throws RemoteException {
        if (controller.getGamePhase().equals(GamePhase.MAINPHASE)) {
            int playerIndex = getPlayerIndex(client);
            int cardId;

            try {
                cardId = Integer.parseInt(cardIdString);
            } catch (NumberFormatException e) {
                client.printError("Please enter a valid card id");
                return;
            }

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

    private boolean ping(VirtualView client){
        try {
            client.ping("ping");
        } catch (RemoteException e) {
            System.out.println("client" + getPlayerIndex(client) + "is disconnected");
            return false;
        }
        return true;
    }



    private void updatePlayersConnected(){
        AtomicInteger index = new AtomicInteger();
        clients.values().stream().forEach(e->{
            index.getAndIncrement();
            ping(e);
            boolean connected = true;
            controller.setConnected(index.get(), connected);
        });
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
}