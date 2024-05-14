package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;

import java.util.ArrayList;
import java.util.HashMap;

public class ElementChallenge extends Challenge {
    private final ArrayList<Element> elements;

    public ElementChallenge(ArrayList<Element> elements){
        // should we put a copy in here???
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

    private int getTimesWon(Player player) {
        HashMap<Element, Integer> challengeElementsOccurences = new HashMap<>();
        HashMap<Element, Integer> playerElementsOccurences = new HashMap<>();
        var ref = new Object() {
            int min = Integer.MAX_VALUE;
        };

        //finds all the elements of the challenge
        elements.stream().forEach((element)->{
            if (challengeElementsOccurences.containsKey(element)) {
                challengeElementsOccurences.put(element, challengeElementsOccurences.get(element) + 1);
            } else {
                challengeElementsOccurences.put(element, 1);
            }
        });

        //find the elements of the player that are in challengeElementOccurences
        challengeElementsOccurences.keySet().stream().forEach((element ->{
            playerElementsOccurences.put(element, player.getAllElements().get(element));
        }));


        challengeElementsOccurences.keySet().stream().forEach((element -> {
            int min = playerElementsOccurences.get(element) / challengeElementsOccurences.get(element);
            if (min < ref.min){
                ref.min = min;
            }
        }));

        return ref.min;
    }
}