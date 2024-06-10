package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;


import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateGamePhaseEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.ArrayList;

public enum GamePhase {
    NICKNAMEPHASE,
    CHOOSESTARTERSIDEPHASE,
    CHOOSECOLORPHASE,
    CHOOSEOBJECTIVEPHASE,
    MAINPHASE,
    ENDPHASE,  // last rounds
    FINALPHASE, // calculate points and declare winner
    TIMEOUT; //there is one or zero player connected

    public void changePhase(GameState gameState){
        GamePhase nextPhase = GamePhase.values()[this.ordinal() + 1];
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateGamePhaseEvent(gameState, gameState.allConnectedClients(), nextPhase));
            gameState.getEventQueue().notifyAll();
        }
        gameState.setCurrentGamePhase(nextPhase);
    }
}