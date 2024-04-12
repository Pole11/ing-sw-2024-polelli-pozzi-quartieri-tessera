package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;

import java.util.ArrayList;
import java.util.Random;

public class Controller {
    // a player can draw a card from the
    // - 2 shared resources card
    // - 2 shared gold card
    // - resource deck
    // - gold deck
    // The side of the shared card is 1 by specific
    void drawCard(DrawType drawType) throws InvalidHandException {
        Main.gameState.setCurrentGameTurn(TurnPhase.DRAWPHASE); //dobbiamo valutare se si intende
        // la fase che c'è dopo o da ora in poi
        Main.gameState.setCurrentPlayerIndex((Main.gameState.getCurrentPlayerIndex() % Main.gameState.getPlayers().size() ) + 1);
        Board board = Main.gameState.getMainBoard();
        Player currentPlayer = Main.gameState.getCurrentPlayer();

        if (currentPlayer.getHand().values().size() >= Config.MAX_HAND_CARDS - 1) {
            throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
        }

        switch(drawType) {
            case DrawType.DECKGOLD:
                Main.gameState.drawGoldFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDGOLD1:
                Main.gameState.drawGoldFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDGOLD2:
                Main.gameState.drawGoldFromShared(board, currentPlayer, 2);
                break;
            case DrawType.DECKRESOURCE:
                Main.gameState.drawResourceFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDRESOURCE1:
                Main.gameState.drawResourceFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDRESOURCE2:
                Main.gameState.drawResourceFromShared(board, currentPlayer, 2);
                break;
            default:
        }
    }

    public void startGame() {
        // populate in main already runned, after that and after that all players are in the game,
        // they decide to start playing and this script begins
        Main.gameState.setStarters(); // set the starters cards for every player
        setSecretObjectiveOptions(); // give every player two objective cards to choose from
        Main.gameState.setSharedGoldCards();
        Main.gameState.setSharedResourceCards();
        Main.gameState.setHands();
    }

    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) {
        // check that the card is in the hand of the player
        Main.gameState.setCurrentGameTurn(TurnPhase.PLACINGPHASE);
        CornerPos placingCornerPos = null;
        switch (existingCornerPos){
            case CornerPos.UPLEFT:
                placingCornerPos = CornerPos.DOWNRIGHT;
                break;
            case CornerPos.UPRIGHT:
                placingCornerPos = CornerPos.DOWNLEFT;
                break;
            case CornerPos.DOWNLEFT:
                placingCornerPos = CornerPos.UPRIGHT;
                break;
            case CornerPos.DOWNRIGHT:
                placingCornerPos = CornerPos.UPLEFT;
        }

        Main.gameState.placeCard(player, placingCardId, tableCardId, existingCornerPos, placingCornerPos, side);
        player.updateBoard(placingCardId, tableCardId, existingCornerPos);
        player.updateCardsMaps(placingCardId, side);


    }

    public void flipCard(Player player, int cardId){}

    public void openChat(){}

    public void addMessage(Player player, String content){}

    private void setSecretObjectiveOptions(){
        // for every player set two objectives cards to choose from
        ArrayList<Player> players = Main.gameState.getPlayers();
        // get all the starters
        ArrayList<ObjectiveCard> objectives = new ArrayList<>();

        for (int i = 0; i < Main.gameState.getCardsMap().size(); i++) {
            if (Main.gameState.getCardsMap().get(i) instanceof ObjectiveCard) {
                objectives.add((ObjectiveCard) Main.gameState.getCardsMap().get(i));
            }
        }

        Random rand = new Random();
        int key = rand.nextInt(98);

        // get 8 consequences cards from a random point (make it more random later maybe)
        ObjectiveCard[] objectiveCards = new ObjectiveCard[2];
        for (int i = 0; i < Main.gameState.getPlayers().size(); i++) {
            objectiveCards[0] = objectives.get((key + i) % objectives.size());
            objectiveCards[1] = objectives.get((key + i + Main.gameState.getPlayers().size()) % objectives.size());
            // [..., ..., P1, P2, P3, P4, P1, P2, P3, P4, ...]
            // [P2, P3, P4, ..., ..., ..., P1, P2, P3, P4, P1]

            players.get(i).setSecretObjectiveCardOptions(objectiveCards);
        }
    }

    public void setSecretObjective(Player player, ObjectiveCard objectiveCard) {
        Main.gameState.setSecretObjective(player, objectiveCard);
    }

}
