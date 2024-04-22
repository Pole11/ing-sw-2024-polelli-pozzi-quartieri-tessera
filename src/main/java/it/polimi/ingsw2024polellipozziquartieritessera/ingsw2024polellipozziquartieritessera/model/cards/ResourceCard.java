package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;

public class ResourceCard extends CornerCard {
    private final Element resourceType; //color of the card, determined by the major resourceType
    private final int points; // points earned with the placement of the card

    // CONSTRUCTOR
    public ResourceCard(int id, Element resourceType, int points, Corner[] frontCorners, Corner[] backCorners) {
        super(id, frontCorners, backCorners);
        this.resourceType = resourceType;
        this.points = points;
    }

    // GETTER...
    public Element getResourceType() {
        return resourceType;
    }

    public int getPoints() {
        return points;
    }

    // METHODS
    // returns the resource visible on the side requested
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