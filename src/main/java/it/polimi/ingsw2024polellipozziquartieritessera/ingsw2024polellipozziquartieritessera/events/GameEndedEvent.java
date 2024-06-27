package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameEndedEvent extends Event {
    /**
     * List of the winning players
     */
    private final ArrayList<Integer> winners;

    /**
     * Informs the clients about the end of the game and the winners
     * @param gameState Current game state
     * @param clients List of all clients
     * @param winners List of winning players
     */
    public GameEndedEvent(GameState gameState, ArrayList<VirtualView> clients, ArrayList<Integer> winners) {
        super(gameState, clients);
        this.winners = winners;
    }
    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateWinner(winners);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
