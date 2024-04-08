package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.DrawType;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.InvalidHandException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;

public class Controller {

    // a player can draw a card from the
    // - 2 shared resources card
    // - 2 shared gold card
    // - resource deck
    // - gold deck
    // The side of the shared card is 1 by specific
    void drawCard(GameState gameState, DrawType drawType) throws InvalidHandException {
        Board board = gameState.getMainBoard();
        Player currentPlayer = gameState.getCurrentPlayer();

        if (currentPlayer.getHand().values().size() >= Config.MAX_HAND_CARDS - 1) {
            throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
        }

        switch(drawType) {
            case DrawType.DECKGOLD:
                drawGoldFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDGOLD1:
                drawGoldFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDGOLD2:
                drawGoldFromShared(board, currentPlayer, 2);
                break;
            case DrawType.DECKRESOURCE:
                drawResourceFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDRESOURCE1:
                drawResourceFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDRESOURCE2:
                drawResourceFromShared(board, currentPlayer, 2);
                break;
            default:
        }
    }

    private void drawGoldFromDeck(Board board, Player currentPlayer) {
        /*GoldCard newGoldCard = board.getGoldDeck().getLast();
        board.getGoldDeck().removeLast();
        currentPlayer.getHand().put(newGoldCard.getId(), true);*/

        // get new card
        GoldCard newGoldCard = board.getFromGoldDeck();
        // had card to player hand
        currentPlayer.getHand().put(newGoldCard.getId(), true);
    }

    private void drawGoldFromShared(Board board, Player currentPlayer, int index) {
        /*GoldCard[] goldCards = board.getSharedGoldCards();
        GoldCard newGoldCard = goldCards[index];
        currentPlayer.getHand().put(newGoldCard.getId(), true);
        goldCards[index] = board.getGoldDeck().getLast();*/

        // get new gold card
        GoldCard newGoldCard = board.getSharedGoldCard(index);
        currentPlayer.getHand().put(newGoldCard.getId(), true);
        // fill the gap
        board.fillSharedCardsGap();
    }

    private void drawResourceFromDeck(Board board, Player currentPlayer) {
        // get new card
        ResourceCard newResourceCard = board.getFromResourceDeck();
        // had card to player hand
        currentPlayer.getHand().put(newResourceCard.getId(), true);
    }

    private void drawResourceFromShared(Board board, Player currentPlayer, int index) {
        // get new resource card
        ResourceCard newResourceCard = board.getSharedResourceCard(index);
        currentPlayer.getHand().put(newResourceCard.getId(), true);
        // fill the gap
        board.fillSharedCardsGap();
    }

    public void placeCard(GameState gameState, Player player, int placingCardId, int tableCardId, int configuration) {}

    public void flipCard(Player player, int cardId){}

    public void openChat(){}

    public void addMessage(Player player, String content){}

}
