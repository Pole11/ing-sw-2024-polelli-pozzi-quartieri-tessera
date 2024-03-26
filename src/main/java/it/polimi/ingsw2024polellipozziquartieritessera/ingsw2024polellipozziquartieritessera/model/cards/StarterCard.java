package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;
import java.util.Arrays;

public class StarterCard extends CornerCard {
    private final ArrayList<Element> centerResource; // array of center resources in the front side

    // CONSTRUCTOR
    public StarterCard(int id, Corner[] frontCorners, Corner[] backCorners, ArrayList<Element> centerResource) {
        super(id, frontCorners, backCorners);
        this.centerResource = centerResource;
    }

    // GETTER
    public ArrayList<Element> getCenterResource() {
        return centerResource;
    }

    // METHODS
    // returns the resource visible on the side requested
    public ArrayList<Element> getUncoveredElements(int isFront){
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
            uncoveredResources.addAll(centerResource);
        }

        return uncoveredResources;
    }
}