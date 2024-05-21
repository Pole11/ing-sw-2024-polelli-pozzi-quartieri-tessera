package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateSecretObjectiveEvent extends Event{
    private final ArrayList<ObjectiveCard> objectiveCards;


    public UpdateSecretObjectiveEvent(GameState gameState, ArrayList<VirtualView> clients, ArrayList<ObjectiveCard> objectiveCards) {
        super(gameState, clients);
        this.objectiveCards = objectiveCards;
    }

    @Override
    public void execute() {
        for (VirtualView client: clients) {
            try {
                int id1, id2;
                if (objectiveCards.get(0) != null) {
                    id1 = objectiveCards.get(0).getId();
                } else {
                    id1 = -1;
                }
                if (objectiveCards.get(1) != null) {
                    id2 = objectiveCards.get(1).getId();
                } else{
                    id2 = -1;
                }
                client.updateSecretObjective(id1, id2);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
