package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateSharedObjectiveEvent extends Event {
    private final ArrayList<ObjectiveCard> objectiveCards;

    public UpdateSharedObjectiveEvent(GameState gameState, ArrayList<VirtualView> clients, ArrayList<ObjectiveCard> objectiveCards) {
        super(gameState, clients);
        this.objectiveCards = objectiveCards;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateSharedObjective(objectiveCards.get(0).getId(), objectiveCards.get(1).getId());
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
