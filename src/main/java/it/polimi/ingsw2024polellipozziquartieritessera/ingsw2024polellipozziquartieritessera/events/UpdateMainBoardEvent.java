package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Board;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public class UpdateMainBoardEvent extends Event {
    private final Board mainBoard;

    /**
     * Informs the clients about a change in the main board
     * @param gameState current game state
     * @param clients List of all clients
     * @param mainBoard new main board
     */
    public UpdateMainBoardEvent(GameState gameState, ArrayList<VirtualView> clients, Board mainBoard) {
        super(gameState, clients);
        this.mainBoard = mainBoard;
    }

    @Override
    public void execute() {
        int sharedGoldCard1;
        try {
            sharedGoldCard1 = mainBoard.getSharedGoldCard(0).getId();
        } catch (NullPointerException e) {
            sharedGoldCard1 = -1;
        }
        int sharedGoldCard2;
        try {
            sharedGoldCard2 = mainBoard.getSharedGoldCard(1).getId();
        } catch (NullPointerException e) {
            sharedGoldCard2 = -1;
        }
        int sharedResourceCard1;
        try {
            sharedResourceCard1 = mainBoard.getSharedResourceCard(0).getId();
        } catch (NullPointerException e) {
            sharedResourceCard1 = -1;
        }
        int sharedResourceCard2;
        try {
            sharedResourceCard2 = mainBoard.getSharedResourceCard(1).getId();
        } catch (NullPointerException e) {
            sharedResourceCard2 = -1;
        }
        int firstGoldDeckCard;
        try {
            firstGoldDeckCard = mainBoard.getFirstGoldDeckCard().getId();
        } catch (NoSuchElementException e) {
            firstGoldDeckCard = -1;
        }
        int firstResourceDeckCard;
        try {
            firstResourceDeckCard = mainBoard.getFirstResourceDeckCard().getId();
        } catch (NoSuchElementException e) {
            firstResourceDeckCard = -1;
        }
        for (VirtualView client : clients) {
            try {
                client.updateMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firstGoldDeckCard, firstResourceDeckCard);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
