package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class UpdateSharedObjectiveEvent extends Event{
    private final ObjectiveCard objectiveCard;

    public UpdateSharedObjectiveEvent(GameState gameState, ArrayList<VirtualView> clients, ObjectiveCard objectiveCard) {
        super(gameState, clients);
        this.objectiveCard = objectiveCard;
    }

    @Override
    public void execute() {

    }
}
