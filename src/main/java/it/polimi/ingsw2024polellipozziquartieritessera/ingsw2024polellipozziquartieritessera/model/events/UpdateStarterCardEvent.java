package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class UpdateStarterCardEvent extends Event{
    private final int starterCardId;

    public UpdateStarterCardEvent(GameState gameState, ArrayList<VirtualView> clients, int starterCardId) {
        super(gameState, clients);
        this.starterCardId = starterCardId;
    }

    @Override
    public void execute() {

    }
}
