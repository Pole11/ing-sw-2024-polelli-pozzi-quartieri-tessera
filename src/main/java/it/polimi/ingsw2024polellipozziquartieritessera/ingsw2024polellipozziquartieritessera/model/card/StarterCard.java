package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

public class StarterCard extends CornerCard {
    private final Resource[] centerResource;
    public StarterCard(int id, Corner[] frontCorners, Corner[] backCorners, Resource[] centerResource) {
        super(id, frontCorners, backCorners);
        this.centerResource = centerResource;
    }
}