package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;

/**
 * Challenge abstract class
 */
public abstract class Challenge {
    /**
     * Get the number of completion of the current challenge
     * @param player Selected player
     * @param card  Gold card to verify
     * @return Number of completion
     */
    public abstract int getTimesWon(Player player, GoldCard card);
    /**
     * Get the number of completion of the current challenge
     * @param player Selected player
     * @param card  Objective card to verify
     * @return Number of completion
     */
    public abstract int getTimesWon(Player player, ObjectiveCard card);
}
