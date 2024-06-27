package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class SendIndexEvent extends Event {
    /**
     * PlayerIndex
     */
    private final int id;

    /**
     * informs a client about his index
     * @param gameState current game state
     * @param clients Player
     * @param id new indexPlayer
     */
    public SendIndexEvent(GameState gameState, ArrayList<VirtualView> clients, int id) {
        super(gameState, clients);
        this.id = id;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.sendIndex(id);
            } catch (RemoteException e) {
                this.playerDisconnected(client);
            }
        }
    }
}
