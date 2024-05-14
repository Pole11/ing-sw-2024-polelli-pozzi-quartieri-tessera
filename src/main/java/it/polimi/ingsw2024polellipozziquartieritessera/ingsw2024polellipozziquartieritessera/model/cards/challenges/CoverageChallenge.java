package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;

import java.util.ArrayList;

public class CoverageChallenge extends Challenge{

    @Override
    public int getTimesWon(Player player, GoldCard card) {
        ArrayList<Corner> uncoveredCorners = card.getUncoveredCorners(player.getPlacedCardSide(card.getId()));
        int times = 0;
        for (int i = 0; i < uncoveredCorners.size(); i++) {
            Corner corner = uncoveredCorners.get(i);
            Corner linkedCorner = corner.getLinkedCorner();
            if (linkedCorner != null)
                times++;
        }

        return times;
    }

    @Override
    public int getTimesWon(Player player, ObjectiveCard card) {
        throw new RuntimeException();
        //SISTEMA
    }
}
