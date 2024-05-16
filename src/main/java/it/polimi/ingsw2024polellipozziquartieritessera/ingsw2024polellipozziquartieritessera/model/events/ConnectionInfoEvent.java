package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public class ConnectionInfoEvent extends Event{
    String nickname;
    boolean connected;

    public ConnectionInfoEvent(GameState gameState, ArrayList<VirtualView> clients, String nickname, boolean connected) {
        super(gameState, clients);
        this.nickname = nickname;
        this.connected = connected;
    }

    @Override
    public void execute() {

    }
}
