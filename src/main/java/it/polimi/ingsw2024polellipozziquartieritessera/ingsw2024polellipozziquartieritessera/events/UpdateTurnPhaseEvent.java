package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.TurnPhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateTurnPhaseEvent extends Event {
    private final TurnPhase turnPhase;

    /**
     * Informs the clients about the change of the turn phase
     * @param gameState current game state
     * @param clients List of all clients
     * @param turnPhase new turn phase
     */
    public UpdateTurnPhaseEvent(GameState gameState, ArrayList<VirtualView> clients, TurnPhase turnPhase) {
        super(gameState, clients);
        this.turnPhase = turnPhase;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateTurnPhase(turnPhase);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
