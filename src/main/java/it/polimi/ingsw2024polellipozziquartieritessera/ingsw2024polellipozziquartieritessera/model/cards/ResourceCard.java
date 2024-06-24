package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;

import java.util.ArrayList;

/**
 * Resource card class
 */
public class ResourceCard extends CornerCard {
    /**
     * Type of the card, determined by the major resourceType
     */
    private final Element resourceType;

    /**
     * Resource card constructor
     *
     * @param id           Identifier of the card
     * @param resourceType Card element type
     * @param points       Number of points given by the card
     * @param frontCorners Front corners array
     * @param backCorners  Back corners array
     */
    public ResourceCard(int id, Element resourceType, int points, Corner[] frontCorners, Corner[] backCorners) {
        super(id, frontCorners, backCorners, points);
        this.resourceType = resourceType;
    }

    // GETTER
    public Element getResourceType() {
        return resourceType;
    }

    // METHODS
    @Override
    public Challenge getChallenge() {
        return null;
    }

    @Override
    public int calculatePoints(Player player) throws CardNotPlacedException {
        if (!player.placedCardContains(this.getId())) throw new CardNotPlacedException("The card is not placed");
        if (player.getBoardSide(this.getId()).equals(Side.BACK)) return 0;
        return this.getPoints();
    }

    public ArrayList<Element> getUncoveredElements(Side side) {
        // Initialize ArrayList to store uncovered elements
        ArrayList<Element> uncoveredElements = new ArrayList<>();

        // If the card is on the back side, add its center resource to uncoveredElements
        if (side == Side.BACK) {
            uncoveredElements.add(this.resourceType);
        } else { // If the card is on the front side, add resources of uncovered corners
            for (Corner corner : this.getUncoveredCorners(side)) {
                if (corner.getElement() != Element.EMPTY) {
                    uncoveredElements.add(corner.getElement());
                }
            }
        }
        return uncoveredElements;
    }
}