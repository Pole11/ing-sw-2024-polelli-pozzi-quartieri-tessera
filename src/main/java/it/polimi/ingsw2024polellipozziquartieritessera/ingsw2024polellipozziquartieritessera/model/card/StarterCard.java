package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class StarterCard extends CornerCard {
    private final Element[] centerResource; // array of center resources in the front side

    // CONSTRUCTOR
    public StarterCard(int id, Corner[] frontCorners, Corner[] backCorners, Element[] centerResource) {
        super(id, frontCorners, backCorners);
        this.centerResource = centerResource;
    }

    // GETTER
    public Element[] getCenterResource() {
        return centerResource;
    }

    // METHODS
    // returns the resource visible on the side requested
    public Element[] getUncoveredElements(int isFront){
        // resource array initialization
        ArrayList<Element> uncoveredResources = new ArrayList<>();

        // set corners resources
        for (Corner corner : this.getUncoveredCorners(isFront)){
            if(corner.getElement() != null){
                uncoveredResources.add(corner.getElement());
            }
        }

        // add center resources if on front side
        if (isFront == 1){
            uncoveredResources.addAll(Arrays.asList(centerResource));
        }

        return uncoveredResources.toArray(new Element[uncoveredResources.size()]);
    }
}