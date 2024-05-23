package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CLIController {
    private ViewModel viewModel;


    public void manageInput(VirtualServer server, String[] message, VirtualView client) throws RemoteException {
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
                        System.err.print(("Invalid side, please enter a valid side (Front / Back)\n> "));
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
                        System.err.print(("Invalid color, please enter a valid color (Blue / Green / Yellow / Red)\n> "));
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
                        System.err.print(("Invalid card id, please insert a valid card id\n> "));
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
                        System.err.print(("Invalid id of the placing card, please insert a valid id\n> "));
                        return;
                    }
                    try {
                        tableCardId = Integer.parseInt(message[2]);
                    } catch (IllegalArgumentException e) {
                        System.err.print(("Invalid id of the table card, please insert a valid id\n> "));
                        return;
                    }
                    try {
                        tableCornerPos = CornerPos.valueOf(message[3].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.print(("Invalid corner, please insert a valid corner position (Upleft / Upright / Downleft / Downright)\n> "));
                        return;
                    }
                    try {
                        placingCardSide = Side.valueOf(message[4].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.print(("Invalid side, please insert a valid side (Front / Back)\n> "));
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
                        drawType = DrawType.valueOf(message[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.print(("Invalid draw option, please choose a valid option (SharedGold1 / SharedGold2 / DeckGold / SharedResource1 / SharedResource2 / DeckResource)"));
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
            case Command.PING:
                server.ping(client);
                break;
            default:
                System.err.print("INVALID COMMAND\n> ");
                break;
        }
    }

    public void printAllCommands() {
        System.out.print("The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + " ");
        });
        System.out.print("]\n> ");
    }

    public void printCard(int CardId, Side side){
        ArrayList<String> cardToPrint = new ArrayList<>();
        cardToPrint = getPrintedCard(CardId, side);

        for(int i = 0; i < cardToPrint.size(); i++){
            System.out.println(cardToPrint.get(i));
        }
    }

    public ArrayList<String> getPrintedCard(int cardId, Side side) {
        String filePath = new File("").getAbsolutePath();
        String jsonString = null;
        ArrayList<String> cardToPrint = null;
        try {
            jsonString = Populate.readJSON(filePath + Config.CARD_JSON_PATH);
        } catch (IOException e) {
            System.out.print("Error while loading image, pls try again");
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
        }
        return cardToPrint;
    }

    public void printBoard(ArrayList<ArrayList<Integer>> board, ArrayList<Integer> cardsOrder){
        ArrayList<ArrayList<Integer>> rotatedBoard = new ArrayList<>();
        ArrayList<String> printedBoard = new ArrayList<>();
        ArrayList<String> card = null;

        int irel = 0, jrel = 0;

        for(int i = 0; i < board.size() + 2; i++){
            rotatedBoard.add(new ArrayList<>());
            for(int j  = 0; j < board.get(i).size() + 2; j++){
                rotatedBoard.get(i).add(-1);
            }
        }
        //rotate board of 45°
        int centeri = (int)Math.floor((double) board.size()/2);
        int centerj = (int)Math.floor((double) board.get(0).size()/2);
        for(int i = 0; i < board.size(); i++){
            for(int j = 0; j < board.get(i).size(); j++){
                irel = i - 1;
                jrel = j - 1;
                rotatedBoard.get(centeri + i + jrel).set(centerj + j - irel, board.get(i).get(j));
            }
        }
        //for each id in the rotated board, create a Arraylist of strings that represent the card and place it in the right position in the printedBoard
        for(int i = 0; i < rotatedBoard.size(); i++){
            for(int j = 0; j < rotatedBoard.get(i).size(); j++){
                card = new ArrayList<>();

                int id = rotatedBoard.get(i).get(j);
                if(id < 0){
                    ;
                }

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
            int centeri = (int)Math.floor((double) Config.N_STRUCTURE_CHALLENGE_CONFIGURATION/2);
            int centerj = (int)Math.floor((double) Config.N_STRUCTURE_CHALLENGE_CONFIGURATION/2);
            //todo testing
            for(int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                for(int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                    irel = i - 1;
                    jrel = j - 1;
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
