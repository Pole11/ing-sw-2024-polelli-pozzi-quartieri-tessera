package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdatePointsEvent extends Event{
    private final Player player;
    private final int points;

    public UpdatePointsEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, int points) {
        super(gameState, clients);
        this.player = player;
        this.points = points;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients){
            try {
                client.updatePoints(gameState.getPlayerIndex(player), points);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
