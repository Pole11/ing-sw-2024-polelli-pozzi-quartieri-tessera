package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.CornerCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class UpdateBoardEvent extends Event{
    private final Player player;
    private final int placingCardId;
    private final int tableCardId;
    private final CornerPos existingCornerPos;



    public UpdateBoardEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, int placingCardId, int tableCardId, CornerPos existingCornerPos) {
        super(gameState, clients);
        this.player = player;
        this.placingCardId = placingCardId;
        this.tableCardId = tableCardId;
        this.existingCornerPos = existingCornerPos;
    }

    @Override
    public void execute() {

    }
}
