package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.CornerCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class UpdateBoardEvent extends Event{
    private final int playerIndex;
    private final CornerCard cornerCard;
    private final Side side;


    public UpdateBoardEvent(GameState gameState, ArrayList<VirtualView> clients, int playerIndex, CornerCard cornerCard, Side side) {
        super(gameState, clients);
        this.playerIndex = playerIndex;
        this.cornerCard = cornerCard;
        this.side = side;
    }

    @Override
    public void execute() {

    }
}
