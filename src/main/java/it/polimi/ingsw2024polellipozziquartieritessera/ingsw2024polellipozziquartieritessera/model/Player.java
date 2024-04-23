package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;

import java.util.*;
import java.util.stream.*;

public class Player {
    private final String nickname;
    private Color color;
    private final ArrayList<ArrayList<Integer>> playerBoard;
    private final HashMap<Integer, Side> placedCardsMap;
    private final HashMap<Integer, Side> handCardsMap;

    private int points;
    private int objectivesWon;
    private StarterCard starterCard; // it is the most important card because it is used to create all the composition of the cards
    private ObjectiveCard objectiveCard; // it is the secret objective

    private final HashMap<Integer, Element> centerResource;
    private final HashMap<Element, Integer> allElements;

    //per ora objectiveCardOptions serve, poi secondo me si potrà rimuovere
    private ObjectiveCard[] objectiveCardOptions; // it is the  array of the choice for secret objective

    public Player(String nickname, Color color){
        this.points = 0;
        this.nickname = nickname;
        this.color = color;

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
        this.getPlacedCardsMap().put(this.getStarterCard().getId(), Side.FRONT); // also set the default side to FRONT
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

    public void placeCard(int placingCardId, CornerCard placingCard, CornerCard tableCard, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongInstanceTypeException, GoldCardCannotBePlaced, CardAlreadyPresent, PlacingOnHiddenCornerException {
        Corner tableCorner = tableCard.getCorners(this.placedCardsMap.get(tableCardId))[tableCornerPos.getCornerPosValue()];
        if (tableCorner.getLinkedCorner() != null){
            throw new CardAlreadyPresent("you cannot place a card here, the corner is already linked");
        }
        if (tableCorner.getHidden()){
            throw new PlacingOnHiddenCornerException("you cannot place a card on a hidden corner");
        }

        // execute this block if the card is gold and has a challenge (is front)
        if (placingCard instanceof GoldCard && placingCardSide.equals(Side.FRONT)){
            boolean cardIsPlaceable = true;

            for (Element ele : Element.values()) {
                if (this.getAllElements().get(ele) < getElementOccurencies(((GoldCard) placingCard).getResourceNeeded(), ele)) {
                    cardIsPlaceable = false;
                    break;
                }
            }

            if (!cardIsPlaceable){
                throw new GoldCardCannotBePlaced("You haven't the necessary resources to place the goldcard " + placingCardId);
            }
        }

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

// -----------------------Challenge Managing---------------------------------

    public int getCardPoints(ObjectiveCard objCard) throws WrongInstanceTypeException, CardNotPlacedException {
        if (!placedCardsMap.containsKey(objCard.getId())) throw new CardNotPlacedException("The card is not placed");
        Challenge cardChallenge = objCard.getChallenge();
        int cardPoints = objCard.getPoints();
        int timesWon = -1;

        if (cardChallenge instanceof StructureChallenge) {
            timesWon = getTimesWonStructure((StructureChallenge) cardChallenge);
        } else if (cardChallenge instanceof ElementChallenge) {
            ArrayList<Element> elements = ((ElementChallenge) cardChallenge).getElements();
            timesWon = getTimesWonElement(elements);
        } else {
            throw new WrongInstanceTypeException("cardChallenge is neither a structure or a element challenge");
        }
        // update if challenge is completed at least one time
        if (timesWon>0){
            objectivesWon++;
        }

        return Math.max(-1, timesWon * cardPoints);
    };

    public int getCardPoints(CornerCard cornerCard) throws WrongInstanceTypeException, CardNotPlacedException {
        if (this.getBoardSide(cornerCard.getId()).equals(Side.BACK)) return 0;

        if (cornerCard instanceof GoldCard){
            return getCardPoints((GoldCard) cornerCard);
        } else if (cornerCard instanceof ResourceCard)
            return getCardPoints((ResourceCard) cornerCard);
        else {
            throw new WrongInstanceTypeException("CornerCard is neither a Gold or a Resource card");
        }
    }

    public int getCardPoints(ResourceCard resourceCard) throws CardNotPlacedException {
        Side side = null;
        side = placedCardsMap.get(resourceCard.getId());
        if (placedCardsMap.containsKey(resourceCard.getId())) {
            side = placedCardsMap.get(resourceCard.getId());
        } else {
            throw new CardNotPlacedException("The card is not placed");
        }
        if (side == Side.FRONT){
            return resourceCard.getPoints();
        } else {
            return 0;
        }
    }

    public int getCardPoints(GoldCard goldCard) throws WrongInstanceTypeException, CardNotPlacedException {
        if (!placedCardsMap.containsKey(goldCard.getId())) throw new CardNotPlacedException("The card is not placed");
        Challenge cardChallenge = goldCard.getChallenge();
        int cardPoints = goldCard.getPoints();
        int timesWon = 0;

        if (cardChallenge instanceof ElementChallenge) {
            ArrayList<Element> elements = ((ElementChallenge) cardChallenge).getElements();
            timesWon = getTimesWonElement(elements);
        } else if (cardChallenge instanceof CoverageChallenge) {
            timesWon = getTimesWonCoverage(goldCard);
        } else {
            throw new WrongInstanceTypeException("cardChallenge is neither a structure or a element challenge");
        }

        return timesWon * cardPoints; // if timesWon is not changed, then return -1
    };

// ---------------------Get Times Won ----------------------------------

    private int getTimesWonCoverage(GoldCard goldCard) {
        ArrayList<Corner> uncoveredCorners = goldCard.getUncoveredCorners(placedCardsMap.get(goldCard.getId()));
        // Stream<Corner> linkedCorners = uncoveredCorners.stream().map(Corner::getLinkedCorner);
        // int times = linkedCorners.mapToInt((Corner c) -> c == null ? 0 : 1).sum();
        int times = 0;
        for (int i = 0; i < uncoveredCorners.size(); i++) {
            Corner corner = uncoveredCorners.get(i);
            Corner linkedCorner = corner.getLinkedCorner();
            if (linkedCorner != null)
                times++;
        }

        return times;
    }
    // remember that placedCardsMap.get() returns the side of the card played

    private int getTimesWonElement(ArrayList<Element> elements) {
        int min = Integer.MAX_VALUE;
        for (Element ele : elements) {
            if (this.getAllElements().get(ele) < min) {
                min = this.getAllElements().get(ele);
            }
        }

        return min;
    }

    private int getTimesWonStructure(StructureChallenge challenge) throws WrongInstanceTypeException {
        int rows = getPlayerBoard().size();
        int cols = getPlayerBoard().getFirst().size();

        // instantiate the element board and fill it with the cards elements on playerBoard
        Element[][] elementBoard = new Element[rows][cols];
        for (int i=0; i<rows; i++){
            for (int j=0; j<cols; j++){
                int cardId = getPlayerBoard().get(i).get(j);
                if (cardId != -1){
                    elementBoard[i][j] = this.getCenterResource().get(cardId); // get this info from gs
                } else{
                    elementBoard[i][j] = Element.EMPTY;
                }
            }
        }

        // instantiate the checkBoard
        int[][] checkBoard = new int[rows][cols];

        // verify the recurrences of the configuration (from up-left to down-right)
        int recurrences = 0;

        for (int i=0; i<rows; i++){
            for (int j=0; j<cols; j++){
                boolean isValidOccurrence = true;
                for (int k = 0; k < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION && isValidOccurrence; k++){
                    for(int w = 0; w < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION && isValidOccurrence; w++){
                        if (challenge.getConfiguration()[k][w] != Element.EMPTY){
                            // if we are outside bounds
                            if (i+k >= rows || j+w >= cols) {
                                isValidOccurrence = false;
                            }
                            // if the configuration is not matched
                            else if(elementBoard[i+k][j+w] != challenge.getConfiguration()[k][w]){
                                isValidOccurrence = false;
                            }
                            // if the elements are used more than one time
                            else if(checkBoard[i+k][j+w] == 1){
                                isValidOccurrence = false;
                            }
                        }
                    }
                }
                if (isValidOccurrence){
                    // set in check board the used cards from 0 to 1
                    for (int k = 0; k < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; k++){
                        for(int w = 0; w < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; w++){
                            if (i+k<rows && j+w<cols){
                                if (elementBoard[i+k][j+w] != Element.EMPTY) {
                                    checkBoard[i + k][j + w] = 1;
                                }
                            }
                        }
                    }
                    recurrences++;
                }
            }
        }

        return recurrences;
    }

}
