package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class RedirectOutEvent extends Event {
    private final boolean bool;
    public RedirectOutEvent(GameState gameState, ArrayList<VirtualView> clients, boolean bool) {
        super(gameState, clients);
        this.bool = bool;
    }
    @Override
    public void execute() {
        try {
            clients.get(0).redirectOut(bool);
        }catch (RemoteException e){
            e.printStackTrace();
            System.out.println("redirect oout dico");
            playerDisconnected(clients.get(0));
        }
    }
}
