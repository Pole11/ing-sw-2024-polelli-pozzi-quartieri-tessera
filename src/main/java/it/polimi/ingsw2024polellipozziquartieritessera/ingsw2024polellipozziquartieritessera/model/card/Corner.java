package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
public class Corner {
    private Boolean covered;
    private final Item item;
    private final Resource resource;
    private Corner linkedCorner;
    private CornerCard card;

    public Corner(Item item, Resource resource, CornerCard card) {
        covered = false;
        this.item = item;
        this.resource = resource;
        this.card = card;
        linkedCorner = null;
    }

    // getters & setters

    public Boolean getCovered() {
        return covered;
    }

    public void setCovered(Boolean covered) {
        this.covered = covered;
    }

    public Item getItem() {
        return item;
    }

    public Resource getResource() {
        return resource;
    }

    public void setLinkedCorner(Corner linkedCorner) {
        this.linkedCorner = linkedCorner;
    }

    public void setCard(CornerCard card) {
        this.card = card;
    }

    // return the corner linked to this one
    public Corner getLinkedCorner() {
        return linkedCorner;
    }

    // return the parent card of this corner
    public CornerCard getCard() {
        return card;
    }
}