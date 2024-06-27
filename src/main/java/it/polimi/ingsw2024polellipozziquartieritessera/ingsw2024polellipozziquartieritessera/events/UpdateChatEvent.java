package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateChatEvent extends Event {
    int playerIndex;
    String content;

    /**
     * Informs all clients about a new message in chat
     * @param gameState Current game state
     * @param clients List of all clients
     * @param playerIndex Author of the message
     * @param content Text of the message
     */
    public UpdateChatEvent(GameState gameState, ArrayList<VirtualView> clients, int playerIndex, String content) {
        super(gameState, clients);
        this.playerIndex = playerIndex;
        this.content = content;
    }

    @Override
    public void execute() {
        String decodedContent = content.replace("~", " ");
        for (VirtualView client : clients) {
            try {
                client.updateChat(playerIndex, decodedContent);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
