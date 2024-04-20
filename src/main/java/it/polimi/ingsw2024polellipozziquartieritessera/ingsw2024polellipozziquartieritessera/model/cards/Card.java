package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

public abstract class Card {
    private int id;

    public Card(int id){
        this.id = id;
    }

    // getter
    public int getId() {
        return id;
    }
}
