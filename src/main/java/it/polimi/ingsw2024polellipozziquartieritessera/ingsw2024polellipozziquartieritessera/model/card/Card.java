package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card;

public abstract class Card {
    private final int id;

    public Card(int id){
        this.id = id;
    }

    // getter
    public int getId() {
        return id;
    }
}
