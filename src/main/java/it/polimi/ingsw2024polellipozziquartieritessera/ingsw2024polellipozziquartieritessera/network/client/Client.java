package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;
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

public class Client {
    private static boolean meDoGui;
    private static GUIController guiController;

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
        meDoGui = false;
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
        new GUIApplication().runGui();
        meDoGui = true;
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

        for (Object key : cards.keySet()) {
            Map card = gson.fromJson(cards.get(key).toString(), Map.class);
            Integer id = Integer.parseInt(key.toString());

            if (id == cardId) {
                // ------ creating challenges ------
                CoverageChallenge coverageChallenge = null;
                ElementChallenge elementChallenge = null;
                StructureChallenge structureChallenge = null;
                Challenge challenge = null;

                if (card.get("Type").equals("Objective") || card.get("Type").equals("Gold")){
                    if (card.get("ChallengeType").equals("ElementChallenge")){
                        ArrayList<Element> elements = new ArrayList<>();
                        for (Object e : (ArrayList) card.get("ChallengeElements")) {
                            elements.add(Element.valueOf(e.toString().toUpperCase()));
                        }
                        // printa elementChallenge con elements
                    } else if (card.get("ChallengeType").equals("StructureChallenge")){
                        Element[][] configuration = new Element[Config.N_STRUCTURE_CHALLENGE_CONFIGURATION][Config.N_STRUCTURE_CHALLENGE_CONFIGURATION];
                        for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                            for (int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                                configuration[i][j] = Element.valueOf( ( (ArrayList) card.get("Structure") ).get(3*i+j).toString().toUpperCase() );
                            }
                        }
                        // printa structureChallenge configuration
                    } else if (card.get("ChallengeType").equals("CoverageChallenge")){
                        // printa coverageChallenge
                    } else if (card.get("ChallengeType").equals("NoChallenge")){
                        // non printare challenge ma spazi vuoti tipo
                    } else {
                        throw new RuntimeException(); //sistema
                    }
                }

                if (card.get("Type").equals("Objective")){
                    // printa (int) Double.parseDouble(card.get("Points").toString())
                } else {
                    // ------ creating corners ------
                    Corner[] frontCorners = new Corner[Config.N_CORNERS];
                    Corner[] backCorners = new Corner[Config.N_CORNERS];


                    //for in frontCorners
                    for (int i = 0; i < Config.N_CORNERS; i++){
                        String element = ((ArrayList) card.get("FrontCorners")).get(i).toString();
                        // card.get("FrontCorners")).getClass() returns ArrayList so casting should be good
                        if (element.equals("Empty")){
                            //probably can be removed because it falls in the else clause, also in line 135
                            frontCorners[i] = new Corner(Element.EMPTY, id, false);
                        } else if(element.equals("Hidden")) {
                            frontCorners[i] = new Corner(Element.EMPTY, id, true);;
                        } else {
                            frontCorners[i] = new Corner(Element.valueOf(element.toUpperCase()), id, false);
                        }
                        // printa contenuto dei corner
                    }
                    //for in backCorners
                    for (int i = 0; i < Config.N_CORNERS; i++) {
                        String element = ((ArrayList) card.get("BackCorners")).get(i).toString();
                        // card.get("BackCorners")).getClass() returns ArrayList so casting should be good
                        if (element.equals("Empty")) {
                            backCorners[i] = new Corner(Element.EMPTY, id, false);
                        } else if(element.equals("Hidden")) {
                            backCorners[i] = new Corner(Element.EMPTY, id, true);
                        } else {
                            backCorners[i] = new Corner(Element.valueOf(element.toUpperCase()), id, false);
                        }
                        // printa contenuto dei corner
                    }
                    // ------ creating cards ------
                    if (card.get("Type").equals("Resource")) {
                        // printa Element.valueOf(card.get("ResourceType").toString().toUpperCase())
                        // printa (int) Double.parseDouble(card.get("Points").toString())
                        // printa i corner
                    } else if (card.get("Type").equals("Gold")) {
                        ArrayList<Element> elements = new ArrayList<>();
                        for (Object e : (ArrayList) card.get("ResourceNeeded")) {
                            elements.add(Element.valueOf(e.toString().toUpperCase()));
                        }
                        // printa Element.valueOf(card.get("ResourceType").toString().toUpperCase())
                        // printa elements
                        // printa Double.parseDouble(card.get("Points").toString())
                    } else if (card.get("Type").equals("Starter")) {
                        ArrayList<Element> elements = new ArrayList<>();
                        for (Object e : (ArrayList) card.get("CenterResources")) {
                            elements.add(Element.valueOf(e.toString().toUpperCase()));
                        }
                        // printa frontCorners
                        // printa backCorners
                        // printa elements
                    }
                }
            }
        }

    }

    public static void printMessage(String msg) {
        if (meDoGui) {
            guiController.setServerMessage(msg);
        } else {
            System.out.print("\nINFO FROM SERVER: " + msg + "\n> ");
        }
    }

    public static void printError(String msg) {
        if (meDoGui) {

        } else {
            System.err.print("\nERROR FROM SERVER: " + msg + "\n> ");
        }
    }
}
