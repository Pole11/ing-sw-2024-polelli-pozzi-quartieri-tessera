package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.Corner;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;

import java.util.ArrayList;

/**
 * Coverage Challenge Class
 */
public class CoverageChallenge extends Challenge {

    @Override
    public int getTimesWon(Player player, GoldCard card) {
        // Get the list of uncovered corners on the specified side of the GoldCard
        ArrayList<Corner> uncoveredCorners = card.getUncoveredCorners(player.getPlacedCardSide(card.getId()));
        int times = 0;
        // Count how many of these corners have a linked corner
        for (Corner corner : uncoveredCorners) {
            Corner linkedCorner = corner.getLinkedCorner();
            if (linkedCorner != null) times++;
        }
        return times;
    }

    @Override
    public int getTimesWon(Player player, ObjectiveCard card) {
        throw new RuntimeException("ObjectiveCard does not participate in CoverageChallenge");
    }
}
