package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

public abstract class Challenge {
    private final int cardId;

    protected Challenge(int cardId) {
        this.cardId = cardId; // if of the card it belongs
    }

    public int getCardId() {
        return cardId;
    }
}
