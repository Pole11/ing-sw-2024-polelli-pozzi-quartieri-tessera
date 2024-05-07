package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Client{

    public static void main(String[] args) throws IOException {
        String input = args[0];
        String host = args[1];
        String port = args[2];

        if (input.equalsIgnoreCase("socket")) {
            SocketClient.execute(host, port);
        } else { //default rmi
            RmiClient.execute(host, port);
        }
    }

    public static void runCli(VirtualServer server, VirtualView client) {
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter a nickname to start, with the command ADDUSER <nickname>");
        System.out.print("> ");
        while (running) {
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                try {
                    manageInput(server, message, client);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            } else if (line != null){
                System.out.print("> ");
            }
        }
    }

    public static void runGui(){

    }

    private static void manageInput(VirtualServer server, String[] message, VirtualView client) throws RemoteException {
        try {
            Command.valueOf(message[0].toUpperCase());
        } catch(IllegalArgumentException e) {
            System.err.print("INVALID COMMAND\n> ");
            return;
        }

        switch (Command.valueOf(message[0].toUpperCase())) {
            case Command.HELP:
                printAllCommands();
                break;
            case Command.ADDUSER:
                try {
                    server.addConnectedPlayer(client, message[1]);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.SHOWNICKNAME:
                server.showNickname(client);
                break;
            case Command.START:
                server.startGame(client);
                break;
            case Command.SHOWHAND:
                server.showHand(client);
                break;
            case Command.SHOWOBJECTIVES:
                server.showCommonObjectives(client);
                break;
            case Command.CHOOSESTARTER:
                try {
                    server.chooseInitialStarterSide(client, message[1]);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.CHOOSECOLOR:
                try {
                    server.chooseInitialColor(client, message[1]);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.CHOOSEOBJECTIVE:
                try {
                    server.chooseInitialObjective(client, message[1]);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.PLACECARD:
                try {
                    server.placeCard(client, message[1], message[2], message[3], message[4]);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.DRAWCARD:
                try {
                    server.drawCard(client, message[1]);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.FLIPCARD:
                try {
                    server.flipCard(client, message[1]);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.OPENCHAT:
                server.openChat();
                break;
            default:
                System.err.print("INVALID COMMAND\n> ");
                break;
        }
    }

    public static void printAllCommands() {
        System.out.print("The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + " ");
        });
        System.out.print("]\n> ");
    }

    public static void printCard(int id1, Side side1, int id2, Side side2, int id3, Side side3){
        printCard(id1, side1);
        printCard(id2, side2);
        printCard(id3, side3);
        // TODO better
    }

    public static void printCard(int id1, Side side1, int id2, Side side2){
        printCard(id1, side1);
        printCard(id2, side2);
        // TODO better
    }

    public static void printCard(int cardId, Side side) {
        String filePath = new File("").getAbsolutePath();
        String jsonString = null;
        try {
            jsonString = Populate.readJSON(filePath + Config.CARD_JSON_PATH);
        } catch (IOException e) {
            System.out.println("Error while loading image, pls try again");
        }
        Gson gson = new Gson();
        Map cards = gson.fromJson(jsonString, Map.class);


        for (Object key : cards.keySet()){
            Map card = gson.fromJson(cards.get(key).toString(), Map.class);
            int id = Integer.parseInt(key.toString());

            if (id == cardId){
                switch (card.get("Type").toString()){
                    case "Objective":
                        printObjectiveCard(card);
                        break;
                    case "Resource":
                        printResourceCard(card, side);
                        break;
                    case "Gold":
                        printGoldCard(card, side);
                        break;
                    case "Starter":
                        printStarterCard(card, side);
                        break;
                }
            }
        }
    }

    private static void printObjectiveCard(Map card){
        // attributes
        String points;
        ArrayList<Element> elementChallenge = null;
        Element[][] structureChallenge = null;

        // set points
        points = card.get("Points").toString();

        // set challenge
        if (card.get("ChallengeType").equals("StructureChallenge")){
            Element[][] configuration = new Element[Config.N_STRUCTURE_CHALLENGE_CONFIGURATION][Config.N_STRUCTURE_CHALLENGE_CONFIGURATION];
            for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                for (int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                    configuration[i][j] = Element.valueOf( ( (ArrayList) card.get("Structure") ).get(3*i+j).toString().toUpperCase() );
                }
            }
            structureChallenge = configuration;
        } else if (card.get("ChallengeType").equals("ElementChallenge")){
            ArrayList<Element> elements = new ArrayList<>();
            for (Object e : (ArrayList) card.get("ChallengeElements")) {
                elements.add(Element.valueOf(e.toString().toUpperCase()));
            }
            elementChallenge = elements;
        }

        // TODO: printing
    }

    private static void printResourceCard(Map card, Side side){
        // attributes
        String points;
        Element resource;
        Element[] frontCorners = new Element[Config.N_CORNERS];
        Element[] backCorners = new Element[Config.N_CORNERS];

        // set points
        points = card.get("Points").toString();

        // set resource
        resource = Element.valueOf(card.get("Resource").toString().toUpperCase());

        // set corners
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            frontCorners[i] = Element.valueOf(((ArrayList) card.get("FrontCorners")).get(i).toString().toUpperCase());
        }
        //for in backCorners
        for (int i = 0; i < Config.N_CORNERS; i++) {
            backCorners[i] = Element.valueOf(((ArrayList) card.get("BackCorners")).get(i).toString().toUpperCase());
        }

        // TODO: printing

        // if (side) ...
    }

    private static void printGoldCard(Map card, Side side){
        // attributes
        String points;
        Element resource;
        Element[] frontCorners = new Element[Config.N_CORNERS];
        Element[] backCorners = new Element[Config.N_CORNERS];
        boolean coverageChallenge = false;
        boolean noChallenge = false;
        ArrayList<Element> elementChallenge = null;
        ArrayList<Element> resourceNeeded = null;

        // set points
        points = card.get("Points").toString();

        // set resource
        resource = Element.valueOf(card.get("Resource").toString().toUpperCase());

        // set corners
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            frontCorners[i] = Element.valueOf(((ArrayList) card.get("FrontCorners")).get(i).toString().toUpperCase());
        }
        //for in backCorners
        for (int i = 0; i < Config.N_CORNERS; i++) {
            backCorners[i] = Element.valueOf(((ArrayList) card.get("BackCorners")).get(i).toString().toUpperCase());
        }

        // set challenge
        if (card.get("ChallengeType").equals("Coverage")){
            coverageChallenge = true;
        } else if (card.get("ChallengeType").equals("ElementChallenge")){
            ArrayList<Element> elements = new ArrayList<>();
            for (Object e : (ArrayList) card.get("ChallengeElements")) {
                elements.add(Element.valueOf(e.toString().toUpperCase()));
            }
            elementChallenge = elements;
        } else{
            noChallenge = true;
        }

        // set needed resources
        for (Object e : (ArrayList) card.get("ResourceNeeded")) {
            resourceNeeded.add(Element.valueOf(e.toString().toUpperCase()));
        }

        // TODO: printing

        // if (side) ...
    }

    private static void printStarterCard(Map card, Side side){
        // attributes
        Element[] frontCorners = new Element[Config.N_CORNERS];
        Element[] backCorners = new Element[Config.N_CORNERS];
        ArrayList<Element> centerResources = null;

        // set corners
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            frontCorners[i] = Element.valueOf(((ArrayList) card.get("FrontCorners")).get(i).toString().toUpperCase());
        }
        //for in backCorners
        for (int i = 0; i < Config.N_CORNERS; i++) {
            backCorners[i] = Element.valueOf(((ArrayList) card.get("BackCorners")).get(i).toString().toUpperCase());
        }

        // set center resources
        for (Object e : (ArrayList) card.get("CenterResources")) {
            centerResources.add(Element.valueOf(e.toString().toUpperCase()));
        }

        // TODO: printing
    }

    private static int getId(Object key) {
        return Integer.parseInt(key.toString());
    }
}
