package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;


import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.UpdateGamePhaseEvent;

/**
 * Enumeration of the phases of the game
 */
public enum GamePhase {
    NICKNAMEPHASE,
    CHOOSESTARTERSIDEPHASE,
    CHOOSECOLORPHASE,
    CHOOSEOBJECTIVEPHASE,
    MAINPHASE,
    ENDPHASE,  // last rounds
    FINALPHASE, // calculate points and declare winner
    TIMEOUT; //there is one or zero player connected

    /**
     * Changes the current game phase to the next phase.
     *
     * @param gameState The current game state.
     */
    public void changePhase(GameState gameState) {
        // Determine the next game phase
        GamePhase nextPhase = GamePhase.values()[this.ordinal() + 1];

        synchronized (gameState.getEventQueue()) {
            // Add event to the event queue and notify all waiting threads
            gameState.addToEventQueue(new UpdateGamePhaseEvent(gameState, gameState.allConnectedClients(), nextPhase));
            gameState.getEventQueue().notifyAll();
        }

        // Update the current game phase
        gameState.setCurrentGamePhase(nextPhase);
        System.out.println("The game phase has changed: " + nextPhase);
    }
}