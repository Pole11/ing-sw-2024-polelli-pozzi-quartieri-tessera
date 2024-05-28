package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Element Challenge class
 */
public class ElementChallenge extends Challenge {
    /**
     * List of needed elements
     */
    private final ArrayList<Element> elements;

    /**
     * Element Challenge Constructor
     * @param elements List of elements of the challenge
     */
    public ElementChallenge(ArrayList<Element> elements){
        this.elements = elements;
    }

    public ArrayList<Element> getElements() {
        return this.elements;
    }

    @Override
    public int getTimesWon(Player player, GoldCard card) {
        return getTimesWon(player);
    }

    @Override
    public int getTimesWon(Player player, ObjectiveCard card) {
        return getTimesWon(player);
    }

    /**
     * Get number of occurrences of elements in a player board
     * @param player Selected player
     * @return Number of elements occurrences
     */
    private int getTimesWon(Player player) {
        HashMap<Element, Integer> challengeElementsOccurrences = new HashMap<>();
        HashMap<Element, Integer> playerElementsOccurrences = new HashMap<>();
        var ref = new Object() {
            int min = Integer.MAX_VALUE;
        };

        //finds all the elements of the challenge
        elements.forEach((element)->{
            if (challengeElementsOccurrences.containsKey(element)) {
                challengeElementsOccurrences.put(element, challengeElementsOccurrences.get(element) + 1);
            } else {
                challengeElementsOccurrences.put(element, 1);
            }
        });

        //find the elements of the player that are in challengeElementOccurrences
        challengeElementsOccurrences.keySet().forEach((element ->{
            playerElementsOccurrences.put(element, player.getAllElements().get(element));
        }));


        challengeElementsOccurrences.keySet().forEach((element -> {
            int min = playerElementsOccurrences.get(element) / challengeElementsOccurrences.get(element);
            if (min < ref.min){
                ref.min = min;
            }
        }));

        return ref.min;
    }
}