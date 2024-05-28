package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;

/**
 * Abstract Card class
 * */
public abstract class Card {
    /**
     * Card identifier (from 1 to 102)
     */
    private final int id;

    /**
     * Card Constructor
     * @param id Card identifier
     */
    public Card(int id){
        this.id = id;
    }

    // getter
    public int getId() {
        return id;
    }

    /**
     * Abstract method for calculating the points gained from the card challenge
     * @param player Owner of the card
     * @return Points gained from the card challenge
     * @throws CardNotPlacedException Card has not been placed yet
     */
    public abstract int calculatePoints(Player player) throws CardNotPlacedException;
}
