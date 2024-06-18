package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;

import java.io.IOException;
import java.util.*;
import java.util.Collections;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

public class Controller {
    GameState gameState;

    public void setGameState(GameState gameState) {
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
    public int getPlayerIndex(VirtualView client){
        return gameState.getPlayerIndex(client);
    }


    public GamePhase getGamePhase() {
        return this.gameState.getCurrentGamePhase();
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gameState.setCurrentGamePhase(gamePhase);
    }

    public TurnPhase getTurnPhase() {
        return this.gameState.getCurrentGameTurn();
    }

    public int getCurrentPlayerIndex(){
        return this.gameState.getCurrentPlayerIndex();
    }

    public String getPlayerNickname(int index){
        return this.gameState.getPlayer(index).getNickname();
    }

    public ObjectiveCard[] getObjectiveCardOptions(int playerId) {
        ObjectiveCard[] objectiveCardsOptions = new ObjectiveCard[Config.N_OBJECTIVE_CARD_OPTIONS];
        for (int i = 0; i < Config.N_OBJECTIVE_CARD_OPTIONS; i++) {
            objectiveCardsOptions[i] = this.gameState.getPlayer(playerId).getObjectiveCardOption(i);
        }
        return objectiveCardsOptions;
    }

    //settare players

    public void addPlayer(VirtualView client, String nickname) {
        synchronized (this.gameState) {
            gameState.addPlayer(nickname, client);
        }
    }

    //this is runned after gameState is populated with cards and players
    public void startGame(VirtualView client) {
        synchronized (this.gameState) {
            try {
                this.gameState.startGame(client);
            } catch (EmptyDeckException e) {
                //if in this moment of the game the deck is empty, it's a programming error
                throw new RuntimeException(e);
            }
        }
    }

    public void chooseInitialStarterSide(int playerIndex, Side side){
        synchronized (this.gameState) {
            gameState.setStarterSide(playerIndex, side);
        }
    }

    public void chooseInitialColor(int playerIndex, Color color) {
        synchronized (this.gameState) {
            gameState.setColor(playerIndex, color);
        }
    }

    public void chooseInitialObjective(int playerIndex, int cardIndex) {
        synchronized (this.gameState) {
            //cardId is 0 or 1, index of CardObjectiveOptions
            gameState.setSecretObjective(playerIndex, cardIndex);
        }
    }

    //----------------place, draw, flip-----------------------

    private void placeCardCheckings(Player player, CornerCard placingCard, int placingCardId,  Corner tableCorner, Corner placingCorner, Side placingCardSide) throws WrongInstanceTypeException, CardIsNotInHandException, CardAlreadPlacedException, WrongPlacingPositionException, CardAlreadyPresentOnTheCornerException, PlacingOnHiddenCornerException, GoldCardCannotBePlacedException {

        //sent to client
        if (placingCard instanceof StarterCard) throw new WrongInstanceTypeException("you cannot place a starter card in the middle of the game");

        //FORSE da togliere se mettiamo controllo su client, è un errore che può arrivare solo da cli
        if (!player.handCardContains(placingCardId)) throw new CardIsNotInHandException("the card you are trying to place is not in your hand");

        //check that the card is not already placed
        //FORSE da togliere se mettiamo controllo su client, è un errore che può arrivare solo da cli
        for (int i = 0; i < gameState.getPlayersSize(); i++){
            if (gameState.getPlayer(i).placedCardContains(placingCardId)) throw new CardAlreadPlacedException("you cannot place a card that is already placed");
        }

        //thrown as runtimeException in server
        if (tableCorner == null) throw new WrongPlacingPositionException("table corner is null");
        if (placingCorner == null) throw new WrongPlacingPositionException("placing corner is null");

        //sent to client
        if (tableCorner.getLinkedCorner() != null) throw new CardAlreadyPresentOnTheCornerException("you cannot place a card here, the corner is already linked");
        if (tableCorner.getHidden()) throw new PlacingOnHiddenCornerException("you cannot place a card on a hidden corner");

        // execute this block if the card is gold and has a challenge (is front)
        //sent to client
        if (!goldPlaceable(player, placingCard, placingCardSide)) throw new GoldCardCannotBePlacedException("You haven't the necessary resources to place the goldcard " + placingCardId);
    }


    public void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongPlacingPositionException, CardNotPlacedException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, PlacingOnHiddenCornerException, CardAlreadPlacedException, CardIsNotInHandException, WrongInstanceTypeException {
        synchronized (this.gameState) {
            Player player = gameState.getPlayer(playerIndex);
            // check that the card is in the hand of the player
            CornerPos placingCornerPos = switch (tableCornerPos) {
                case CornerPos.UPLEFT -> CornerPos.DOWNRIGHT;
                case CornerPos.UPRIGHT -> CornerPos.DOWNLEFT;
                case CornerPos.DOWNLEFT -> CornerPos.UPRIGHT;
                case CornerPos.DOWNRIGHT -> CornerPos.UPLEFT;
            };

            CornerCard placingCard = null;
            CornerCard tableCard = null;

            try {
                placingCard = (CornerCard) gameState.getCard(placingCardId);
                tableCard = (CornerCard) gameState.getCard(tableCardId);
            } catch (ClassCastException e){
                //sent to client
                throw new WrongInstanceTypeException("the card you selected is not a CornerCard");
            }

            Corner placingCorner = placingCard.getCorners(placingCardSide).get(placingCornerPos.ordinal());
            Corner tableCorner = tableCard.getCorners(player.getPlacedCardSide(tableCardId)).get(tableCornerPos.ordinal());

            placeCardCheckings(player, placingCard, placingCardId, tableCorner, placingCorner, placingCardSide);

            this.gameState.placeCard(player, placingCardId, tableCardId, tableCornerPos, placingCornerPos, placingCardSide);

            try {
                player.updatePlayerCardsMap(placingCardId, placingCard, tableCard, tableCardId, tableCornerPos, placingCardSide);
            } catch (WrongInstanceTypeException e) {
                //throw exception if placingCard is a starter card, it is already checked so if it happens here it's a programming mistake
                throw new RuntimeException(e);
            }

            player.addPoints(placingCard.calculatePoints(player));

            gameState.getCurrentGameTurn().changePhase(gameState);
        }
    }

    private boolean goldPlaceable(Player player, CornerCard placingCard, Side placingCardSide){
        synchronized (this.gameState) {
            boolean cardIsPlaceable = true;
            if (placingCard instanceof GoldCard && placingCardSide.equals(Side.FRONT)) {
                for (Element ele : Element.values()) {
                    if (player.getAllElements().get(ele) < player.getElementOccurrencies(((GoldCard) placingCard).getResourceNeeded(), ele)) {
                        cardIsPlaceable = false;
                        break;
                    }
                }
            }
            return cardIsPlaceable;
        }
    }


    public void drawCard(DrawType drawType) throws InvalidHandException, EmptyDeckException {
        synchronized (this.gameState) {
            Board board = this.gameState.getMainBoard();
            Player currentPlayer = this.gameState.getCurrentPlayer();

            if (currentPlayer.getHandSize() >= Config.MAX_HAND_CARDS) throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");
            GoldCard newGoldCard = null;
            ResourceCard newResourceCard = null;

            switch (drawType) {
                case DrawType.DECKGOLD:
                    // get new card
                    newGoldCard = board.drawFromGoldDeck();
                    // add card to player hand
                    currentPlayer.addToHandCardsMap(newGoldCard.getId(), Side.FRONT); // the front side is the default
                    break;
                case DrawType.SHAREDGOLD1:
                    newGoldCard = board.drawSharedGoldCard(1);
                    currentPlayer.addToHandCardsMap(newGoldCard.getId(), Side.FRONT);
                    break;
                case DrawType.SHAREDGOLD2:
                    newGoldCard = board.drawSharedGoldCard(2);
                    currentPlayer.addToHandCardsMap(newGoldCard.getId(), Side.FRONT);
                    break;
                case DrawType.DECKRESOURCE:
                    newResourceCard = board.drawFromResourceDeck();
                    currentPlayer.addToHandCardsMap(newResourceCard.getId(), Side.FRONT);
                    break;
                case DrawType.SHAREDRESOURCE1:
                    newResourceCard = board.drawSharedResourceCard(1);
                    currentPlayer.addToHandCardsMap(newResourceCard.getId(), Side.FRONT);
                    break;
                case DrawType.SHAREDRESOURCE2:
                    newResourceCard = board.drawSharedResourceCard(2);
                    currentPlayer.addToHandCardsMap(newResourceCard.getId(), Side.FRONT);
                    break;
                default:
            }
        }
        this.gameState.changeCurrentPlayer();
        gameState.checkGameEnded();
        gameState.updateMainBoard();
    }

    public void flipCard(int playerIndex, int cardId) throws CardIsNotInHandException {
        synchronized (this.gameState) {
            Player player = gameState.getPlayer(playerIndex);
            Side side;
            if (!player.handCardContains(cardId)) {
                throw new CardIsNotInHandException("The card" + cardId + " is not in the player" + playerIndex + "hand");
            }
            if (player.getHandCardSide(cardId).equals(Side.FRONT)) {
                side = Side.BACK;
            } else {
                side = Side.FRONT;
            }
            player.changeHandSide(cardId, side);}
    }

    public void addMessage(int playerIndex, String content){
        synchronized (this.gameState){
            gameState.addMessage(playerIndex, content);
        }
    }

    public synchronized void ping(VirtualView client){
        gameState.pingAnswer(client);
    }

    public void manageDisconnection(){
        gameState.updatePlayersConnected();
    }

/*
    public void setConnected(int index, boolean connected){
        synchronized (this.gameState) {
            gameState.setPlayersConnected(index, connected);
        }
    }

    public ArrayList<Integer> getHandId(int playerIndex){
        ArrayList<Integer> cardHandId = new ArrayList<>();
        cardHandId.addAll(gameState.getPlayer(playerIndex).getHandCardsMap().keySet());
        Collections.sort(cardHandId);
        return cardHandId;
    }

    public ArrayList<Side> getHandSide(int playerIndex){
        ArrayList<Integer> cardHandId = new ArrayList<>();
        cardHandId.addAll(gameState.getPlayer(playerIndex).getHandCardsMap().keySet());
        Collections.sort(cardHandId);

        ArrayList<Side> cardHandsSide = new ArrayList<>();
        for (int cardId : cardHandId){
            cardHandsSide.add(gameState.getPlayer(playerIndex).getHandCardsMap().get(cardId));
        }

        return cardHandsSide;
    }

 */
}