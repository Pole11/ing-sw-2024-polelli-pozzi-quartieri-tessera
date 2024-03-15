package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

public abstract class CornerCard extends Card {
    private final Corner[] frontCorners;
    private final Corner backCorners[];
    private final Resource centerBackResource[];
    public CornerCard(int id, Corner[] frontCorners, Corner[] backCorners, Resource[] centerBackResource) {
        super(id);
        this.frontCorners = frontCorners;
        this.backCorners =  backCorners;
        this.centerBackResource = centerBackResource;
    }


    // l'interno del metodo è a caso, solo le signature sono giuste ora
    public CornerCard[] getLinkedCards() {
        return new CornerCard[0];
    }

    public CornerCard[] getUncoveredCorners() {
        return new CornerCard[0];
    }

    public Resource[] getUncoveredResources(int side) {
        return new Resource[0];
    }

    public Item[] getUncoveredItems(int side) {
        return Item.INKWELL;
    }

}