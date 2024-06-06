package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class GameEndedEvent extends Event{
    private final ArrayList<Integer> winners;

    public GameEndedEvent(GameState gameState, ArrayList<VirtualView> clients, ArrayList<Integer> winners) {
        super(gameState, clients);
        this.winners = winners;
    }

    @Override
    public void execute() {
        for (VirtualView client: clients) {
            try {
                client.updateWinner(winners);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
