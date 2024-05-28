package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

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
     * @param id Identifier of the card
     * @param resourceType Card element type
     * @param challenge Challenge on the card
     * @param resourceNeeded Resources needed for card placement
     * @param points Number of points given by the card
     * @param frontCorners Front corners array
     * @param backCorners Back corners array
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
        if (player.getBoardSide(this.getId()).equals(Side.BACK)) return 0;
        if (!player.placedCardContains(this.getId())) throw new CardNotPlacedException("The card is not placed");
        if (challenge == null) {
            return this.getPoints();
        } else {
            int timesWon = challenge.getTimesWon(player, this);
            return this.getPoints() * timesWon;
        }
    }

    public ArrayList<Element> getUncoveredElements(Side side){
        // resource array initialization
        ArrayList<Element> uncoveredElements = new ArrayList<>();

        // set center resource if the card is on the back side
        if (side == Side.BACK){
            uncoveredElements.add(this.resourceType);
        } else { // set all the corners resources if the card is on the front side
            for (Corner corner : this.getUncoveredCorners(side)){
                if(corner.getElement() != Element.EMPTY){
                    uncoveredElements.add(corner.getElement());
                }
            }
        }
        return uncoveredElements;
    }
}