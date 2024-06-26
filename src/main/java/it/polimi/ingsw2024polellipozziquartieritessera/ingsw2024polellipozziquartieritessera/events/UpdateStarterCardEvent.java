package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateStarterCardEvent extends Event {
    private final int playerIndex;
    private final int cardId;
    private final Side side;


    public UpdateStarterCardEvent(GameState gameState, ArrayList<VirtualView> clients, int playerIndex, int cardId, Side side) {
        super(gameState, clients);
        this.playerIndex = playerIndex;
        this.cardId = cardId;
        this.side = side;
    }


    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateStarterCard(playerIndex, cardId, side);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
