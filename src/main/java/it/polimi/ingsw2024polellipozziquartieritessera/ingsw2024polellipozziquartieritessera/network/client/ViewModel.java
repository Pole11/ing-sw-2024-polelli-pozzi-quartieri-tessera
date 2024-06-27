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

public class ViewModel {
    private int playerIndex;
    private final HashMap<Integer,String> nicknamesMap;
    private final HashMap<Integer, Boolean> connessionMap;
    private final HashMap<Integer, Color> colorsMap;
    private final HashMap<Integer, Integer> pointsMap;
    private final HashMap<Integer, HashMap> elementsMap;
    private final Chat chat;
    private int starterCardId;
    private final int[] objectives; // 0,1 are common - 2,3 are secret (2 is the chosen one) // if the card is not set it is -1
    private GamePhase gamePhase;
    private TurnPhase turnPhase;
    private int currentPlayer;
    private final HashMap<Integer, Card> cardsMap;
    private final HashMap<Integer,ArrayList<Integer>> handsMap; // mappa delle hands dei player
    private final HashMap<Integer,Side> placedSideMap; // side delle carte sulla board (unico per id)
    private final HashMap<Integer,Side> handsSideMap; // side delle carte in mano (unico per id)
    private final HashMap<Integer,ArrayList<ArrayList<Integer>>> boardsMap; // mappa delle board dei players
    private int[] mainBoard; // se la carta non c'è viene inizializzata a -1
    private HashMap<Integer,ArrayList<Integer>> placingCardOrderMap;
    private int newMessages; // only for cli
    private ArrayList<Integer> winners;

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
    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }
    public void setStarterCard(int starterCardId){
        this.starterCardId = starterCardId;
    }

    public void setNickname(int playerIndex, String nickname) {
        this.nicknamesMap.put(playerIndex, nickname);
    }

    public void setConnection(int playerIndex, boolean isConnected){
        this.connessionMap.put(playerIndex, isConnected);
    }

    public void setColor(int playerIndex, Color color){
        this.colorsMap.put(playerIndex, color);
    }

    // GAME UPDATES
    public void addWinner(int winner){
        winners.add(winner);
    }


    public void setSharedObjectives(int objectiveCardId1, int objectiveCardId2) {
        objectives[0] = objectiveCardId1;
        objectives[1] = objectiveCardId2;
    }
    public void setSecretObjective(int objectiveCardId1, int objectiveCardId2){
        objectives[2] = objectiveCardId1;
        objectives[3] = objectiveCardId2;
    }

    public void initializeElementMap(int playerIndex){
        if (elementsMap.get(playerIndex) != null){
            return;
        }
        HashMap <Element, Integer> map = new HashMap<>();
        Arrays.stream(Element.values()).forEach(e-> {
            map.put(e, 0);
        });
        elementsMap.put(playerIndex, map);
    }

    public void setElement(int playerIndex, Element element, int numberOfElements){
        if (elementsMap.get(playerIndex) == null){
            initializeElementMap(playerIndex);
        }
        elementsMap.get(playerIndex).put(element, numberOfElements);
    }

    public void setTurnPhase(TurnPhase turnPhase){
        this.turnPhase = turnPhase;
    }

    public void setGamePhase(GamePhase gamePhase){
        this.gamePhase = gamePhase;
    }
    public void setCurrentPlayer(int playerIndex){
        currentPlayer = playerIndex;
    }

    public void setPoints(int playerIndex, int points){
        this.pointsMap.put(playerIndex, points);
    }

    // position are: resource [0,1], gold [2,3], firstResource[4] firstGold[5]
    public void setMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firtGoldDeckCard, int firstResourceDeckCard) {
        mainBoard[0] = sharedResourceCard1;
        mainBoard[1] = sharedResourceCard2;
        mainBoard[2] = sharedGoldCard1;
        mainBoard[3] = sharedGoldCard2;
        mainBoard[4] = firstResourceDeckCard;
        mainBoard[5] = firtGoldDeckCard;
    }


    public void addedCardToHand(int playerIndex, int cardId) {
        if (!handsMap.containsKey(playerIndex)){
            handsMap.put(playerIndex, new ArrayList<>());
        }
        handsMap.get(playerIndex).add(cardId);
    }

    public void removedCardFromHand(int playerIndex, int cardId){
        handsMap.get(playerIndex).remove(handsMap.get(playerIndex).indexOf(cardId));
        handsSideMap.remove(cardId);
    }

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

    public void setHandSide(int cardId, Side side){
        handsSideMap.put(cardId, side);
    }
    public void setPlacedSide(int cardId, Side side){
        placedSideMap.put(cardId, side);
    }

    // GETTERS FOR CLI&GUI
    public ArrayList<Integer> getWinners(){
        return new ArrayList<>(winners);
    }

    public HashMap<Integer, HashMap> getElementsMap(){return elementsMap;}
    public int getPlayerIndex() {
        return playerIndex;
    }

    public String getNickname(int playerIndex) {
        return nicknamesMap.get(playerIndex);
    }

    public int getPlayersSize(){
        return nicknamesMap.keySet().size();
    }

    public int[] getObjectives() {
        return objectives;
    }

    public Card cardById(int id){
        if(id < 0){
            return null;
        }
        return cardsMap.get(id);
    }

    public int[] getCommonObjectiveCards() {
        int[] commonObjectives = new int[2];
        commonObjectives[0] = objectives[0];
        commonObjectives[1] = objectives[1];
        return commonObjectives;
    }

    public int[] getSecretObjectiveCards(){
        int[] secretObjectives = new int[2];
        secretObjectives[0] = objectives[2];
        secretObjectives[1] = objectives[3];
        return secretObjectives;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public TurnPhase getTurnPhase() {
        return turnPhase;
    }

    public int getStarterCard() {
        return starterCardId;
    }

    public ArrayList<Integer> getHand(int playerIndex){
        return handsMap.get(playerIndex);
    }

    public Side getHandCardsSide(int cardId){
        return handsSideMap.get(cardId);
    }

    public Side getPlacedCardSide(int cardId){
        return placedSideMap.get(cardId);
    }

    public ArrayList<ArrayList<Integer>> getPlayerBoard(int playerIndex){
        return boardsMap.get(playerIndex);
    }

    public int[] getSharedCards() {
        return mainBoard;
    }

    public int[] getSharedResourceCards() {
        return new int[] { mainBoard[0], mainBoard[1] };
    }

    public int[] getSharedGoldCards() {
        return new int[] { mainBoard[2], mainBoard[3] };
    }

    public boolean getConnession(int playerIndex) {
        return connessionMap.get(playerIndex);
    }

    public Color getColorsMap(int playerIndex) {
        return colorsMap.get(playerIndex);
    }

    public int getPointsMap(int playerIndex) {
        return pointsMap.get(playerIndex);
    }

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

    public ArrayList<Integer> getPlacingCardOrderMap(int playerIndex) {
        return new ArrayList<>(this.placingCardOrderMap.get(playerIndex));
    }

    // UTILS METHOD
    public void initializeBoard(int playerIndex, int placingCardId){
        this.boardsMap.put(playerIndex, new ArrayList<>());
        this.boardsMap.get(playerIndex).clear();
        ArrayList<Integer> row = new ArrayList<>();
        row.add(placingCardId);
        this.boardsMap.get(playerIndex).add(row);

        placingCardOrderMap.put(playerIndex, new ArrayList<>());
        placingCardOrderMap.get(playerIndex).addLast(placingCardId);
    }

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
    public void fillPoints(){
        for (int i = 0; i < nicknamesMap.size(); i++){
            pointsMap.put(i, 0);
        }
    }

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
    private void populateCardsMap() {
        String jsonString = null;
        try {
            jsonString = readJSON(Config.CARD_JSON_PATH);
        } catch (IOException e) {
            System.err.println("Error while loading JSON, please try again");
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

    public void setNewMessage(int playerIndex, String content) {
        chat.addMessage(playerIndex, content);
        newMessages+=1;
    }

    public int getNewMessages() {
        return newMessages;
    }

    public void resetNewMessages(){
        newMessages = 0;
    }

    public Chat getChat(){
        return chat;
    }
}