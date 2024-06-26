package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateSecretObjectiveEvent extends Event {
    private final ArrayList<ObjectiveCard> objectiveCards;


    public UpdateSecretObjectiveEvent(GameState gameState, ArrayList<VirtualView> clients, ArrayList<ObjectiveCard> objectiveCards) {
        super(gameState, clients);
        this.objectiveCards = objectiveCards;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            int id1 = -1, id2 = -1;
            switch (objectiveCards.size()) {
                case 1:
                    id1 = objectiveCards.getFirst().getId();
                    break;
                case 2:
                    id1 = objectiveCards.get(0).getId();
                    id2 = objectiveCards.get(1).getId();
                    break;
            }
            try {
                client.updateSecretObjective(id1, id2);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
