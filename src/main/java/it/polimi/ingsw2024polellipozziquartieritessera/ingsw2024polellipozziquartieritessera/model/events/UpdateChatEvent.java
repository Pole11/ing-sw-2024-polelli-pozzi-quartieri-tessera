package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateChatEvent extends Event {
    int playerIndex;
    String content;
    public UpdateChatEvent(GameState gameState, ArrayList<VirtualView> clients, int playerIndex, String content) {
        super(gameState, clients);
        this.playerIndex = playerIndex;
        this.content = content;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateChat(playerIndex, content);
            } catch (RemoteException e) {
                playerDisconnected(client);
            }
        }
    }
}
