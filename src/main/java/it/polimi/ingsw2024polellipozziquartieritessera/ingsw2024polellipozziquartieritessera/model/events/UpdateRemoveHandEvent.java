package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateRemoveHandEvent extends Event{
    private final Player player;
    private final int index;

    public UpdateRemoveHandEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, int index) {
        super(gameState, clients);
        this.player = player;
        this.index = index;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients){
            try {
                client.updateRemoveHand(gameState.getPlayerIndex(player), index);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}