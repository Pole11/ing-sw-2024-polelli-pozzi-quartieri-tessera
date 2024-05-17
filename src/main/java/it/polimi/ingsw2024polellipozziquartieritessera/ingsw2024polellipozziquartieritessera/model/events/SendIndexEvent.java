package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class SendIndexEvent extends Event{
    private final int id;

    public SendIndexEvent(GameState gameState, ArrayList<VirtualView> clients, int id) {
        super(gameState, clients);
        this.id = id;
    }

    @Override
    public void execute() {
        for (VirtualView client: clients) {
            try {
                client.sendIndex(id);
            } catch (RemoteException e) {
                this.playerDisconnected(client);
            }
        }
    }
}
