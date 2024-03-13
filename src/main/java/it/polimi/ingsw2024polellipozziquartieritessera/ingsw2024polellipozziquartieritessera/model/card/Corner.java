package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
public class Corner {
    private Boolean covered;
    private final Item item;
    private final Resource resource;
    private Corner linkedCorner;

    public Corner(Item item, Resource resource) {
        covered = false;
        this.item = item;
        this.resource = resource;
        linkedCorner = null;
    }
}