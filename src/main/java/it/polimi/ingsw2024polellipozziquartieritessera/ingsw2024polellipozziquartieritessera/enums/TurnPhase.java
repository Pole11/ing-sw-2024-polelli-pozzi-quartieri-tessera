package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateGamePhaseEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateTurnPhaseEvent;

public enum TurnPhase {
    PLACINGPHASE,
    DRAWPHASE;


    public void changePhase(GameState gameState){
        //TOOD: non cambia bene, mantiene lo stesso
        TurnPhase nextPhase;
        if (this.equals(PLACINGPHASE)){
            nextPhase = DRAWPHASE;
        } else {
            nextPhase = PLACINGPHASE;
        }
        synchronized (gameState.getEventQueue()){
            gameState.addToEventQueue(new UpdateTurnPhaseEvent(gameState, gameState.allConnectedClients(), nextPhase));
            gameState.getEventQueue().notifyAll();
        }
        gameState.setCurrentGameTurn(nextPhase);
        System.out.println("the turnPhase has changed: " + nextPhase);
    }
}