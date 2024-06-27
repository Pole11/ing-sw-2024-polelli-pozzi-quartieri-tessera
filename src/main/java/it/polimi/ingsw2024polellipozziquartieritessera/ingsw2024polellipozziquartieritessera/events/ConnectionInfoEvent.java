package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ConnectionInfoEvent extends Event {
    /**
     * The interested player
     */
    private final Player player;
    /**
     * Information about the connection / disconnection of a player
     */
    private final boolean connected;

    /**
     * Informs all the clients about the connection / disconnection of a player
     * @param gameState Current game state
     * @param clients All connected clients
     * @param player The player which the information is about
     * @param connected Connected / Disconnected
     */
    public ConnectionInfoEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, boolean connected) {
        super(gameState, clients);
        this.player = player;
        this.connected = connected;
    }
    @Override
    public void execute() {
        for (VirtualView client : clients) {
            if (client != null) {
                try {
                    client.connectionInfo(gameState.getPlayerIndex(player), connected);
                } catch (RemoteException e) {
                    playerDisconnected(client);
                }
            }
        }
    }
}
