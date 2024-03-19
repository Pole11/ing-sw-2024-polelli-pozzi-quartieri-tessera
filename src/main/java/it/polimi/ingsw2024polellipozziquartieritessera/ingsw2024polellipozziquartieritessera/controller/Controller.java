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
    // Refactor this method to use less duplicate code
    void drawSharedGold(GameState gameState, DrawType drawType) throws InvalidHandException {
        Board board = gameState.getMainBoard();
        Player currentPlayer = gameState.getCurrentPlayer();
        switch(drawType) {
            case DrawType.DECKGOLD:
                ArrayList<GoldCard> goldDeck = board.getGoldDeck();
                GoldCard goldCard = goldDeck.getLast();
                goldDeck.removeLast();
                if (currentPlayer.getHand().values().stream().mapToInt(inHand -> (inHand ? 1 : 0)).sum() >= Config.MAX_HAND_CARDS - 1) {
                    throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
                }
                currentPlayer.getHand().put(goldCard.getId(), true);
                break;
            case DrawType.SHAREDGOLD1:

                break;
            case DrawType.SHAREDGOLD2:
                break;
            case DrawType.DECKRESOURCE:
                ArrayList<ResourceCard> sharedDeck = board.getResourceDeck();
                ResourceCard resourceCard = sharedDeck.getLast();
                sharedDeck.removeLast();
                if (currentPlayer.getHand().values().stream().mapToInt(inHand -> (inHand ? 1 : 0)).sum() >= Config.MAX_HAND_CARDS - 1) {
                    throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
                }
                currentPlayer.getHand().put(resourceCard.getId(), true);
                break;
            case DrawType.SHAREDRESOURCE1:
                break;
            case DrawType.SHAREDRESOURCE2:
                break;
            default:
        }
    }

    private void fillSharedGap() {

    }


}
