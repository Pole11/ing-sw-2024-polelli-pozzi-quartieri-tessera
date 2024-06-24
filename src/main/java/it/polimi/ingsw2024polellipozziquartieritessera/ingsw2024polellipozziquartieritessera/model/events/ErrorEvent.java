package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class ErrorEvent extends Event {
    private final String error;

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
