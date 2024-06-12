package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;

/**
 * Objective Cards class
 */
public class ObjectiveCard extends Card {
    /**
     * Challenge related to this card (not null)
     */
    private final Challenge challenge;
    /**
     * Number used to calculate the gain of the challenge
     */
    private final int points;

    /**
     * Object Card Constructor
     * @param id Card identifier
     * @param challenge Challenge of the card
     * @param points Number of points for the card challenge
     */
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

    public int calculatePoints(Player player) {
        int timesWon = this.challenge.getTimesWon(player, this);
        if (timesWon > 0) {
            player.incrementObjectivesWon();
        }
        return timesWon * this.points;
    }
}
