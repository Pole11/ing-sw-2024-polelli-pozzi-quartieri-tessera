package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ConnectionInfoEvent extends Event{
    private final Player player;
    private final boolean connected;

    public ConnectionInfoEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, boolean connected) {
        super(gameState, clients);
        this.player = player;
        this.connected = connected;
    }

    @Override
    public void execute() {
        for (VirtualView client :clients){
            if (client.equals(player.getClient())){
                try {
                    //TODO: nuova fase
                    client.connectionInfo(gameState.getPlayerIndex(player), connected);
                    client.updateGamePhase(GamePhase.NICKNAMEPHASE);
                } catch (RemoteException e) {
                    playerDisconnected(client);
                }
            } else {
                try {
                    client.connectionInfo(gameState.getPlayerIndex(player), connected);
                } catch (RemoteException e) {
                    playerDisconnected(client);
                }
            }
        }
    }
}
