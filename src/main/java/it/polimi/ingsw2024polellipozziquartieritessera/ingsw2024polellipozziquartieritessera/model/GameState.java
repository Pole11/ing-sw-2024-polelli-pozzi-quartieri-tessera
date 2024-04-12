package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Main;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.*;

public class GameState {

    private final HashMap<Integer, Card> cardsMap; // map id and card
    private final Board mainBoard;
    private final ArrayList<Player> players; //player[0] is blackPlayer
    private final Chat chat;
    private int currentPlayerIndex;
    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;

    // CONSTRUCTOR
    public GameState(HashMap<Integer, Card> cardsMap, ArrayList<Player> players) throws NotUniquePlayerException, NotUniquePlayerNicknameException, NotUniquePlayerColorException {
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
        for (int i = 0; i < players.size(); i++) {
            for (int j = i+1; j < players.size(); j++) {
                if (players.get(i).getNickname().equals(players.get(j).getNickname())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean ColorsAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.size(); i++) {
            for (int j = i+1; j < players.size(); j++) {
                if (players.get(i).getColor().equals(players.get(j).getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean NicknameAndColorsAreValid() {
        // check if players are unique (by nickname and color)
        for (int i = 0; i < players.size(); i++) {
            for (int j = i+1; j < players.size(); j++) {
                if (players.get(i).getNickname().equals(players.get(j).getNickname()) && players.get(i).getColor().equals(players.get(j).getColor())) {
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

    public ArrayList<Player> getPlayers() {
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

    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos existingCornerPos, CornerPos placingCornerPos, Side side){

    }



//---------------get player----------------------

    // return the current player
    public Player getCurrentPlayer() {
        return players.get(this.currentPlayerIndex);
    }

    public Player getBlackPlayer(){
        return this.players.get(0);
    }



//--------------- get card from id ----------------------


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



//--------------All draw cards called from controller------------------


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




//-----------Initial setters called from startGame in controller--------------


    public void setStarters() {
        // for every player set his starters (you have access to every player from the array players)
        ArrayList<Player> players = getPlayers();
        // get all the starters
        ArrayList<StarterCard> starters = new ArrayList<>();
        
        for (int i = 0; i < this.cardsMap.size(); i++) {
            if (cardsMap.get(i) instanceof StarterCard) {
                starters.add((StarterCard) cardsMap.get(i));
            }
        }

        Random rand = new Random();
        int key = rand.nextInt(60);

        for (int i = 0; i < getPlayers().size(); i++) {
            StarterCard starterCard = starters.get((key + i) % starters.size());
            players.get(i).setStarterCard(starterCard);
        }
    }

    public void setSecretObjective(Player player, ObjectiveCard objectiveCard) {
        player.setObjectiveCard(objectiveCard);
    }

    public void setSharedGoldCards() {
        // it's like fillSharedGaps, look at the draw from shared methods to take inspiration
        GoldCard card1 = mainBoard.getFromGoldDeck();
        GoldCard card2 = mainBoard.getFromGoldDeck();

        GoldCard[] cards = {card1, card2};

        mainBoard.setSharedGoldCards(cards);
    }


    public void setSharedResourceCards() {
        ResourceCard card1 = mainBoard.getFromResourceDeck();
        ResourceCard card2 = mainBoard.getFromResourceDeck();

        ResourceCard[] cards = {card1, card2};

        mainBoard.setSharedResourceCard(cards);
    }


    public void setHands() {
        // popolate hands for every player
        for (int i = 0; i < getPlayers().size(); i++) {
            GoldCard goldCard = mainBoard.getFromGoldDeck();
            ResourceCard resourceCard1 = mainBoard.getFromResourceDeck();
            ResourceCard resourceCard2 = mainBoard.getFromResourceDeck();

            getPlayers().get(i).getHand().put(goldCard.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).getHand().put(resourceCard1.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).getHand().put(resourceCard2.getId(), Side.FRONT); // default is front side
        }
    }




//------------ others --------------------

    // !!! to implement !!!
    public void saveGameState(){
    }

    // !!! optional !!!
    public int getTurnTime(){
        return 0;
    }


}
