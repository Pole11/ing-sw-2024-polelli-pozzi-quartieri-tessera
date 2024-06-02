package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Client implements VirtualView {
    private boolean meDoGui;
    private CLIController cliController;
    private GUIApplication guiApplication;
    private final ViewModel viewModel;
    private VirtualView client;
    private VirtualServer server;

    public Client(){
        this.viewModel = new ViewModel();
    }

    public static void main(String[] args) throws IOException {
        String input = args[0];
        String host = args[1];
        String port = args[2];

        (new Client()).startClient(input, host, port);

    }

    //GETTER
    public VirtualView getClient(){
        return client;
    }

    public CLIController getCliController() {
        return cliController;
    }

    public void startClient(String input, String host, String portString) throws IOException {
        if (input.equalsIgnoreCase("socket")) {
            int port = Integer.parseInt(portString);
            Socket socketToServer = new Socket(host, port);

            InputStreamReader socketRx = new InputStreamReader(socketToServer.getInputStream());
            OutputStreamWriter socketTx = new OutputStreamWriter(socketToServer.getOutputStream());
            BufferedReader bufferedReader = new BufferedReader(socketRx);
            BufferedWriter bufferedWriter = new BufferedWriter(socketTx);

            this.server = new ServerProxy(bufferedWriter);
            this.client = new SocketClient(bufferedReader, server, this);
            ((SocketClient) client).run();
        } else { //default rmi
            try {
                Registry registry = LocateRegistry.getRegistry(host, Integer.parseInt(portString));
                this.server = (VirtualServer) registry.lookup("VirtualServer");
                this.client = new RmiClient(server, this);
                ((RmiClient) client).run();
            } catch (RemoteException | NotBoundException e) {
                e.printStackTrace();
                System.out.println("An error occurred while executing RmiClient!");
            }
        }
    }

    public void runCli(VirtualServer server) {
        meDoGui = false;

        cliController = new CLIController(viewModel);

        boolean running = true;
        Scanner scan = new Scanner(System.in);

        System.out.print("Please enter a nickname to start, with the command ADDUSER <nickname>\n> ");
        while (running) {
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                try {
                    cliController.manageInput(server, client, this, message);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else if (line != null){
                System.out.print("> "); // print phase
            }
        }
    }

    public void runGui(VirtualServer server, VirtualView client){
        meDoGui = true;
        guiApplication = new GUIApplication();
        guiApplication.runGui(client, server, this, viewModel);
    }


    @Override
    public void updateGamePhase(GamePhase nextGamePhase) {
        viewModel.setGamePhase(nextGamePhase);

        if (meDoGui) {
            switch (nextGamePhase) {
                case GamePhase.NICKNAMEPHASE -> {
                    guiApplication.changeScene("/fxml/startGame.fxml");
                }
                case GamePhase.CHOOSESTARTERSIDEPHASE -> {
                    guiApplication.changeScene("/fxml/chooseStarter.fxml");
                }
                case GamePhase.CHOOSECOLORPHASE -> {
                    System.out.println("Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]");
                    guiApplication.changeScene("/fxml/chooseColor.fxml");
                }
                case GamePhase.CHOOSEOBJECTIVEPHASE -> {
                    guiApplication.changeScene("/fxml/chooseObjective.fxml");
                }
                case GamePhase.MAINPHASE -> {
                    System.out.println("---Game started---");
                    guiApplication.changeScene("/fxml/game.fxml");
                }
                case GamePhase.ENDPHASE -> {
                    System.out.println("NON SO CHI reached 20 points o NON SO");
                }
                case GamePhase.FINALPHASE -> {
                    guiApplication.changeScene("/fxml/final.fxml");
                }
            }
            guiApplication.updateController();
        }
    }

    @Override
    public void updateTurnPhase(TurnPhase nextTurnPhase){
        viewModel.setTurnPhase(nextTurnPhase);
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        viewModel.setPlayerIndex(index);
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void nicknameUpdate(int playerIndex, String nickname) {
        viewModel.setNickname(playerIndex, nickname);
        viewModel.setConnection(playerIndex, true);
        if (playerIndex == viewModel.getPlayerIndex()){
            if (viewModel.getPlayersSize() == 1){
                System.out.println("you successfully entered the game with the nickname " + nickname + ", wait for at least two players to start the game");
            } else {
                System.out.println("you successfully entered the game with the nickname " + nickname + ", there are " + viewModel.getPlayersSize() + " players connected, to start the game type START");
            }
        } else {
            System.out.println("a new player has connected with the name:" + nickname);
        }
        if (meDoGui) guiApplication.updateController();
    }


    @Override
    public void start() throws RemoteException {
    }

    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        viewModel.setConnection(playerIndex, connected);
        System.out.println("playerIndex is " + viewModel.getPlayerIndex());
        if (playerIndex == viewModel.getPlayerIndex()){
            if (connected){
                System.out.println("you re-connected to the game");
            } else {
                //SISTEMA
                throw new RuntimeException();
            }
        } else {
            if (connected) {
                System.out.println(viewModel.getNickname(playerIndex) + " connected");
            } else {
                System.out.println(viewModel.getNickname(playerIndex) + " disconnected");
                if (meDoGui) guiApplication.changeScene("/fxml/final.fxml");
            }
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.addedCardToHand(playerIndex, cardIndex);
        viewModel.setHandSide(cardIndex, Side.FRONT);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you have drawn a card");
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "drew a card");
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.removedCardFromHand(playerIndex, cardIndex);
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        viewModel.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you placed a card, now you have to draw your card with DRAW [SHAREDGOLD1/SHAREDGOLD2/SHAREDRESOURCE1/SHAREDRESOURCE/DECKGOLD/DECKRESOURCE]");
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "placed a card");
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {
        viewModel.setColor(playerIndex, color);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you chose the color:" + color);
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "chose the color" + color);
        }
        if (meDoGui)  guiApplication.updateController();
    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        viewModel.setCurrentPlayer(currentPlayerIndex);
        if (viewModel.getPlayerIndex() == currentPlayerIndex) {
            System.out.println("it's your turn");
            System.out.println("to place your card use the command PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)] to place your card");
        } else {
            System.out.println("it's the turn of" + viewModel.getNickname(currentPlayerIndex));
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateHandSide(int cardIndex, Side side) throws RemoteException {
        viewModel.setHandSide(cardIndex, side);
        System.out.println("you flipped your card");
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {
        viewModel.setPoints(playerIndex, points);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you now have " + points + "points");
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "has" + points + "points");
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        viewModel.setSecretObjective(objectiveCardId1, objectiveCardId2);
        if (objectiveCardId2 == -1){
            System.out.println("you have chosen the objective card: " + objectiveCardId1);
        } else{
            System.out.println("Everyone chose his color, now please select one of the objective card from the selection with the command CHOOSEOBJECTIVE [0/1], to see your card use the command SHOWOBJECTIVE");
            // TODO: cliController.showSecretObjectives();

        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        viewModel.setSharedObjectives(sharedObjectiveCardId1, sharedObjectiveCardId2);
        System.out.println("the shared objectives are: " + sharedObjectiveCardId1 + "," + sharedObjectiveCardId2);
        //TODO: cliController.showSharedObjectvives();
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        if (side == null){
            viewModel.setStarterCard(cardId1);
            System.out.println("Chose your preferred side for the starter card [Front/Back]:");
            // TODO : cliController.showStarterSides();
        } else {
            viewModel.initializeBoard(playerIndex, cardId1);
            viewModel.setPlacedSide(cardId1, side);
            if (viewModel.getPlayerIndex() == playerIndex) {
                System.out.println("you now have chosen the starter side");
            } else {
                System.out.println(viewModel.getNickname(playerIndex) + "has chosen the starter side");
            }
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateWinner(int playerIndex){
        viewModel.addWinner(playerIndex);
    }

    @Override
    public void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) {
        viewModel.setMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firtGoldDeckCard, firstResourceDeckCard);
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void ping(String ping) throws RemoteException {
        if (meDoGui){

        } else {
            cliController.ping(client, server);
        }
    }

    @Override
    public void sendError(String error) throws RemoteException {
        if (meDoGui) {
            guiApplication.getGUIController().setServerError(error);
        }
        System.err.print("\nERROR FROM SERVER: " + error + "\n> ");
    }
}

// TODO: capire come fare a chiamare le print card... da dove, chi lo fa, etc.
