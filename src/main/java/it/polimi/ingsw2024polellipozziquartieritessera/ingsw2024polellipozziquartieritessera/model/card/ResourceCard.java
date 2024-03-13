package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
public class ResourceCard extends CornerCard {
    private final Resource resourceType; //color of the card, determined by the major resourceType
    private final int points;

    public ResourceCard(int id, Resource resourceType, int points, Corner[] frontCorners, Corner[] backCorners, Resource[] centerBackResource) {
        super(id, frontCorners, backCorners, centerBackResource);
        this.resourceType = resourceType;
        this.points = points;
    }
    // l'interno del metodo è a caso, solo le signature sono giuste ora
    public Item[] getUncoveredItems(int side){ // mi da un errore in Item[], che ho tolto , controllare compilando
        return new Item[4];
    }
}