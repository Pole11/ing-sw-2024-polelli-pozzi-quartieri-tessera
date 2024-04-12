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

    public Player(String nickname, Color color){
        this.points = 0;
        this.gameState = null;
        this.nickname = nickname;
        this.objectiveCard = null;
        this.starterCard = null;
        this.color = color;
        this.board = new HashMap<Integer, Side>();
        this.hand = new HashMap<Integer, Side>();
    }
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
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
        this.hand = hand;
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

    public int[][] getPlayerStructure(StarterCard starterCard) {

        // Track all visited cards in the algorithm
        ArrayList<CornerCard> visited = new ArrayList<>();

        // Initial matrix with center card at (0, 0)
        int size = 1;
        int[][] matrix = new int[size][size];
        matrix[0][0] = starterCard.getId();

        return exploreConnectedCards(visited, starterCard, 0, 0, matrix);
    }

    private int[][] exploreConnectedCards(ArrayList<CornerCard> visited, CornerCard card, int row, int col, int[][] matrix) {
        if (visited.contains(card)) {
            return matrix;
        }
        visited.add(card);
        matrix[row][col] = card.getId();
        Side side = this.getBoardSide(card.getId());
        Corner[] corners = card.getCorners(side);

        // Explore connections in each direction
        for (int i = 0; i < Config.N_CORNERS; i++) {
            int newRow = row;
            int newCol = col;

            if (corners[i]!=null){
                // up direction (up-left)
                if (i==0 && corners[i].getLinkedCorner()!=null) {
                    newRow--;
                }
                // right direction (up-right)
                else if (i==1 && corners[i].getLinkedCorner()!=null) {
                    newCol++;
                }
                // down direction (down-right)
                else if (i==2 && corners[i].getLinkedCorner()!=null) {
                    newRow++;
                }
                // left direction (down-left)
                else if (i==3 && corners[i].getLinkedCorner()!=null) {
                    newCol--;;
                }
            }

            // Expand the matrix if needed to accommodate connections
            if (newRow < 0 || newRow >= matrix.length || newCol < 0 || newCol >= matrix[0].length) {
                int newSize = Math.max(matrix.length, Math.max(newRow + 1, row + 1));
                newSize = Math.max(newSize, Math.max(newCol + 1, col + 1));
                int[][] newMatrix = new int[newSize][newSize];
                for (int j = 0; j < matrix.length; j++) {
                    System.arraycopy(matrix[j], 0, newMatrix[j], 0, matrix[j].length);
                }
                matrix = newMatrix;
            }

            exploreConnectedCards(visited, gameState.getCornerCard(corners[i].getLinkedCorner().getCard()), newRow, newCol, matrix);
        }

        return matrix;
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
        return goldCard.getUncoveredCorners(board.get(goldCard.getId())).stream().map(Corner::getLinkedCorner).mapToInt((Corner c) -> c == null ? 1 : 0).sum();
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







