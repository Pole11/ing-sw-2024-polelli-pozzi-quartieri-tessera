package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongInstanceTypeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;

import java.util.ArrayList;

/**
 * Starter Card class
 */
public class StarterCard extends CornerCard {
    /**
     * List of center resources given by the front side of the card
     */
    private final ArrayList<Element> centerResources;

    /**
     * Starter Card Constructor
     *
     * @param id             Card identifier
     * @param frontCorners   Front corners array
     * @param backCorners    Back corners array
     * @param centerResource Center resources of the front side array
     */
    public StarterCard(int id, Corner[] frontCorners, Corner[] backCorners, ArrayList<Element> centerResource) {
        super(id, frontCorners, backCorners, 0);
        this.centerResources = centerResource;
    }

    // GETTER
    public ArrayList<Element> getCenterResources() {
        return centerResources;
    }

    // METHODS
    @Override
    public Challenge getChallenge() {
        return null;
    }

    @Override
    public int calculatePoints(Player player) {
        return 0;
    }

    public Element getResourceType() throws WrongInstanceTypeException {
        throw new WrongInstanceTypeException("called a method on starter card that is implemented only on Resource or Gold card");
    }

    public ArrayList<Element> getUncoveredElements(Side side) {
        // Initialize ArrayList to store uncovered resources
        ArrayList<Element> uncoveredResources = new ArrayList<>();

        // Add resources of uncovered corners
        for (Corner corner : this.getUncoveredCorners(side)) {
            if (corner.getElement() != Element.EMPTY) {
                uncoveredResources.add(corner.getElement());
            }
        }

        // If the card is on the front side, add center resources to uncoveredResources
        if (side == Side.FRONT) {
            uncoveredResources.addAll(centerResources);
        }

        return uncoveredResources;
    }
}