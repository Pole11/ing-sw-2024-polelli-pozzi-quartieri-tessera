package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateHandSideEvent extends Event {
    private final int index;
    private final Side side;

    /**
     * Informs clients about the change of the side of a card
     * @param gameState Current game state
     * @param clients Owner of the hand
     * @param index Card ID
     * @param side new side
     */

    public UpdateHandSideEvent(GameState gameState, ArrayList<VirtualView> clients, int index, Side side) {
        super(gameState, clients);
        this.index = index;
        this.side = side;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateHandSide(index, side);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
