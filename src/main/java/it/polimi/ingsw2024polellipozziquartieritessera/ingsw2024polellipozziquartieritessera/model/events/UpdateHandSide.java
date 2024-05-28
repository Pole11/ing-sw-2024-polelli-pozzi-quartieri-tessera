package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateHandSide extends Event{
    private final int index;
    private final Side side;

    public UpdateHandSide(GameState gameState, ArrayList<VirtualView> clients, int index, Side side) {
        super(gameState, clients);
        this.index = index;
        this.side = side;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients){
            try {
                client.updateHandSide(index, side);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
