package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;

public class ResourceCard extends CornerCard {
    private final Resource resourceType; //color of the card, determined by the major resourceType
    private final int points;

    public ResourceCard(int id, Resource resourceType, int points, Corner[] frontCorners, Corner[] backCorners) {
        super(id, frontCorners, backCorners);
        this.resourceType = resourceType;
        this.points = points;
    }

    // getter
    public Resource getResourceType() {
        return resourceType;
    }

    public int getPoints() {
        return points;
    }

    // returns the resource visible on the side requested
    public Resource[] getUncoveredResources(int front){
        // resource array initialization
        ArrayList<Resource> uncoveredResources = new ArrayList<>();

        // set center resource if the card is on the back side
        if (front == 0){
            uncoveredResources.add(this.resourceType);
        }

        // set all the corners resources if the card is on the front side
        else{
            for (Corner corner : this.getUncoveredCorners(front)){
                if(corner.getResource() != null){
                    uncoveredResources.add(corner.getResource());
                }
            }
        }

        return uncoveredResources.toArray(new Resource[uncoveredResources.size()]);
    }

    // returns the items visible on the side requested
    public Item[] getUncoveredItems(int front){
        // items array initialization
        ArrayList<Item> uncoveredItems = new ArrayList<>();

        // set all the corners items if the card is on the front side
        if (front == 1){
            for (Corner corner : this.getUncoveredCorners(front)){
                if(corner.getItem() != null){
                    uncoveredItems.add(corner.getItem());
                }
            }
        }

        // there is no need to verify for the back side because there are never items

        return uncoveredItems.toArray(new Item[uncoveredItems.size()]);
    }
}