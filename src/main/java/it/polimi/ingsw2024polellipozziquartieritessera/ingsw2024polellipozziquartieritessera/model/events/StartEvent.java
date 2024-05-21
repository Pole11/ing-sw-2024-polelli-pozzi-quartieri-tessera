package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class StartEvent extends Event{

    public StartEvent(GameState gameState, ArrayList<VirtualView> clients) {
        super(gameState, clients);
    }

    @Override
    public void execute() {
        for (VirtualView client : clients){
            try {
                client.start();
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
