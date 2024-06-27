package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Message;

import java.rmi.*;
import java.util.ArrayList;

/**
 * This interface defines the methods that a virtual view can expose to interact remotely with a client.
 * Each method corresponds to a specific update or action that can be communicated between the server and client.
 */
public interface VirtualView extends Remote {

    /**
     * Ping method to test connection and responsiveness.
     *
     * @param ping A ping message.
     * @throws RemoteException If there is a communication error.
     */
    void ping(String ping) throws RemoteException;

    /**
     * Pong method to acknowledge a ping.
     *
     * @throws RemoteException If there is a communication error.
     */
    void pong() throws RemoteException;

    /**
     * Sends an error message to the client.
     *
     * @param error The error message to send.
     * @throws RemoteException If there is a communication error.
     */
    void sendError(String error) throws RemoteException;

    /**
     * Sends the index of a player.
     *
     * @param index The index of the player.
     * @throws RemoteException If there is a communication error.
     */
    void sendIndex(int index) throws RemoteException;

    /**
     * Updates the nickname of a player.
     *
     * @param index The index of the player.
     * @param nickname The updated nickname.
     * @throws RemoteException If there is a communication error.
     */
    void nicknameUpdate(int index, String nickname) throws RemoteException;

    /**
     * Updates the game phase to the next phase.
     *
     * @param nextGamePhase The next game phase.
     * @throws RemoteException If there is a communication error.
     */
    void updateGamePhase(GamePhase nextGamePhase) throws RemoteException;

    /**
     * Updates the turn phase to the next phase.
     *
     * @param nextTurnPhase The next turn phase.
     * @throws RemoteException If there is a communication error.
     */
    void updateTurnPhase(TurnPhase nextTurnPhase) throws RemoteException;

    /**
     * Provides information about the connection status of a player.
     *
     * @param playerIndex The index of the player.
     * @param connected True if connected, false otherwise.
     * @throws RemoteException If there is a communication error.
     */
    void connectionInfo(int playerIndex, boolean connected) throws RemoteException;

    /**
     * Updates the addition of a card to a player's hand.
     *
     * @param playerIndex The index of the player.
     * @param cardIndex The index of the card added to the hand.
     * @throws RemoteException If there is a communication error.
     */
    void updateAddHand(int playerIndex, int cardIndex) throws RemoteException;

    /**
     * Updates the removal of a card from a player's hand.
     *
     * @param playerIndex The index of the player.
     * @param cardIndex The index of the card removed from the hand.
     * @throws RemoteException If there is a communication error.
     */
    void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException;

    /**
     * Updates the player's board with a new card placement.
     *
     * @param playerIndex The index of the player.
     * @param placingCardId The ID of the card being placed.
     * @param tableCardId The ID of the card on the table.
     * @param existingCornerPos The existing corner position of the card on the table.
     * @param side The side of the card being placed.
     * @throws RemoteException If there is a communication error.
     */
    void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException;

    /**
     * Updates the color assigned to a player.
     *
     * @param playerIndex The index of the player.
     * @param color The color assigned to the player.
     * @throws RemoteException If there is a communication error.
     */
    void updateColor(int playerIndex, Color color) throws RemoteException;

    /**
     * Updates the current player's index.
     *
     * @param currentPlayerIndex The index of the current player.
     * @throws RemoteException If there is a communication error.
     */
    void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException;

    /**
     * Updates the side of a hand card.
     *
     * @param cardIndex The index of the card in the hand.
     * @param side The side of the card.
     * @throws RemoteException If there is a communication error.
     */
    void updateHandSide(int cardIndex, Side side) throws RemoteException;

    /**
     * Updates the points of a player.
     *
     * @param playerIndex The index of the player.
     * @param points The updated points of the player.
     * @throws RemoteException If there is a communication error.
     */
    void updatePoints(int playerIndex, int points) throws RemoteException;

    /**
     * Updates the secret objective cards for the game.
     *
     * @param objectiveCardId1 The ID of the first secret objective card.
     * @param objectiveCardId2 The ID of the second secret objective card.
     * @throws RemoteException If there is a communication error.
     */
    void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException;

    /**
     * Updates the shared objective cards for the game.
     *
     * @param sharedObjectiveCardId1 The ID of the first shared objective card.
     * @param sharedObjectiveCardId2 The ID of the second shared objective card.
     * @throws RemoteException If there is a communication error.
     */
    void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException;

    /**
     * Updates the main board with shared gold and resource cards.
     *
     * @param sharedGoldCard1 The ID of the first shared gold card.
     * @param sharedGoldCard2 The ID of the second shared gold card.
     * @param sharedResourceCard1 The ID of the first shared resource card.
     * @param sharedResourceCard2 The ID of the second shared resource card.
     * @param firstGoldDeckCard The ID of the first gold deck card.
     * @param firstResourceDeckCard The ID of the first resource deck card.
     * @throws RemoteException If there is a communication error.
     */
    void updateMainBoard(int sharedGoldCard1, int sharedGoldCard2, int sharedResourceCard1, int sharedResourceCard2, int firstGoldDeckCard, int firstResourceDeckCard) throws RemoteException;

    /**
     * Updates the starter card for a player.
     *
     * @param playerIndex The index of the player.
     * @param cardId1 The ID of the starter card.
     * @param side The side of the starter card, or null if undecided.
     * @throws RemoteException If there is a communication error.
     */
    void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException;

    /**
     * Updates the winner(s) of the game.
     *
     * @param playerIndexes The indexes of the winning player(s).
     * @throws RemoteException If there is a communication error.
     */
    void updateWinner(ArrayList<Integer> playerIndexes) throws RemoteException;

    /**
     * Updates the element held by a player.
     *
     * @param playerIndex The index of the player.
     * @param element The element held by the player.
     * @param numberOfElements The number of elements held.
     * @throws RemoteException If there is a communication error.
     */
    void updateElement(int playerIndex, Element element, int numberOfElements) throws RemoteException;

    /**
     * Updates the chat with a new message.
     *
     * @param playerIndex The index of the player who sent the message.
     * @param content The content of the message.
     * @throws RemoteException If there is a communication error.
     */
    void updateChat(int playerIndex, String content) throws RemoteException;

    /**
     * Redirects the standard output to a remote location.
     *
     * @param bool True to redirect output, false to disable redirection.
     * @throws RemoteException If there is a communication error.
     */
    void redirectOut(boolean bool) throws RemoteException;
}