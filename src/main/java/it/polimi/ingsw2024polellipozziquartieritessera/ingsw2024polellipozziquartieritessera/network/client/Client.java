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
        System.out.print("Please enter a nickname to start, with the command ADDUSER <nickname>\n> ");
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
        //System.out.print("CCC" + guiController);
        //guiApplication.getGUIController() = new GUIControllerOld(client, server);

        guiApplication.runGui(client, server);

    }


    public static void changePhase(GamePhase nextGamePhase) {
        viewModel.setGamePhase(nextGamePhase);
        currentGamePhase = nextGamePhase;

        switch (nextGamePhase) {
            case GamePhase.NICKNAMEPHASE -> {
                if (meDoGui) guiApplication.getGUIController().goToScene("/fxml/start.fxml");
            }
            case GamePhase.CHOOSESTARTERSIDEPHASE -> {
                if (meDoGui) guiApplication.getGUIController().goToScene("/fxml/chooseStarter.fxml");
            }

            case GamePhase.CHOOSECOLORPHASE -> {
                System.out.print("Everyone chose his side, now please select a valid color from one of the lists with the command CHOOSECOLOR [Blue, Green, Yellow, Red]\n> ");
                if (meDoGui) guiApplication.getGUIController().goToScene("/fxml/chooseColor.fxml");

            }
            case GamePhase.CHOOSEOBJECTIVEPHASE -> {
                if (meDoGui) guiApplication.getGUIController().goToScene("/fxml/chooseObjective.fxml");
            }

            case GamePhase.MAINPHASE -> {
                System.out.print("---Game started---\n> ");
                if (meDoGui) guiApplication.getGUIController().goToScene("/fxml/game.fxml");
            }
            case GamePhase.ENDPHASE -> {
                System.out.print("NON SO CHI reached 20 points o NON SO\n> ");
            }
            case GamePhase.FINALPHASE -> {
                if (meDoGui) guiApplication.getGUIController().goToScene("/fxml/final.fxml");
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
            System.out.print("you successfully entered the game with the nickname \"" + nickname + "\", ");
        } else {
            System.out.print("a new player has connected with the name \"" + nickname + "\", ");
        }
        if (viewModel.getPlayersSize() == 1){
            System.out.print("wait for at least two players to start the game\n> ");
        } else {
            System.out.print("there are " + viewModel.getPlayersSize() + " players connected, to start the game type START\n> ");
        }

    }


    public static void start() throws RemoteException {
    }

    public static void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        viewModel.setConnection(playerIndex, connected);
        if (playerIndex == viewModel.getPlayerIndex()){
            if (connected){
                System.out.print("you re-connected to the game\n> ");
            } else {
                //SISTEMA
                throw new RuntimeException();
            }
        } else {
            if (connected) {
                System.out.print(viewModel.getNickname(playerIndex) + " connected\n> ");
            } else {
                System.out.print(viewModel.getNickname(playerIndex) + " disconnected\n> ");
                guiApplication.getGUIController().goToScene("/fxml/final.fxml");
            }
        }
    }

    public static void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.addedCardToHand(playerIndex, cardIndex);
        viewModel.setHandSide(cardIndex, Side.FRONT);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you have drawn a card\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + "drew a card\n> ");
        }
    }

    public static void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.removedCardFromHand(playerIndex, cardIndex);
    }

    public static void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        viewModel.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you placed a card, now you have to draw your card with DRAW [SHAREDGOLD1/SHAREDGOLD2/SHAREDRESOURCE1/SHAREDRESOURCE/DECKGOLD/DECKRESOURCE]\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + "placed a card\n> ");
        }
    }

    public static void updateColor(int playerIndex, Color color) throws RemoteException {
        viewModel.setColor(playerIndex, color);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you chose the color " + color+"\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + "chose the color " + color+"\n> ");
        }
    }

    public static void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        viewModel.setCurrentPlayer(currentPlayerIndex);
        if (viewModel.getPlayerIndex() == currentPlayerIndex) {
            System.out.print("it's your turn\n> ");
            System.out.print("to place your card use the command PLACECARD [placingCardId] [tableCardId] [tableCornerPos(Upright/Upleft/Downright/Downleft)] [placingCardSide(Front/Back)] to place your card\n> ");
        } else {
            System.out.print("it's the turn of " + viewModel.getNickname(currentPlayerIndex)+"\n> ");
        }
    }

    public static void updateHandSide(int cardIndex, Side side) throws RemoteException {
        viewModel.setHandSide(cardIndex, side);
        System.out.print("you flipped your card\n> ");
    }

    public static void updatePoints(int playerIndex, int points) throws RemoteException {
        viewModel.setPoints(playerIndex, points);
        if (viewModel.getPlayerIndex() == playerIndex) {
            System.out.print("you now have " + points + "points\n> ");
        } else {
            System.out.print(viewModel.getNickname(playerIndex) + "has" + points + "points\n> ");
        }
    }

    public static void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        viewModel.setSecretObjective(objectiveCardId1, objectiveCardId2);
        if (objectiveCardId2 == -1){
            System.out.print("you have chosen the objective card: " + objectiveCardId1+"\n> ");
        } else{
            System.out.print("Everyone chose his color, now please select one of the objective card from the selection with the command CHOOSEOBJECTIVE [0/1], to see your card use the command SHOWOBJECTIVE\n> ");
            // TODO: cliController.showSecretObjectives();

        }
    }

    public static void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        viewModel.setSharedObjectives(sharedObjectiveCardId1, sharedObjectiveCardId2);
        System.out.print("the shared objectives are: " + sharedObjectiveCardId1 + "," + sharedObjectiveCardId2 + "\n> ");
        //TODO: cliController.showSharedObjectvives();
    }

    public static void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        if (side == null){
            viewModel.setStarterCard(cardId1);
            System.out.print("Chose your preferred side for the starter card with the command CHOOSESTARTER[Front/Back]\n> ");
            // TODO : cliController.showStarterSides();
        } else {
            viewModel.initializeBoard(playerIndex, cardId1);
            viewModel.setPlacedSide(cardId1, side);
            if (viewModel.getPlayerIndex() == playerIndex) {
                System.out.print("you now have chosen the starter side\n> ");
            } else {
                System.out.print(viewModel.getNickname(playerIndex) + " has chosen the starter side\n> ");
            }
        }
    }


    public static void updateWinner(int playerIndex){
        viewModel.addWinner(playerIndex);
    }

    public static void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) {
        viewModel.setMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firtGoldDeckCard, firstResourceDeckCard);
    }


    public static void ping(String ping) throws RemoteException {
        //TODO : respond to ping to manage disconections
    }

    public static void sendMessage(String message) throws RemoteException {
        if (meDoGui) {
            guiController.setServerMessage(message);
        }
        System.out.print("\nINFO FROM SERVER: " + message + "\n> ");
    }

    public static void sendError(String error) throws RemoteException {
        if (meDoGui) {
            guiController.setServerError(error);
        }
        System.err.print("\nERROR FROM SERVER: " + error + "\n> ");
    }
}

// TODO: capire come fare a chiamare le print card... da dove, chi lo fa, etc.
