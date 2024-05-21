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
    private static GamePhase currentGamePhase;

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
                    manageInputCli(server, message, client);
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
        guiController = new GUIController(client, server);

        guiApplication.runGui(guiController);
    }

    public static void manageInputCli(VirtualServer server, String[] message, VirtualView client) throws RemoteException {
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
            case Command.START:
                server.startGame(client);
                break;
            case Command.CHOOSESTARTER:
                try {
                    Side side;
                    try {
                        side = Side.valueOf(message[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid side, please enter a valid side (Front / Back)"));
                        return;
                    }
                    server.chooseInitialStarterSide(client, side);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.CHOOSECOLOR:
                try {
                    Color color;
                    try {
                        color = Color.valueOf(message[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid color, please enter a valid color (Blue / Green / Yellow / Red)"));
                        return;
                    }
                    server.chooseInitialColor(client, color);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.CHOOSEOBJECTIVE:
                try {
                    int cardId;
                    try {
                        cardId = Integer.parseInt(message[1]);
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid card id, please insert a valid card id"));
                        return;
                    }
                    server.chooseInitialObjective(client, cardId);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.PLACECARD:
                try {
                    int placingCardId;
                    int tableCardId;
                    CornerPos tableCornerPos;
                    Side placingCardSide;
                    try {
                        placingCardId = Integer.parseInt(message[1]);
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid id of the placing card, please insert a valid id"));
                        return;
                    }
                    try {
                        tableCardId = Integer.parseInt(message[2]);
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid id of the table card, please insert a valid id"));
                        return;
                    }
                    try {
                        tableCornerPos = CornerPos.valueOf(message[3].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid corner, please insert a valid corner position (Upleft / Upright / Downleft / Downright)"));
                        return;
                    }
                    try {
                        placingCardSide = Side.valueOf(message[4].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid side, please insert a valid side (Front / Back)"));
                        return;
                    }
                    server.placeCard(client, placingCardId, tableCardId, tableCornerPos, placingCardSide);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.DRAWCARD:
                try {
                    DrawType drawType;
                    try {
                        drawType = DrawType.valueOf(message[1]);
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid draw option, please choose a valid option (SharedGold1 / SharedGold2 / DeckGold / SharedResource1 / SharedResource2 / DeckResource)"));
                        return;
                    }
                    server.drawCard(client, drawType);
                } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    System.err.print("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.FLIPCARD:
                try {
                    int cardId;
                    try {
                        cardId = Integer.parseInt(message[1]);
                    } catch (NumberFormatException e) {
                        client.sendError("Please enter a valid card id");
                        return;
                    }
                    server.flipCard(client, cardId);
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
        ArrayList<String> cardToPrint = null;
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
                        cardToPrint = printObjectiveCard(card);
                        break;
                    case "Resource":
                        cardToPrint = printResourceCard(card, side);
                        break;
                    case "Gold":
                        cardToPrint = printGoldCard(card, side);
                        break;
                    case "Starter":
                        cardToPrint = printStarterCard(card, side);
                        break;
                }
            }
            for(int i = 0; i < cardToPrint.size(); i++){
                System.out.print(cardToPrint.get(i));
            }
        }
    }

    private static ArrayList<String> printObjectiveCard(Map card) {
        // attributes
        String points;
        ArrayList<Element> elementChallenge = null;
        Element[][] structureChallenge = null;
        String elementString = "";
        ArrayList<ArrayList<String>> challengeMatrix = null;
        String structureString = null;
        ArrayList<String> output = new ArrayList<>();

        int irel = 0, jrel = 0;

        // set points
        points = card.get("Points").toString();

        // set challenge
        if (card.get("ChallengeType").equals("StructureChallenge")) {
            Element[][] configuration = new Element[Config.N_STRUCTURE_CHALLENGE_CONFIGURATION][Config.N_STRUCTURE_CHALLENGE_CONFIGURATION];
            for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++) {
                for (int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++) {
                    configuration[i][j] = Element.valueOf(((ArrayList) card.get("Structure")).get(3 * i + j).toString().toUpperCase());
                }
            }
            structureChallenge = configuration;
        } else if (card.get("ChallengeType").equals("ElementChallenge")) {
            ArrayList<Element> elements = new ArrayList<>();
            for (Object e : (ArrayList) card.get("ChallengeElements")) {
                elements.add(Element.valueOf(e.toString().toUpperCase()));
            }
            elementChallenge = elements;
        }
        //printing objects settings

        if(card.get("ChallengeType").equals("ElementChallenge")) {
            for (Element e : elementChallenge) {
                elementString = " " + elementString + e.toString().substring(0, 3).toUpperCase();
            }
            if (elementString.startsWith(" ")) {
                elementString = elementString.substring(1);
            }
        } else if (card.get("ChallengeType").equals("StructureChallenge")) {
            challengeMatrix = new ArrayList<ArrayList<String>>();
            for(int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION + 2; i++){
                challengeMatrix.add(new ArrayList<String>());
                for(int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION + 2; j++){
                    challengeMatrix.get(i).add("");
                }
            }
            //rotation of 45° of the structureChallenge
            for(int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                for(int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                    int centeri = (int)Math.floor((double) Config.N_STRUCTURE_CHALLENGE_CONFIGURATION/2);
                    int centerj = (int)Math.floor((double) Config.N_STRUCTURE_CHALLENGE_CONFIGURATION/2);
                    challengeMatrix.get(centeri + i + jrel).set(centerj + j - irel, structureChallenge[i][j].toString().substring(0,3));
                }
            }
            //remove empty rows and columns
            resize(challengeMatrix);


        }
        if (card.get("ChallengeType").equals("ElementChallenge")) {
            output.add("+-----------------+");
            output.add("|" + getFormattedString(17, card.get("ChallengeType").toString().toUpperCase()) + "|");
            output.add("|" + getFormattedString(17, "points: "+points.toString()) + "|");
            output.add("|" + getFormattedString(17, "<"+ elementString +">") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("+-----------------+");
            //todo testing
        } else if (card.get("ChallengeType").equals("StructureChallenge")) {
            output.add("+-----------------+");
            output.add("|" + getFormattedString(17, card.get("ChallengeType").toString().toUpperCase()) + "|");
            output.add("|" + getFormattedString(17, "points: "+points.toString()) + "|");
            for(int i = 0; i < challengeMatrix.size(); i++){
                for(int j = 0; j < challengeMatrix.get(i).size(); j++){
                    if(challengeMatrix.get(i).get(j).isEmpty())
                        structureString = structureString + "   ";
                    else
                        structureString = structureString + challengeMatrix.get(i).get(j);
                }
                output.add("|" + getFormattedString(17, structureString) + "|");
            }
            output.add("+-----------------+");
        }
        return output;
    }
    private static ArrayList<String> printResourceCard(Map card, Side side){
        // attributes
        String points;
        Element resource;
        Element[] frontCorners = new Element[Config.N_CORNERS];
        Element[] backCorners = new Element[Config.N_CORNERS];
        ArrayList<String> output = new ArrayList<>();

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

        if (side == Side.BACK){
            output.add("+-----------------+");
            output.add("|"+backCorners[0].toString().substring(0, 3).toUpperCase()+ getFormattedString(11, "") +backCorners[1].toString().substring(0, 3).toUpperCase()+"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") +"|");
            output.add("|" + getFormattedString(17, resource.toString().substring(0,3).toUpperCase()) +"|");
            output.add("|"+backCorners[3].toString().substring(0, 3).toUpperCase()+ getFormattedString(11, "id: "+card.get("id").toString()) +backCorners[2].toString().substring(0, 3).toUpperCase()+"|");
            output.add("+-----------------+");
        } else{
            output.add("+-----------------+");
            output.add("|"+backCorners[0].toString().substring(0, 3).toUpperCase()+getFormattedString(11, points)+backCorners[1].toString().substring(0, 3).toUpperCase()+"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") +"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|"+backCorners[3].toString().substring(0, 3).toUpperCase()+getFormattedString(11, "id: "+card.get("id").toString())+backCorners[2].toString().substring(0, 3).toUpperCase()+"|");
            output.add("+-----------------+");
        }
        return output;
    }

    private static ArrayList<String> printGoldCard(Map card, Side side){
        // attributes
        String points;
        Element resource;
        Element[] frontCorners = new Element[Config.N_CORNERS];
        Element[] backCorners = new Element[Config.N_CORNERS];
        boolean coverageChallenge = false;
        boolean noChallenge = false;
        ArrayList<Element> elementChallenge = null;
        ArrayList<Element> resourceNeeded = null;
        ArrayList<String> output = new ArrayList<>();

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

        // define cost and challenge strings
        HashMap<Element, Integer> cost = new HashMap<>();
        for (Element e : resourceNeeded){
            if(!cost.containsKey(e)){
                cost.put(e,1);
            } else{
                cost.put(e, cost.get(e)+1);
            }
        }
        String costString = "";
        for (Element key : cost.keySet()){
            costString = cost.get(key) + key.toString().substring(0,3).toUpperCase();
        }

        String challengeString = "";
        if (coverageChallenge){
            challengeString = "cha: COV";
        } else if (elementChallenge != null){
            challengeString = "cha: " + elementChallenge.getFirst().toString().substring(0,3).toUpperCase();
        }

        if (side == Side.BACK){
            output.add("+-----------------+");
            output.add("|"+backCorners[0].toString().substring(0, 3).toUpperCase()+ getFormattedString(11, "") +backCorners[1].toString().substring(0, 3).toUpperCase()+"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") +"|");
            output.add("|" + getFormattedString(17, resource.toString().substring(0,3).toUpperCase()) +"|");
            output.add("|"+backCorners[3].toString().substring(0, 3).toUpperCase()+ getFormattedString(11, "id: "+card.get("id".toString())) +backCorners[2].toString().substring(0, 3).toUpperCase()+"|");
            output.add("+-----------------+");
        } else{
            output.add("+-----------------+");
            output.add("|"+backCorners[0].toString().substring(0, 3).toUpperCase()+getFormattedString(11, points)+backCorners[1].toString().substring(0, 3).toUpperCase()+"|");
            output.add("|"+ getFormattedString(17, costString)+"|");
            output.add("|"+ getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") +"|");
            output.add("|"+ getFormattedString(17, challengeString)+"|");
            output.add("|"+backCorners[3].toString().substring(0, 3).toUpperCase()+getFormattedString(11, "id: "+card.get("id").toString())+backCorners[2].toString().substring(0, 3).toUpperCase()+"|");
            output.add("+-----------------+");
        }
        return output;
    }

    private static ArrayList<String> printStarterCard(Map card, Side side){
        // attributes
        Element[] frontCorners = new Element[Config.N_CORNERS];
        Element[] backCorners = new Element[Config.N_CORNERS];
        ArrayList<Element> centerResources = null;
        ArrayList<String> output = new ArrayList<>();
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

        // create centerString
        String centerString = "";
        for (Element e : centerResources){
            centerString = centerString + " " + e.toString().substring(0, 3).toUpperCase();
        }
        if (centerString.startsWith(" ")) {
            centerString = centerString.substring(1);
        }

        if (side == Side.BACK){
            output.add("+-----------------+");
            output.add("|"+backCorners[0].toString().substring(0, 3).toUpperCase()+getFormattedString(11, "")+backCorners[1].toString().substring(0, 3).toUpperCase()+"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|"+backCorners[3].toString().substring(0, 3).toUpperCase()+getFormattedString(11, "id: "+card.get("id").toString())+backCorners[2].toString().substring(0, 3).toUpperCase()+"|");
            output.add("+-----------------+");
        } else{
            output.add("+-----------------+");
            output.add("|"+backCorners[0].toString().substring(0, 3).toUpperCase()+getFormattedString(11, "")+backCorners[1].toString().substring(0, 3).toUpperCase()+"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|"+ getFormattedString(17, centerString) +"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|"+backCorners[3].toString().substring(0, 3).toUpperCase()+getFormattedString(11, "id: "+card.get("id").toString())+backCorners[2].toString().substring(0, 3).toUpperCase()+"|");
            output.add("+-----------------+");
        }
        return output;
    }

    private static int getId(Object key) {
        return Integer.parseInt(key.toString());
    }

    private static String getFormattedString(int length, String content){
        // ------
        if (length%2 == 0){
            while (content.length() < length){
                if (content.length()%2 == 0){
                    content = " " + content + " ";
                } else{
                    content = content + " ";
                }
            }
        } else{
            while (content.length() < length){
                if (content.length()%2 == 0){
                    content = content + " ";
                } else{
                    content = " " + content + " ";
                }
            }
        }
        return content;
    }



    public static void printMessage(String msg) {
        if (meDoGui) {
            guiController.setServerMessage(msg);
        }
        System.out.print("\nINFO FROM SERVER: " + msg + "\n> ");
    }

    public static void printError(String msg) {
        if (meDoGui) {
            guiController.setServerError(msg);
        }
        System.err.print("\nERROR FROM SERVER: " + msg + "\n> ");

    }

    public static void changePhase(GamePhase nextGamePhase) {
        if (!meDoGui) {
            return;
        }

        currentGamePhase = nextGamePhase;

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

    }

    public static void nicknameUpdate(int playerIndex, String nickname) {
        System.out.println("nickname" + nickname);
    }

    /*public static void changePhase(TurnPhase nextTurnPhase) {
        if (currentGamePhase.equals(GamePhase.MAINPHASE)) {
            switch (nextTurnPhase) {
                case TurnPhase.PLACINGPHASE -> {

                }
                case TurnPhase.DRAWPHASE -> {

                }
            }
        }
    }*/

    private static void resize(ArrayList<ArrayList<String>> matrix){
        //remove empty rows
        for(int j = 0; j < matrix.size(); j++){
            Boolean isEmpty = true;
            for(int i = 0; i < matrix.get(j).size() && isEmpty; i++){
                if(!matrix.get(j).get(i).equals("")){
                    isEmpty = false;
                }
            }
            if(isEmpty){
                matrix.remove(j);
                j--;
            }
        }
        //remove empty columns
        ArrayList<Boolean> areEmpty = new ArrayList<>();
        for(int i = 0; i < matrix.get(0).size(); i++){
            areEmpty.add(true);
        }

        for(int i = 0; i < matrix.size(); i++){
            for (int j = 0; j < matrix.get(i).size(); j++){
                if(!matrix.get(i).get(j).equals("")){
                    areEmpty.set(j, false);
                }
            }
        }
        for(int j = 0; j < areEmpty.size(); j++){
            if(areEmpty.get(j).equals(true)){
                areEmpty.remove(j);
                for(int i = 0; i < matrix.size(); i++){
                    matrix.get(i).remove(j);
                }
            }
        }
    }
}


// TODO: capire come fare a chiamare le print card... da dove, chi lo fa, etc.
