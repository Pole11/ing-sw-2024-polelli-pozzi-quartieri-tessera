package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import java.util.*;

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

    public void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongInstanceTypeException, WrongPlacingPositionException, CardNotPlacedException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, PlacingOnHiddenCornerException, CardAlreadPlacedException {
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

        if (gameState.getCard(placingCardId) instanceof CornerCard ){
            placingCard = (CornerCard) gameState.getCard(placingCardId);
        } else {
            throw new WrongInstanceTypeException("placing card is not a CornerCard");
        }

        if (gameState.getCard(tableCardId) instanceof CornerCard ){
            tableCard = (CornerCard) gameState.getCard(tableCardId);
        } else {
            throw new WrongInstanceTypeException("table card is not a CornerCard");
        }

        if (!player.getHandCardsMap().containsKey(placingCardId)) {
            //    throw new CardIsNotInHandException("the card you are trying to place is not in your hand");
        }

        // controlla che la carta non sia già presente
        for (Player p : gameState.getPlayers()) {
            if (p.getPlacedCardsMap().get(placingCardId) != null) {
                throw new CardAlreadPlacedException("you cannot place a card that is already placed");
            }
        }

        Corner placingCorner = placingCard.getCorners(placingCardSide)[placingCornerPos.getCornerPosValue()];
        Corner tableCorner = tableCard.getCorners(player.getPlacedCardsMap().get(tableCardId))[tableCornerPos.getCornerPosValue()];

        if (tableCorner.getLinkedCorner() != null){
            throw new CardAlreadyPresentOnTheCornerException("you cannot place a card here, the corner is already linked");
        }
        if (tableCorner.getHidden()){
            throw new PlacingOnHiddenCornerException("you cannot place a card on a hidden corner");
        }

        // execute this block if the card is gold and has a challenge (is front)
        if (placingCard instanceof GoldCard && placingCardSide.equals(Side.FRONT)){
            boolean cardIsPlaceable = true;

            for (Element ele : Element.values()) {
                if (player.getAllElements().get(ele) < player.getElementOccurencies(((GoldCard) placingCard).getResourceNeeded(), ele)) {
                    cardIsPlaceable = false;
                    break;
                }
            }

            if (!cardIsPlaceable){
                throw new GoldCardCannotBePlacedException("You haven't the necessary resources to place the goldcard " + placingCardId);
            }
        }

        if (placingCorner == null){
            throw new WrongPlacingPositionException("placing corner is null");
        }

        if (tableCorner == null){
            throw new WrongPlacingPositionException("table corner is null");
        }

        // check if the indirect corners are hidden
        ArrayList<ArrayList<Integer>> playerBoard = player.getPlayerBoard();
        for (Integer row = 0; row < playerBoard.size(); row++) {
            for (Integer col = 0; col < playerBoard.get(row).size(); col++) {
                if (playerBoard.get(row).get(col).equals(tableCardId)) { // if the card is the one on the table
                    int placingRow = row;
                    int placingCol = col;

                    switch (tableCornerPos) {
                        case CornerPos.UPLEFT -> placingRow--;
                        case CornerPos.UPRIGHT -> placingCol++;
                        case CornerPos.DOWNRIGHT -> placingRow++;
                        case CornerPos.DOWNLEFT -> placingCol--;
                    }

                    // check if there is a card in the up left corner
                    if (placingRow - 1 >= 0 && placingRow - 1 < playerBoard.size() && placingCol >= 0 && placingCol < playerBoard.getFirst().size() && playerBoard.get(placingRow - 1).get(placingCol) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow - 1).get(placingCol);
                        CornerCard matrixCard = (CornerCard) gameState.getCardsMap().get(matrixCardId); // use the keys of player.placedCardsMap
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.DOWNRIGHT.getCornerPosValue()];

                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }
                    }
                    // check if there is a card in the up right corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol + 1 >= 0 && placingCol + 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol + 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol + 1);
                        CornerCard matrixCard = (CornerCard) gameState.getCardsMap().get(matrixCardId);
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.DOWNLEFT.getCornerPosValue()];

                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }
                    }
                    // check if there is a card in the down right corner
                    if (placingRow + 1 >= 0 && placingRow + 1 < playerBoard.size() && placingCol >= 0 && placingCol < playerBoard.getFirst().size() && playerBoard.get(placingRow + 1).get(placingCol) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow + 1).get(placingCol);
                        CornerCard matrixCard = (CornerCard) gameState.getCardsMap().get(matrixCardId);
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.UPLEFT.getCornerPosValue()];

                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }
                    }

                    // check if there is a card in the down left corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol - 1 >= 0 && placingCol - 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol - 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol - 1);
                        CornerCard matrixCard = (CornerCard) gameState.getCardsMap().get(matrixCardId);
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.UPRIGHT.getCornerPosValue()];

                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }
                    }

                    break;
                }
            }
        }

        player.placeCard(placingCardId, placingCard, tableCard, tableCardId, tableCornerPos, placingCardSide);
        player.updateBoard(placingCardId, tableCardId, tableCornerPos);
        this.gameState.placeCard(player, placingCardId, tableCardId, tableCornerPos, placingCornerPos, placingCardSide);
        // place card of gameState must be runned at last because it needs the player already updated

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