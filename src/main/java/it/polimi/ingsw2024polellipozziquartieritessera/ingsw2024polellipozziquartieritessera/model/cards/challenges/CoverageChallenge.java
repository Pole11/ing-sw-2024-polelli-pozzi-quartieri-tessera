package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;

import java.util.ArrayList;

/**
 * Coverage Challenge Class
 */
public class CoverageChallenge extends Challenge{

    @Override
    public int getTimesWon(Player player, GoldCard card) {
        ArrayList<Corner> uncoveredCorners = card.getUncoveredCorners(player.getPlacedCardSide(card.getId()));
        int times = 0;
        for (Corner corner : uncoveredCorners) {
            Corner linkedCorner = corner.getLinkedCorner();
            if (linkedCorner != null)
                times++;
        }

        return times;
    }

    @Override
    public int getTimesWon(Player player, ObjectiveCard card) {
        throw new RuntimeException();
    }
}
