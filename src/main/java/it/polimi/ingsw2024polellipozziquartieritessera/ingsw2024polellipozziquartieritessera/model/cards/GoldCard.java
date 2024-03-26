package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;

public class GoldCard extends CornerCard {
    private final Element resourceType; // color of the card, determined by the major resourceType
    private final Challenge challenge; // challenge related to this card (if null, points are gained when played)
    private final Element[] resourceNeeded; // resource needed to place the card
    private final int points; // value used to calculate the gain (if challenge is null, it is simply returned)

    // CONSTRUCTOR
    public GoldCard(int id, Element resourceType, Challenge challenge, Element[] resourceNeeded, int points, Corner[] frontCorners, Corner[] backCorners) {
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

    public Element[] getResourceNeeded() {
        return resourceNeeded;
    }

    public int getPoints() {
        return points;
    }

    // METHODS
    // returns the elements visible on the requested side
    public Element[] getUncoveredElements(int isFront){
        // resource array initialization
        ArrayList<Element> uncoveredElements = new ArrayList<>();

        // set center resource if the card is on the back side
        if (isFront == 0){
            uncoveredElements.add(this.resourceType);
        }

        // set all the corners resources if the card is on the front side
        else{
            for (Corner corner : this.getUncoveredCorners(isFront)){
                if(corner.getElement() != Element.EMPTY){
                    uncoveredElements.add(corner.getElement());
                }
            }
        }

        return uncoveredElements.toArray(new Element[uncoveredElements.size()]);
    }
}