package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class NicknameEvent extends Event {
    private final String nickname;
    /**
     * ID of the player who is setting his nickname
     */
    private final int playerIndex;

    /**
     * Informs all clients about the update of a nickname
     * @param gameState Current game state
     * @param clients List of all clients
     * @param playerIndex ID of the player who is setting his nickname
     * @param nickname String
     */
    public NicknameEvent(GameState gameState, ArrayList<VirtualView> clients, int playerIndex, String nickname) {
        super(gameState, clients);
        this.nickname = nickname;
        this.playerIndex = playerIndex;
    }

    @Override
    public void execute() {
        for (VirtualView clientIterator : this.clients) {
            try {
                clientIterator.nicknameUpdate(playerIndex, nickname);
            } catch (RemoteException e) {
                this.playerDisconnected(clientIterator);
            }
        }

    }
}


