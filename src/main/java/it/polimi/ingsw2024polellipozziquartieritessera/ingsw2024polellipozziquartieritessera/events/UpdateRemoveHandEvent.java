package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateRemoveHandEvent extends Event {
    private final Player player;
    private final int index;

    /**
     * Informs clients of the removing of card in a player's hand
     * @param gameState current game state
     * @param clients List of all clients
     * @param player Owner of the hand
     * @param index Removed card ID
     */
    public UpdateRemoveHandEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, int index) {
        super(gameState, clients);
        this.player = player;
        this.index = index;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateRemoveHand(gameState.getPlayerIndex(player), index);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
