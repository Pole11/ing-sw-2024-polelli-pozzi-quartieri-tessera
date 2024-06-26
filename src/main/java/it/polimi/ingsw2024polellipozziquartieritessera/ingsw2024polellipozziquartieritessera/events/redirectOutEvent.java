package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class redirectOutEvent extends Event {
    private final boolean bool;

    public redirectOutEvent(GameState gameState, ArrayList<VirtualView> clients, boolean bool) {
        super(gameState, clients);
        this.bool = bool;
    }

    @Override
    public void execute() {
        try {
            clients.get(0).redirectOut(bool);
        } catch (RemoteException e) {
            playerDisconnected(clients.get(0));
        }
    }
}
