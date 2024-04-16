package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.Challenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;

import java.lang.reflect.Array;
import java.util.*;

public class GameState {

    private final HashMap<Integer, Card> cardsMap; // map id and card
    private final Board mainBoard;
    private final ArrayList<Player> players; //player[0] is blackPlayer
    private final Chat chat;
    private int currentPlayerIndex;
    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;
    private Boolean isLastTurn;
    int numberAnswered = 0;

    // CONSTRUCTOR
    public GameState(HashMap<Integer, Card> cardsMap, ArrayList<Player> players) throws NotUniquePlayerException, NotUniquePlayerNicknameException, NotUniquePlayerColorException {
        this.cardsMap = cardsMap;
        this.mainBoard = new Board();
        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentGamePhase = GamePhase.MAINPHASE;
        this.currentGameTurn = null;
        this.chat = new Chat();
        this.isLastTurn = false;

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

    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos tableCornerPos, CornerPos placingCornerPos, Side side){
        Side tableSide = player.getBoardSide(tableCardId);
        Corner placingCorner = ((CornerCard) cardsMap.get(placingCardId)).getCorners(side)[placingCornerPos.getCornerPosValue()];
        Corner tableCorner = ((CornerCard) cardsMap.get(tableCardId)).getCorners(tableSide)[tableCornerPos.getCornerPosValue()];
        placingCorner.setLinkedCorner(tableCorner);
        tableCorner.setCovered(false);// is false at default anyway
        tableCorner.setLinkedCorner(placingCorner);
        tableCorner.setCovered(true);
    }

    //------------ Starting Phases flow --------------------

    // I: start game / set phase
    public void startPhaseMethod() {
        this.currentGamePhase = GamePhase.STARTPHASE;

        // next phase
        deckInitPhase();
    }




    // II: initialize deck and shared cards / starter cards
    private void deckInitPhase(){
        this.getMainBoard().shuffleCards();
        this.setSharedGoldCards();
        this.setSharedResourceCards();
        this.setStarters(); // set the starters cards for every player
        this.chooseStarterSidePhase();
    }

    //III: let players choose the side
    public void chooseStarterSidePhase(){
        if (numberAnswered == players.size()-1) {
            numberAnswered = 0;
            chooseColorPhase();
        } else {
            // tell clients that they have to decide the starterSide
            // and that it is the turn of players[numberAnswered] to choose
            numberAnswered ++;
        }

    }

    // IV: let the player choose the color from the available colors
    // V: set hands and objectives for choice
    public void chooseColorPhase(){
        if (numberAnswered == players.size()-1) {
            numberAnswered = 0;
            setHands();
            setObjectives();
            chooseObjectivePhase();
        } else {
            // tell clients that they have to decide the color
            // and that it is the turn of players[numberAnswered] to choose
            // remember that you have pass the color available
            numberAnswered ++;
        }
    }

    // VI: let the player choose the objective
    public void chooseObjectivePhase(){
        if (numberAnswered == players.size()-1) {
            numberAnswered = 0;
            currentGamePhase = GamePhase.MAINPHASE;
            currentGameTurn = TurnPhase.PLACINGPHASE;
            playTurn();
        } else {
            // tell clients that they have to decide the objective
            // and that it is the turn of players[numberAnswered] to choose
            numberAnswered ++;
        }
    }

    // VII: si potrebbe come da specifica scegliere a questo punto
    // un giocatore randomico per essere il blackPlayer (al momento sembra superflua)
    // ma fa schifo

    //------------ Starting Phases setter --------------------


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
            players.get(i).initializeBoard(); // add the starter card to the board of the player
        }
    }

    public void setStarterSide(int playerIndex, Side side){
        Player player = getPlayerByIndex(playerIndex);
        player.getPlacedCardsMap().put(player.getStarterCard().getId(), side); // s
    }

    public void setColor(int playerIndex, Color color){//method called by view when the player chooses the side
        Player player = getPlayerByIndex(playerIndex);
        player.setColor(color);
    }

    public void setHands() {
        // popolate hands for every player
        for (int i = 0; i < getPlayers().size(); i++) {
            GoldCard goldCard = mainBoard.getFromGoldDeck();
            ResourceCard resourceCard1 = mainBoard.getFromResourceDeck();
            ResourceCard resourceCard2 = mainBoard.getFromResourceDeck();

            getPlayers().get(i).getHandCardsMap().put(goldCard.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).getHandCardsMap().put(resourceCard1.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).getHandCardsMap().put(resourceCard2.getId(), Side.FRONT); // default is front side
        }
    }

    //set SecretObjectiveOptions and SharedObjectiveCards
    private void setObjectives(){
        // for every player set two objectives cards to choose from
        ArrayList<Player> players = getPlayers();
        // get all the starters
        ArrayList<ObjectiveCard> objectives = new ArrayList<>();

        for (int i = 0; i < getCardsMap().size(); i++) {
            if (getCardsMap().get(i) instanceof ObjectiveCard) {
                objectives.add((ObjectiveCard) getCardsMap().get(i));
            }
        }

        // TODO: shuffle objective card deck

        Random rand = new Random();
        int key = rand.nextInt(98);

        // get 8 consequences cards from a random point (make it more random later maybe)
        ObjectiveCard[] objectiveCards = new ObjectiveCard[2];
        for (int i = 0; i < getPlayers().size(); i++) {
            objectiveCards[0] = objectives.get((key + i) % objectives.size());
            objectiveCards[1] = objectives.get((key + i + getPlayers().size()) % objectives.size());
            // [..., ..., P1, P2, P3, P4, P1, P2, P3, P4, ...]
            // [P2, P3, P4, ..., ..., ..., P1, P2, P3, P4, P1]

            players.get(i).setSecretObjectiveCardOptions(objectiveCards);
        }
        mainBoard.getSharedObjectiveCards()[0]=objectives.get((key + 4) % objectives.size());
        mainBoard.getSharedObjectiveCards()[1]=objectives.get((key + 4 + getPlayers().size()) % objectives.size());
    }


    public void setSecretObjective (int playerIndex, int cardId) throws InvalidObjectiveCardException {
        Player player = getPlayerByIndex(playerIndex);
        if (player.getObjectiveCardOptions()[0].getId() == cardId) {
            player.setObjectiveCard(player.getObjectiveCardOptions()[0]);
        } else if (player.getObjectiveCardOptions()[1].getId() == cardId) {
            player.setObjectiveCard(player.getObjectiveCardOptions()[1]);
        } else {
            throw new InvalidObjectiveCardException("The card choosen was not in the options list");
        }
    }




    //-------------------Game Phase flow---------------------

    private void playTurn(){
        // notify current player to place card (notify all changes)
        // wait
        this.setCurrentGameTurn(TurnPhase.DRAWPHASE);
        // notify current player to draw a card (notify all changes)
        //wait

        // Base Case: game ended
        if (isGameEnded()){
            this.setCurrentGamePhase(GamePhase.ENDPHASE);
            this.setCurrentGameTurn(TurnPhase.PLACINGPHASE);
            playEndTurn();
        }

        // play next turn
        else{
            // update current player
            this.setCurrentPlayerIndex((this.getCurrentPlayerIndex()+1) % this.getPlayers().size());
            this.setCurrentGameTurn(TurnPhase.PLACINGPHASE);

            playTurn();
        }
    }

    private void playEndTurn(){
        // BASE CASE:
        if (this.getPlayers().get(getCurrentPlayerIndex()) == this.getBlackPlayer()){
            if (isLastTurn) {
                this.setCurrentGamePhase(GamePhase.FINALPHASE);
                calculateFinalPoints();

            }
            else {
                isLastTurn = true;
            }
        }

        // Recursive call of the EndPhaseTurn
        else{
            // notify current player to place card (notify all changes)
            // wait
            this.setCurrentGameTurn(TurnPhase.DRAWPHASE);
            // notify current player to draw a card (notify all changes)
            //wait

            // play next turn
            // update current player
            this.setCurrentPlayerIndex((this.getCurrentPlayerIndex()+1) % this.getPlayers().size());
            this.setCurrentGameTurn(TurnPhase.PLACINGPHASE);

            playEndTurn();
        }
    }

    private Boolean isGameEnded() {
        // 20
        for (int i = 0; i < this.getPlayers().size(); i++) {
            if (this.getPlayers().get(i).getPoints() >= 20) {
                return true;
            }
        }
        // fine carte
        if (this.getMainBoard().getResourceDeck().isEmpty()) {
            return true;
        }
        return this.getMainBoard().getGoldDeck().isEmpty();
    }

    private void calculateFinalPoints() {
        for (int i = 0; i < this.getPlayers().size(); i++) {
            try {
                int points = this.getPlayers().get(i).getCardPoints(this.getPlayers().get(i).getObjectiveCard());
                this.getPlayers().get(i).addPoints(points);
            } catch(WrongInstanceTypeException e) {

            }
        }
    }

    private ArrayList<Integer> getWinnerPlayerIndex() {
        int maxPoints = 0;
        ArrayList<Integer> winnerPlayerIndeces = new ArrayList<>();
        for (int i = 0; i < this.getPlayers().size(); i++) {
            if (this.getPlayers().get(i).getPoints() > maxPoints) {
                winnerPlayerIndeces = new ArrayList<>();
                winnerPlayerIndeces.add(i);
            } else if (this.getPlayers().get(i).getPoints() == maxPoints) {
                winnerPlayerIndeces.add(i);
            }
        }

        // now winnerPlayerIndeces contains who has the maximum number of points
        // decide the real winner
        int maxChallengesWon = 0;
        ArrayList<Integer> winnerObjectives = new ArrayList<>();
        if (winnerPlayerIndeces.size() > 1) {
            int mostObjectivesWins = -1;
            for (int i=0; i<winnerPlayerIndeces.size(); i++) {
                if (this.getPlayers().get(i).getObjectivesWon() > mostObjectivesWins){
                    winnerObjectives.clear();
                    winnerObjectives.add(i);
                    mostObjectivesWins = this.getPlayers().get(i).getObjectivesWon();
                }
                else if (this.getPlayers().get(i).getObjectivesWon() == mostObjectivesWins){
                    winnerObjectives.add(i);
                }
            }
            winnerPlayerIndeces = winnerObjectives;
        }

        return(winnerPlayerIndeces);
    }

//---------------get player----------------------

    // return the current player
    public Player getCurrentPlayer() {
        return players.get(this.currentPlayerIndex);
    }

    public Player getBlackPlayer(){
        return this.players.getFirst();
    }

    public Player getPlayerByIndex(int index){return this.getPlayers().get(index);    }

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
        currentPlayer.getHandCardsMap().put(newGoldCard.getId(), Side.FRONT); // the front side is the default
    }

    public void drawGoldFromShared(Board board, Player currentPlayer, int index) {
        /*GoldCard[] goldCards = board.getSharedGoldCards();
        GoldCard newGoldCard = goldCards[index];
        currentPlayer.getHand().put(newGoldCard.getId(), true);
        goldCards[index] = board.getGoldDeck().getLast();*/

        // get new gold card
        GoldCard newGoldCard = board.getSharedGoldCard(index);
        currentPlayer.getHandCardsMap().put(newGoldCard.getId(), Side.FRONT);
        // fill the gap
        board.fillSharedCardsGap();
    }

    public void drawResourceFromDeck(Board board, Player currentPlayer) {
        // get new card
        ResourceCard newResourceCard = board.getFromResourceDeck();
        // had card to player hand
        currentPlayer.getHandCardsMap().put(newResourceCard.getId(), Side.FRONT);
    }

    public void drawResourceFromShared(Board board, Player currentPlayer, int index) {
        // get new resource card
        ResourceCard newResourceCard = board.getSharedResourceCard(index);
        currentPlayer.getHandCardsMap().put(newResourceCard.getId(), Side.FRONT);
        // fill the gap
        board.fillSharedCardsGap();
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
