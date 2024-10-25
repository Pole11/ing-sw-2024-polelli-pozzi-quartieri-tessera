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

/**
 * Server class
 */
public class Server implements VirtualServer {
    private final Controller controller;
    private final ServerSocket listenSocket;
    private final Registry registry;

    /**
     * Constructs a Server object with the specified listen socket, controller, and registry.
     * @param listenSocket the ServerSocket object that the server will use to listen for incoming connections
     * @param controller the Controller object that will handle the business logic for the server
     * @param registry the Registry object used for service registration
     */
    public Server(ServerSocket listenSocket, Controller controller, Registry registry) {
        this.listenSocket = listenSocket;
        this.controller = controller;
        this.registry = registry;
    }

    public static void main(String[] argv) throws IOException {
        System.out.println("Executing server");

        try {
            String host = argv[0];
            int socketport = Integer.parseInt(argv[1]);
            int rmiport = Integer.parseInt(argv[2]);
            boolean restartRescue = false;
            if (argv.length == 4) {
                 restartRescue = Boolean.parseBoolean(argv[3]);
            }

            if (restartRescue){
                //erase state
                Gson gson = new Gson();
                String filePath = new File("").getAbsolutePath() + Config.GAME_STATE_PATH;
                try (Writer writer = new FileWriter(filePath)) {
                    gson.toJson(new HashMap<>(), writer);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }

            startServer(host, socketport, rmiport);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("This is the server, please remember to use the right parameters: [server ip] [server port socket] [server port rmi]");
        }
    }

    /**
     * Starts the server with the specified host, socket port, and RMI port.
     *
     * @param host the hostname or IP address of the server
     * @param socketport the port number for the server socket
     * @param rmiport the port number for the RMI registry
     * @throws IOException if an I/O error occurs when opening the socket or registry
     */
    public static void startServer(String host, int socketport, int rmiport) throws IOException, CardNotPlacedException, PlacingOnHiddenCornerException, CardNotOnBoardException {
        // listen to socket
        ServerSocket listenSocket = new ServerSocket(socketport);

        // listen to rmi
        System.setProperty("java.rmi.server.hostname", host);
        System.setProperty("sun.rmi.transport.tcp.responseTimeout", "1000");


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

    /**
     * Restarts the gamestate
     */
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

    /**
     * Runs the server, accepting incoming client connections and handling them with separate threads.
     *
     * @throws IOException if an I/O error occurs when waiting for a connection.
     */
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
            } catch (CardNotOnBoardException | WrongInstanceTypeException | CardIsNotInHandException | CardAlreadPlacedException | CardAlreadyPresentOnTheCornerException | PlacingOnHiddenCornerException | GoldCardCannotBePlacedException e){
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

        try {
            this.controller.ping(client);
        } catch (IndexOutOfBoundsException e){
            client.sendError("you don't exist, to reconnect login with ADDUSER <your previous nickname>");
            client.connectionInfo(getPlayerIndex(client), false);
        }
    }

    @Override
    public synchronized void pong(VirtualView client) throws RemoteException {
        client.pong();
    }

    @Override
    public void gameEnded(VirtualView client) throws RemoteException {
        try {
            this.controller.gameEnded(client);
        } catch (IndexOutOfBoundsException e){
            client.sendError("you don't exist, to reconnect login with ADDUSER <your previous nickname>");
            client.connectionInfo(getPlayerIndex(client), false);
        }
    }

    /**
     * Checks if the specified client is connected.
     *
     * @param client the VirtualView instance representing the client to check.
     * @return true if the client is connected; false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    private boolean checkConnected(VirtualView client) throws RemoteException {
        boolean connected;
        try{
            connected = controller.isConnected(client);
        } catch (IndexOutOfBoundsException e){
            client.sendError("you don't exist, to reconnect login with ADDUSER <your previous nickname>");
            client.connectionInfo(getPlayerIndex(client), false);
            return false;
        }
        return connected;

    }


    /**
     * Retrieves the player index for the specified client.
     *
     * @param client the VirtualView instance representing the client.
     * @return the player index of the specified client.
     */
    private int getPlayerIndex(VirtualView client) {
        return controller.getPlayerIndex(client);
    }

    /**
     * Checks if it is the specified player's turn.
     *
     * @param playerIndex the index of the player to check.
     * @return true if it is the specified player's turn; false otherwise.
     */
    private boolean isRightTurn(int playerIndex){
        return (controller.getCurrentPlayerIndex() == playerIndex);
    }

}