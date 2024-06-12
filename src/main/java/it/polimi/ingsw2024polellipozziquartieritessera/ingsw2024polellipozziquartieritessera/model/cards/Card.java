package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import com.google.gson.annotations.SerializedName;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;

public abstract class Card {
    public final int id;

    public Card(int id){
        this.id = id;
    }

    // getter
    public int getId() {
        return id;
    }

    public abstract Card getCard();
    public abstract int calculatePoints(Player player) throws CardNotPlacedException;
}
