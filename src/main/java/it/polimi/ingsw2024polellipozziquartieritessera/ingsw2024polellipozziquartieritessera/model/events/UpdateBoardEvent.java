package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateBoardEvent extends Event{
    private final Player player;
    private final int placingCardId;
    private final int tableCardId;
    private final CornerPos existingCornerPos;
    private final Side side;



    public UpdateBoardEvent(GameState gameState, ArrayList<VirtualView> clients, Player player, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) {
        super(gameState, clients);
        this.player = player;
        this.placingCardId = placingCardId;
        this.tableCardId = tableCardId;
        this.existingCornerPos = existingCornerPos;
        this.side = side;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients){
            try {
                client.updatePlayerBoard(gameState.getPlayerIndex(player), placingCardId, tableCardId, existingCornerPos, side);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
