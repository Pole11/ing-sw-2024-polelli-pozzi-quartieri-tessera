package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.*;

public class Player {
    GameState gameState;
    int points;
    String nickname;
    ObjectiveCard objectiveCard; // it is the secret objective
    StarterCard starterCard; // it is the most important card because it is used to create all the composition of the cards
    Color color;
    HashMap<Element, Integer> elements;
    HashMap<Integer, Side> board;
    HashMap<Integer, Side> hand;
    ArrayList<ArrayList<Integer>> playerBoard;

    public Player(GameState gameState, String nickname, Color color){
        this.points = 0;
        this.gameState = gameState;
        this.nickname = nickname;
        this.objectiveCard = null;
        this.starterCard = null;
        this.color = color;
        this.board = new HashMap<Integer, Side>();
        this.hand = new HashMap<Integer, Side>();
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

    public Color getColor() {
        return color;
    }

    public HashMap<Element, Integer> getResources() {
        return elements;
    }

    public HashMap<Integer, Side> getBoard() {
        return board;
    }

    public HashMap<Integer, Side> getHand() {
        return hand;
    }

    public GameState getGameState() {
        return gameState;
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

    // METHODS

    public void initializeBoard(){
        // Iitialization of player board as a 1x1 with the StarterCard in the center
        playerBoard = new ArrayList<>();
        ArrayList<Integer> row = new ArrayList<>();
        row.add(starterCard.getId());
        playerBoard.add(row);
    }

    private void updateBoard(int newCard, int existingCard, int cornerPos){
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
        switch(cornerPos){
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

    public int getCardPoints(ObjectiveCard objCard) {
        Challenge cardChallenge = objCard.getChallenge();
        int cardPoints = objCard.getPoints();
        int timesWon = -1;

        if (cardChallenge instanceof StructureChallenge) {
            timesWon = getTimesWonStructure();
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

    private int getTimesWonCoverage(GoldCard goldCard) {
        return goldCard.getUncoveredCorners(Side.FRONT).stream().map(Corner::getLinkedCorner).mapToInt((Corner c) -> c == null ? 1 : 0).sum();
    }

    private int getTimesWonElement(ArrayList<Element> elements) {
        ArrayList<Element> allElements = getAllElements();
        Map<Element, Long> counts = elements.stream()
                .collect(Collectors.toMap(
                        e -> e,  // Key mapper (identity function for the element)
                        e -> allElements.stream().filter(ae -> ae.equals(e)).count()  // Value mapper (count occurrences)
                ));
        return counts.values().stream().reduce((a, b) -> a < b ? a : b).orElseThrow().intValue();
    }

    public ArrayList<Element> getAllElements() {
        ArrayList<Element> elements = new ArrayList<>();
        getBoard().forEach((id, side) -> elements.addAll(getGameState().getCornerCard(id).getUncoveredElements(side)));

        return elements;
    }

    private int getTimesWonStructure() {
        return 0;
    }

}







