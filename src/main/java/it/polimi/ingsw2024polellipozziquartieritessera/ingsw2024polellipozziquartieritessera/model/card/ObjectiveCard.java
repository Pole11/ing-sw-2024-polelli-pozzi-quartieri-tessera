package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.challenge.Challenge;

public class ObjectiveCard extends Card {
    private final Challenge challenge; // challenge related to this card (not null)
    private final int points; // number used to calculate the gain of the challenge

    // CONSTRUCTOR
    public ObjectiveCard(int id, Challenge challenge, int points) {
        super(id);
        this.challenge = challenge;
        this.points = points;
    }

    // GETTER
    public int getPoints() {
        return points;
    }

    public Challenge getChallenge() {
        return challenge;
    }
}
