package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;

import java.util.ArrayList;

/**
 * Gold Card class
 */
public class GoldCard extends CornerCard {
    /**
     * Type of the card, determined by the major resourceType
     */
    private final Element resourceType;
    /**
     * Challenge related to this card (if null, points are gained when played)
     */
    private final Challenge challenge;
    /**
     * Resource needed to place the card
     */
    private final ArrayList<Element> resourceNeeded;

    /**
     * Gold Card Constructor
     *
     * @param id             Identifier of the card
     * @param resourceType   Card element type
     * @param challenge      Challenge on the card
     * @param resourceNeeded Resources needed for card placement
     * @param points         Number of points given by the card
     * @param frontCorners   Front corners array
     * @param backCorners    Back corners array
     */
    public GoldCard(int id, Element resourceType, Challenge challenge, ArrayList<Element> resourceNeeded, int points, Corner[] frontCorners, Corner[] backCorners) {
        super(id, frontCorners, backCorners, points);
        this.resourceType = resourceType;
        this.challenge = challenge;
        this.resourceNeeded = resourceNeeded;
    }

    // GETTER
    public Element getResourceType() {
        return resourceType;
    }

    public ArrayList<Element> getResourceNeeded() {
        return resourceNeeded;
    }

    // METHODS
    @Override
    public Challenge getChallenge() {
        return challenge;
    }

    @Override
    public int calculatePoints(Player player) throws CardNotPlacedException {
        // If the card is on the back side, return 0 points
        if (player.getBoardSide(this.getId()).equals(Side.BACK)) {
            return 0;
        }

        // Check if the card is placed on the player's board
        if (!player.placedCardContains(this.getId())) {
            throw new CardNotPlacedException("The card is not placed");
        }

        // If there is no challenge associated with the card, return its base points
        if (challenge == null) {
            return this.getPoints();
        } else {
            // Calculate points based on the challenge and how many times it has been won
            int timesWon = challenge.getTimesWon(player, this);
            return this.getPoints() * timesWon;
        }
    }


    public ArrayList<Element> getUncoveredElements(Side side) {
        // Initialize ArrayList to store uncovered elements
        ArrayList<Element> uncoveredElements = new ArrayList<>();

        // If the card is on the back side, add its center resource
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