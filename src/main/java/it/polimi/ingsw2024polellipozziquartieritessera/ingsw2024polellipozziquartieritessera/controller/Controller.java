package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;

public class Controller {
    // probably we need an attribute for the game state

    // a player can draw a card from the
    // - 2 shared resources card
    // - 2 shared gold card
    // - resource deck
    // - gold deck
    // The side of the shared card is 1 by specific
    void drawCard(GameState gameState, DrawType drawType) throws InvalidHandException {
        // todo: change game phase and turn phase
        Board board = gameState.getMainBoard();
        Player currentPlayer = gameState.getCurrentPlayer();

        if (currentPlayer.getHand().values().size() >= Config.MAX_HAND_CARDS - 1) {
            throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
        }

        switch(drawType) {
            case DrawType.DECKGOLD:
                gameState.drawGoldFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDGOLD1:
                gameState.drawGoldFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDGOLD2:
                gameState.drawGoldFromShared(board, currentPlayer, 2);
                break;
            case DrawType.DECKRESOURCE:
                gameState.drawResourceFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDRESOURCE1:
                gameState.drawResourceFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDRESOURCE2:
                gameState.drawResourceFromShared(board, currentPlayer, 2);
                break;
            default:
        }
    }

    public void setSecretsObjective(GameState gameState) { // choose from one of the two objective shared cards

    }

    public void startGame(GameState gameState) {
        gameState.setStarters(); // set the starters cards for every player
        gameState.setSecretsObjectiveChoice(); // give every player two objective cards to choose from
        gameState.setSharedGoldCards();
        gameState.setSharedResources();
        gameState.setHands();
    }

    public void placeCard(GameState gameState, Player player, int placingCardId, int tableCardId, int configuration) {
        // todo: change game phase and turn phase
    }

    public void flipCard(Player player, int cardId){}

    public void openChat(){}

    public void addMessage(Player player, String content){}

}
