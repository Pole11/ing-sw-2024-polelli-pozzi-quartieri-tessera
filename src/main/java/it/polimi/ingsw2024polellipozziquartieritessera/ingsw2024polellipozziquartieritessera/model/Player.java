package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.ConnectionInfoEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateAddHandEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateBoardEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateSecretObjectiveEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.*;

public class Player {
    private final String nickname;
    private final ArrayList<ArrayList<Integer>> playerBoard;
    private final HashMap<Integer, Side> placedCardsMap;
    private final HashMap<Integer, Side> handCardsMap;
    private Color color;

    private int points;
    private int objectivesWon;
    private StarterCard starterCard; // it is the most important card because it is used to create all the composition of the cards
    private ObjectiveCard objectiveCard; // it is the secret objective
    private ObjectiveCard[] objectiveCardOptions; // it is the  array of the choice for secret objective

    private final HashMap<Integer, Element> centerResource;
    private final HashMap<Element, Integer> allElements;

    private boolean connected;
    private final VirtualView client;
    private final GameState gameState;

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
        this.centerResource = new HashMap<>();

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
        return centerResource.get(index);
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

    //--------------contains-------------

    public boolean placedCardContains(Integer index){
        return this.placedCardsMap.containsKey(index);
    }


    public boolean handCardContains(Integer index){
        return this.handCardsMap.containsKey(index);
    }


    //SETTER

    public void setObjectiveCard(ObjectiveCard objectiveCard) {
        this.objectiveCard = objectiveCard;
        synchronized (gameState.getEventQueue()){
            //RIGUARDARE SE VA BENE PASSARE DA ARRAY A ARRAYLIST, NON E TANTO BELLO
            gameState.addToEventQueue(new UpdateSecretObjectiveEvent(gameState, gameState.singleClient(this.getClient()), new ArrayList<>(List.of(objectiveCard))));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.placedCardsMap.put(this.getStarterCard().getId(), Side.FRONT); // also set the default side to FRONT
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateStarterCardEvent(gameState, gameState.allClients(), starterCard.getId()));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void setSecretObjectiveCardOptions(ObjectiveCard[] objectiveCards) {
        this.objectiveCardOptions = objectiveCards;
        synchronized (gameState.getEventQueue()){
            //RIGUARDARE SE VA BENE PASSARE DA ARRAY A ARRAYLIST, NON E TANTO BELLO
            gameState.addToEventQueue(new UpdateSecretObjectiveEvent(gameState, gameState.singleClient(this.getClient()), new ArrayList<>(List.of(objectiveCards))));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void setColor(Color color){
        this.color = color;
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateColorEvent(gameState, gameState.allClients(), this, color));
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

    //ADDER/REMOVER

    public void incrementObjectivesWon(){
        this.objectivesWon ++;
    }

    public void addToPlacedCardsMap(Integer index, Side side){
        this.placedCardsMap.put(index, side);
    }

    public void addToHandCardsMap(Integer index, Side side){
        this.handCardsMap.put(index, side);
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateAddHandEvent(gameState, gameState.allClients(), this, index, side));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void removeFromHandCardsMap(Integer index){
        this.handCardsMap.remove(index);
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateRemoveHandEvent(gameState, gameState.allClients(), this, index));
            gameState.getEventQueue().notifyAll();
        }
    }

    public void addPoints(int points) {
        this.points += points;
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdatePointsEvent(gameState, gameState.allClients(), this, points));
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
            gameState.addToEventQueue(new UpdateHandSide(gameState, gameState.singleClient(this.getClient()), index, side));
        }
    }

    // METHODS

    //usato dal controller, da cambiare nome, poco esplicativo (plurale)
    public Side getBoardSide(int cardId) {
        return this.placedCardsMap.get(cardId);
    }

// -------------------Place Cards Map Managing-----------------

    public void updatePlayerCardsMap(int placingCardId, CornerCard placingCard, CornerCard tableCard, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongInstanceTypeException {
        addToPlacedCardsMap(placingCardId, placingCardSide);
        removeFromHandCardsMap(placingCardId);

        this.centerResource.put(placingCardId, placingCard.getResourceType());
    }

    public static int getElementOccurencies(ArrayList<Element> elements, Element targetElement) {
        int count = 0;
        for (Object element : elements) {
            if (element != null && element.equals(targetElement)) {
                count++;
            }
        }
        return count;
    }

// -------------------Board Matrix Managing-----------------------

    public void initializeBoard(){
        this.playerBoard.clear(); // re-initialize the board it is previously contained something
        // Initialization of player board as a 1x1 with the StarterCard in the center
        ArrayList<Integer> row = new ArrayList<>();
        row.add(getStarterCard().getId());
        this.playerBoard.add(row);
    }

    public void printBoard() {
        for (int i = 0; i < this.playerBoard.size(); i++) {
            ArrayList<Integer> row = this.playerBoard.get(i);
            for (int j = 0; j < row.size(); j++) {
                System.out.print(this.playerBoard.get(i).get(j) + " ");
            }
            System.out.println("");
        }
    }

    public void updateBoard(int placingCardId, int tableCardId, CornerPos tableCornerPos){
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
        if (rowIndex < 0 || rowIndex >= this.playerBoard.size() || colIndex < 0 || colIndex >= this.playerBoard.getFirst().size()) {
            // Expand the matrix if necessary
            expandBoard(rowIndex, colIndex);
            if (rowIndex < 0){
                rowIndex ++;
            } else if (colIndex < 0){
                colIndex ++;
            }
        }
        // Place the new card at the specified position
        this.playerBoard.get(rowIndex).set(colIndex, placingCardId);

        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateBoardEvent(gameState, gameState.allClients(), this, placingCardId, tableCardId, tableCornerPos));
            gameState.getEventQueue().notifyAll();
        }
    }

    private void expandBoard(int rowIndex, int colIndex){
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