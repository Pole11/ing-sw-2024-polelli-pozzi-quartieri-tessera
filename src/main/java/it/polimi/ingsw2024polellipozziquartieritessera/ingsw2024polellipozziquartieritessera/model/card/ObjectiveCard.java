package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.challenge.Challenge;

public class ObjectiveCard extends Card {
    private final Challenge challenge;
    private final int points;
    
    public ObjectiveCard(int id, Challenge challenge, int points) {
        super(id);
        this.challenge = challenge;
        this.points = points;
    }

    public Challenge getChallenge() {
        return challenge;
    }
}
