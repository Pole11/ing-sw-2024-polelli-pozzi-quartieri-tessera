package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

/**
 * Represents an abstract event in the game.
 */
public abstract class Event implements Cloneable {

    /**
     * List of clients associated with this event.
     */
    protected ArrayList<VirtualView> clients;

    /**
     * The current game state.
     */
    protected final GameState gameState;

    /**
     * Constructs an event with the given game state and clients.
     *
     * @param gameState The current game state.
     * @param clients   List of clients associated with this event.
     */
    public Event(GameState gameState, ArrayList<VirtualView> clients) {
        this.clients = new ArrayList<>(clients);
        this.gameState = gameState;
    }

    /**
     * Executes the event action.
     */
    public abstract void execute();

    /**
     * Handles disconnection of a player.
     *
     * @param client The client (virtual view) that disconnected.
     */
    public void playerDisconnected(VirtualView client) {
        if (gameState.getPlayer(gameState.getPlayerIndex(client)).isConnected()) {
            System.out.println("Client " + gameState.getPlayer(gameState.getPlayerIndex(client)).getNickname() + " disconnected because a remoteException was thrown");
            gameState.setPlayersConnected(gameState.getPlayerIndex(client), false);
            gameState.playerDisconnected(gameState.getPlayerIndex(client));
        }
    }

    public void setClients(ArrayList<VirtualView> clients){
        this.clients = clients;
    }

    public ArrayList<VirtualView> getClients(){
        return clients;
    }

    @Override
    public Event clone() throws CloneNotSupportedException {
        return (Event) super.clone();
    }
}
