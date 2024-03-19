package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class StarterCard extends CornerCard {
    private final Resource[] centerResource;
    public StarterCard(int id, Corner[] frontCorners, Corner[] backCorners, Resource[] centerResource) {
        super(id, frontCorners, backCorners);
        this.centerResource = centerResource;
    }

    // getter
    public Resource[] getCenterResource() {
        return centerResource;
    }

    // returns the resource visible on the side requested
    public Resource[] getUncoveredResources(int front){
        // resource array initialization
        ArrayList<Resource> uncoveredResources = new ArrayList<>();

        // set corners resources
        for (Corner corner : this.getUncoveredCorners(front)){
            if(corner.getResource() != null){
                uncoveredResources.add(corner.getResource());
            }
        }

        // add center resources if on front side
        if (front == 1){
            uncoveredResources.addAll(Arrays.asList(centerResource));
        }

        return uncoveredResources.toArray(new Resource[uncoveredResources.size()]);
    }
}