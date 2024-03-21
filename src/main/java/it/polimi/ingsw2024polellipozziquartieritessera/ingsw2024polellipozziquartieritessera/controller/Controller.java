package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.DrawType;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exception.InvalidHandException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.*;

import java.util.ArrayList;

public class Controller {

    // a player can draw a card from the
    // - 2 shared resources card
    // - 2 shared gold card
    // - resource deck
    // - gold deck
    // The side of the shared card is 1 by specific
    void drawSharedGold(GameState gameState, DrawType drawType) throws InvalidHandException {
        Board board = gameState.getMainBoard();
        Player currentPlayer = gameState.getCurrentPlayer();

        if (currentPlayer.getHand().values().stream().mapToInt(inHand -> (inHand ? 1 : 0)).sum() >= Config.MAX_HAND_CARDS - 1) {
            throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
        }

        switch(drawType) {
            case DrawType.DECKGOLD:
                drawGoldFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDGOLD1:
                drawGoldFromShared(board, currentPlayer, 0);
                break;
            case DrawType.SHAREDGOLD2:
                drawGoldFromShared(board, currentPlayer, 1);
                break;
            case DrawType.DECKRESOURCE:
                drawResourceFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDRESOURCE1:
                drawResourceFromShared(board, currentPlayer, 0);
                break;
            case DrawType.SHAREDRESOURCE2:
                drawResourceFromShared(board, currentPlayer, 1);
                break;
            default:
        }
    }

    void drawGoldFromDeck(Board board, Player currentPlayer) {
        GoldCard newGoldCard = board.getGoldDeck().getLast();
        board.getGoldDeck().removeLast();
        currentPlayer.getHand().put(newGoldCard.getId(), true);
    }

    void drawGoldFromShared(Board board, Player currentPlayer, int index) {
        GoldCard[] goldCards = board.getSharedGoldCards();
        GoldCard newGoldCard = goldCards[index];
        currentPlayer.getHand().put(newGoldCard.getId(), true);
        goldCards[index] = board.getGoldDeck().getLast();
    }

    void drawResourceFromDeck(Board board, Player currentPlayer) {
        ResourceCard newResourceCard = board.getResourceDeck().getLast();
        board.getResourceDeck().removeLast();
        currentPlayer.getHand().put(newResourceCard.getId(), true);
    }

    void drawResourceFromShared(Board board, Player currentPlayer, int index) {
        ResourceCard[] resourceCards = board.getSharedResourceCard();
        ResourceCard newResourceCard = resourceCards[index];
        currentPlayer.getHand().put(newResourceCard.getId(), true);
        resourceCards[index] = board.getResourceDeck().getLast();
    }

    void placeCard(GameState gameState, Player player, int placingCardId, int tableCardId, int configuration) {

    }

}
