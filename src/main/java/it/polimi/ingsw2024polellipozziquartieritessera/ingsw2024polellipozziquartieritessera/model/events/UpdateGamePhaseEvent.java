package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateGamePhaseEvent extends Event{
    private final GamePhase gamePhase;

    public UpdateGamePhaseEvent(GameState gameState, ArrayList<VirtualView> clients, GamePhase gamePhase) {
        super(gameState, clients);
        this.gamePhase = gamePhase;
    }

    @Override
    public void execute() {
        for (VirtualView client: clients) {
            try {
                client.updateGamePhase(gamePhase);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
