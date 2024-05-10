package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;

public class GoldCard extends CornerCard {
    private final Element resourceType; // color of the card, determined by the major resourceType
    private final Challenge challenge; // challenge related to this card (if null, points are gained when played)
    private final ArrayList<Element> resourceNeeded; // resource needed to place the card

    // CONSTRUCTOR
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

    @Override
    public GoldCard getCard() {
        return this;
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

    // returns the elements visible on the requested side
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