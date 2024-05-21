package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.MessageEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.*;

public class Client {
    private static boolean meDoGui;
    private static GUIController guiController;
    private static CLIController cliController = new CLIController();
    public static GUIApplication guiApplication; // TODO: public only for testing purpose, put private after finished testing
    public static GUIControllerGame guiControllerOld; // TODO: public only for testing purpose, put private after finished testing
    private static GamePhase currentGamePhase;
    private static ViewModel viewModel = new ViewModel();


    public static void main(String[] args) throws IOException {
        String input = args[0];
        String host = args[1];
        String port = args[2];

        startClient(input, host, port);
    }

    public static void startClient(String input, String host, String port) throws IOException {
        if (input.equalsIgnoreCase("socket")) {
            SocketClient.execute(host, port);
        } else { //default rmi
            RmiClient.execute(host, port);
        }
    }

    public static void runCli(VirtualServer server, VirtualView client) {
        meDoGui = false;
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter a nickname to start, with the command ADDUSER <nickname>");

        System.out.print("> "); // print phase
        while (running) {
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                try {
                    cliController.manageInput(server, message, client);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else if (line != null){
                System.out.print("> "); // print phase
            }
        }
    }

    public static void runGui(VirtualServer server, VirtualView client){
        
        meDoGui = true;

        guiApplication = new GUIApplication();

        //guiApplication.setServer(server);
        //guiApplication.setClient(client);

        // il punto è che dovresti fare questo ma dopo il runGui
        //guiController = guiApplication.getGUIController();
        //System.out.println("CCC" + guiController);
        //guiApplication.getGUIController() = new GUIControllerOld(client, server);

        guiApplication.runGui(client, server);

    }


    public static void changePhase(GamePhase nextGamePhase) {
        viewModel.setGamePhase(nextGamePhase);

        currentGamePhase = nextGamePhase;


        switch (nextGamePhase) {
            case GamePhase.NICKNAMEPHASE -> {
                guiApplication.getGUIController().goToScene("/fxml/start.fxml");
            }
            case GamePhase.CHOOSESTARTERSIDEPHASE -> {
                guiApplication.getGUIController().goToScene("/fxml/chooseStarter.fxml");
            }

            case GamePhase.CHOOSECOLORPHASE -> {
                System.out.println("Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]");
                guiApplication.getGUIController().goToScene("/fxml/chooseColor.fxml");

            }
            case GamePhase.CHOOSEOBJECTIVEPHASE -> {
                guiApplication.getGUIController().goToScene("/fxml/chooseObjective.fxml");
            }

            case GamePhase.MAINPHASE -> {
                System.out.println("---Game started---");
                guiApplication.getGUIController().goToScene("/fxml/game.fxml");
            }
            case GamePhase.ENDPHASE -> {
                System.out.println("NON SO CHI reached 20 points o NON SO");
            }
            case GamePhase.FINALPHASE -> {
                guiApplication.getGUIController().goToScene("/fxml/final.fxml");
            }
        }
    }

    public static void updateTurnPhase(TurnPhase nextTurnPhase){
        viewModel.setTurnPhase(nextTurnPhase);
    }

    public static void sendIndex(int index) throws RemoteException {
        viewModel.setPlayerIndex(index);
    }

    public static void nicknameUpdate(int playerIndex, String nickname) {
        viewModel.setNickname(playerIndex, nickname);
        viewModel.setConnection(playerIndex, true);
        if (playerIndex == viewModel.getPlayerIndex()){
            if (viewModel.getPlayersSize() == 1){
                System.out.println("you successfully entered the game with the nickname " + nickname + ", wait for at least two players to start the game");
            } else {
                System.out.println("you successfully entered the game with the nickname " + nickname + ", there are " + viewModel.getPlayersSize() + " players connected, to start the game type START");
            }
        }

        System.out.println("a new player has connected with the name:" + nickname);
    }


    public static void start() throws RemoteException {
    }

    public static void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        viewModel.setConnection(playerIndex, connected);
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
                guiApplication.getGUIController().goToScene("/fxml/final.fxml");
            }
        }

    }

    public static void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.addedCardToHand(playerIndex, cardIndex);
        viewModel.setHandSide(cardIndex, Side.FRONT);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you have drawn a card");
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "drew a card");
        }
    }

    public static void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.removedCardFromHand(playerIndex, cardIndex);
    }

    public static void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        viewModel.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you placed a card, now you have to draw your card with DRAW [SHAREDGOLD1/SHAREDGOLD2/SHAREDRESOURCE1/SHAREDRESOURCE/DECKGOLD/DECKRESOURCE]");
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "placed a card");
        }
    }

    public static void updateColor(int playerIndex, Color color) throws RemoteException {
        viewModel.setColor(playerIndex, color);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you chose the color:" + color);
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "chose the color" + color);
        }
    }

    public static void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        viewModel.setCurrentPlayer(currentPlayerIndex);
        if (viewModel.getPlayerIndex() == currentPlayerIndex) {
            System.out.println("it's your turn");
            System.out.println("to place your card use the command PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)] to place your card");
        } else {
            System.out.println("it's the turn of" + viewModel.getNickname(currentPlayerIndex));
        }
    }

    public static void updateHandSide(int cardIndex, Side side) throws RemoteException {
        viewModel.setHandSide(cardIndex, side);
        System.out.println("you flipped your card");
    }

    public static void updatePoints(int playerIndex, int points) throws RemoteException {
        viewModel.setPoints(playerIndex, points);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.println("you now have " + points + "points");
        } else {
            System.out.println(viewModel.getNickname(playerIndex) + "has" + points + "points");
        }
    }

    public static void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        viewModel.setSecretObjective(objectiveCardId1, objectiveCardId2);
        if (objectiveCardId2 == -1){
            System.out.println("you have chosen the objective card: " + objectiveCardId1);
        } else{
            System.out.println("Everyone chose his color, now please select one of the objective card from the selection with the command CHOOSEOBJECTIVE [0/1], to see your card use the command SHOWOBJECTIVE");
            // TODO: cliController.showSecretObjectives();

        }
    }

    public static void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        viewModel.setSharedObjectives(sharedObjectiveCardId1, sharedObjectiveCardId2);
        System.out.println("the shared objectives are: " + sharedObjectiveCardId1 + "," + sharedObjectiveCardId2);
        //TODO: cliController.showSharedObjectvives();
    }

    public static void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
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
    }

    public static void ping(String ping) throws RemoteException {
        //TODO : respond to ping to manage disconections
    }

    public static void sendMessage(String message) throws RemoteException {
        if (meDoGui) {
            //guiController.setServerMessage(message);
        }
        System.out.print("\nINFO FROM SERVER: " + message + "\n> ");
    }

    public static void sendError(String error) throws RemoteException {
        if (meDoGui) {
            //guiController.setServerError(error);
        }
        System.err.print("\nERROR FROM SERVER: " + error + "\n> ");

    }

}

// TODO: capire come fare a chiamare le print card... da dove, chi lo fa, etc.
