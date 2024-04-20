package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;

public class ObjectiveCard extends Card {
    private Challenge challenge; // challenge related to this card (not null)
    private int points; // number used to calculate the gain of the challenge

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
