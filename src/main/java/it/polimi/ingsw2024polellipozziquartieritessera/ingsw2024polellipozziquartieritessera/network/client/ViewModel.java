package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import com.google.gson.Gson;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Global;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Chat;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Message;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate.readJSON;

/**
 * The ViewModel class represents the data model for managing game state and player information inside the client.
 * It holds various maps and attributes to track game elements, player details, and game phases.
 */
public class ViewModel {
    /**
     * Index of the player using this ViewModel.
     */
    private int playerIndex;

    /**
     * Maps player indices to their corresponding nicknames.
     */
    private final HashMap<Integer, String> nicknamesMap;

    /**
     * Maps player indices to their connection status (true if connected, false otherwise).
     */
    private final HashMap<Integer, Boolean> connessionMap;

    /**
     * Maps player indices to their chosen colors.
     */
    private final HashMap<Integer, Color> colorsMap;

    /**
     * Maps player indices to their current points.
     */
    private final HashMap<Integer, Integer> pointsMap;

    /**
     * Maps player indices to their element counts.
     */
    private final HashMap<Integer, HashMap> elementsMap;

    /**
     * Chat instance for managing in-game communication.
     */
    private final Chat chat;

    /**
     * ID of the starter card.
     */
    private int starterCardId;

    /**
     * Array representing objectives:
     * - indices 0 and 1: common objectives
     * - indices 2 and 3: secret objectives (index 2 is the chosen secret objective)
     * If a card is not set, it is represented as -1.
     */
    private final int[] objectives;

    /**
     * Current game phase.
     */
    private GamePhase gamePhase;

    /**
     * Current turn phase.
     */
    private TurnPhase turnPhase;

    /**
     * Index of the current player whose turn it is.
     */
    private int currentPlayer;

    /**
     * Map of card IDs to their corresponding Card objects.
     */
    private final HashMap<Integer, Card> cardsMap;

    /**
     * Map of player indices to lists of card IDs in their hands.
     */
    private final HashMap<Integer, ArrayList<Integer>> handsMap;

    /**
     * Map of card IDs to their placed sides on the board.
     */
    private final HashMap<Integer, Side> placedSideMap;

    /**
     * Map of card IDs to their held sides in the hands.
     */
    private final HashMap<Integer, Side> handsSideMap;

    /**
     * Map of player indices to lists of card IDs on their boards.
     */
    private final HashMap<Integer, ArrayList<ArrayList<Integer>>> boardsMap;
    /**
     * Array representing the main board:
     * - indices 0 and 1: shared resource cards
     * - indices 2 and 3: shared gold cards
     * - indices 4 and 5: first resource and gold deck cards respectively
     * If a card is not present, it is initialized to -1.
     */
    private int[] mainBoard;

    /**
     * Map of player indices to lists of card IDs in the order they were placed.
     */
    private HashMap<Integer, ArrayList<Integer>> placingCardOrderMap;

    /**
     * Number of new unread messages (for CLI only).
     */
    private int newMessages;

    /**
     * List of player indices representing the winners of the game.
     */
    private ArrayList<Integer> winners;

    /**
     * Constructs a new ViewModel instance initializing all necessary data structures.
     */
    public ViewModel() {
        playerIndex = -1;
        elementsMap = new HashMap<>();
        nicknamesMap = new HashMap<>();
        handsSideMap = new HashMap<>();
        placedSideMap = new HashMap<>();
        //contains also the secretOptions, when the objective is decided the [3] is -1
        objectives = new int[4];
        Arrays.fill(objectives, -1);
        mainBoard = new int[6];
        Arrays.fill(mainBoard, -1);
        gamePhase = GamePhase.NICKNAMEPHASE;
        turnPhase = TurnPhase.PLACINGPHASE;
        cardsMap = new HashMap<>();
        handsMap = new HashMap<>();
        boardsMap = new HashMap<>();
        connessionMap = new HashMap<>();
        colorsMap = new HashMap<>();
        pointsMap = new HashMap<>();
        populateCardsMap();
        chat = new Chat();
        winners = new ArrayList<>();

        placingCardOrderMap = new HashMap<>();
    }

    // BASIC SETTER
    /**
     * Sets the index of the current player.
     * @param playerIndex The index of the player
     */
    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    /**
     * Sets the ID of the starter card.
     * @param starterCardId The ID of the starter card
     */
    public void setStarterCard(int starterCardId){
        this.starterCardId = starterCardId;
    }

    /**
     * Sets the nickname of a player identified by playerIndex.
     * @param playerIndex The index of the player
     * @param nickname The nickname to set
     */
    public void setNickname(int playerIndex, String nickname) {
        this.nicknamesMap.put(playerIndex, nickname);
    }

    /**
     * Sets the connection status of a player identified by playerIndex.
     * @param playerIndex The index of the player
     * @param isConnected The connection status to set
     */
    public void setConnection(int playerIndex, boolean isConnected){
        this.connessionMap.put(playerIndex, isConnected);
    }

    /**
     * Sets the color chosen by a player identified by playerIndex.
     * @param playerIndex The index of the player
     * @param color The color to set
     */
    public void setColor(int playerIndex, Color color){
        this.colorsMap.put(playerIndex, color);
    }

    // GAME UPDATES
    /**
     * Adds a player index to the list of winners.
     * @param winner The index of the winning player
     */
    public void addWinner(int winner){
        winners.add(winner);
    }

    /**
     * Sets the shared objectives for the game.
     * @param objectiveCardId1 ID of the first shared objective card
     * @param objectiveCardId2 ID of the second shared objective card
     */
    public void setSharedObjectives(int objectiveCardId1, int objectiveCardId2) {
        objectives[0] = objectiveCardId1;
        objectives[1] = objectiveCardId2;
    }

    /**
     * Sets the secret objectives for a player.
     * @param objectiveCardId1 ID of the first secret objective card
     * @param objectiveCardId2 ID of the second secret objective card
     */
    public void setSecretObjective(int objectiveCardId1, int objectiveCardId2){
        objectives[2] = objectiveCardId1;
        objectives[3] = objectiveCardId2;
    }

    /**
     * Initializes the element map for a player.
     * @param playerIndex The index of the player
     */
    public void initializeElementMap(int playerIndex){
        HashMap <Element, Integer> map = new HashMap<>();
        Arrays.stream(Element.values()).forEach(e-> {
            map.put(e, 0);
        });
        elementsMap.put(playerIndex, map);
    }

    /**
     * Sets the number of a specific element for a player.
     * @param playerIndex The index of the player
     * @param element The element to set
     * @param numberOfElements The number of elements to set
     */
    public void setElement(int playerIndex, Element element, int numberOfElements){
        System.out.println("ELEMENTSMAP");
        System.out.println(elementsMap.keySet());
        System.out.println(elementsMap.values());

        elementsMap.get(playerIndex).put(element, numberOfElements);
    }

    /**
     * Sets the turn phase of the game.
     * @param turnPhase The turn phase to set
     */
    public void setTurnPhase(TurnPhase turnPhase){
        this.turnPhase = turnPhase;
    }

    /**
     * Sets the game phase of the game.
     * @param gamePhase The game phase to set
     */
    public void setGamePhase(GamePhase gamePhase){
        this.gamePhase = gamePhase;
    }

    /**
     * Sets the index of the current player.
     * @param playerIndex The index of the current player
     */
    public void setCurrentPlayer(int playerIndex){
        currentPlayer = playerIndex;
    }

    /**
     * Sets the points of a player.
     * @param playerIndex The index of the player
     * @param points The points to set
     */
    public void setPoints(int playerIndex, int points){
        this.pointsMap.put(playerIndex, points);
    }

    /**
     * Sets the main board with shared resource and gold cards.
     * @param sharedGoldCard1 ID of the first shared gold card
     * @param sharedGoldCard2 ID of the second shared gold card
     * @param sharedResourceCard1 ID of the first shared resource card
     * @param sharedResourceCard2 ID of the second shared resource card
     * @param firtGoldDeckCard ID of the first gold deck card
     * @param firstResourceDeckCard ID of the first resource deck card
     */
    public void setMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) {
        mainBoard[0] = sharedResourceCard1;
        mainBoard[1] = sharedResourceCard2;
        mainBoard[2] = sharedGoldCard1;
        mainBoard[3] = sharedGoldCard2;
        mainBoard[4] = firstResourceDeckCard;
        mainBoard[5] = firtGoldDeckCard;
    }

    /**
     * Adds a card to the hand of a specified player.
     * If the player's hand does not exist yet, it initializes an empty hand.
     *
     * @param playerIndex The index of the player.
     * @param cardId The ID of the card to add to the hand.
     */
    public void addedCardToHand(int playerIndex, int cardId) {
        if (!handsMap.containsKey(playerIndex)){
            handsMap.put(playerIndex, new ArrayList<>());
        }
        handsMap.get(playerIndex).add(cardId);
    }

    /**
     * Removes a specified card from the hand of a player.
     * Also removes any side association of the card.
     *
     * @param playerIndex The index of the player.
     * @param cardId The ID of the card to remove from the hand.
     */
    public void removedCardFromHand(int playerIndex, int cardId){
        handsMap.get(playerIndex).remove(handsMap.get(playerIndex).indexOf(cardId));
        handsSideMap.remove(cardId);
    }

    /**
     * Updates the player's board by placing a card at a specific position.
     *
     * @param playerIndex The index of the player.
     * @param placingCardId The ID of the card being placed.
     * @param tableCardId The ID of the existing card on the table where 'placingCardId' is being placed.
     * @param existingCornerPos The position on the existing card where 'placingCardId' will be placed.
     * @param side The side of the card being placed.
     */
    public void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side){
        // for starter cards (initialization)
        if (!boardsMap.containsKey(playerIndex)){
            new IllegalStateException("Starter card not initialized for this player!").printStackTrace();
        }

        // for all the other placements
        else{
            placeCard(playerIndex, placingCardId, tableCardId, existingCornerPos);
        }

        // update placed card side
        placedSideMap.put(placingCardId, side);
    }

    /**
     * Sets the side (front or back) of a card in the player's hand.
     *
     * @param cardId The ID of the card.
     * @param side The side (front or back) to set for the card.
     */
    public void setHandSide(int cardId, Side side){
        handsSideMap.put(cardId, side);
    }

    /**
     * Sets the side (front or back) of a placed card on the table.
     *
     * @param cardId The ID of the card.
     * @param side The side (front or back) to set for the placed card.
     */
    public void setPlacedSide(int cardId, Side side){
        placedSideMap.put(cardId, side);
    }

    // GETTERS FOR CLI&GUI

    /**
     * Get the game winners
     * @return the winners ArrayList
     */
    public ArrayList<Integer> getWinners(){
        return new ArrayList<>(winners);
    }

    /**
     * Get the elements map
     * @return the elements map
     */
    public HashMap<Integer, HashMap> getElementsMap(){return elementsMap;}
    public int getPlayerIndex() {
        return playerIndex;
    }

    /**
     * Get the nickname of a specific player
     * @param playerIndex the index of the player
     * @return the nickname of the chosen player
     */
    public String getNickname(int playerIndex) {
        return nicknamesMap.get(playerIndex);
    }

    /**
     * Get the size of the players in game
     * @return the number of player in game
     */
    public int getPlayersSize(){
        return nicknamesMap.keySet().size();
    }

    /**
     * Get the objcetives of the player
     * @return the objectives list
     */
    public int[] getObjectives() {
        return objectives;
    }

    /**
     * Method to get a card by it's id
     * @param id the identifier of the card
     * @return the card with that id
     */
    public Card cardById(int id){
        if(id < 0){
            return null;
        }
        return cardsMap.get(id);
    }

    /**
     * Get the common objectives in the game
     * @return the common objective cards identifiers
     */
    public int[] getCommonObjectiveCards() {
        int[] commonObjectives = new int[2];
        commonObjectives[0] = objectives[0];
        commonObjectives[1] = objectives[1];
        return commonObjectives;
    }

    /**
     * Get the secret objectives in the game
     * @return the secret objective cards identifiers
     */
    public int[] getSecretObjectiveCards(){
        int[] secretObjectives = new int[2];
        secretObjectives[0] = objectives[2];
        secretObjectives[1] = objectives[3];
        return secretObjectives;
    }

    /**
     * Retrieves the current game phase.
     *
     * @return The current GamePhase enum value representing the game phase.
     */
    public GamePhase getGamePhase() {
        return gamePhase;
    }

    /**
     * Retrieves the current turn phase.
     *
     * @return The current TurnPhase enum value representing the turn phase.
     */
    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    /**
     * Retrieves the ID of the starter card.
     *
     * @return The ID of the starter card.
     */
    public int getStarterCard() {
        return starterCardId;
    }

    /**
     * Retrieves the hand of cards for a specified player.
     *
     * @param playerIndex The index of the player.
     * @return An ArrayList of integers representing the cards in the player's hand.
     */
    public ArrayList<Integer> getHand(int playerIndex){
        return handsMap.get(playerIndex);
    }

    /**
     * Retrieves the side (front or back) of a card in the player's hand.
     *
     * @param cardId The ID of the card.
     * @return The Side enum value representing the side of the card in the player's hand.
     */
    public Side getHandCardsSide(int cardId){
        return handsSideMap.get(cardId);
    }

    /**
     * Retrieves the side (front or back) of a placed card on the table.
     *
     * @param cardId The ID of the card.
     * @return The Side enum value representing the side of the placed card on the table.
     */
    public Side getPlacedCardSide(int cardId){
        return placedSideMap.get(cardId);
    }

    /**
     * Retrieves the player's board configuration.
     *
     * @param playerIndex The index of the player.
     * @return An ArrayList of ArrayLists of integers representing the player's board.
     */
    public ArrayList<ArrayList<Integer>> getPlayerBoard(int playerIndex){
        return boardsMap.get(playerIndex);
    }

    /**
     * Retrieves an array of integers representing the shared cards on the main board.
     *
     * @return An array of integers representing the shared cards on the main board.
     */
    public int[] getSharedCards() {
        return mainBoard;
    }

    /**
     * Retrieves an array of integers representing the shared resource cards on the main board.
     *
     * @return An array of integers representing the shared resource cards on the main board.
     */
    public int[] getSharedResourceCards() {
        return new int[] { mainBoard[0], mainBoard[1] };
    }

    /**
     * Retrieves an array of integers representing the shared gold cards on the main board.
     *
     * @return An array of integers representing the shared gold cards on the main board.
     */
    public int[] getSharedGoldCards() {
        return new int[] { mainBoard[2], mainBoard[3] };
    }

    /**
     * Checks if the player at the specified index is connected (online or offline).
     *
     * @param playerIndex The index of the player.
     * @return true if the player is connected, false otherwise.
     */
    public boolean getConnession(int playerIndex) {
        return connessionMap.get(playerIndex);
    }

    /**
     * Retrieves the color associated with the player at the specified index.
     *
     * @param playerIndex The index of the player.
     * @return The Color object representing the player's color.
     */
    public Color getColorsMap(int playerIndex) {
        return colorsMap.get(playerIndex);
    }

    /**
     * Retrieves the points scored by the player at the specified index.
     *
     * @param playerIndex The index of the player.
     * @return The total points scored by the player.
     */
    public int getPointsMap(int playerIndex) {
        return pointsMap.get(playerIndex);
    }

    /**
     * Retrieves the index of the current player whose turn it is.
     *
     * @return The index of the current player.
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Verify if the first card is on top or under the second one
     * @param card1 Card that needs to be verified
     * @param card2 Referement card on table
     * @return True if the first card is on top, else false
     */
    public boolean isCardOnTop(int playerId, int card1, int card2){
        return placingCardOrderMap.get(playerId).indexOf(card1) > placingCardOrderMap.get(playerId).indexOf(card2);
    }

    /**
     * Retrieves the placing card order map for the specified player.
     *
     * @param playerIndex The index of the player.
     * @return An ArrayList of integers representing the placing card order for the player.
     */
    public ArrayList<Integer> getPlacingCardOrderMap(int playerIndex) {
        return new ArrayList<>(this.placingCardOrderMap.get(playerIndex));
    }

    // UTILS METHOD
    /**
     * Initializes the board for the specified player with a placing card.
     *
     * @param playerIndex The index of the player.
     * @param placingCardId The ID of the placing card to initialize the board.
     */
    public void initializeBoard(int playerIndex, int placingCardId){
        this.boardsMap.put(playerIndex, new ArrayList<>());
        this.boardsMap.get(playerIndex).clear();
        ArrayList<Integer> row = new ArrayList<>();
        row.add(placingCardId);
        this.boardsMap.get(playerIndex).add(row);

        placingCardOrderMap.put(playerIndex, new ArrayList<>());
        placingCardOrderMap.get(playerIndex).addLast(placingCardId);
    }

    /**
     * Places a card on the player's board at the specified position.
     *
     * @param playerIndex The index of the player.
     * @param placingCardId The ID of the card to place.
     * @param tableCardId The ID of the card already on the table where placingCardId will be placed.
     * @param tableCornerPos The position (corner) where the placingCardId will be placed relative to tableCardId.
     */
    private void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos){
        ArrayList<ArrayList<Integer>> playerBoard = this.boardsMap.get(playerIndex);

        int rowIndex = -1;
        int colIndex = -1;

        // Find the coordinates of the existing card on the board
        for (int i = 0; i < playerBoard.size(); i++) {
            ArrayList<Integer> row = playerBoard.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == tableCardId) {
                    rowIndex = i;
                    colIndex = j;
                } else if (row.get(j) == placingCardId) {
                    // la carta è già piazzata, esci subito
                    return;
                }
            }
        }

        if (rowIndex == -1 && colIndex == -1) {
            // non ha trovato la carta, magari un'eccezione?
        }

        // Define the position of the new card
        switch(tableCornerPos) {
            case CornerPos.UPLEFT:
                rowIndex--;
                break;
            case CornerPos.UPRIGHT:
                colIndex++;
                break;
            case CornerPos.DOWNRIGHT:
                rowIndex++;
                break;
            case CornerPos.DOWNLEFT:
                colIndex--;
                break;
        }

        // Check if the new position is outside the bounds of the current matrix
        if (rowIndex < 0 || rowIndex >= playerBoard.size() || colIndex < 0 || colIndex >= playerBoard.getFirst().size()) {
            // Expand the matrix if necessary
            expandBoard(rowIndex, colIndex, playerBoard);
            if (rowIndex < 0){
                rowIndex ++;
            } else if (colIndex < 0){
                colIndex ++;
            }
        }
        // Place the new card at the specified position
        playerBoard.get(rowIndex).set(colIndex, placingCardId);

        placingCardOrderMap.get(playerIndex).addLast(placingCardId);
    }

    /**
     * Fills the points map with initial values for all players.
     */
    public void fillPoints(){
        for (int i = 0; i < nicknamesMap.size(); i++){
            pointsMap.put(i, 0);
        }
    }

    /**
     * Expands the player's board matrix to accommodate a new card position.
     *
     * @param rowIndex The index of the row where expansion is needed.
     * @param colIndex The index of the column where expansion is needed.
     * @param playerBoard The player's board matrix to expand.
     */
    private void expandBoard(int rowIndex, int colIndex, ArrayList<ArrayList<Integer>> playerBoard){
        // Expand the matrix to include the new position (remember that this supports only one execute: row++/-- or col++/--)
        // Expand rows if needed
        int rowDim = playerBoard.getFirst().size();
        if (rowIndex < 0) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int i = 0; i < rowDim; i++) {
                newRow.add(-1);
            }
            playerBoard.addFirst(newRow);
        } else if (rowIndex >= playerBoard.size()) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int i = 0; i < rowDim; i++) {
                newRow.add(-1);
            }
            playerBoard.add(newRow);
        }
        // Expand columns if needed
        for (ArrayList<Integer> row : playerBoard) {
            if (colIndex < 0) {
                row.addFirst(-1); // Adding -1 if there is no card
            } else if (colIndex >= row.size()) {
                row.add(-1); // Placeholder for empty cell
            }
        }
    }

    //TODO: guardare se servono le modifiche di Global
    /**
     * Populates the cards map from the JSON file.
     */
    private void populateCardsMap() {
        String jsonString = null;
        try {
            jsonString = readJSON(Config.CARD_JSON_PATH);
        } catch (IOException e) {
            System.out.println("Error while loading JSON, please try again");
            return;
        }
        Gson gson = new Gson();
        Map<String, ?> cards = gson.fromJson(jsonString, Map.class);
        // Your code to use the loaded cards map


        for (Object key : cards.keySet()){
            Map card = gson.fromJson(cards.get(key).toString(), Map.class);
            int id = Integer.parseInt(key.toString());
            // ------ creating challenges ------
            Challenge challenge = createChallenge(card);

            if (card.get("Type").equals("Objective")){
                if (id < Global.firstObjectiveCardId) Global.firstObjectiveCardId = id;
                cardsMap.put(id, new ObjectiveCard(id, challenge, (int) Double.parseDouble(card.get("Points").toString())));
            } else {

                // ------ creating corners ------
                Corner[] frontCorners = createCorners("FrontCorners", card, id);
                Corner[] backCorners = createCorners("BackCorners", card, id);


                // ------ creating cards ------
                if (card.get("Type").equals("Resource")){
                    if (id < Global.firstResourceCardId) Global.firstResourceCardId = id;
                    ResourceCard resourceCard = new ResourceCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), (int) Double.parseDouble(card.get("Points").toString()), frontCorners, backCorners);
                    cardsMap.put(id, resourceCard);
                } else if (card.get("Type").equals("Gold")){
                    if (id < Global.firstGoldCardId) Global.firstGoldCardId = id;
                    ArrayList<Element> elements = new ArrayList<>();
                    for (Object e : (ArrayList) card.get("ResourceNeeded")){
                        elements.add(Element.valueOf(e.toString().toUpperCase()));
                    }
                    GoldCard goldCard = new GoldCard(id, Element.valueOf(card.get("ResourceType").toString().toUpperCase()), challenge, elements, (int) Double.parseDouble(card.get("Points").toString()) , frontCorners, backCorners);
                    cardsMap.put(id, goldCard);
                } else if (card.get("Type").equals("Starter")){
                    if (id < Global.firstStarterCardId) Global.firstStarterCardId = id;
                    ArrayList<Element> elements = new ArrayList<>();
                    for (Object e : (ArrayList) card.get("CenterResources")){
                        elements.add(Element.valueOf(e.toString().toUpperCase()));
                    }
                    cardsMap.put(id, new StarterCard(id, frontCorners, backCorners, elements));
                }
            }
        }
    }

    /**
     * Creates a challenge based on the card details.
     *
     * @param card The card details in Map form.
     * @return The created Challenge object.
     */
    private static Challenge createChallenge(Map card) {
        Challenge challenge = null;
        if (card.get("Type").equals("Objective") || card.get("Type").equals("Gold")){
            if (card.get("ChallengeType").equals("ElementChallenge")){
                ArrayList<Element> elements = new ArrayList<>();
                for (Object e : (ArrayList) card.get("ChallengeElements")){
                    elements.add(Element.valueOf(e.toString().toUpperCase()));
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
     * Creates an array of corners based on the card details.
     *
     * @param side The side of corners (FrontCorners or BackCorners).
     * @param card The card details in Map form.
     * @param id The ID of the card.
     * @return An array of Corner objects.
     */
    private static Corner[] createCorners(String side, Map card, Integer id){
        Corner[] corners = new Corner[Config.N_CORNERS];
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            String element = ((ArrayList) card.get(side)).get(i).toString();
            // card.get("FrontCorners")).getClass() returns ArrayList so casting should be good
            if (element.equals("Empty")){
                //probably can be removed because it falls in the else clause, also in line 135
                corners[i] = new Corner(Element.EMPTY, id, false);
            } else if(element.equals("Hidden")) {
                corners[i] = new Corner(Element.EMPTY, id, true);
            } else {
                corners[i] = new Corner(Element.valueOf(element.toUpperCase()), id, false);
            }
        }
        return corners;
    }

    /**
     * Sets a new message in the chat for the specified player.
     *
     * @param playerIndex The index of the player.
     * @param content The content of the message.
     */
    public void setNewMessage(int playerIndex, String content) {
        chat.addMessage(playerIndex, content);
        newMessages+=1;
    }

    /**
     * Retrieves the number of new messages.
     *
     * @return The number of new messages.
     */
    public int getNewMessages() {
        return newMessages;
    }

    /**
     * Resets the count of new messages to zero.
     */
    public void resetNewMessages(){
        newMessages = 0;
    }

    /**
     * Retrieves the chat.
     *
     * @return The entire Chat object.
     */
    public Chat getChat(){
        return chat;
    }
}