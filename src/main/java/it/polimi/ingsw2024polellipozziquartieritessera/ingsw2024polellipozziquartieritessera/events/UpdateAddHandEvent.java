package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateAddHandEvent extends Event {
    /**
     * Owner of the hand
     */
    private final Player player;
    /**
     * CardId
     */
    private final int index;

    /**
     * Informs all clients about the drawn of a card
     * @param gameState Current game state
     * @param clients List of clients to be informed
     * @param player The player who drew the card
     * @param index CardID
     */
    public UpdateAddHandEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, int index) {
        super(gameState, clients);
        this.player = player;
        this.index = index;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateAddHand(gameState.getPlayerIndex(player), index);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
