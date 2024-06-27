package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Message;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PingCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.GUIControllerLobby;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import javafx.scene.control.Alert;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Client implements VirtualView {
    private boolean meDoGui;
    private CLIController cliController;
    private GUIApplication guiApplication;
    private ViewModel viewModel;
    private VirtualView client;
    private VirtualServer server;

    private final String rmiOrSocket;
    private final String host;
    private final String myIp;
    private final int port;
    private boolean running;
    ByteArrayOutputStream baos;
    PrintStream restoreStream;
    PrintStream oldPrintStream;

    private boolean serverIsConnected;

    public Client(String rmiOrSocket, String host, String port, String myIp){
        this.viewModel = new ViewModel();
        this.rmiOrSocket = rmiOrSocket;
        this.host = host;
        this.myIp = myIp;
        this.port = Integer.parseInt(port);
        this.running = false;
        this.baos = new ByteArrayOutputStream();
        this.restoreStream = new PrintStream(baos);
        this.oldPrintStream = null;
        this.serverIsConnected = true;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Executing client");


        try {
            String rmiOrSocket = args[0];
            String host = args[1];
            String port = args[2];
            String myIp = "";
            try {
                myIp = args[3];
            } catch (IndexOutOfBoundsException e) {
                if (rmiOrSocket.equals("rmi")) System.out.println("If you are encountering an error with rmi, please provide the ip address of the current machine");
            }
            (new Client(rmiOrSocket, host, port, myIp)).startClient();
        } catch(Exception e) {
            e.printStackTrace();
            System.out.println("This is the client, please remember to use the right parameters: [rmi/socket] [server ip] [server port (depends on rmi and socket)] [optional with rmi: ip address of the current machine]");
        }

    }

    public void resetViewModel(){
        viewModel = new ViewModel();
        if (meDoGui) {
            guiApplication.setViewModel(viewModel);
        } else {
            cliController.setViewModel(viewModel);
        }
    }

    //GETTER
    public VirtualView getClient(){
        return client;
    }

    public CLIController getCliController() {
        return cliController;
    }

    public GUIApplication getGuiApplication() {
        return guiApplication;
    }

    public ViewModel getViewModel(){
        return viewModel;
    }

    public boolean isMeDoGui(){
        return meDoGui;
    }

    public boolean isRunning(){
        return running;
    }

    public void startClient() throws IOException {
        if (rmiOrSocket.equalsIgnoreCase("socket")) {
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
                if (myIp != null && !myIp.isEmpty()) System.setProperty("java.rmi.server.hostname", myIp);
                System.setProperty("sun.rmi.transport.tcp.responseTimeout", "1000");

                Registry registry = LocateRegistry.getRegistry(host, port);
                this.server = (VirtualServer) registry.lookup("VirtualServer");
                this.client = new RmiClient(server, this);
                ((RmiClient) client).run();
            } catch (RemoteException e) {
                System.out.println("disconncetion from client");
                if (!running)serverDisconnected();
                System.out.println("An error occurred while executing RmiClient!");
            } catch (NotBoundException e){
                e.printStackTrace();
            }
        }
    }



    public void runCli() {
        //the first time this code is executed, running is false and is changed to true
        //the following times this code is executed running is true
        // in this way runCli will be runned one time only
        meDoGui = false;
        cliController = new CLIController(viewModel);
        cliController.restartPong(server, client, this);

        running = true;
        Scanner scan = new Scanner(System.in);

        System.out.print("Please enter a nickname to start, with the command ADDUSER <nickname>\n> ");
        while (running) {
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (!line.isEmpty() && !line.isBlank()) {
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

    public void runGui(){
        running = true;
        meDoGui = true;
        guiApplication = new GUIApplication();
        guiApplication.runGui(client, server, this, viewModel);
    }





    public void serverDisconnected(){
        System.out.println(serverIsConnected);
        /*if (!serverIsConnected){
            return;
        }

         */
        serverIsConnected = false;
        //OLD
        /*if (meDoGui){
            guiApplication.getGUIController().setServerError("There was an error in server, the server is not available, wait to be rederected in the login page");
        } else {
            System.out.println("server disconnected, wait to be notified of being connected to the server");
        }


        try {
            Thread.sleep(1000* Config.WAIT_DISCONNECTED_SERVER);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            startClient();
        } catch (ConnectException e){
            System.out.println("disconnection from client 2");
            serverDisconnected();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

         */
        /*while (true) {

        }

         */
        if (meDoGui) {
            guiApplication.getGUIController().setServerError("There was an error in server, the server is not available, wait to be redirected to the login page");
        } else {
            System.out.println("Server disconnected, wait to be notified of being connected to the server");
        }

        /*try {
            Thread.sleep(1000 * Config.WAIT_DISCONNECTED_SERVER);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

         */

        try {
            startClient();
            serverIsConnected = true;
            //break; // If startClient() succeeds, break the loop
        } catch (ConnectException e) {
            System.out.println("Disconnection from client 2");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void updateGamePhase(GamePhase nextGamePhase) {
        System.out.println("The Phase is " + nextGamePhase);
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
                    System.out.print("Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]\n> ");
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
                    guiApplication.changeScene("/fxml/game.fxml");
                    System.out.print("someone reached 20 points, the end phase has started\n> ");
                    guiApplication.getGUIController().showAlert(Alert.AlertType.INFORMATION, "Message from server", "someone reached 20 points, the end phase has started");
                }
                case GamePhase.FINALPHASE -> {
                }
                case GamePhase.TIMEOUT -> {
                    guiApplication.getGUIController().showAlert(Alert.AlertType.INFORMATION, "Timeout", "No one is connected, we are waiting " + Config.TIMEOUT_TIME + " seconds for the other players to re-join");
                }
            }
            guiApplication.updateController();
        } else {
            switch (nextGamePhase) {
                case GamePhase.CHOOSESTARTERSIDEPHASE -> {
                    cliController.showStarterCard();
                }
                case GamePhase.CHOOSECOLORPHASE -> {
                    System.out.print("Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]\n> ");
                }
                case GamePhase.CHOOSEOBJECTIVEPHASE ->  {
                    cliController.ShowSecretObjective();
                }
                case GamePhase.MAINPHASE -> {
                    System.out.println("---Game started---");
                }
                case GamePhase.ENDPHASE -> {
                    System.out.print("someone reached 20 points, the end phase has started\n> ");
                }
                case GamePhase.FINALPHASE -> {

                }
            }
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
        System.out.print(viewModel);
        viewModel.setNickname(playerIndex, nickname);
        viewModel.setConnection(playerIndex, true);
        viewModel.initializeElementMap(playerIndex);
        if (playerIndex == viewModel.getPlayerIndex()){
            if (viewModel.getPlayersSize() == 1){
                System.out.print("you successfully entered the game with the nickname " + nickname + ", wait for at least two players to start the game\n> ");
            } else {
                System.out.print("you successfully entered the game with the nickname " + nickname + ", there are " + viewModel.getPlayersSize() + " players connected, to start the game type START\n> ");
            }
        } else {
            System.out.print("a new player has connected with the name: " + nickname + "\n> ");
        }
        if (meDoGui) guiApplication.updateController();
    }




    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        if (playerIndex == -1) { // me stesso
            System.out.println("you disconnected from the game, to reconnect login with ADDUSER <your previous nickname>\n> ");
            viewModel = new ViewModel();
            guiApplication.changeScene("/fxml/lobby.fxml");
            return;
        }
        viewModel.setConnection(playerIndex, connected);
        if (playerIndex == viewModel.getPlayerIndex()){
            if (connected){
                System.out.print("you re-connected to the game\n> ");
            } else {
                System.out.println("you disconnected from the game, to reconnect login with ADDUSER <your previous nickname>\n> ");
                viewModel = new ViewModel();
                guiApplication.changeScene("/fxml/lobby.fxml");
            }
        } else {
            if (connected) {
                System.out.print(viewModel.getNickname(playerIndex) + " connected\n> ");
            } else {
                System.out.print(viewModel.getNickname(playerIndex) + " disconnected\n> ");
            }
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.addedCardToHand(playerIndex, cardIndex);
        viewModel.setHandSide(cardIndex, Side.FRONT);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you have drawn a card\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + " drew a card\n> ");
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
        System.err.println("updating board");
        viewModel.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you placed a card, now you have to draw your card with DRAWCARD [SHAREDGOLD1/SHAREDGOLD2/SHAREDRESOURCE1/SHAREDRESOURCE/DECKGOLD/DECKRESOURCE]\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + " placed a card\n> ");
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {
        viewModel.setColor(playerIndex, color);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you chose the color: " + color + "\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + " chose the color: " + color + "\n> ");
        }
        if (meDoGui)  guiApplication.updateController();
    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        viewModel.setCurrentPlayer(currentPlayerIndex);
        if (viewModel.getPlayerIndex() == currentPlayerIndex) {
            System.out.print("it's your turn\n> ");
            if (!meDoGui && viewModel.getGamePhase().ordinal()>=GamePhase.MAINPHASE.ordinal()) {
                cliController.showBoard();
                cliController.showHand();
            }
            showMessageAlert();
            System.out.print("to place your card use the command PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)] to place your card\n> ");
        } else {
            System.out.print("it's the turn of " + viewModel.getNickname(currentPlayerIndex) + "\n> ");
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateHandSide(int cardIndex, Side side) throws RemoteException {
        viewModel.setHandSide(cardIndex, side);
        System.out.print("you flipped your card\n> ");
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {
        viewModel.setPoints(playerIndex, points);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you now have " + points + " points\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + " has " + points + " points\n> ");
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        viewModel.setSecretObjective(objectiveCardId1, objectiveCardId2);
        if (objectiveCardId2 == -1){
            System.out.print("you have chosen the objective card: " + objectiveCardId1 + "\n> ");
        } else{
            System.out.print("Everyone chose his color, now please select one of the objective card from the selection with the command CHOOSEOBJECTIVE [0/1],\n to see your card use the command SHOWSECRETOBJECTIVES\n");
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        viewModel.setSharedObjectives(sharedObjectiveCardId1, sharedObjectiveCardId2);
        System.out.print("the shared objectives are: " + sharedObjectiveCardId1 + "," + sharedObjectiveCardId2 + "\n");
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        viewModel.fillPoints();
        if (side == null){
            viewModel.setStarterCard(cardId1);
            System.out.print("Chose your preferred side for the starter card with the command CHOOSESTARTER[Front/Back]\n");
        } else {
            viewModel.initializeBoard(playerIndex, cardId1);
            viewModel.setPlacedSide(cardId1, side);
            if (viewModel.getPlayerIndex() == playerIndex) {
                System.out.print("you now have chosen the starter side\n> ");
            } else {
                System.out.print(viewModel.getNickname(playerIndex) + " has chosen the starter side\n> ");
            }
        }
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateWinner(ArrayList<Integer> playerIndexes){
        viewModel.setGamePhase(GamePhase.FINALPHASE);
        playerIndexes.stream().forEach(e-> {
            viewModel.addWinner(e);
        });
        if (meDoGui){
            HashMap<String, Object> params = new HashMap<>();
            ArrayList<String> playerNicknames = new ArrayList<>();
            playerIndexes.stream().map(e-> viewModel.getNickname(e)).forEach(playerNicknames::add);
            params.put("playerNicknames", playerNicknames);
            guiApplication.changeScene("/fxml/final.fxml", params);
        }
        System.out.println("---------GAME ENDED----------");
        if (playerIndexes.isEmpty()){
            System.out.println("No one won");
        }
        if (playerIndexes.contains(viewModel.getPlayerIndex())){
            if (playerIndexes.size() == 1){
                System.out.println("Congratulations! You Won");
            } else {
                System.out.print("Congratulations! Congratulations, you tied for first place with ");
                playerIndexes.stream().filter(e -> e != viewModel.getPlayerIndex()).forEach(e -> {
                    System.out.print(viewModel.getNickname(e)+ ", ");
                });
            }
            System.out.println();
        } else {
            System.out.println("You Lost");
            playerIndexes.forEach(e -> {
                System.out.print(viewModel.getNickname(e)+ ", ");
            });
            System.out.println("won");
        }
        restart();
    }

    @Override
    public void updateElement(int playerIndex, Element element, int numberOfElements) throws RemoteException {
        viewModel.setElement(playerIndex, element, numberOfElements);
    }

    @Override
    public void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) {
        viewModel.setMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firtGoldDeckCard, firstResourceDeckCard);
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void updateChat(int playerIndex, String content){
        viewModel.setNewMessage(playerIndex, content);
        if (meDoGui) guiApplication.updateController();
    }

    @Override
    public void redirectOut(boolean bool) throws RemoteException {
        /*if(bool){
            oldPrintStream = System.out;
            System.setOut(restoreStream);
        } else {
            System.setOut(oldPrintStream);
        }*/
    }

    @Override
    public void ping(String ping) throws RemoteException {
        if (meDoGui){
            guiApplication.getGUIController().ping(client, server);
        } else {
            cliController.ping(client, server);
        }
    }

    @Override
    public void pong() throws RemoteException {
        if (meDoGui) {
            getGuiApplication().getGUIController().pongAnswer();
        } else {
            getCliController().pongAnswer();
        }
    }

    @Override
    public void sendError(String error) throws RemoteException {
        if (meDoGui) {
            guiApplication.getGUIController().setServerError(error);
        }
        System.err.print("\nERROR FROM SERVER: " + error + "\n> ");
    }

    private void showMessageAlert(){
        if (viewModel.getNewMessages() > 0){
            System.out.println("Unread messages: " + viewModel.getNewMessages() + " (use OPENCHAT command to read)");
            System.out.print("> ");
            viewModel.resetNewMessages();
        }
    }
    private void restart(){
        System.out.println("Restarting...");
        if (!meDoGui) cliController.restart(client, server);
        if (meDoGui) guiApplication.getGUIController().restart(client, server);
        viewModel = new ViewModel();
        GUIApplication.setViewModel(viewModel);
        System.out.print("the game finished, to enter a new match, use the command ADDUSER <nickname>\n> ");
    }
}
