package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateCurrentPlayerEvent extends Event {
    private final int currentPlayerIndex;

    /**
     * Informs the clients about the change of the current player
     * @param gameState current game state
     * @param clients List of all clients
     * @param currentPlayerIndex new current player
     */
    public UpdateCurrentPlayerEvent(GameState gameState, ArrayList<VirtualView> clients, int currentPlayerIndex) {
        super(gameState, clients);
        this.currentPlayerIndex = currentPlayerIndex;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateCurrentPlayer(currentPlayerIndex);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
