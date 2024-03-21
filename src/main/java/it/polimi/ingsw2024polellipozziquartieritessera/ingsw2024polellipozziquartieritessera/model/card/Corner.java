package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

public class Corner {
    private final Element element; // contains the resource/item on the corner (empty if has no element)
    private final int cardId; // identifier of the parent card (from 1 to 102)
    private Boolean covered; //true if there is another card on top (false otherwise)
    private Corner linkedCorner; // the corner linked to this one (null if not exists)


    //corner is null in the array of corners if it is hidden

    //CONSTRUCTOR
    public Corner(Element element, int cardId) {
        covered = false;
        this.element = element;
        this.cardId = cardId;
        linkedCorner = null;
    }

    // GETTER
    public Boolean getCovered() {
        return covered;
    }

    public Element getElement() {
        return element;
    }

    // SETTER
    public void setCovered(Boolean covered) {
        this.covered = covered;
    }

    public void setLinkedCorner(Corner linkedCorner) {
        this.linkedCorner = linkedCorner;
    }

    // return the corner linked to this one
    public Corner getLinkedCorner() {
        return linkedCorner;
    }

    // return the parent card of this corner
    public int getCard() {
        return cardId;
    }
}