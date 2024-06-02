package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public abstract class Event {
    //non so se sia giusto proteceted
    protected final ArrayList<VirtualView> clients;
    protected final GameState gameState;

    public Event(GameState gameState, ArrayList<VirtualView> clients) {
        this.clients = new ArrayList<>(clients);
        this.gameState = gameState;
    }

    public abstract void execute();

    public void playerDisconnected(VirtualView client){
        if (gameState.getPlayer(gameState.getPlayerIndex(client)).isConnected()){
            System.out.println("client " + gameState.getPlayerIndex(client) + " disconnected");
            gameState.setPlayersConnected(gameState.getPlayerIndex(client), false);
            gameState.playerDisconnected();
        }
    }
}
