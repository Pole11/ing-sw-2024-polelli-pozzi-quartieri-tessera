package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameState {
    private final HashMap<Integer, Card> cardsMap;

    private final Board mainBoard;
    private final Chat chat;

    private final ArrayList<Player> players; //player[0] is blackPlayer
    private final ArrayList<Boolean> playersConnected; //player[0] is blackPlayer
    private int currentPlayerIndex;

    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;

    private Boolean isLastTurn;


    // CONSTRUCTOR
    public GameState() {
        this.cardsMap = new HashMap<>();
        this.playersConnected = new ArrayList<>();

        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentGamePhase = GamePhase.NICKNAMEPHASE;
        this.currentGameTurn = TurnPhase.PLACINGPHASE;
        this.chat = new Chat();
        this.isLastTurn = false;

        this.mainBoard = new Board();
    }

    // GETTER
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

    public Player getPlayer(int index) {
        return players.get(index);
    }

    public GamePhase getCurrentGamePhase() {
        return currentGamePhase;
    }

    public TurnPhase getCurrentGameTurn() {
        return currentGameTurn;
    }

    public Card getCard(int cardId) {
        return cardsMap.get(cardId);
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


    //ADDER

    public void addCardToCardsMap(int cardId, Card card) {
        this.cardsMap.put(cardId, card);
    }


    // METHODS

    public void setPlayersConnected(int index, boolean connected){
        playersConnected.set(index, Boolean.valueOf(connected));
    }

    public boolean isConnected(int index){
        return playersConnected.get(index);
    }

    public void setPlayer(int index, Player player) throws NotUniquePlayerNicknameException{
        for (int j = 0; j < players.size(); j++) {
            if (player.getNickname().equals(players.get(j).getNickname())) {
                throw new NotUniquePlayerNicknameException("nickame already used");
            }
        }
        players.add(index, player);
    }




    //------------ Starting Phases setter --------------------


    // I: start game / set phase

    // II: initialize deck and shared cards / starter cards

    //III: let players choose the side

    // IV: let the player choose the color from the available colors
    // V: set hands and objectives for choice

    // VI: let the player choose the objective

    // VII: si potrebbe come da specifica scegliere a questo punto
    // un giocatore randomico per essere il blackPlayer (al momento sembra superflua)


    public void startPhaseMethod() {
        this.mainBoard.shuffleCards();
        this.mainBoard.initSharedGoldCards();
        this.mainBoard.initSharedResourceCards();
        this.initStarters(); // set the starters cards for every player
    }

    public void initStarters() {
        // for every player set his starters (you have access to every player from the array players)
        ArrayList<Player> players = getPlayers();

        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < getPlayers().size()) {
            int randomNumber = Config.firstStarterCardId + ThreadLocalRandom.current().nextInt(Config.STARTERQTY); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }

        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < randomKeysList.size(); i++) {
            StarterCard starterCard = (StarterCard) cardsMap.get((randomKeysList.get(i)));
            players.get(i).setStarterCard(starterCard); // we might pass it the map of all the cards
            players.get(i).initializeBoard(); // add the starter card to the board of the player
        }
    }

    public void setStarterSide(int playerIndex, Side side){
        Player player = getPlayerByIndex(playerIndex);
        player.addToPlacedCardsMap(player.getStarterCard().getId(), side);

        // add the initial elements of the starter card
        for (Element ele : player.getStarterCard().getUncoveredElements(side)) {
            int currentOccurencies = player.getAllElements().get(ele);
            player.addToAllElements(ele, currentOccurencies + 1);
        }
    }

    public void setColor(int playerIndex, Color color) throws NotUniquePlayerColorException {//method called by view when the player chooses the side
        for (int j = 0; j < players.size(); j++) {
            if (color.equals(players.get(j).getColor())) {
                throw new NotUniquePlayerColorException("While creating the GameState Object I encountered a problem regarding the creation of players with the same color");
            }
        }
        Player player = getPlayerByIndex(playerIndex);
        player.setColor(color);
    }

    public void setHands() {
        // popolate hands for every player
        for (int i = 0; i < getPlayers().size(); i++) {
            GoldCard goldCard = mainBoard.getFromGoldDeck();
            ResourceCard resourceCard1 = mainBoard.getFromResourceDeck();
            ResourceCard resourceCard2 = mainBoard.getFromResourceDeck();


            getPlayers().get(i).addToHandCardsMap(goldCard.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).addToHandCardsMap(resourceCard1.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).addToHandCardsMap(resourceCard2.getId(), Side.FRONT); // default is front side
        }
    }

    //set SecretObjectiveOptions and SharedObjectiveCards
    public void setObjectives(){
        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < 2 * getPlayers().size() + 2) { // 2 for each player and 2 shared
            int randomNumber = Config.firstObjectiveCardId + ThreadLocalRandom.current().nextInt(Config.OBJECTIVEQTY); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }

        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < players.size(); i++) {
            ObjectiveCard objectiveCard0 = (ObjectiveCard) cardsMap.get(randomKeysList.get(i));
            ObjectiveCard objectiveCard1 = (ObjectiveCard) cardsMap.get(randomKeysList.get(i + getPlayers().size()));
            ObjectiveCard[] objectiveCards = new ObjectiveCard[2];
            objectiveCards[0] = objectiveCard0;
            objectiveCards[1] = objectiveCard1;

            players.get(i).setSecretObjectiveCardOptions(objectiveCards);
        }

        mainBoard.getSharedObjectiveCards()[0] = (ObjectiveCard) cardsMap.get(randomKeysList.get(randomKeysList.size() - 2));
        mainBoard.getSharedObjectiveCards()[1] = (ObjectiveCard) cardsMap.get(randomKeysList.getLast());
    }


    //called from controller
    public void setSecretObjective (int playerIndex, int cardIndex) throws InvalidObjectiveCardException {
        Player player = getPlayerByIndex(playerIndex);
        if (cardIndex == 0 || cardIndex == 1){
            player.setObjectiveCard(player.getObjectiveCardOption(cardIndex));
        } else {
            throw new InvalidObjectiveCardException("The card choosen was not in the options list");
        }
    }

//-------------------EndGame---------------------

    public boolean isGameEnded() {
        // 20 points?
        for (int i = 0; i < this.getPlayers().size(); i++) {
            if (this.getPlayers().get(i).getPoints() >= Config.POINTSTOENDPHASE) {
                return true;
            }
        }
        // cards ended?
        if (this.getMainBoard().getResourceDeck().isEmpty()) {
            return true;
        }
        return this.getMainBoard().getGoldDeck().isEmpty();
    }

    void calculateFinalPoints() throws CardNotPlacedException, WrongInstanceTypeException {
        ObjectiveCard sharedCard1 = this.mainBoard.getSharedObjectiveCards()[0];
        ObjectiveCard sharedCard2 = this.mainBoard.getSharedObjectiveCards()[1];

        for (int i = 0; i < this.getPlayers().size(); i++) {
            Player player = this.getPlayers().get(i);
            ObjectiveCard objCard = player.getObjectiveCard();

            int points = 0;

            points += objCard.calculatePoints(player);
            points += sharedCard1.calculatePoints(player);
            points += sharedCard2.calculatePoints(player);

            this.getPlayers().get(i).addPoints(points);
        }
    }

    public ArrayList<Integer> getWinnerPlayerIndex() throws GameIsNotEndedException {
        int maxPoints = 0;
        ArrayList<Integer> winnerPlayerIndeces = new ArrayList<>();
        boolean playerReachedPoints = false;
        for (int i = 0; i < this.getPlayers().size(); i++) {
            if (this.getPlayers().get(i).getPoints() >= Config.POINTSTOENDPHASE){
                playerReachedPoints = true;
            }
            if (this.getPlayers().get(i).getPoints() > maxPoints) {
                winnerPlayerIndeces = new ArrayList<>();
                winnerPlayerIndeces.add(i);
                maxPoints = this.getPlayers().get(i).getPoints();
            } else if (this.getPlayers().get(i).getPoints() == maxPoints) {
                winnerPlayerIndeces.add(i);
            }
        }
        if (!playerReachedPoints){
            throw new GameIsNotEndedException("You called getWinnerPlayerIndex even if the game is not ended, no one is at 20 points");
        }

        // now winnerPlayerIndeces contains who has the maximum number of points
        // decide the real winner if there is a draw
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

//---------------Player managing----------------------

    // return the current player
    public Player getCurrentPlayer() {
        return players.get(this.currentPlayerIndex);
    }

    public Player getBlackPlayer(){
        return this.players.getFirst();
    }

    public Player getPlayerByIndex(int index){
        return this.getPlayers().get(index);
    }

    public void removeAllPlayers() {
        this.players.clear();
    }

    public void removePlayer(int index) {
        this.players.remove(index);
    }


//--------------All draw cards called from controller------------------

    public void drawGoldFromDeck(Board board, Player currentPlayer) {
        // get new card
        GoldCard newGoldCard = board.getFromGoldDeck();
        // add card to player hand
        currentPlayer.addToHandCardsMap(newGoldCard.getId(), Side.FRONT); // the front side is the default
    }

    public void drawGoldFromShared(Board board, Player currentPlayer, int index) {
        // get new gold card
        GoldCard newGoldCard = board.drawSharedGoldCard(index);
        currentPlayer.addToHandCardsMap(newGoldCard.getId(), Side.FRONT);
        // fill the gap
        board.fillSharedCardsGap();
    }

    public void drawResourceFromDeck(Board board, Player currentPlayer) {
        // get new card
        ResourceCard newResourceCard = board.getFromResourceDeck();
        // had card to player hand
        currentPlayer.addToHandCardsMap(newResourceCard.getId(), Side.FRONT);
    }

    public void drawResourceFromShared(Board board, Player currentPlayer, int index) {
        // get new resource card
        ResourceCard newResourceCard = board.drawSharedResourceCard(index);
        currentPlayer.addToHandCardsMap(newResourceCard.getId(), Side.FRONT);
        // fill the gap
        board.fillSharedCardsGap();
    }

//----------------------------place Card--------------------------------

    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos tableCornerPos, CornerPos placingCornerPos, Side placingCardSide) throws PlacingOnHiddenCornerException {
        // check if the indirect corners are hidden
        CornerCard placingCard = (CornerCard) cardsMap.get(placingCardId);
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
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.DOWNRIGHT, CornerPos.UPLEFT);

                    }
                    // check if there is a card in the up right corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol + 1 >= 0 && placingCol + 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol + 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol + 1);
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.DOWNLEFT, CornerPos.UPRIGHT);

                    }
                    // check if there is a card in the down right corner
                    if (placingRow + 1 >= 0 && placingRow + 1 < playerBoard.size() && placingCol >= 0 && placingCol < playerBoard.getFirst().size() && playerBoard.get(placingRow + 1).get(placingCol) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow + 1).get(placingCol);
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.UPLEFT, CornerPos.DOWNRIGHT);
                    }

                    // check if there is a card in the down left corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol - 1 >= 0 && placingCol - 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol - 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol - 1);
                        placeCardHelper(player, matrixCardId, placingCard, placingCardSide, CornerPos.UPRIGHT, CornerPos.DOWNLEFT);
                    }
                    break;
                }
            }
        }
        updateElements(player, placingCard, placingCardSide);
    }

    private void placeCardHelper(Player player, int matrixCardId, CornerCard placingCard, Side placingCardSide, CornerPos matrixCardCornerPos, CornerPos indirectPlacingCornerPos) throws PlacingOnHiddenCornerException {
        CornerCard matrixCard = (CornerCard) cardsMap.get(matrixCardId);
        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId)).get(matrixCardCornerPos.ordinal());
        Corner indirectPlacingCorner = placingCard.getCorners(placingCardSide).get(indirectPlacingCornerPos.ordinal());

        if (matrixCorner.getHidden()) {
            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
        }

        // link the corners
        indirectPlacingCorner.setLinkedCorner(matrixCorner);
        indirectPlacingCorner.setCovered(false);// is false at default anyway
        matrixCorner.setLinkedCorner(indirectPlacingCorner);
        matrixCorner.setCovered(true);

        // remove elements of the table card that are covered
        Element cornerEle = matrixCorner.getElement();
        if (player.getAllElements().containsKey(cornerEle)) {
            int currentOccurencies = player.getAllElements().get(cornerEle);
            if (currentOccurencies > 0)
                player.removeFromAllElements(cornerEle);
        }

    }


    public void updateElements(Player player, CornerCard placingCard, Side placingCardSide){
        for (Element ele : Element.values()) {
            if (player.getAllElements().get(ele) != null) {
                int currentOccurencies = player.getAllElements().get(ele);
                int newOccurencies = Collections.frequency(placingCard.getUncoveredElements(placingCardSide), ele);
                player.addToAllElements(ele, currentOccurencies + newOccurencies);
            }
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


/*

    private void playTurn() throws CardNotPlacedException, WrongInstanceTypeException {
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

    private void playEndTurn() throws CardNotPlacedException, WrongInstanceTypeException {
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
            // TODO: 17/04/2024 network place methods
            // notify current player to place card (notify all changes)
            // wait
            this.setCurrentGameTurn(TurnPhase.DRAWPHASE);

            if(this.getMainBoard().getGoldDeck().isEmpty() && this.getMainBoard().getResourceDeck().isEmpty() && this.getMainBoard().getSharedGoldCards().length == 0 && this.getMainBoard().getSharedResourceCards().length == 0){
                ; //there are no card to be drawn, the game continues without drawing
            }
            else{
                // TODO: 17/04/2024 network draw methods
                // notify current player to draw a card (notify all changes)
                //wait
            }
            // play next turn
            // update current player
            this.setCurrentPlayerIndex((this.getCurrentPlayerIndex()+1) % this.getPlayers().size());
            this.setCurrentGameTurn(TurnPhase.PLACINGPHASE);

            playEndTurn();
        }
    }
 */
