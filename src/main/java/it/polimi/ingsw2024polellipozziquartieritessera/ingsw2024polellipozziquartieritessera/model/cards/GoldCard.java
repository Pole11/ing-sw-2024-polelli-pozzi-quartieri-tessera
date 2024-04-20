package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;

public class GoldCard extends CornerCard {
    private Element resourceType; // color of the card, determined by the major resourceType
    private Challenge challenge; // challenge related to this card (if null, points are gained when played)
    private ArrayList<Element> resourceNeeded; // resource needed to place the card
    private int points; // value used to calculate the gain (if challenge is null, it is simply returned)

    // CONSTRUCTOR
    public GoldCard(int id, Element resourceType, Challenge challenge, ArrayList<Element> resourceNeeded, int points, Corner[] frontCorners, Corner[] backCorners) {
        super(id, frontCorners, backCorners);
        this.resourceType = resourceType;
        this.challenge = challenge;
        this.resourceNeeded = resourceNeeded;
        this.points = points;
    }

    // GETTER
    public Element getResourceType() {
        return resourceType;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public ArrayList<Element> getResourceNeeded() {
        return resourceNeeded;
    }

    public int getPoints() {
        return points;
    }

    // METHODS
    // returns the elements visible on the requested side
    public ArrayList<Element> getUncoveredElements(Side side){
        // resource array initialization
        ArrayList<Element> uncoveredElements = new ArrayList<>();

        // set center resource if the card is on the back side
        if (side == Side.BACK){
            uncoveredElements.add(this.resourceType);
        }

        // set all the corners resources if the card is on the front side
        else{
            for (Corner corner : this.getUncoveredCorners(side)){
                if(corner.getElement() != Element.EMPTY){
                    uncoveredElements.add(corner.getElement());
                }
            }
        }

        return uncoveredElements;
    }

}