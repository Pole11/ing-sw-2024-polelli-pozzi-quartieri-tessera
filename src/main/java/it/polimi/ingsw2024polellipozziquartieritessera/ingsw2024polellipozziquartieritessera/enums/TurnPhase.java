package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events.UpdateTurnPhaseEvent;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;

/**
 * Enumeration of the turn actions
 */
public enum TurnPhase {
    PLACINGPHASE,
    DRAWPHASE;

    /**
     * Changes the phase of the game.
     *
     * @param gameState The current game state.
     */
    public void changePhase(GameState gameState) {
        TurnPhase nextPhase;

        // Determine the next phase based on the current phase
        if (this.equals(PLACINGPHASE)) {
            nextPhase = DRAWPHASE;
        } else {
            nextPhase = PLACINGPHASE;
        }

        // Add an event to the event queue to update the turn phase
        synchronized (gameState.getEventQueue()) {
            gameState.addToEventQueue(new UpdateTurnPhaseEvent(gameState, gameState.allConnectedClients(), nextPhase));
            gameState.getEventQueue().notifyAll();
        }

        // Set the current game turn to the next phase
        gameState.setCurrentGameTurn(nextPhase);
        System.out.println("The turn phase has changed: " + nextPhase);
    }
}