package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateColorEvent extends Event {
    private final Player player;
    private final Color color;

    /**
     * Informs all clients about the setting of a player's color
     * @param gameState current game state
     * @param clients List of all clients
     * @param player Player who is updating his color
     * @param color Chosen color
     */
    public UpdateColorEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, Color color) {
        super(gameState, clients);
        this.player = player;
        this.color = color;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateColor(gameState.getPlayerIndex(player), color);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
