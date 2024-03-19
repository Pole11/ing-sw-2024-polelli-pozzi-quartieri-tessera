package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.challenge;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

public class ItemChallenge extends Challenge {
    private final Item[] items;

    public ItemChallenge(Item[] items){
        this.items = items;
    }

    public Item[] getItems() {
        return items;
    }
}
