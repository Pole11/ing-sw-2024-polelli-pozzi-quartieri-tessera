package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.CornerPos;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateBoardEvent extends Event implements Serializable {
    private final Player player;
    private final int placingCardId;
    private final int tableCardId;
    private final CornerPos existingCornerPos;
    private final Side side;
    /**
     * for PlacedEventList, updateBoard.clients needs to be set after construction
     */
    private ArrayList<VirtualView> restoreClients;

    /**
     * Informs the clients about the placing of a card
     * @param gameState current game state
     * @param clients List of clients to be informed
     * @param player Player who is placing
     * @param placingCardId Card that is going to be placed
     * @param tableCardId Card that is going to be covered
     * @param existingCornerPos Position of the covered corner
     * @param side Side of the placing card
     */
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
        ArrayList<VirtualView> actualClients;
        if (clients.isEmpty()) {
            actualClients = restoreClients;
        } else {
            actualClients = clients;
        }
        for (VirtualView client : actualClients) {
            try {
                client.updatePlayerBoard(gameState.getPlayerIndex(player), placingCardId, tableCardId, existingCornerPos, side);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }

    public int getPlayerIndex() {
        return gameState.getPlayerIndex(player);
    }

    public int getPlacingCardId() {
        return placingCardId;
    }

    public int getTableCardId() {
        return tableCardId;
    }

    public CornerPos getExistingCornerPos() {
        return existingCornerPos;
    }

    public Side getSide() {
        return side;
    }

    public void setRestoreClients(ArrayList<VirtualView> restoreClients) {
        this.restoreClients = restoreClients;
    }
}
