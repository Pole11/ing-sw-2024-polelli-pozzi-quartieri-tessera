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
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events.UpdateBoardEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

/**
 * Populate class that parse the cards from the json file and save the state of the game
 */
public class Populate {

    /**
     * Reads a JSON file from the classpath and returns its content as a string.
     *
     * @param fileName name of the JSON file to read
     * @return the content of the JSON file as a string
     * @throws IOException if an I/O error occurs while reading the file
     */
    public static String readJSON(String fileName) throws IOException {
        InputStream inputStream = Populate.class.getResourceAsStream(fileName);
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: " + fileName);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line).append(ls);
        }
        // Remove the last line separator
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        reader.close();
        return stringBuilder.toString();
    }

    /**
     * Populates the given GameState object with data from a predefined JSON file.
     *
     * @param gameState the gamestate to populate
     * @throws IOException if an I/O error occurs while reading the JSON file
     */
    public static void populate(GameState gameState) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = Populate.class.getResourceAsStream("/cards.json");
        if (inputStream == null) {
            throw new FileNotFoundException("Resource not found: /cards.json");
        }
        Map<String, ?> cards = mapper.readValue(inputStream, Map.class);


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

    /**
     * Creates a Card object based on the provided card data and id.
     *
     * @param setup boolean indicating if this method is called during setup
     * @param card map containing the card data
     * @param id card identifier
     * @return created card
     */
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

    /**
     * Creates the Challenge object from the cards map
     *
     * @param card map containing the cards data
     * @return created challenge
     */
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

    /**
     * Creates the Corner object from the cards map
     *
     * @param side Side of the card where the corner is located
     * @param card map containg the cards data
     * @param id card identifier
     * @return created corner
     */
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

    /**
     * Saves the current state of the game to a file.
     *
     * @param gameState the gamestate that has to be saved
     */
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

    /**
     * Creates a save map for the given GameState object.
     *
     * @param gameState the gamestate to create a save map for
     * @return HashMap representing the saved state of the game
     */
    public static HashMap<String, Object> createSaveMap(GameState gameState){
        HashMap<String, Object> saveMap = new HashMap<>();

        HashMap<String, Object> mainBoard = createMainBoardSave(gameState.getMainBoard());
        ArrayList<HashMap<String, Object>> players = new ArrayList<>();
        for (int i = 0; i<gameState.getPlayersSize(); i++){
            players.add(createPlayerSave(gameState.getPlayer(i)));
        }

        ArrayList<HashMap<String , Object>> messages = new ArrayList<>();
        gameState.getChat().getMessages().stream().forEach(e->{
            HashMap<String, Object> message = new HashMap<>();
            message.put("author", e.getAuthor());
            message.put("content", e.getContent());
            messages.add(message);
        });


        ArrayList<Object> placedEventList = new ArrayList<>();
        for (UpdateBoardEvent event : gameState.getPlacedEventList()) {
            HashMap<String, Object> eventMap = new HashMap<>();
            eventMap.put("playerIndex", event.getPlayerIndex());
            eventMap.put("placingCardId", event.getPlacingCardId());
            eventMap.put("tableCardId", event.getTableCardId());
            eventMap.put("existingCornerPos", event.getExistingCornerPos());
            eventMap.put("side", event.getSide());
            placedEventList.add(eventMap);
        }

        saveMap.put("placedEventList", placedEventList);
        saveMap.put("mainBoard", mainBoard);
        saveMap.put("chat", messages);
        saveMap.put("players", players);
        saveMap.put("currentPlayerIndex", gameState.getCurrentPlayerIndex());
        saveMap.put("currentGamePhase", gameState.getCurrentGamePhase().toString());
        saveMap.put("currentGameTurn", gameState.getCurrentGameTurn().toString());
        saveMap.put("answered", gameState.getAnswered());
        saveMap.put("turnToPlay", gameState.getTurnToPlay());
        saveMap.put("prevGamePhase", gameState.getPrevGamePhase() != null ? gameState.getPrevGamePhase().toString() : "");

        return saveMap;

    }

    /**
     * Creates a save map for the given Board object.
     *
     * @param mainBoard the Board object to create a save map for
     * @return a HashMap representing the saved state of the board
     */
    public static HashMap<String, Object> createMainBoardSave(Board mainBoard){
        HashMap<String, Object> saveMap = new HashMap<>();

        saveMap.put("sharedGoldCards", Arrays.stream(mainBoard.getSharedGoldCards()).filter(e -> e != null).map(GoldCard::getId).toList());
        saveMap.put("sharedResourceCards", Arrays.stream(mainBoard.getSharedResourceCards()).filter(e -> e != null).map(ResourceCard::getId).toList());
        saveMap.put("sharedObjectiveCards", Arrays.stream(mainBoard.getSharedObjectiveCards()).filter(e -> e != null).map(ObjectiveCard::getId).toList());
        saveMap.put("goldDeck", mainBoard.getGoldDeck().stream().filter(e -> e != null).map(GoldCard::getId).toList());
        saveMap.put("resourceDeck", mainBoard.getResourceDeck().stream().filter(e -> e != null).map(ResourceCard::getId).toList());

        return saveMap;
    }

    /**
     * Creates a save map for the given Player object.
     *
     * @param player the Player object to create a save map for
     * @return a HashMap representing the saved state of the player
     */
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

    /**
     * Restores the game state from a previously saved file.
     *
     * @param gameState the GameState object to restore the state into
     * @throws IOException if an I/O error occurs while reading the saved state file
     */
    public static void restoreState(GameState gameState) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        String filePath = new File("").getAbsolutePath();

        Map<String, ?> jsonState = null;
        try {
            jsonState = mapper.readValue(Paths.get(filePath + Config.GAME_STATE_PATH).toFile(), Map.class);
        } catch (IOException e) {
            System.out.println("there is no state");
            return;
        }
        System.out.println("Found a state file in location: " + filePath + Config.GAME_STATE_PATH);

        // Load cards.json
        InputStream cardsInputStream = Populate.class.getResourceAsStream("/cards.json");
        if (cardsInputStream == null) {
            throw new FileNotFoundException("Resource not found: /cards.json");
        }
        Map<String, ?> cards = mapper.readValue(cardsInputStream, Map.class);

        //if the json is an empty object
        if (jsonState.isEmpty()) {
            System.out.println("there is no state");
            return;
        }



        //restore MainBoard
        Map<String, ?> jsonBoard = (Map<String, ?>) jsonState.get("mainBoard");
        List<Integer> sharedGold = (List<Integer>) jsonBoard.get("sharedGoldCards");
        List<Integer> sharedResource = (List<Integer>) jsonBoard.get("sharedResourceCards");
        List<Integer> sharedObjectives = (List<Integer>) jsonBoard.get("sharedObjectiveCards");

        for (int i = 0; i<sharedGold.size(); i++){
            int goldId = sharedGold.get(i);
            gameState.getMainBoard().setSharedGoldCard(i, (GoldCard) createCard(false, (Map) cards.get(String.valueOf(goldId)), goldId));
        }
        for (int i = 0; i<sharedResource.size(); i++){
            int resourceId = sharedResource.get(i);
            gameState.getMainBoard().setSharedResourceCard(i,(ResourceCard) createCard(false, (Map) cards.get(String.valueOf(resourceId)), resourceId));
        }
        for (int i = 0; i<sharedObjectives.size(); i++){
            int objectiveId = sharedObjectives.get(i);
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


        //restore chat
        ArrayList chat = (ArrayList) jsonState.get("chat");
        chat.stream().forEach(e->{
            gameState.getChat().addMessage( (int) ((HashMap) e).get("author"), (String) ((HashMap) e).get("content"));
        });

        //restore players
        ArrayList<?> players = (ArrayList<?>) jsonState.get("players");
        if (players.isEmpty()){
            System.out.println("the players are empty");
        }
        for (int i = 0; i < players.size(); i++){
            Map playerJson = (Map) players.get(i);
            Player player = new Player((String) playerJson.get("nickname"), null, gameState);

            player.setConnected(false);
            player.addPoints((int) playerJson.get("points"));
            Object playerBoardObj = playerJson.get("playerBoard");
            System.out.println(playerBoardObj);
            ArrayList<ArrayList<Integer>> playerBoardArrayList = (ArrayList<ArrayList<Integer>>) playerBoardObj;
            System.out.println(playerBoardArrayList);
            player.setPlayerBoard(playerBoardArrayList);


            Map<?, ?> handJson = (Map<?, ?>) playerJson.get("handCardsMap");
            HashMap<Integer, Side> handCardsMap = new HashMap<>();
            for (Object key : handJson.keySet()){
                handCardsMap.put(Integer.parseInt((String) key), Side.valueOf((String) handJson.get(key)));
            }
            System.out.println(playerJson.get("handCardsMap"));
            System.out.println(handCardsMap);
            player.setHandCardsMap(handCardsMap);
            System.out.println(player.getHandCardsMap());

            Color color = null;
            if (!playerJson.get("color").equals("")){
                color = Color.valueOf((String) playerJson.get("color"));
                player.setColor(color);
            }


            int objectivesWon = (int) playerJson.get("objectivesWon");
            player.setObjectivesWon(objectivesWon);

            StarterCard starterCard = null;
            if (!playerJson.get("starterCard").equals("")){
                starterCard = (StarterCard) createCard(false, (Map) cards.get(String.valueOf(playerJson.get("starterCard"))) , (Integer) playerJson.get("starterCard"));
                player.setStarterCard(starterCard);
            }


            Map<?, ?> placedJson = (Map<?, ?>) playerJson.get("placedCardsMap");
            HashMap<Integer, Side> placedCardsMap = new HashMap<>();
            for (Object key : placedJson.keySet()){
                placedCardsMap.put(Integer.parseInt((String) key), Side.valueOf((String) placedJson.get(key)));
            }
            player.setPlacedCardsMap(placedCardsMap);

            ObjectiveCard objectiveCard = null;
            if (!playerJson.get("objectiveCard").equals("")){
                objectiveCard = (ObjectiveCard) createCard(false, (Map) cards.get(String.valueOf(playerJson.get("objectiveCard"))) , (Integer) playerJson.get("objectiveCard"));
                player.setObjectiveCard(objectiveCard);
            }


            ArrayList<Integer> ids = (ArrayList<Integer>) playerJson.get("objectiveCardOptions");

            if (!ids.isEmpty()){
                ObjectiveCard[] ObjectiveCardOptions = new ObjectiveCard[2];
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

            gameState.addPlayerThread(i);
            gameState.addPlayer(player);

        }


        //restore placedList
        ArrayList<Map> jsonPlacedList = (ArrayList<Map>) jsonState.get("placedEventList");
        for (Map event : jsonPlacedList){
            int index = (int) event.get("playerIndex");
            Player player = gameState.getPlayer(index);
            UpdateBoardEvent backupEvent = new UpdateBoardEvent(gameState, new ArrayList<>(), player, (int) event.get("placingCardId"), (int) event.get("tableCardId") , CornerPos.valueOf((String) event.get("existingCornerPos")), Side.valueOf((String) event.get("side")));
            gameState.addPlacedEvent(backupEvent);
        }


        gameState.setCurrentPlayerIndex((int)jsonState.get("currentPlayerIndex"));
        gameState.setCurrentGamePhase(GamePhase.valueOf((String) jsonState.get("currentGamePhase")));
        gameState.setCurrentGameTurn(TurnPhase.valueOf((String) jsonState.get("currentGameTurn")));
        String prevGamePhase = (String) jsonState.get("prevGamePhase");
        gameState.setPrevGamePhase(prevGamePhase != "" ? GamePhase.valueOf(prevGamePhase) : null);
        gameState.setTurnToPlay((int)jsonState.get("turnToPlay"));


        Map<?, ?> answeredJson = (Map<?, ?>) jsonState.get("answered");
        for (Object key : answeredJson.keySet()){
            gameState.setAnswered((Integer.parseInt((String) key)), (Boolean) answeredJson.get((String) key));
        }

        System.out.println("I restored the state");
    }
}
