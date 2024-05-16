package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class ChangePhaseEvent extends Event{
    GamePhase gamePhase;

    public ChangePhaseEvent(GameState gameState, ArrayList<VirtualView> clients, GamePhase gamePhase) {
        super(gameState, clients);
        this.gamePhase = gamePhase;
    }

    @Override
    public void execute() {

    }
}
