package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.*;

import java.util.*;
import java.util.stream.*;

public class Player {
    int points;
    String nickname;
    ObjectiveCard objectiveCard; // it is the secret objective
    ObjectiveCard[] objectiveCardOptions; // it is the  array of the choice for secret objective
    StarterCard starterCard; // it is the most important card because it is used to create all the composition of the cards
    Color color;
    HashMap<Element, Integer> elements;
    ArrayList<ArrayList<Integer>> playerBoard;
    HashMap<Integer, Side> placedCardsMap;
    HashMap<Integer, Side> handCardsMap;

    public Player(String nickname, Color color){
        this.points = 0;
        this.nickname = nickname;
        this.objectiveCard = null;
        this.starterCard = null;
        this.color = color;
        this.placedCardsMap = new HashMap<Integer, Side>();
        this.handCardsMap = new HashMap<Integer, Side>();
    }

    //GETTER

    public int getPoints() {
        return points;
    }

    public String getNickname() {
        return nickname;
    }

    public ObjectiveCard getObjectiveCard() {
        return objectiveCard;
    }

    public StarterCard getStarterCard() {
        return starterCard;
    }

    public void setObjectiveCard(ObjectiveCard objectiveCard) {
        this.objectiveCard = objectiveCard;
    }

    public void setStarterCard(StarterCard starterCard) {
        this.starterCard = starterCard;
    }

    public void setHand(HashMap<Integer, Side> hand) {
        this.handCardsMap = hand;
    }

    public Color getColor() {
        return color;
    }

    public HashMap<Element, Integer> getResources() {
        return elements;
    }

    public HashMap<Integer, Side> getBoard() {
        return placedCardsMap;
    }

    public HashMap<Integer, Side> getHand() {
        return handCardsMap;
    }

    public Side getBoardSide(int cardId) {
        return this.getBoard().get(cardId);
    }

    public Side getHandSide(int cardId) {
        return this.getHand().get(cardId);
    }

    public ArrayList<ArrayList<Integer>> getPlayerBoard() {
        return playerBoard;
    }

    public void setSecretObjectiveCardOptions(ObjectiveCard[] objectiveCards) {
        this.objectiveCardOptions = objectiveCards;
    }

    public ObjectiveCard[] getObjectiveCardOptions() {
        return this.objectiveCardOptions;
    }


    // METHODS

    public ArrayList<Element> getAllElements() {
        ArrayList<Element> elements = new ArrayList<>();
        getBoard().forEach((id, side) -> elements.addAll(Main.gameState.getCornerCard(id).getUncoveredElements(side)));

        return elements;
    }

// -------------------Place Cards Map Managing-----------------

    public void updateCardsMaps(int placingCardId, Side side){

    }




// -------------------Board Matrix Managing-----------------------

    public void initializeBoard(){
        // Iitialization of player board as a 1x1 with the StarterCard in the center
        playerBoard = new ArrayList<>();
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
                rowIndex++;
            case CornerPos.UPRIGHT:
                colIndex++;
            case CornerPos.DOWNRIGHT:
                rowIndex--;
            case CornerPos.DOWNLEFT:
                colIndex--;
        }

        // Check if the new position is outside the bounds of the current matrix
        if (rowIndex < 0 || rowIndex >= playerBoard.size() || colIndex < 0 || colIndex >= playerBoard.get(rowIndex).size()) {
            // Expand the matrix if necessary
            expandBoard(rowIndex, colIndex);
        }

        // Place the new card at the specified position
        playerBoard.get(rowIndex).set(colIndex, newCard);
    }

    private void expandBoard(int rowIndex, int colIndex){
        // Expand the matrix to include the new position (remember that this supports only one update: row++/-- or col++/--)
        // Expand rows if needed
        if (rowIndex < 0) {
            playerBoard.add(0, new ArrayList<>());
        } else if (rowIndex >= playerBoard.size()) {
            playerBoard.add(new ArrayList<>());
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


    public int getCardPoints(ObjectiveCard objCard) {
        Challenge cardChallenge = objCard.getChallenge();
        int cardPoints = objCard.getPoints();
        int timesWon = -1;

        if (cardChallenge instanceof StructureChallenge) {
            timesWon = getTimesWonStructure((StructureChallenge) cardChallenge);
        } else if (cardChallenge instanceof ElementChallenge) {
            ArrayList<Element> elements = ((ElementChallenge) cardChallenge).getElements();
            timesWon = getTimesWonElement(elements);
        }

        return Math.max(-1, timesWon * cardPoints);
    };

    public int getCardPoints(GoldCard goldCard) {
        Challenge cardChallenge = goldCard.getChallenge();
        int cardPoints = goldCard.getPoints();
        int timesWon = -1;

        if (cardChallenge instanceof ElementChallenge) {
            ArrayList<Element> elements = ((ElementChallenge) cardChallenge).getElements();
            timesWon = getTimesWonElement(elements);
        } else if (cardChallenge instanceof CoverageChallenge) {
            timesWon = getTimesWonCoverage(goldCard);
        }

        return Math.max(-1, timesWon * cardPoints); // if timesWon is not changed, then return -1
    };

// ---------------------Get Times Won ----------------------------------

    private int getTimesWonCoverage(GoldCard goldCard) {
        return goldCard.getUncoveredCorners(placedCardsMap.get(goldCard.getId())).stream().map(Corner::getLinkedCorner).mapToInt((Corner c) -> c == null ? 1 : 0).sum();
    }
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



    private int getTimesWonStructure(StructureChallenge challenge) {
        int rows = getPlayerBoard().size();
        int cols = getPlayerBoard().get(0).size();
        
        // instantiate the element board and fill it with the cards elements on playerBoard
        Element[][] elementBoard = new Element[rows][cols];
        for (int i=0; i<rows; i++){
            for (int j=0; j<cols; j++){
                int cardId = getPlayerBoard().get(i).get(j);
                if (cardId != -1){
                    elementBoard[i][j] = Main.gameState.getCornerCard(cardId).getResourceType();
                } else{
                    elementBoard[i][j] = Element.EMPTY;
                }
            }
        }

        // Instantiate a specific matrix for the configuration (it has to adapt to the perfect side)
        // getConfiguration() gives a 3x3 every time

        // instantiate the checkBoard
        int[][] checkBoard = new int[rows][cols];

        // verify the recurrences of the configuration (from up-left to down-right)
        int recurrences = 0;

        // !!! I have to update from 3 to the length of the new sized configuration matrix
        for (int i=0; i<rows-3; i++){
            for (int j=0; j<cols-3; j++){
                boolean isValidOccurrence = true;
                for (int k = 0; k < 3 && isValidOccurrence; k++){
                    for(int w = 0; w < 3 && isValidOccurrence; w++){
                        if (challenge.getConfiguration()[k][w] != Element.EMPTY && elementBoard[i+k][j+w] != challenge.getConfiguration()[k][w] && checkBoard[i+k][j+w] == 0){
                            isValidOccurrence = false;
                        }
                    }
                }
                if (isValidOccurrence){
                    // set in check board the used cards from 0 to 1
                    for (int k = 0; k < 3; k++){
                        for(int w = 0; w < 3; w++){
                            if (elementBoard[i+k][j+w] != challenge.getConfiguration()[k][w] && checkBoard[i+k][j+w] == 0){
                                isValidOccurrence = false;
                            }
                        }
                    }
                    recurrences++;
                }
            }
        }

        return 0;
    }

}







