package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongInstanceTypeException;

import java.util.ArrayList;
import java.util.Arrays;

public class StarterCard extends CornerCard {
    private final ArrayList<Element> centerResources; // array of center resources in the front side

    // CONSTRUCTOR
    public StarterCard(int id, Corner[] frontCorners, Corner[] backCorners, ArrayList<Element> centerResource) {
        super(id, frontCorners, backCorners, 0);
        this.centerResources = centerResource;
    }

    // GETTER
    public ArrayList<Element> getCenterResources() {
        return centerResources;
    }

    @Override
    public StarterCard getCard(){
        return this;
    }

    // METHODS
    // return null (it needs to be implemented because it's an abstract method in CornerCard)
    public Element getResourceType() throws WrongInstanceTypeException {
        throw new WrongInstanceTypeException("called a method on starter card that is implemented only on Resource or Gold card");
    }

    // returns the resource visible on the side requested
    public ArrayList<Element> getUncoveredElements(Side side){
        // resource array initialization
        ArrayList<Element> uncoveredResources = new ArrayList<>();

        // set corners resources
        for (Corner corner : this.getUncoveredCorners(side)){
            if(corner.getElement() != Element.EMPTY){
                uncoveredResources.add(corner.getElement());
            }
        }

        // add center resources if on front side
        if (side == Side.FRONT){
            uncoveredResources.addAll(centerResources);
        }

        return uncoveredResources;
    }
}