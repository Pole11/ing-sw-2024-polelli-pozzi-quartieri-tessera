package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class PingEvent extends Event {

    public PingEvent(GameState gameState, ArrayList<VirtualView> clients) {
        super(gameState, clients);
    }


    @Override
    public void execute() {
        System.out.println("I will ping to your mother and the following");
        System.out.println(clients.stream().map(e->gameState.getPlayer(gameState.getPlayerIndex(e)).getNickname()).toList());
        for (VirtualView clientIterator : this.clients) {
            try {
                System.out.println("i will ping " + gameState.getPlayer(gameState.getPlayerIndex(clientIterator)).getNickname());
                clientIterator.ping("ping");
                System.out.println("I finished pinging to " + gameState.getPlayer(gameState.getPlayerIndex(clientIterator)).getNickname());
            } catch (RemoteException e) {
                System.out.println("remote exception");
                playerDisconnected(clientIterator);
            }
        }
    }
}
