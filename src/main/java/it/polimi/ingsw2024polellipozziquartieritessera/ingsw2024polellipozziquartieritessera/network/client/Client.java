package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIController;
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
    private static GUIApplication guiApplication;
    private static GUIController guiController;
    private static CLIController cliController;
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
        /*
        meDoGui = true;

        guiApplication = new GUIApplication();

        //guiApplication.setServer(server);
        //guiApplication.setClient(client);

        // il punto è che dovresti fare questo ma dopo il runGui
        //guiController = guiApplication.getGUIController();
        //System.out.println("CCC" + guiController);
        guiController = new GUIController(client, server);

        guiApplication.runGui(guiController);

         */
    }


    public static void changePhase(GamePhase nextGamePhase) {
        viewModel.setGamePhase(nextGamePhase);
        if (!meDoGui) {
            return;
        }

        currentGamePhase = nextGamePhase;

        /*
        switch (nextGamePhase) {
            case GamePhase.NICKNAMEPHASE -> {
                guiController.goToScene("/fxml/start.fxml");
            }
            case GamePhase.CHOOSESTARTERSIDEPHASE -> {
                guiController.goToScene("/fxml/chooseStarter.fxml");
            }
            case GamePhase.CHOOSECOLORPHASE -> {
                guiController.goToScene("/fxml/chooseColor.fxml");
            }
            case GamePhase.CHOOSEOBJECTIVEPHASE -> {
                guiController.goToScene("/fxml/chooseObjective.fxml");
            }
            case GamePhase.MAINPHASE -> {
                guiController.goToScene("/fxml/game.fxml");
            }
            case GamePhase.ENDPHASE -> {
            }
            case GamePhase.FINALPHASE -> {
                guiController.goToScene("/fxml/final.fxml");
            }
        }

         */

    }

    public static void start() throws RemoteException {
    }

    public static void connectionInfo(int playerIndex, boolean connected) throws RemoteException {
        viewModel.setConnection(playerIndex, connected);
    }

    public static void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.addedCardToHand(playerIndex, cardIndex);
        viewModel.setHandSide(cardIndex, Side.FRONT);
    }

    public static void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {
        viewModel.removedCardFromHand(playerIndex, cardIndex);
    }

    public static void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {
        viewModel.updatePlayerBoard(playerIndex, placingCardId, tableCardId, existingCornerPos, side);
    }

    public static void updateColor(int playerIndex, Color color) throws RemoteException {
        viewModel.setColor(playerIndex, color);
    }

    public static void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {
        viewModel.setCurrentPlayer(currentPlayerIndex);
    }

    public static void updateHandSide(int cardIndex, Side side) throws RemoteException {
        viewModel.setHandSide(cardIndex, side);
    }

    public static void updatePoints(int playerIndex, int points) throws RemoteException {
        viewModel.setPoints(playerIndex, points);
    }

    public static void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {
        viewModel.setSecretObjective(objectiveCardId1, objectiveCardId2);
    }

    public static void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {
        viewModel.setSharedObjectives(sharedObjectiveCardId1, sharedObjectiveCardId2);
    }

    public static void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {
        if (side == null){
            viewModel.setStarterCard(cardId1);
        } else {
            viewModel.initializeBoard(playerIndex, cardId1);
            viewModel.setPlacedSide(cardId1, side);
        }
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
            //guiController.setServerError(error);
        }
        System.err.print("\nERROR FROM SERVER: " + error + "\n> ");

    }


    public static void sendIndex(int index) throws RemoteException {
        viewModel.setPlayerIndex(index);
    }

    public static void nicknameUpdate(int playerIndex, String nickname) {
        System.out.println("nickname" + nickname);
    }

}

// TODO: capire come fare a chiamare le print card... da dove, chi lo fa, etc.
