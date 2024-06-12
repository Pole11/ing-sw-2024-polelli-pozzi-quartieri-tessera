package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongInstanceTypeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Corner card class
 */
public abstract class CornerCard extends Card {
    /**
     * Corners of the front side (from top-left clockwise)
     */
    private final Corner[] frontCorners;
    /**
     * Corners of the back side (from top-left clockwise)
     */
    private final Corner[] backCorners;
    /**
     * Base points of the card
     */
    private final int points;

    /**
     * CornerCard Constructor
     * @param id Card identifier
     * @param frontCorners Front coners array
     * @param backCorners Back corners array
     * @param points Number of points given by the card
     */
    public CornerCard(int id, Corner[] frontCorners, Corner[] backCorners, int points) {
        super(id);
        this.frontCorners = frontCorners;
        this.backCorners =  backCorners;
        this.points = points;
    }

    // GETTER
    public Corner[] getFrontCorners() {
        return frontCorners;
    }

    public Corner[] getBackCorners() {
        return backCorners;
    }

    public int getPoints() { return points; }

    // METHODS

    public abstract int calculatePoints(Player player) throws CardNotPlacedException;

    /**
     * Get the card challenge
     * @return Current card challenge
     */
    public abstract Challenge getChallenge();


    /**
     * Get all card corners
     * @return All corners of the card (back and front)
     */
    public ArrayList<Corner> getCorners() {
        // corner array initialization
        ArrayList<Corner> corners = new ArrayList<>();
        // adding front and back corners
        corners.addAll(Arrays.asList(frontCorners));
        corners.addAll(Arrays.asList(backCorners));

        return corners;
    }

    /**
     * Returns all corners of a specific card side
     * @param side Specifies the desired side
     * @return Corners of the specified side
     */
    public ArrayList<Corner> getCorners(Side side) {
        if (side == Side.FRONT) {
            ArrayList<Corner> corners = new ArrayList<>();
            corners.addAll(Arrays.asList(frontCorners));
            return corners;
        }
        else{
            ArrayList<Corner> corners = new ArrayList<>();
            corners.addAll(Arrays.asList(backCorners));
            return corners;
        }
    }

    /**
     * Get the cards linked to this one
     * @return Linked card
     */
    public ArrayList<Integer> getLinkedCards() {
        // card array initialization
        ArrayList<Integer> linkedCards = new ArrayList<>();

        // corners verification
        for (Corner corner : this.getCorners()){
            if (corner.getLinkedCorner() != null){
                linkedCards.add(corner.getLinkedCorner().getCard());
            }
        }
        return linkedCards;
    }

    /**
     * Get all uncovered corners of the card
     * @param side Specification of the side
     * @return List of uncovered card corners
     */
    public ArrayList<Corner> getUncoveredCorners(Side side) {
        // corner array initialization
        ArrayList<Corner> uncoveredCorners = new ArrayList<>();

        // verification for each side
        if (side == Side.BACK){
            for (Corner corner : backCorners){
                if (!corner.getCovered()){
                    uncoveredCorners.add(corner);
                }
            }
        } else if (side == Side.FRONT) {
            for (Corner corner : frontCorners){
                if (!corner.getCovered()){
                    uncoveredCorners.add(corner);
                }
            }
        }

        return uncoveredCorners;
    }

    /**
     * Get the resource type of the card (null if StarterCard)
     * @return Element type of the card
     * @throws WrongInstanceTypeException Card has no resource type
     */
    public abstract Element getResourceType() throws WrongInstanceTypeException;

    /**
     * Get all the uncovered elements on the card
     * @param side Current card side
     * @return List of uncovered elements
     */
    public abstract ArrayList<Element> getUncoveredElements(Side side);
}