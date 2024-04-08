package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import java.util.HashMap;

public class GameState {

    private final HashMap<Integer, Card> cardsMap; // map id and card
    private final Board mainBoard;
    private final Player[] players; //player[0] is blackPlayer
    private final Chat chat;
    private int currentPlayerIndex;
    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;

    // CONSTRUCTOR
    public GameState(HashMap<Integer, Card> cardsMap, Player[] players) throws NotUniquePlayerException, NotUniquePlayerNicknameException, NotUniquePlayerColorException {
        this.cardsMap = cardsMap;
        this.mainBoard = new Board();
        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentGamePhase = GamePhase.MAINPHASE;
        this.currentGameTurn = null;
        this.chat = new Chat();

        if (!NicknameAndColorsAreValid()) {
            throw new NotUniquePlayerException("While creating the GameState Object I encountered a problem regarding the creation of players with the same nickname AND the same color");
        }
        if (!NicknamesAreValid()) {
            throw new NotUniquePlayerNicknameException("While creating the GameState Object I encountered a problem regarding the creation of players with the same nickname");
        }
        if (!ColorsAreValid()) {
            throw new NotUniquePlayerColorException("While creating the GameState Object I encountered a problem regarding the creation of players with the same color");
        }
    }

    // TESTING

    private boolean NicknamesAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.length; i++) {
            for (int j = i+1; j < players.length; j++) {
                if (players[i].getNickname().equals(players[j].getNickname())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean ColorsAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.length; i++) {
            for (int j = i+1; j < players.length; j++) {
                if (players[i].getColor().equals(players[j].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean NicknameAndColorsAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.length; i++) {
            for (int j = i+1; j < players.length; j++) {
                if (players[i].getNickname().equals(players[j].getNickname()) && players[i].getColor().equals(players[j].getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    // GETTER
    public HashMap<Integer, Card> getCardsMap() {
        return cardsMap;
    }

    public Board getMainBoard() {
        return mainBoard;
    }

    public Player[] getPlayers() {
        return players;
    }

    public Chat getChat() {
        return chat;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public GamePhase getCurrentGamePhase() {
        return currentGamePhase;
    }

    public TurnPhase getCurrentGameTurn() {
        return currentGameTurn;
    }

    // SETTER
    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public void setCurrentGamePhase(GamePhase currentGamePhase) {
        this.currentGamePhase = currentGamePhase;
    }

    public void setCurrentGameTurn(TurnPhase currentGameTurn) {
        this.currentGameTurn = currentGameTurn;
    }

    // METHODS
    // return the current player
    public Player getCurrentPlayer() {
        return this.getPlayers()[this.currentPlayerIndex];
    }

    public Player getBlackPlayer(){
        return this.players[0];
    }

    // returns the specific Card pointer given his id
    public Card getCard(int id){
        return this.getCardsMap().get(id);
    }
    public CornerCard getCornerCard(int id){
        Card card = this.getCardsMap().get(id);
        if (card instanceof CornerCard) {
            return (CornerCard) card;
        }
        return null;
    }
    public ObjectiveCard getObjectiveCard(int id){
        Card card = this.getCardsMap().get(id);
        if (card instanceof ObjectiveCard) {
            return (ObjectiveCard) card;
        }
        return null;
    }

    // !!! to implement !!!
    public void saveGameState(){
    }

    // !!! optional !!!
    public int getTurnTime(){
        return 0;
    }


    public void drawGoldFromDeck(Board board, Player currentPlayer) {
        /*GoldCard newGoldCard = board.getGoldDeck().getLast();
        board.getGoldDeck().removeLast();
        currentPlayer.getHand().put(newGoldCard.getId(), true);*/

        // get new card
        GoldCard newGoldCard = board.getFromGoldDeck();
        // add card to player hand
        currentPlayer.getHand().put(newGoldCard.getId(), Side.FRONT); // the front side is the default
    }

    public void drawGoldFromShared(Board board, Player currentPlayer, int index) {
        /*GoldCard[] goldCards = board.getSharedGoldCards();
        GoldCard newGoldCard = goldCards[index];
        currentPlayer.getHand().put(newGoldCard.getId(), true);
        goldCards[index] = board.getGoldDeck().getLast();*/

        // get new gold card
        GoldCard newGoldCard = board.getSharedGoldCard(index);
        currentPlayer.getHand().put(newGoldCard.getId(), Side.FRONT);
        // fill the gap
        board.fillSharedCardsGap();
    }

    public void drawResourceFromDeck(Board board, Player currentPlayer) {
        // get new card
        ResourceCard newResourceCard = board.getFromResourceDeck();
        // had card to player hand
        currentPlayer.getHand().put(newResourceCard.getId(), Side.FRONT);
    }

    public void drawResourceFromShared(Board board, Player currentPlayer, int index) {
        // get new resource card
        ResourceCard newResourceCard = board.getSharedResourceCard(index);
        currentPlayer.getHand().put(newResourceCard.getId(), Side.FRONT);
        // fill the gap
        board.fillSharedCardsGap();
    }
}
