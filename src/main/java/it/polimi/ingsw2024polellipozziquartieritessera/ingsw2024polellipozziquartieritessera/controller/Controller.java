package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
lueOf(e.toString().toUpperCase()));
                    }
                    gameState.setGoldCard(id,
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
        return this.gameState.getPlayer(playerId).getObjectiveCardOptions();
    }

    //settare players

    public void addPlayer(String nickname) throws NotUniquePlayerNicknameException {
        synchronized (this.gameState) {
            gameState.setPlayer(gameState.getPlayers().size(), new Player(nickname));
            if (gameState.getPlayers().size() >= Config.MAX_PLAYERS){
                startGame();
            }
        }
    }

    //this is runned after gameState is populated with cards and players
    public void startGame(){
        synchronized (this.gameState) {
            this.gameState.startPhaseMethod();
        }
    }

    public void chooseInitialStarterSide(int playerIndex, Side side){
        synchronized (this.gameState) {
            gameState.setStarterSide(playerIndex, side);
            //gameState.chooseStarterSidePhase();
        }
    }

    public void chooseInitialColor(int playerIndex, Color color) throws NotUniquePlayerColorException {
        synchronized (this.gameState) {
            gameState.setColor(playerIndex, color);
            //gameState.chooseColorPhase();
        }
    }

    public void chooseInitialObjective(int playerIndex, int cardIndex) throws InvalidObjectiveCardException {
        synchronized (this.gameState) {
            //cardId is 0 or 1, index of CardObjectiveOptions
            gameState.setSecretObjective(playerIndex, cardIndex);
            //gameState.chooseObjectivePhase();
        }
    }

    public void colorChoosed(){
        synchronized (this.gameState) {
            gameState.setHands();
            gameState.setObjectives();
        }
    }

    //----------------place, draw, flip-----------------------

    public void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws WrongInstanceTypeException, WrongPlacingPositionException, CardNotPlacedException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, PlacingOnHiddenCornerException, CardAlreadPlacedException, CardIsNotInHandException {
        synchronized (this.gameState) {
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
            try {
                placingCard = (CornerCard) gameState.getCard(placingCardId);
                tableCard = (CornerCard) gameState.getCard(tableCardId);
            } catch (ClassCastException e){
                throw new WrongInstanceTypeException("the card you selected is not a CornerCard");
            }

            if (placingCard instanceof StarterCard) throw new WrongInstanceTypeException("you cannot place a starter card in the middle of the game");


            Corner placingCorner = placingCard.getCorners(placingCardSide).get(placingCornerPos.ordinal());
            Corner tableCorner = tableCard.getCorners(player.getPlacedCardsMap().get(tableCardId)).get(tableCornerPos.ordinal());

            if (!player.getHandCardsMap().containsKey(placingCardId)) throw new CardIsNotInHandException("the card you are trying to place is not in your hand");

            // controlla che la carta non sia già presente
            for (Player p : gameState.getPlayers()) {
                if (p.getPlacedCardsMap().get(placingCardId) != null) throw new CardAlreadPlacedException("you cannot place a card that is already placed");
            }

            if (tableCorner == null) throw new WrongPlacingPositionException("table corner is null");
            if (tableCorner.getLinkedCorner() != null) throw new CardAlreadyPresentOnTheCornerException("you cannot place a card here, the corner is already linked");
            if (tableCorner.getHidden()) throw new PlacingOnHiddenCornerException("you cannot place a card on a hidden corner");
            if (placingCorner == null) throw new WrongPlacingPositionException("placing corner is null");

            // execute this block if the card is gold and has a challenge (is front)
            if (!goldPlaceable(player, placingCard, placingCardSide)) throw new GoldCardCannotBePlacedException("You haven't the necessary resources to place the goldcard " + placingCardId);

            this.gameState.placeCard(player, placingCardId, tableCardId, tableCornerPos, placingCornerPos, placingCardSide);
            player.placeCard(placingCardId, placingCard, tableCard, tableCardId, tableCornerPos, placingCardSide);
            player.updateBoard(placingCardId, tableCardId, tableCornerPos);
            gameState.updateElements(player, placingCard, placingCardSide);

            int newPoints = placingCard.calculatePoints(player);

            player.setPoints(player.getPoints() + newPoints);
            gameState.setCurrentGameTurn(TurnPhase.DRAWPHASE);
        }
    }

    private boolean goldPlaceable(Player player, CornerCard placingCard, Side placingCardSide){
        synchronized (this.gameState) {
            boolean cardIsPlaceable = true;
            if (placingCard instanceof GoldCard && placingCardSide.equals(Side.FRONT)) {
                for (Element ele : Element.values()) {
                    if (player.getAllElements().get(ele) < player.getElementOccurencies(((GoldCard) placingCard).getResourceNeeded(), ele)) {
                        cardIsPlaceable = false;
                        break;
                    }
                }
            }
            return cardIsPlaceable;
        }
    }


    public void drawCard(DrawType drawType) throws InvalidHandException {
        synchronized (this.gameState) {
            // non so se vada messo qui
            this.gameState.setCurrentGameTurn(TurnPhase.DRAWPHASE); //dobbiamo valutare se si intende
            // la fase che c'è dopo o da ora in poi
            Board board = this.gameState.getMainBoard();
            Player currentPlayer = this.gameState.getCurrentPlayer();

            if (currentPlayer.getHandCardsMap().values().size() >= Config.MAX_HAND_CARDS)
                throw new InvalidHandException("Player " + currentPlayer + " has too many cards in hand");

            //RICORDARSI DI CONTROLLARE SE LA CHIAMATA ARRIVA DAL CURRENT PLAYER
            switch (drawType) {
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
            this.gameState.setCurrentPlayerIndex(((this.gameState.getCurrentPlayerIndex() + 1) % this.gameState.getPlayers().size()));
            //if (gameState.isConnected(getCurrentPlayerIndex()))
            //da mettere in while, va al successivo giocatore se quello è disconnesso e controlla se sono tutti disconnessi, che stoppi il gioco
        }
    }

    public void flipCard(int playerIndex, int cardId) throws CardIsNotInHandException {
        synchronized (this.gameState) {
            Player player = gameState.getPlayerByIndex(playerIndex);
            Side side;
            if (!player.getHandCardsMap().containsKey(cardId)) {
                throw new CardIsNotInHandException("The card" + cardId + " is not in the player" + playerIndex + "hand");
            }
            if (player.getHandCardsMap().get(cardId).equals(Side.FRONT)) {
                side = Side.BACK;
            } else {
                side = Side.FRONT;
            }
            player.getHandCardsMap().replace(cardId, side);
        }
    }

    public void openChat(){}

    public void addMessage(int playerIndex, String content){}


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
}