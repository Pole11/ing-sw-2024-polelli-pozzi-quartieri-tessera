package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongInstanceTypeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;

import java.util.ArrayList;
import java.util.Arrays;

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
     * @param id Card identifier
     * @param frontCorners Front corners array
     * @param backCorners Back corners array
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

    public ArrayList<Element> getUncoveredElements(Side side){
        // resource array initialization
        ArrayList<Element> uncoveredResources = new ArrayList<>();

        // set corners resources
        for (Corner corner : this.getUncoveredCorners(side)){
            if(corner.getElement() != Element.EMPTY){
                uncoveredResources.add(corner.getElement());
            }
        }

        // add center resources if on front side
        if (side == Side.FRONT){
            uncoveredResources.addAll(centerResources);
        }

        return uncoveredResources;
    }
}