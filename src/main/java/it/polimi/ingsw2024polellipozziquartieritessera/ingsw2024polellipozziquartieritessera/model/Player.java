package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;

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

    private final HashMap<Integer, Element> centerResource;
    private final HashMap<Element, Integer> allElements;

    //per ora objectiveCardOptions serve, poi secondo me si potrà rimuovere
    private ObjectiveCard[] objectiveCardOptions; // it is the  array of the choice for secret objective

    public Player(String nickname){
        this.points = 0;
        this.nickname = nickname;

        this.color = null;
        this.objectiveCard = null;
        this.starterCard = null;
        this.playerBoard = new ArrayList<>();
        this.placedCardsMap = new HashMap<Integer, Side>();
        this.handCardsMap = new HashMap<Integer, Side>();
        this.objectivesWon = 0;
        this.centerResource = new HashMap<>();
        allElements = new HashMap<>();

        for (Element element : Element.values()) {
            allElements.put(element, 0);
        }
    }

    //GETTER

    public String getNickname() {
        return nickname;
    }

    public Color getColor() {
        return color;
    }

    public int getPoints() {
        return points;
    }

    public StarterCard getStarterCard() {
        return starterCard;
    }

    public ObjectiveCard getObjectiveCard() {
        return objectiveCard;
    }

    public HashMap<Integer, Side> getPlacedCardsMap() {
        return placedCardsMap;
    }

    public HashMap<Integer, Side> getHandCardsMap() {
        return handCardsMap;
    }

    public ArrayList<ArrayList<Integer>> getPlayerBoard() {
        return playerBoard;
    }

    public int getObjectivesWon(){
        return objectivesWon;
    }

    public HashMap<Integer, Element> getCenterResource() {
        return centerResource;
    }

    public HashMap<Element, Integer> getAllElements() {
        return allElements;
    }

    public ObjectiveCard[] getObjectiveCardOptions() {
        return this.objectiveCardOptions;
    }

//SETTER

    public void setObjectiveCard(ObjectiveCard objectiveCard) {
        this.objectiveCard = objectiveCard;
    }

    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.placedCardsMap.put(this.getStarterCard().getId(), Side.FRONT); // also set the default side to FRONT
        // !!! add elements
    }

    public void setSecretObjectiveCardOptions(ObjectiveCard[] objectiveCards) {
        this.objectiveCardOptions = objectiveCards;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    //ADDER
    public void incrementObjectivesWon(){
        objectivesWon ++;
    }

    // METHODS

    public void addPoints(int points) {
        this.points += points;
    }

    public Side getBoardSide(int cardId) {
        return this.getPlacedCardsMap().get(cardId);
    }

    public Side getHandSide(int cardId) {
        return this.getHandCardsMap().get(cardId);
    }

// -------------------Place Cards Map Managing-----------------

    public void placeCard(int placingCardId, CornerCard placingCard, CornerCard tableCard, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongInstanceTypeException {
        this.placedCardsMap.put(placingCardId, placingCardSide);
        this.handCardsMap.remove(placingCardId);

        this.getCenterResource().put(placingCardId, placingCard.getResourceType());
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
        playerBoard.clear(); // re-initialize the board it is previously contained something
        // Initialization of player board as a 1x1 with the StarterCard in the center
        ArrayList<Integer> row = new ArrayList<>();
        row.add(getStarterCard().getId());
        playerBoard.add(row);
    }

    public void printBoard() {
        for (int i = 0; i < playerBoard.size(); i++) {
            ArrayList<Integer> row = playerBoard.get(i);
            for (int j = 0; j < row.size(); j++) {
                System.out.print(playerBoard.get(i).get(j) + " ");
            }
            System.out.println("");
        }
    }

    public void updateBoard(int newCard, int existingCard, CornerPos existingCornerPos){
        int rowIndex = -1;
        int colIndex = -1;

        // Find the coordinates of the existing card on the board
        for (int i = 0; i < playerBoard.size(); i++) {
            ArrayList<Integer> row = playerBoard.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (row.get(j) == existingCard) {
                    rowIndex = i;
                    colIndex = j;
                } else if (row.get(j) == newCard) {
                    // la carta è già piazzata, esci subito
                    return;
                }
            }
        }

        if (rowIndex == -1 && colIndex == -1) {
            // non ha trovato la carta, magari un'eccezione?
        }

        // Define the position of the new card
        switch(existingCornerPos) {
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
            expandBoard(rowIndex, colIndex);
            if (rowIndex < 0){
                rowIndex ++;
            } else if (colIndex < 0){
                colIndex ++;
            }
        }
        // Place the new card at the specified position
        playerBoard.get(rowIndex).set(colIndex, newCard);
    }

    private void expandBoard(int rowIndex, int colIndex){
        // Expand the matrix to include the new position (remember that this supports only one update: row++/-- or col++/--)
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
                row.add(0, -1); // Adding -1 if there is no card
            } else if (colIndex >= row.size()) {
                row.add(-1); // Placeholder for empty cell
            }
        }
    }
}
