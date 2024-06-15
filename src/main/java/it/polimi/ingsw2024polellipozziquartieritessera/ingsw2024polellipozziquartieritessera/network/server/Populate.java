package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.ToNumberPolicy;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Global;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Board;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Populate {

    public static String readJSON(String fileName) throws IOException {
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

    public static void populate(GameState gameState) throws IOException {
        String filePath = new File("").getAbsolutePath();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, ?> cards = mapper.readValue(Paths.get(filePath + Config.CARD_JSON_PATH).toFile(), Map.class);;


        for (Object key : cards.keySet()) {
            Map<String, ?> card = (Map) cards.get(String.valueOf(key));
            int id = Integer.parseInt(key.toString());
            if (card.get("Type").equals("Objective") || card.get("Type").equals("Starter")){
                gameState.addCardToCardsMap(id, createCard(true, card, id));
            } else {
                Card newCard = createCard(true, card, id);
                gameState.addCardToCardsMap(id, newCard);
                if (card.get("Type").equals("Resource")){
                    gameState.getMainBoard().addToResourceDeck((ResourceCard) newCard);
                } else if (card.get("Type").equals("Gold"))  {
                    gameState.getMainBoard().addToGoldDeck((GoldCard) newCard);
                }
            }

        }
    }


    public static Card createCard(boolean setup, Map<String, ?> card, Integer id) {
        Card returnCard = null;

        // ------ creating challenges ------
        Challenge challenge = createChallenge(card);

        if (card.get("Type").equals("Objective")){
            //this runs only if this method is called from createCardsMap
            if (setup){
                if (id < Global.firstObjectiveCardId) Global.firstObjectiveCardId = id;
            }
            returnCard = new ObjectiveCard(id, challenge, (int) Double.parseDouble(card.get("Points").toString()));
            //gameState.addCardToCardsMap(id, cardI);
        } else {

            // ------ creating corners ------
            Corner[] frontCorners = createCorners("FrontCorners", card, id);
            Corner[] backCorners = createCorners("BackCorners", card, id);

            // ------ creating cards ------
            if (card.get("Type").equals("Resource")){
                //this runs only if this method is called from createCardsMap
                if (setup){
                    if (id < Global.firstResourceCardId) Global.firstResourceCardId = id;
                }
                returnCard = new ResourceCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), (int) Double.parseDouble(card.get("Points").toString()), frontCorners, backCorners);
            } else if (card.get("Type").equals("Gold")){
                //this runs only if this method is called from createCardsMap
                if (setup){
                    if (id < Global.firstGoldCardId) Global.firstGoldCardId = id;
                }
                ArrayList<Element> elements = new ArrayList<>();
                for (String e : (ArrayList<String>) card.get("ResourceNeeded")){
                    elements.add(Element.valueOf(e.toUpperCase()));
                }
                returnCard = new GoldCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), challenge, elements, (int) Double.parseDouble(card.get("Points").toString()) , frontCorners, backCorners);
            } else if (card.get("Type").equals("Starter")){
                //this runs only if this method is called from createCardsMap
                if (setup){
                    if (id < Global.firstStarterCardId) Global.firstStarterCardId = id;
                }
                ArrayList<Element> elements = new ArrayList<>();
                for (String e : (ArrayList<String>) card.get("CenterResources")){
                    elements.add(Element.valueOf(e.toUpperCase()));
                }
                returnCard = new StarterCard(id, frontCorners, backCorners, elements);
            }
        }
        return returnCard;
    }

    private static Challenge createChallenge(Map<String, ?> card) {
        Challenge challenge = null;
        if (card.get("Type").equals("Objective") || card.get("Type").equals("Gold")){
            if (card.get("ChallengeType").equals("ElementChallenge")){
                ArrayList<Element> elements = new ArrayList<>();
                for (String e : (ArrayList<String>) card.get("ChallengeElements")){
                    elements.add(Element.valueOf(e.toUpperCase()));
                }

                challenge = new ElementChallenge(elements);
            } else if (card.get("ChallengeType").equals("StructureChallenge")){
                Element[][] configuration = new Element[Config.N_STRUCTURE_CHALLENGE_CONFIGURATION][Config.N_STRUCTURE_CHALLENGE_CONFIGURATION];
                for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                    for (int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                        configuration[i][j] = Element.valueOf( ( (ArrayList) card.get("Structure") ).get(3*i+j).toString().toUpperCase() );
                    }
                }
                challenge = new StructureChallenge(configuration);
            } else if (card.get("ChallengeType").equals("CoverageChallenge")){
                challenge = new CoverageChallenge();
            } else if (card.get("ChallengeType").equals("NoChallenge")){

            } else {
                System.out.println(card.get("ChallengeType"));
                throw new RuntimeException(); //sistema
            }
        }
        return challenge;
    }

    private static Corner[] createCorners(String side, Map<String, ?> card, Integer id){
        Corner[] corners = new Corner[Config.N_CORNERS];
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            String element = ((ArrayList) card.get(side)).get(i).toString();
            // card.get("FrontCorners")).getClass() returns ArrayList so casting should be good
            if (element.equals("Empty")){
                //probably can be removed because it falls in the else clause, also in line 135
                corners[i] = new Corner(Element.EMPTY, id, false);
            } else if(element.equals("Hidden")) {
                corners[i] = new Corner(Element.EMPTY, id, true);;
            } else {
                corners[i] = new Corner(Element.valueOf(element.toUpperCase()), id, false);
            }
        }
        return corners;
    }

    public static void saveState(GameState gameState) {
        Gson gson = new Gson();

        //todo: cambia Gson con Jackson
        // write to this file
        String filePath = new File("").getAbsolutePath();
        try (Writer writer = new FileWriter(filePath + Config.GAME_STATE_PATH)) {
            gson.toJson(createSaveMap(gameState), writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, Object> createSaveMap(GameState gameState){
        HashMap<String, Object> saveMap = new HashMap<>();

        HashMap<String, Object> mainBoard = createMainBoardSave(gameState.getMainBoard());
        ArrayList<HashMap<String, Object>> players = new ArrayList<>();
        for (int i = 0; i<gameState.getPlayersSize(); i++){
            players.add(createPlayerSave(gameState.getPlayer(i)));
        }

        saveMap.put("mainBoard", mainBoard);
        saveMap.put("chat", "bohNonC'èLaChat");
        saveMap.put("players", players);
        saveMap.put("currentPlayerIndex", gameState.getCurrentPlayerIndex());
        saveMap.put("currentGamePhase", gameState.getCurrentGamePhase().toString());
        saveMap.put("currentGameTurn", gameState.getCurrentGameTurn().toString());
        saveMap.put("answered", gameState.getAnswered());
        saveMap.put("turnToPlay", gameState.getTurnToPlay());
        saveMap.put("prevGamePhase", gameState.getPrevGamePhase() != null ? gameState.getPrevGamePhase().toString() : "");

        return saveMap;

    }

    public static HashMap<String, Object> createMainBoardSave(Board mainBoard){
        HashMap<String, Object> saveMap = new HashMap<>();

        saveMap.put("sharedGoldCards", Arrays.stream(mainBoard.getSharedGoldCards()).filter(e -> e != null).map(GoldCard::getId).toList());
        saveMap.put("sharedResourceCards", Arrays.stream(mainBoard.getSharedResourceCards()).filter(e -> e != null).map(ResourceCard::getId).toList());
        saveMap.put("sharedObjectiveCards", Arrays.stream(mainBoard.getSharedObjectiveCards()).filter(e -> e != null).map(ObjectiveCard::getId).toList());
        saveMap.put("goldDeck", mainBoard.getGoldDeck().stream().filter(e -> e != null).map(GoldCard::getId).toList());
        saveMap.put("resourceDeck", mainBoard.getResourceDeck().stream().filter(e -> e != null).map(ResourceCard::getId).toList());

        return saveMap;
    }


    public static HashMap<String, Object> createPlayerSave(Player player){
        //all attributes but gameState and client
        HashMap<String, Object> saveMap = new HashMap<>();
        saveMap.put("nickname", player.getNickname());
        saveMap.put("points", player.getPoints());
        saveMap.put("playerBoard", player.getPlayerBoard());
        HashMap <Integer, Side> placedCardsMap = player.getPlacedCardsMap();
        placedCardsMap.values().stream().map(e -> e.toString());
        saveMap.put("placedCardsMap", placedCardsMap);
        HashMap <Integer, Side> handCardsMap = player.getHandCardsMap();
        handCardsMap.values().stream().map(e -> e.toString());
        saveMap.put("handCardsMap", handCardsMap);
        saveMap.put("color", player.getColor() != null ? player.getColor().toString() : "");
        saveMap.put("objectivesWon", player.getObjectivesWon());
        saveMap.put("starterCard", player.getStarterCard() != null ? player.getStarterCard().getId() : "");
        saveMap.put("objectiveCard", player.getObjectiveCard() != null ? player.getObjectiveCard().getId() : "");
        saveMap.put("objectiveCardOptions", player.getObjectiveCardOptions() != null ? Arrays.stream(player.getObjectiveCardOptions()).filter(e->e!=null).map(ObjectiveCard::getId).toList() : "");
        HashMap <Integer, Element> centerResource = player.getCenterResources();
        centerResource.values().stream().map(e -> e.toString());
        saveMap.put("centerResource", centerResource);
        HashMap <Element, Integer> allElements = player.getAllElements();
        allElements.keySet().stream().map(e -> e.toString());
        saveMap.put("allElements", allElements);
        return saveMap;
    }

    public static void restoreState(GameState gameState) throws IOException {
        String filePath = new File("").getAbsolutePath();
        ObjectMapper mapper = new ObjectMapper();

        Map<String, ?> jsonState = null;
        try {
            jsonState = mapper.readValue(Paths.get(filePath + Config.GAME_STATE_PATH).toFile(), Map.class);
        } catch (IOException e) {
            System.out.println(e.getClass());
            System.out.println("there is no state");
            return;
        }

        final Map<String, ?> cards = mapper.readValue(Paths.get(filePath + Config.CARD_JSON_PATH).toFile(), Map.class);;


        //if the json is an empty object
        if (jsonState.isEmpty()){
            System.out.println("there is no state");
            return;
        }

        //restore MainBoard
        Map<String, ?> jsonBoard = (Map<String, ?>) jsonState.get("mainBoard");
        List<Integer> sharedGold = (List<Integer>) jsonBoard.get("sharedGoldCards");
        List<Integer> sharedResource = (List<Integer>) jsonBoard.get("sharedResourceCards");
        List<Integer> sharedObjectives = (List<Integer>) jsonBoard.get("sharedObjectiveCards");

        for (int i = 0; i<sharedGold.size(); i++){
            int goldId = (int) sharedGold.get(i);
            gameState.getMainBoard().setSharedGoldCard(i, (GoldCard) createCard(false, (Map) cards.get(String.valueOf(goldId)), goldId));
        }
        for (int i = 0; i<sharedResource.size(); i++){
            int resourceId = (int) sharedResource.get(i);
            gameState.getMainBoard().setSharedResourceCard(i,(ResourceCard) createCard(false, (Map) cards.get(String.valueOf(resourceId)), resourceId));
        }
        for (int i = 0; i<sharedObjectives.size(); i++){
            int objectiveId = (int) sharedObjectives.get(i);
            gameState.getMainBoard().setSharedObjectiveCard(i,(ObjectiveCard) createCard(false, (Map) cards.get(String.valueOf(objectiveId)), objectiveId));
        }

        ArrayList<Integer> goldDeckInt = (ArrayList<Integer>) jsonBoard.get("goldDeck");
        ArrayList<Integer> resourceDeckInt = (ArrayList<Integer>) jsonBoard.get("resourceDeck");
        ArrayList<GoldCard> goldDeck = new ArrayList<>();
        ArrayList<ResourceCard> resourceDeck = new ArrayList<>();


        goldDeckInt.forEach(e -> {
            goldDeck.add((GoldCard) createCard(false, (Map) cards.get(String.valueOf(e)), e));
        });
        gameState.getMainBoard().setGoldDeck(goldDeck);

        resourceDeckInt.forEach(e -> {
            resourceDeck.add((ResourceCard) createCard(false, (Map) cards.get(String.valueOf(e)), e));
        });
        gameState.getMainBoard().setResourceDeck(resourceDeck);

        //NON HO ANCORA ASSEGNATO I DECKS

        //restore players
        ArrayList<?> players = (ArrayList<?>) jsonState.get("players");
        for (int i = 0; i < players.size(); i++){
            Map playerJson = (Map) players.get(i);
            Player player = new Player((String) playerJson.get("nickname"), null, gameState);

            player.setConnected(false);
            player.addPoints( (int) playerJson.get("points"));
            player.setPlayerBoard((ArrayList<ArrayList<Integer>>) playerJson.get("playerBoard"));

            Map<?, ?> placedJson = (Map<?, ?>) playerJson.get("placedCardsMap");
            HashMap<Integer, Side> placedCardsMap = new HashMap<>();
            for (Object key : placedJson.keySet()){
                placedCardsMap.put(Integer.parseInt((String) key), Side.valueOf((String) placedJson.get(key)));
            }
            player.setPlacedCardsMap(placedCardsMap);


            Map<?, ?> handJson = (Map<?, ?>) playerJson.get("handCardsMap");
            HashMap<Integer, Side> handCardsMap = new HashMap<>();
            for (Object key : handJson.keySet()){
                handCardsMap.put(Integer.parseInt((String) key), Side.valueOf((String) handJson.get(key)));
            }
            player.setHandCardsMap(handCardsMap);

            Color color = null;
            if (playerJson.get("color") != ""){
                color = Color.valueOf((String) playerJson.get("color"));
            }
            player.setColor(color);

            int objectivesWon = (int) playerJson.get("objectivesWon");
            player.setObjectivesWon(objectivesWon);

            StarterCard starterCard = (StarterCard) createCard(false, (Map) cards.get(String.valueOf(playerJson.get("starterCard"))) , (Integer) playerJson.get("starterCard"));
            player.setStarterCard(starterCard);

            ObjectiveCard objectiveCard = (ObjectiveCard) createCard(false, (Map) cards.get(String.valueOf(playerJson.get("objectiveCard"))) , (Integer) playerJson.get("objectiveCard"));
            player.setObjectiveCard(objectiveCard);


            if (playerJson.get("objectiveCardOptions") != ""){
                ObjectiveCard[] ObjectiveCardOptions = new ObjectiveCard[2];
                ArrayList<Integer> ids = (ArrayList<Integer>) playerJson.get("objectiveCardOptions");
                ObjectiveCardOptions[0] = (ObjectiveCard) createCard(false, (Map) cards.get(String.valueOf(ids.get(0))), ids.get(0));
                ObjectiveCardOptions[1] = (ObjectiveCard) createCard(false, (Map) cards.get(String.valueOf(ids.get(1))), ids.get(1));
                player.setSecretObjectiveCardOptions(ObjectiveCardOptions);
            }

            Map<?, ?> centerJson = (Map<?, ?>) playerJson.get("centerResource");
            HashMap<Integer, Element> centerResources = new HashMap<>();
            for (Object key : centerJson.keySet()){
                centerResources.put(Integer.parseInt((String) key), Element.valueOf((String) centerJson.get(key)));
            }
            player.setCenterResources(centerResources);


            Map<?, ?> elementJson = (Map<?, ?>) playerJson.get("allElements");
            HashMap<Element, Integer> allElements = new HashMap<>();
            for (Object key : elementJson.keySet()){
                allElements.put(Element.valueOf((String) key),(int) elementJson.get(key));
            }
            player.setAllElements(allElements);

            gameState.addPlayer(player);
            player.setConnected(true);

        }
        //System.out.println("j");
        /*



        HashMap <Element, Integer> allElements = player.getAllElements();
        allElements.keySet().stream().map(e -> e.toString());
        saveMap.put("allElements", allElements);
        saveMap.put("connected", player.isConnected());


 */
        //gameState.addPlayer();
        
        //gameState.restoreData();


    }

}
