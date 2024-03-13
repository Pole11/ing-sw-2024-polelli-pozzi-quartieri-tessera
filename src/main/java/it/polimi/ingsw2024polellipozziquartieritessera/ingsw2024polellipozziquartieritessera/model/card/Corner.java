package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

public class Corner {
    private Boolean covered;
    private Item item;
    private Resource resource;
    private Corner linkedCorner;

    public Corner(Boolean covered, Item item, Resource resource, Corner linkedCorner) {
        this.covered = covered;
        this.item = item;
        this.resource = resource;
        this.linkedCorner = linkedCorner;
    }
}