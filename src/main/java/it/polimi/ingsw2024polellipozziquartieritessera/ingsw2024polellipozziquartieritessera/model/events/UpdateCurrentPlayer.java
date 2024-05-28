package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateCurrentPlayer extends Event {
    private final int currentPlayerIndex;

    public UpdateCurrentPlayer(GameState gameState, ArrayList<VirtualView> clients, int currentPlayerIndex) {
        super(gameState, clients);
        this.currentPlayerIndex = currentPlayerIndex;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateCurrentPlayer(currentPlayerIndex);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
