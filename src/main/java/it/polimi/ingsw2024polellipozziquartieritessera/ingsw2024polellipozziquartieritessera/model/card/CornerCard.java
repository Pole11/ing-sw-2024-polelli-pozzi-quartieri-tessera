package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CornerCard extends Card {
    private final Corner[] frontCorners;
    private final Corner[] backCorners;
    public CornerCard(int id, Corner[] frontCorners, Corner[] backCorners) {
        super(id);
        this.frontCorners = frontCorners;
        this.backCorners =  backCorners;
    }

    // setter
    public Corner[] getFrontCorners() {
        return frontCorners;
    }

    public Corner[] getBackCorners() {
        return backCorners;
    }

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
    public CornerCard[] getLinkedCards() {
        // card array initialization
        ArrayList<CornerCard> linkedCards = new ArrayList<>();

        // front corners verification
        for (Corner corner : this.getCorners()){
            if (corner.getLinkedCorner() != null){
                linkedCards.add(corner.getCard());
            }
        }

        return linkedCards.toArray(new CornerCard[linkedCards.size()]);
    }

    // return all uncovered corners of the card
    public Corner[] getUncoveredCorners(int front) {
        // corner array initialization
        ArrayList<Corner> uncoveredCorners = new ArrayList<>();

        // verification for each side
        if (front == 0){
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
    public abstract Resource[] getUncoveredResources(int front);
}