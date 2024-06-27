package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class RedirectOutEvent extends Event {
    /**
     * If true: redirect output on an unused buffer, if false: redirect output on System.out
     */
    private final boolean bool;

    /**
     * Changes the client's output buffer in order to mask useless prints, such as the one made by restoreView
     * @param gameState Current game state
     * @param clients Client who is in restore
     * @param bool If true: redirect output on an unused buffer, if false: redirect output on System.out
     */
    public RedirectOutEvent(GameState gameState, ArrayList<VirtualView> clients, boolean bool) {
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
