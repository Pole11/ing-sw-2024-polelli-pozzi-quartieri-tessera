package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

public class ObjectiveCard {
    private Challenge challenge;
    private int points;
    
    public ObjectiveCard(Challenge challenge, int points) {
        this.challenge = challenge;
        this.points = points;
    }

    public Challenge getChallenge() {
        return challenge;
    }
}
