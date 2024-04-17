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
    private final HashMap<Integer, CornerCard> cornerCardsMap;

    private int points;
    private int objectivesWon;
    private StarterCard starterCard; // it is the most important card because it is used to create all the composition of the cards
    private ObjectiveCard objectiveCard; // it is the secret objective

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
        this.cornerCardsMap = new HashMap<Integer, CornerCard>();
        this.objectivesWon = 0;
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

    //SETTER

    public void setObjectiveCard(ObjectiveCard objectiveCard) {
        this.objectiveCard = objectiveCard;
    }

    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
        this.getPlacedCardsMap().put(this.getStarterCard().getId(), Side.FRONT); // also set the default side to FRONT
        this.cornerCardsMap.put(starterCard.getId(), starterCard);
    }

    public void setSecretObjectiveCardOptions(ObjectiveCard[] objectiveCards) {
        this.objectiveCardOptions = objectiveCards;
    }

    public void setColor(Color color){
        this.color = color;
    }

    public ObjectiveCard[] getObjectiveCardOptions() {
        return this.objectiveCardOptions;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    // METHODS

    public void addPoints(int points) {
        this.points += points;
    }

    public ArrayList<Element> getAllElements() {
        ArrayList<Element> elements = new ArrayList<>();
        getPlacedCardsMap().forEach((id, side) -> elements.addAll(this.cornerCardsMap.get(id).getUncoveredElements(side)));

        return elements;
    }

    public Side getBoardSide(int cardId) {
        return this.getPlacedCardsMap().get(cardId);
    }

    public Side getHandSide(int cardId) {
        return this.getHandCardsMap().get(cardId);
    }

// -------------------Place Cards Map Managing-----------------

    public void updateCardsMaps(int placingCardId, CornerCard placingCard, Side side){
        placedCardsMap.put(placingCardId, side);
        handCardsMap.remove(placingCardId);
        cornerCardsMap.put(placingCardId, placingCard);
    }

// -------------------Board Matrix Managing-----------------------

    public void initializeBoard(){
        // Initialization of player board as a 1x1 with the StarterCard in the center
        ArrayList<Integer> row = new ArrayList<>();
        row.add(getStarterCard().getId());
        playerBoard.add(row);
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
                }
            }
        }

        // Define the position of the new card
        switch(existingCornerPos){
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
            } else if (colIndex<0){
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

    public int getCardPoints(ObjectiveCard objCard) throws WrongInstanceTypeException {
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

    public int getCardPoints(GoldCard goldCard) throws WrongInstanceTypeException {
        Challenge cardChallenge = goldCard.getChallenge();
        int cardPoints = goldCard.getPoints();
        int timesWon = -1;

        if (cardChallenge instanceof ElementChallenge) {
            ArrayList<Element> elements = ((ElementChallenge) cardChallenge).getElements();
            timesWon = getTimesWonElement(elements);
        } else if (cardChallenge instanceof CoverageChallenge) {
            timesWon = getTimesWonCoverage(goldCard);
        } else {
            throw new WrongInstanceTypeException("cardChallenge is neither a structure or a element challenge");
        }

        return Math.max(-1, timesWon * cardPoints); // if timesWon is not changed, then return -1
    };

// ---------------------Get Times Won ----------------------------------

    private int getTimesWonCoverage(GoldCard goldCard) {
        // TODO: check if right after removing the possibility of a corner to be null
        return goldCard.getUncoveredCorners(placedCardsMap.get(goldCard.getId())).stream().map(Corner::getLinkedCorner).mapToInt((Corner c) -> c == null ? 1 : 0).sum();
    }
    //does not work
    // remember that placedCardsMap.get() returns the side of the card played


    private int getTimesWonElement(ArrayList<Element> elements) {
        ArrayList<Element> allElements = getAllElements();
        Map<Element, Long> counts = elements.stream()
                .collect(Collectors.toMap(
                        e -> e,  // Key mapper (identity function for the element)
                        e -> allElements.stream().filter(ae -> ae.equals(e)).count()  // Value mapper (count occurrences)
                ));
        return counts.values().stream().reduce((a, b) -> a < b ? a : b).orElseThrow().intValue();
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
                    elementBoard[i][j] = this.cornerCardsMap.get(cardId).getResourceType();
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







