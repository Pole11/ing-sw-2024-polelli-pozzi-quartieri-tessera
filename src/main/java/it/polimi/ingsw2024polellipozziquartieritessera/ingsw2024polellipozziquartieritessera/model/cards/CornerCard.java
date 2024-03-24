package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CornerCard extends Card {
    private final Corner[] frontCorners; // the corners of the front side (from top-left clockwise) 0 1
    private final Corner[] backCorners; // the corners of the back side (from top-left clockwise)   3 2

    // CONSTRUCTOR
    public CornerCard(int id, Corner[] frontCorners, Corner[] backCorners) {
        super(id);
        this.frontCorners = frontCorners;
        this.backCorners =  backCorners;
    }

    // GETTER
    public Corner[] getFrontCorners() {
        return frontCorners;
    }

    public Corner[] getBackCorners() {
        return backCorners;
    }

    // METHODS
    // return all the corners of the card (back and front)
    public Corner[] getCorners() {
        // corner array initialization
        ArrayList<Corner> corners = new ArrayList<>();
        // adding front and back corners
        corners.addAll(Arrays.asList(frontCorners));
        corners.addAll(Arrays.asList(backCorners));

        return corners.toArray(new Corner[corners.size()]);
    }

    // return the linked cards to this one
    public Integer[] getLinkedCards() {
        // card array initialization
        ArrayList<Integer> linkedCards = new ArrayList<Integer>();

        // front corners verification
        for (Corner corner : this.getCorners()){
            if (corner.getLinkedCorner() != null){
                //linkedCards.add(corner.getCard()); bisognerà usare cardsMap del gamestate
            }
        }

        return linkedCards.toArray(new Integer[linkedCards.size()]);
    }

    // return all uncovered corners of the card
    public Corner[] getUncoveredCorners(int isFront) {
        // corner array initialization
        ArrayList<Corner> uncoveredCorners = new ArrayList<>();

        // verification for each side
        if (isFront == 0){
            for (Corner corner : backCorners){
                if (corner != null && !corner.getCovered()){
                    uncoveredCorners.add(corner);
                }
            }
        }
        else{
            for (Corner corner : frontCorners){
                if (corner != null && !corner.getCovered()){
                    uncoveredCorners.add(corner);
                }
            }
        }

        return uncoveredCorners.toArray(new Corner[uncoveredCorners.size()]);
    }

    // this method will be ovverrided by all the corner cards subclasses
    public abstract Element[] getUncoveredElements(int isFront);
}