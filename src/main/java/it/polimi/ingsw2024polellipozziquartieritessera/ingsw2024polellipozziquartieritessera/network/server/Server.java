package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;


public class Server implements VirtualServer {
    final Controller controller;
    final HashMap<Integer, VirtualView> clients = new HashMap<>();

    public Server(Controller controller) {
        this.controller = controller;
    }

    public static void main(String argv[]) throws IOException, WrongStructureConfigurationSizeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException {
        GameState gameState = null;
        boolean store = Populate.existStore(); //FA persistance
        if (store){
            // takes data from store
        } else {
            gameState = Populate.populate();
        }
        // listen to rmi
        VirtualServer engine = new Server(new Controller(gameState));
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        String name = "VirtualServer";
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(engine, 0);
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(name, stub);

        // listen to socket
    }

    @Override
    public void connectRmi(VirtualView client) throws RemoteException {
        if (clients.size() < Config.MAX_PLAYERS) {
            System.out.println("new client connected");
            client.printMsg("Please enter a nickname to start");
        } else {
            client.printMsg("The game is full");
            System.out.println("A new client tried to connect but the game is full");
        }
    }

    @Override
    public void sendInput(VirtualView client, String msg) {
        if (msg == null || msg.isEmpty() || msg.isBlank() || msg.equals("")) return;
        // fase di gioco
        GamePhase gamePhase = this.controller.getGamePhase();
        switch (gamePhase) {
            case GamePhase.NICKNAMEPHASE:
                if (msg.equals("start")) { // if the players wants to start the game
                    if (clients.size() <= 1) {
                        try {
                            client.printMsg("Please wait until 1 or more player enter their username");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    } else {
                        startGame();
                    }
                } else {
                    // controlli nickname
                    addConnectedPlayer(client, msg);
                }
                break;
            case GamePhase.COLORPHASE:
                int playerId = 0;
                for (int i : this.clients.keySet()) {
                    if (this.clients.get(i).equals(client)) {
                        playerId = i;
                        break;
                    }
                }
                try {
                    if (msg.equals("B")) {
                        chooseInitialColor(client, playerId, Color.BLUE);
                    } else if (msg.equals("G")) {
                        chooseInitialColor(client, playerId, Color.GREEN);
                    } else if (msg.equals("Y")) {
                        chooseInitialColor(client, playerId, Color.YELLOW);
                    } else if (msg.equals("R")) {
                        chooseInitialColor(client, playerId, Color.RED);
                    } else {
                        client.printMsg("Please select a valid color from one of the lists [B, G, Y, R]");
                    }
                } catch (RemoteException e) {

                }
                break;
        }
        // capire se input valido
        // chiama metodo controller
        // se va tutto bene devi dire che va tutto bene
        // se va tutto male devi dire che va tutto male
    }

    @Override
    public void addConnectedPlayer(VirtualView client, String nickname) {
        // check if the color is valid

        // nickname
        try {
            if (clients.values().contains(client)) {
                client.printMsg("You already registered an account with this computer, you cannot register more than one.");
                return;
            } else if (clients.size() > 4) {
                for (VirtualView clientIterator : this.clients.values()) {
                    client.printMsg("The game is full");
                }
                return;
            }

            this.controller.addPlayer(nickname);
            clients.put(clients.size(), client);
            System.out.println("New player connected!");
            for (VirtualView clientIterator : this.clients.values()) {
                clientIterator.printMsg("User " + nickname + " has ben added to game!");
            }
        } catch (NotUniquePlayerNicknameException e) {
            try {
                client.printMsg("The nickname already exists :/\nPlease enter a new one");
            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startGame() {
        // TODO: change game phase to choosing colors
        System.out.println("Starting game");
        for (VirtualView client : clients.values()) {
            try {
                client.printMsg("Please enter a color [B, G, Y, R]");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        this.controller.setGamePhase(GamePhase.COLORPHASE);
    }

    @Override
    public void chooseInitialStarterSide(int playerIndex, Side side) throws RemoteException {

    }

    @Override
    public void chooseInitialColor(VirtualView client, int playerIndex, Color color) throws RemoteException {
        try {
            this.controller.chooseInitialColor(playerIndex, color);
        } catch (NotUniquePlayerColorException e) {
            client.printMsg("The color was already selected by another user, please select a new one ;)");
        }
        // understand if it was the last player that had to set the color, if so go to the next phase
    }

    @Override
    public void chooseInitialObjective(int playerIndex, int cardId) throws RemoteException {

    }

    @Override
    public void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException {

    }

    @Override
    public void drawCard(DrawType drawType) throws RemoteException {

    }

    @Override
    public void flipCard(int playerIndex, int cardId) throws RemoteException {

    }

    @Override
    public void openChat() {

    }

    @Override
    public void addMessage(Player player, String content) {

    }

}