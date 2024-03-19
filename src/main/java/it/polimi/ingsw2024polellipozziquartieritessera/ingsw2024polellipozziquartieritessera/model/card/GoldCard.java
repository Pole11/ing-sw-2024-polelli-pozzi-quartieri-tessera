package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.challenge.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

public class GoldCard extends CornerCard {
    private final Resource resourceType; //color of the card, determined by the major resourceType
    private final Challenge challenge;
    private final Resource[] resourceNeeded;
    private final int points;

    public GoldCard(int id, Resource resourceType, Challenge challenge, Resource[] resourceNeeded, int points, Corner[] frontCorners, Corner[] backCorners) {
        super(id, frontCorners, backCorners);
        this.resourceType = resourceType;
        this.challenge = challenge;
        this.resourceNeeded = resourceNeeded;
        this.points = points;
    }

    public Resource getResourceType() {
        return resourceType;
    }

    public Challenge getChallenge() {
        return challenge;
    }

    public Resource[] getResourceNeeded() {
        return resourceNeeded;
    }

    public int getPoints() {
        return points;
    }

    // l'interno del metodo è a caso, solo le signature sono giuste ora
    public Item[] getUncoveredItems(int side){
        return new Item[4];
    }
}