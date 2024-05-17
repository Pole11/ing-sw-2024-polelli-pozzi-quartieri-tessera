package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class MessageEvent extends Event {
    private final String message;

    public MessageEvent(GameState gameState, ArrayList<VirtualView> clients, String message) {
        super(gameState, clients);
        this.message = message;
    }

    @Override
    public void execute() {
        for (VirtualView clientIterator : this.clients){
            try {
                clientIterator.ping(message);
            } catch (RemoteException e) {
                this.playerDisconnected(clientIterator);
            }
        }
    }
}
