package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;

import java.util.ArrayList;
import java.util.Random;

public class Controller {
    GameState gameState;

    public Controller(GameState gameState) {
        this.gameState = gameState;
    }

    // a player can draw a card from the
    // - 2 shared resources card
    // - 2 shared gold card
    // - resource deck
    // - gold deck
    // The side of the shared card is 1 by specific


//-----------------to be left in controller---------------------

    // startGame() *chiama initGame nel model
    // chooseStarterSide() *riceve la scelta del side della starter
    // chooseObjective() *riceve la scelta dell'obbiettivo
    // chooseColor() *riceve la scelta del colore
    // placeCard() *avverte il model del piazzamento della carta
    // drawCard() *avverte il model del pescaggio di una carta
    // flipCard() *avverte il model del flipping della carta
    // ? sendMessage() *permette al model di salvare le informazioni del messaggio


    public void startGame(){
        this.gameState.startPhaseMethod();
    }

    public void chooseInitialStarterSide(int playerIndex, Side side){
        gameState.setStarterSide(playerIndex, side);
        gameState.chooseStarterSidePhase();
    }

    public void chooseInitialColor(int playerIndex, Color color){
        gameState.setColor(playerIndex, color);
        gameState.chooseColorPhase();
    }

    public void chooseInitialObjective(int playerIndex, int cardId) throws InvalidObjectiveCardException {
        gameState.setSecretObjective(playerIndex, cardId);
        gameState.chooseObjectivePhase();
    }


    //----------------place, draw, flip-----------------------

    public void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongInstanceTypeException, WrongPlacingPositionException, CardNotPlacedException, GoldCardCannotBePlaced, CardAlreadyPresent {
        Player player = gameState.getPlayerByIndex(playerIndex);
        // check that the card is in the hand of the player
        CornerPos placingCornerPos = switch (tableCornerPos) {
            case CornerPos.UPLEFT -> CornerPos.DOWNRIGHT;
            case CornerPos.UPRIGHT -> CornerPos.DOWNLEFT;
            case CornerPos.DOWNLEFT -> CornerPos.UPRIGHT;
            case CornerPos.DOWNRIGHT -> CornerPos.UPLEFT;
        };

        CornerCard placingCard = null;
        CornerCard tableCard = null;
        player.updateBoard(placingCardId, tableCardId, tableCornerPos);

        if (gameState.getCard(placingCardId) instanceof CornerCard ){
            placingCard = (CornerCard) gameState.getCard(placingCardId);
            //player.getCornerCardsMap().put(placingCardId, (CornerCard) gameState.getCard(placingCardId));
            //placingCard = player.getCornerCardsMap().get(placingCardId);
        } else {
            throw new WrongInstanceTypeException("placing card is not a CornerCard");
        }

        if (gameState.getCard(tableCardId) instanceof CornerCard ){
            tableCard = (CornerCard) gameState.getCard(tableCardId);
            //player.getCornerCardsMap().put(placingCardId, (CornerCard) gameState.getCard(tableCardId));
            //tableCard = player.getCornerCardsMap().get(tableCardId);
        } else {
            throw new WrongInstanceTypeException("table card is not a CornerCard");
        }

        player.placeCard(placingCardId, placingCard, tableCard, tableCardId, tableCornerPos, placingCardSide); // we might pass the game state map
        this.gameState.placeCard(player, placingCardId, tableCardId, tableCornerPos, placingCornerPos, placingCardSide);
        // place card must be runned at last because it needs the player already updated

        int newPoints = player.getPoints() + player.getCardPoints(placingCard);
        player.setPoints(newPoints);
    }


    void drawCard(DrawType drawType) throws InvalidHandException {
        this.gameState.setCurrentGameTurn(TurnPhase.DRAWPHASE); //dobbiamo valutare se si intende
        // la fase che c'è dopo o da ora in poi
        this.gameState.setCurrentPlayerIndex((this.gameState.getCurrentPlayerIndex() % this.gameState.getPlayers().size() ) + 1);
        Board board = this.gameState.getMainBoard();
        Player currentPlayer = this.gameState.getCurrentPlayer();

        if (currentPlayer.getHandCardsMap().values().size() >= Config.MAX_HAND_CARDS - 1) {
            throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
        }

        switch(drawType) {
            case DrawType.DECKGOLD:
                this.gameState.drawGoldFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDGOLD1:
                this.gameState.drawGoldFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDGOLD2:
                this.gameState.drawGoldFromShared(board, currentPlayer, 2);
                break;
            case DrawType.DECKRESOURCE:
                this.gameState.drawResourceFromDeck(board, currentPlayer);
                break;
            case DrawType.SHAREDRESOURCE1:
                this.gameState.drawResourceFromShared(board, currentPlayer, 1);
                break;
            case DrawType.SHAREDRESOURCE2:
                this.gameState.drawResourceFromShared(board, currentPlayer, 2);
                break;
            default:
        }
    }



    public void flipCard(int playerIndex, int cardId) {
        Player player = gameState.getPlayerByIndex(playerIndex);
        Side side;
        if (player.getHandCardsMap().get(cardId).equals(Side.FRONT)){
            side = Side.BACK;
        } else {
            side = Side.FRONT;
        }
        player.getHandCardsMap().replace(cardId, side);
    }

    public void openChat(){}

    public void addMessage(Player player, String content){}


}