package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.*;


public class Main {
    public static GameState gameState = null;

    public static void main(String argv[]) throws IOException, WrongStructureConfigurationSizeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException {
        boolean store = existStore(); //FA persistance
        if (store){
            // takes data from store
        } else {
            populate();
        }
    }

    private static boolean existStore(){
        //check if exists store
        return false;
    }

    private static String readJSON(String fileName) throws IOException {
        BufferedReader reader= null;
        try {reader = new BufferedReader(new FileReader(fileName));}
        catch (IOException e) {
            e.printStackTrace();// da sistemare
        }
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        String content = stringBuilder.toString();
        return content;
    }

    public static void populate() throws WrongStructureConfigurationSizeException, IOException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException {
        gameState = new GameState();

        createCardsMap();
        createPlayersList();
    }

    private static void createPlayersList() { // helper function, just for testing purpose
        //creazione momentanea dei player, li prenderà dal controller
        try {
            gameState.setPlayer(0, new Player("paolo", Color.RED));
            gameState.setPlayer(1, new Player("piergiorgio", Color.BLUE));
            gameState.setPlayer(2, new Player("fungiforme", Color.GREEN));
            gameState.setPlayer(3, new Player("paola", Color.YELLOW));
        } catch (Exception e) {

        }
    }

    public static void createCardsMap() throws IOException, WrongStructureConfigurationSizeException {
        String filePath = new File("").getAbsolutePath();
        String jsonString = readJSON(filePath + Config.CARD_JSON_PATH);
        Gson gson = new Gson();
        Map cards = gson.fromJson(jsonString, Map.class);

        for (Object key : cards.keySet()) {
            Map card = gson.fromJson(cards.get(key).toString(), Map.class);
            Integer id = Integer.parseInt(key.toString());

            // ------ creating challenges ------
            CoverageChallenge coverageChallenge = null;
            ElementChallenge elementChallenge = null;
            StructureChallenge structureChallenge = null;
            Challenge challenge = null;

            if (card.get("Type").equals("Objective") || card.get("Type").equals("Gold")){
                if (id < Config.firstObjectiveCardId) Config.firstObjectiveCardId = id;
                if (card.get("ChallengeType").equals("ElementChallenge")){
                    ArrayList<Element> elements = new ArrayList<>();
                    for (Object e : (ArrayList) card.get("ChallengeElements")){
                        elements.add(Element.valueOf(e.toString().toUpperCase()));
                    }

                    elementChallenge = new ElementChallenge(id, elements);
                    challenge = elementChallenge;
                    gameState.setElementChallenge(id, elementChallenge);
                } else if (card.get("ChallengeType").equals("StructureChallenge")){
                    Element[][] configuration = new Element[Config.N_STRUCTURE_CHALLENGE_CONFIGURATION][Config.N_STRUCTURE_CHALLENGE_CONFIGURATION];
                    for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                        for (int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                            configuration[i][j] = Element.valueOf( ( (ArrayList) card.get("Structure") ).get(i+j).toString().toUpperCase() );
                        }
                    }
                    structureChallenge = new StructureChallenge(id, configuration);
                    challenge = structureChallenge;
                    gameState.setStructureChallenge(id, structureChallenge);

                } else if (card.get("ChallengeType").equals("CoverageChallenge")){
                    coverageChallenge = new CoverageChallenge(id);
                    challenge = coverageChallenge;
                    gameState.setCoverageChallenge(id, coverageChallenge);
                } else if (card.get("ChallengeType").equals("NoChallenge")){

                } else {
                    System.out.println(card.get("ChallengeType"));
                    throw new RuntimeException(); //sistema
                }
            }

            if (card.get("Type").equals("Objective")){
                gameState.setObjectiveCard(id, new ObjectiveCard(id, challenge, (int) Double.parseDouble(card.get("Points").toString())  ));
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
                }
                // ------ creating cards ------
                if (card.get("Type").equals("Resource")) {
                    gameState.setResourceCard(id, new ResourceCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), (int) Double.parseDouble(card.get("Points").toString()), frontCorners, backCorners));
                } else if (card.get("Type").equals("Gold")) {
                    ArrayList<Element> elements = new ArrayList<>();
                    for (Object e : (ArrayList) card.get("ResourceNeeded")) {
                        elements.add(Element.valueOf(e.toString().toUpperCase()));
                    }
                    gameState.setGoldCard(id, new GoldCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), challenge, elements, (int) Double.parseDouble(card.get("Points").toString()) , frontCorners, backCorners));
                } else if (card.get("Type").equals("Starter")) {
                    if (id < Config.firstStarterCardId) Config.firstStarterCardId = id;
                    ArrayList<Element> elements = new ArrayList<>();
                    for (Object e : (ArrayList) card.get("CenterResources")) {
                        elements.add(Element.valueOf(e.toString().toUpperCase()));
                    }
                    gameState.setStarterCard(id, new StarterCard(id, frontCorners, backCorners, elements));
                }
            }
        }

        gameState.setMainBoard();
    }
}