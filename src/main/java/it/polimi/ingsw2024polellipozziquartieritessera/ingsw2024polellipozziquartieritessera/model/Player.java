package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.*;

/**
 * Player class
 */
public class Player {
    /**
     * Player nickname
     */
    private final String nickname;
    /**
     * Player board, structured like a variable matrix of card IDs
     */
    private ArrayList<ArrayList<Integer>> playerBoard;
    /**
     * The map of all placed cards, that relate a card ID with his played side
     */
    private HashMap<Integer, Side> placedCardsMap;
    /**
     * The map of all cards in hand, that relate a card ID with his hand side
     */
    private HashMap<Integer, Side> handCardsMap;
    /**
     * Color of the player
     */
    private Color color;
    /**
     * Total points gained by the player
     */
    private int points;
    /**
     * Number of won objective challenges
     */
    private int objectivesWon;
    /**
     * Starting card for the board, essential to relate all the cards in board
     */
    private StarterCard starterCard;
    /**
     * Secret objective card, unique for each player
     */
    private ObjectiveCard objectiveCard;
    /**
     * List of the selectable objective cards
     */
    //TODO: cambiare in arrayList magari, ricordarsi di cambiare poi setObj, populate e costruttore, e restoreVie
    private ObjectiveCard[] objectiveCardOptions;
    /**
     * Map of the centerResources of the player cards, relates a card ID with the center Resource
     */
    private HashMap<Integer, Element> centerResources;
    /**
     * Map of all elements the player currently have, each Element is associated with the number of his occurrences
     */
    private HashMap<Element, Integer> allElements;
    /**
     * True if the player is connected (false if disconnected)
     */
    private boolean connected;
    /**
     * Virtual view associated to the client
     */
    private VirtualView client;
    /**
     * Instance of the GameState, needed to make the player aware of the GameState he is in
     */
    private final GameState gameState;

    /**
     * Player Constructor
     * @param nickname Player nickname string
     * @param client Virtual view that this player is linked to
     * @param gameState Instance of the GameState the player is in
     */
    public Player(String nickname, VirtualView client, GameState gameState){
        this.gameState = gameState;

        this.points = 0;
        this.nickname = nickname;
        this.client = client;

        this.color = null;
        this.objectiveCard = null;
        this.starterCard = null;
        this.playerBoard = new ArrayList<>();
        this.placedCardsMap = new HashMap<Integer, Side>();
        this.handCardsMap = new HashMap<Integer, Side>();
        this.objectivesWon = 0;
        this.centerResources = new HashMap<>();

        this.objectiveCardOptions = new ObjectiveCard[2];
        this.objectiveCardOptions[0] = null;
        this.objectiveCardOptions[1] = null;

        this.allElements = new HashMap<>();
        for (Element element : Element.values()) {
            this.allElements.put(element, 0);
        }

        this.connected = true;
    }

    //GETTER


    public String getNickname() {
        return this.nickname;
    }

    public Color getColor() {
        return this.color;
    }

    public int getPoints() {
        return this.points;
    }

    public StarterCard getStarterCard() {
        return this.starterCard;
    }

    public ObjectiveCard getObjectiveCard() {
        return this.objectiveCard;
    }

    public Side getPlacedCardSide(Integer index){
        return this.placedCardsMap.get(index);
    }

    public Side getHandCardSide(Integer index){
        return this.handCardsMap.get(index);
    }

    public int getObjectivesWon(){
        return this.objectivesWon;
    }

    public ObjectiveCard getObjectiveCardOption(int index) {
        return this.objectiveCardOptions[index];
    }

    public Element getCenterResource(int index) {
        return centerResources.get(index);
    }

    public VirtualView getClient(){
        return this.client;
    }

    public boolean isConnected(){
        return this.connected;
    }

    //--------size------------

    public int getHandSize(){
        return this.handCardsMap.size();
    }


    //-----------collections copy----------------

    public HashMap<Element, Integer> getAllElements() {
        return new HashMap<>(allElements);
    }

    public ArrayList<ArrayList<Integer>> getPlayerBoard() {
        return new ArrayList<>(this.playerBoard);
    }


    public HashMap<Integer, Side> getPlacedCardsMap() {
        return new HashMap<>(placedCardsMap);
    }

    public HashMap<Integer, Side> getHandCardsMap() {
        return new HashMap<>(handCardsMap);
    }

    public ObjectiveCard[] getObjectiveCardOptions() {
        if (objectiveCardOptions == null) {
            return null;
        }
        ObjectiveCard[] copy = new ObjectiveCard[objectiveCardOptions.length];
        System.arraycopy(objectiveCardOptions, 0, copy, 0, objectiveCardOptions.length);
        return copy;
    }

    public HashMap<Integer, Element> getCenterResources() {
        return new HashMap<>(centerResources);
    }

    //--------------contains-------------

    public boolean placedCardContains(Integer index){
        return this.placedCardsMap.containsKey(index);
    }


    public boolean handCardContains(Integer index){
        return this.handCardsMap.containsKey(index);
    }


    //SETTER

    public void setClient(VirtualView client){
        this.client = client;
    }

    public void setObjectiveCard(ObjectiveCard objectiveCard) {
        this.objectiveCard = objectiveCard;
        synchronized (gameState.getEventQueue()){
            //RIGUARDARE SE VA BENE PASSARE DA ARRAY A ARRAYLIST, NON E TANTO BELLO
            gameState.addToEventQueue(new UpdateSecretObjectiveEvent(gameState, gameState.singleClient(this.getClient()), new ArrayList<>(List.of(objectiveCard))));
            gameState.getEventQueue().notifyAll();
        }

        // set options to null in order to manage reconnections
        objectiveCardOptions[0] = null;
        objectiveCardOptions[1] = null;
    }

    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.placedCardsMap.put(this.getStarterCard().getId(), null); // also set the default side to FRONT todo: changed side to null because of restore-view needs it, it will crash? we will see
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateStarterCardEvent(gameState, gameState.singleClient(this.getClient()), gameState.getPlayerIndex(this), starterCard.getId(), null));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void setSecretObjectiveCardOptions(ObjectiveCard[] objectiveCards) {
        this.objectiveCardOptions = objectiveCards;
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateSecretObjectiveEvent(gameState, gameState.singleClient(this.getClient()), new ArrayList<>(List.of(objectiveCards))));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void setColor(Color color){
        this.color = color;
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateColorEvent(gameState, gameState.allConnectedClients(), this, color));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void setConnected(boolean connected){
        this.connected = connected;
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new ConnectionInfoEvent(gameState, gameState.allClients(), this, connected));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void setPlayerBoard(ArrayList<ArrayList<Integer>> playerBoard) {
        this.playerBoard = new ArrayList<>(playerBoard);
    }

    public void setPlacedCardsMap(HashMap<Integer, Side> placedCardsMap) {
        this.placedCardsMap = placedCardsMap;
    }

    public void setHandCardsMap(HashMap<Integer, Side> handCardsMap){
        this.handCardsMap = handCardsMap;
    }

    public void setObjectivesWon(int objectivesWon){
        this.objectivesWon = objectivesWon;
    }

    public void setCenterResources(HashMap<Integer, Element> centerResource){
        this.centerResources = centerResource;
    }

    public void setAllElements(HashMap<Element, Integer> allElements){
        this.allElements = allElements;
    }

    //ADDER/REMOVER

    public void incrementObjectivesWon(){
        this.objectivesWon ++;
    }

    public void addToPlacedCardsMap(Integer index, Side side){
        this.placedCardsMap.put(index, side);
    }

    public void addToHandCardsMap(Integer index, Side side) throws InvalidHandException {
        if (handCardsMap.keySet().size() >= Config.MAX_HAND_CARDS){
            throw new InvalidHandException("Player " + this.getNickname() + " has too many cards in hand");
        }
        this.handCardsMap.put(index, side);
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateAddHandEvent(gameState, gameState.allConnectedClients(), this, index));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void removeFromHandCardsMap(Integer index){
        System.out.println("Removing from hand card " + index);
        this.handCardsMap.remove(index);
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateRemoveHandEvent(gameState, gameState.allConnectedClients(), this, index));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void addPoints(int points) {
        this.points += points;
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdatePointsEvent(gameState, gameState.allConnectedClients(), this, this.points));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void removeFromAllElements(Element cornerEle){
        this.allElements.replace(cornerEle, allElements.get(cornerEle) - 1);
    }

    public void addToAllElements(Element element, int number) {
        this.allElements.put(element, number);
    }


    //--------------MODIFIER/UPDATER--------------------

    public void changeHandSide(Integer index, Side side){
        this.handCardsMap.replace(index, side);
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateHandSideEvent(gameState, gameState.singleClient(this.getClient()), index, side));
            gameState.getEventQueue().notifyAll();
        }
    }

    // METHODS

    /**
     * Get the Side of a specific played card on the player board
     * @param cardId Card identifier
     * @return Side of the placed card
     */
    public Side getBoardSide(int cardId) {
        return this.placedCardsMap.get(cardId);
    }

// -------------------Place Cards Map Managing-----------------

    /**
     * Place the card on the player board
     * @param placingCardId ID of the card to place
     * @param placingCard Card to be placed
     * @param tableCard Card on the board
     * @param tableCardId ID of the card on the board
     * @param tableCornerPos Corner position of the board card to which link the placing card
     * @param placingCardSide Side of placing of the placing card
     * @throws WrongInstanceTypeException Card not placeable
     */
    public void updatePlayerCardsMap(int placingCardId, CornerCard placingCard, CornerCard tableCard, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongInstanceTypeException {
        addToPlacedCardsMap(placingCardId, placingCardSide);
        removeFromHandCardsMap(placingCardId);
        updateBoard(placingCardId, tableCardId, tableCornerPos);
        this.centerResources.put(placingCardId, placingCard.getResourceType());
        synchronized (gameState.getEventQueue()){
            Event event = new UpdateBoardEvent(gameState, gameState.allConnectedClients(), this, placingCardId, tableCardId, tableCornerPos, placingCardSide);
            gameState.addToEventQueue(event);
            gameState.getEventQueue().notifyAll();
            UpdateBoardEvent backupEvent = new UpdateBoardEvent(gameState, new ArrayList<>(), this, placingCardId, tableCardId, tableCornerPos, placingCardSide);
            gameState.addPlacedEvent(backupEvent);
        }
    }

    /**
     * Get occurrences of a specific element inside a List of elements
     * @param elements List of elements
     * @param targetElement Element to verify
     * @return Number of occurrences
     */
    public static int getElementOccurrencies(ArrayList<Element> elements, Element targetElement) {
        int count = 0;
        for (Object element : elements) {
            if (element != null && element.equals(targetElement)) {
                count++;
            }
        }
        return count;
    }

// -------------------Board Matrix Managing-----------------------

    /**
     * Initialization of the player board, as a 1x1 with the StarterCard in the center
     */
    public void initializeBoard(){
        this.playerBoard.clear(); // re-initialize the board it is previously contained something
        ArrayList<Integer> row = new ArrayList<>();
        row.add(getStarterCard().getId());
        this.playerBoard.add(row);
    }

    /**
     * Update the board after a card is placed
     * @param placingCardId ID of the placed card
     * @param tableCardId ID of the card on the player board
     * @param tableCornerPos Corner position of the board card to which the new card is linked
     */
    public synchronized void updateBoard(int placingCardId, int tableCardId, CornerPos tableCornerPos) {
        int rowIndex = -1;
        int colIndex = -1;

        // Find the coordinates of the existing card on the board
        for (int i = 0; i < this.playerBoard.size(); i++) {
            ArrayList<Integer> row = this.playerBoard.get(i);
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
        switch (tableCornerPos) {
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
        if (rowIndex < 0 || rowIndex >= this.playerBoard.size() || colIndex < 0 || colIndex >= this.playerBoard.getFirst().size()) {
            // Expand the matrix if necessary
            expandBoard(rowIndex, colIndex);
            if (rowIndex < 0) {
                rowIndex++;
            } else if (colIndex < 0) {
                colIndex++;
            }
        }

        //******
        //TODO: remove (only testing)
        // Check if rowIndex is within bounds
        if (rowIndex < 0 || rowIndex >= playerBoard.size()) {
            System.out.println("ROW");
            printPlayerBoard();
            throw new IllegalArgumentException("Invalid row index: " + rowIndex);
        }

        // Check if colIndex is within bounds
        if (colIndex < 0 || colIndex >= playerBoard.get(rowIndex).size()) {
            System.out.println("COL");
            printPlayerBoard();
            throw new IllegalArgumentException("Invalid column index: " + colIndex);
        }
        //******


        // Place the new card at the specified position
        this.playerBoard.get(rowIndex).set(colIndex, placingCardId);
    }

    public void printPlayerBoard() {
        for (ArrayList<Integer> row : this.playerBoard) {
            for (int col : row) {
                System.out.printf("%4d", col); // Adjust the width as needed
            }
            System.out.println();
        }
    }

    /**
     * Helper function to expand the board size if needed
     * @param rowIndex Final rows number
     * @param colIndex Final column number
     */
    synchronized private void expandBoard(int rowIndex, int colIndex){
        // Expand the matrix to include the new position (remember that this supports only one execute: row++/-- or col++/--)
        // Expand rows if needed
        int rowDim = this.playerBoard.getFirst().size();
        if (rowIndex < 0) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int i = 0; i < rowDim; i++) {
                newRow.add(-1);
            }
            this.playerBoard.addFirst(newRow);
        } else if (rowIndex >= this.playerBoard.size()) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int i = 0; i < rowDim; i++) {
                newRow.add(-1);
            }
            this.playerBoard.add(newRow);
        }
        // Expand columns if needed
        for (ArrayList<Integer> row : this.playerBoard) {
            if (colIndex < 0) {
                row.add(0, -1); // Adding -1 if there is no card
            } else if (colIndex >= row.size()) {
                row.add(-1); // Placeholder for empty cell
            }
        }
    }
}