package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ErrorEvent extends Event {
    /**
     * A string containing the error message
     */
    private final String error;

    /**
     *
     * @param gameState The current game state
     * @param clients List of clients associated with this event
     * @param error The string containing the error message
     */
    public ErrorEvent(GameState gameState, ArrayList<VirtualView> clients, String error) {
        super(gameState, clients);
        this.error = error;
    }
    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.sendError(error);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
