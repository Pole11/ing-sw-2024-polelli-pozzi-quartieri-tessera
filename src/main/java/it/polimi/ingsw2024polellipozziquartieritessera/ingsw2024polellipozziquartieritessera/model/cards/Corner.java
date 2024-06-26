package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;

/**
 * Corner class
 */
public class Corner {
    /**
     * Contains the resource/item on the corner (empty if it has no element)
     */
    private final Element element;
    /**
     * Identifier of the parent card (from 1 to 102)
     */
    private final int cardId;
    /**
     * True if the corner does not allow a linked card (false otherwise)
     */
    private final Boolean hidden;
    /**
     * True if there is another card on top (false otherwise)
     */
    private Boolean covered;
    /**
     * The corner linked to this one (null if not set)
     */
    private Corner linkedCorner;

    //corner is null in the array of corners if it is hidden

    /**
     * Corner Constructor
     *
     * @param element Element given by the corner
     * @param cardId  Parent card identifier
     * @param hidden  Hidden corner specifier
     */
    public Corner(Element element, int cardId, Boolean hidden) {
        covered = false;
        this.hidden = hidden;
        this.element = element;
        this.cardId = cardId;
        linkedCorner = null;
    }

    // GETTER
    public Boolean getCovered() {
        return covered;
    }

    // SETTER
    public void setCovered(Boolean covered) {
        this.covered = covered;
    }

    public Element getElement() {
        return element;
    }

    public Corner getLinkedCorner() {
        return linkedCorner;
    }

    public void setLinkedCorner(Corner linkedCorner) {
        this.linkedCorner = linkedCorner;
    }

    public int getCard() {
        return cardId;
    }

    public Boolean getHidden() {
        return hidden;
    }
}