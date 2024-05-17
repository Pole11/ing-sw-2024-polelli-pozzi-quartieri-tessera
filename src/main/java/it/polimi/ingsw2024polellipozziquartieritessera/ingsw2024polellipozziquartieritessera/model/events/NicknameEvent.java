package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class NicknameEvent extends Event{
    private final String nickname;

    public NicknameEvent(GameState gameState, ArrayList<VirtualView> clients, String nickname) {
        super(gameState, clients);
        this.nickname = nickname;
    }

    @Override
    public void execute() {
        VirtualView client = clients.getLast();

        try {
            client.changePhase(GamePhase.NICKNAMEPHASE.toString());

            if (clients.size() == 1) {
                client.sendMessage("you successfully entered the game with the nickname " + nickname + ", wait for at least two players to start the game");
            } else {
                client.sendMessage("you successfully entered the game with the nickname " + nickname + ", there are " + clients.size() + " players connected, to start the game type START");
                for (int i = 0; i < gameState.getPlayersSize() -1 ; i++){
                    client.nicknameUpdate(gameState.getPlayerIndex(client), gameState.getPlayer(i).getNickname());
                }
            }
        } catch (RemoteException e) {
            System.out.println("client" + gameState.getPlayerIndex(client) + "is disconnected");
            gameState.setPlayersConnected(gameState.getPlayerIndex(client), false);
        }

        for (VirtualView clientIterator : this.clients) {
            if (!clientIterator.equals(client)){
                try {
                    clientIterator.nicknameUpdate(gameState.getPlayerIndex(client), nickname);
                } catch (RemoteException e){
                    this.playerDisconnected(clientIterator);
                }
            }
        }

    }
}


