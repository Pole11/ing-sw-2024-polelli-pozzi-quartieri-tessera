package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.CornerCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class UpdateAddHandEvent extends Event{
    private final int playerIndex;
    private final CornerCard card;

    public UpdateAddHandEvent(GameState gameState, ArrayList<VirtualView> clients, int playerIndex, CornerCard card) {
        super(gameState, clients);
        this.playerIndex = playerIndex;
        this.card = card;
    }

    @Override
    public void execute() {

    }
}
