package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.CornerCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class UpdateAddHandEvent extends Event{
    private final Player player;
    private final int index;
    private final Side side;

    public UpdateAddHandEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, int index, Side side) {
        super(gameState, clients);
        this.player = player;
        this.index = index;
        this.side = side;
    }

    @Override
    public void execute() {

    }
}
