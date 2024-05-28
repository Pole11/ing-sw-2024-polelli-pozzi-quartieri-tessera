package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Board;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class updateMainBoardEvent extends Event{
    private final Board mainBoard;

    public updateMainBoardEvent(GameState gameState, ArrayList<VirtualView> clients, Board mainBoard) {
        super(gameState, clients);
        this.mainBoard = mainBoard;
    }

    @Override
    public void execute() {
        int sharedGoldCard1 = mainBoard.getSharedGoldCard(0).getId();
        int sharedGoldCard2 = mainBoard.getSharedGoldCard(1).getId();
        int sharedResourceCard1 = mainBoard.getSharedResourceCard(0).getId();
        int sharedResourceCard2 = mainBoard.getSharedResourceCard(1).getId();
        int firtGoldDeckCard = mainBoard.getFirstGoldDeckCard().getId();
        int firstResourceDeckCard = mainBoard.getFirstResourceDeckCard().getId();

        for (VirtualView client : clients) {
            try {
                client.updateMainBoard(sharedGoldCard1, sharedGoldCard2, sharedResourceCard1, sharedResourceCard2, firtGoldDeckCard, firstResourceDeckCard);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
