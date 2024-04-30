package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class GameState {

    // !!! THE REFERENCE TO CARDSMAP IS FINAL !!!
    private final HashMap<Integer, GoldCard> goldCardsMap; // map id and card
    private final HashMap<Integer, ResourceCard> resourceCardsMap; // map id and card
    private final HashMap<Integer, StarterCard> starterCardsMap; // map id and card
    private final HashMap<Integer, ObjectiveCard> objectiveCardsMap; // map id and card

    private final HashMap<Integer, CoverageChallenge> coverageChallengeMap; // id of the card to find the related challenge, if null it has no challenge of that type
    private final HashMap<Integer, ElementChallenge> elementChallengeMap; // id of the card to find the related challenge, if null it has no challenge of that type
    private final HashMap<Integer, StructureChallenge> structureChallengeMap; // id of the card to find the related challenge, if null it has no challenge of that type

    private final Board mainBoard;
    private final ArrayList<Player> players; //player[0] is blackPlayer
    private final Chat chat;
    private int currentPlayerIndex;
    private GamePhase currentGamePhase;
    private TurnPhase currentGameTurn;
    private Boolean isLastTurn;
    int numberAnswered = 0;

    // CONSTRUCTOR
    public GameState() {
        this.goldCardsMap = new HashMap<>();
        this.resourceCardsMap = new HashMap<>();
        this.starterCardsMap = new HashMap<>();
        this.objectiveCardsMap = new HashMap<>();

        this.coverageChallengeMap = new HashMap<>();
        this.elementChallengeMap = new HashMap<>();
        this.structureChallengeMap = new HashMap<>();

        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.currentGamePhase = GamePhase.MAINPHASE;
        this.currentGameTurn = null;
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

    public GoldCard getGoldCard(int cardId) {
        return this.goldCardsMap.get(cardId);
    }

    public ResourceCard getResourceCard(int cardId) {
        return this.resourceCardsMap.get(cardId);
    }

    public StarterCard getStarterCard(int cardId) {
        return this.starterCardsMap.get(cardId);
    }

    public ObjectiveCard getObjectiveCard(int cardId) {
        return this.objectiveCardsMap.get(cardId);
    }

    public CornerCard getCornerCard(int cardId) {
        CornerCard cornerCard = getResourceCard(cardId);
        if (cornerCard == null) cornerCard = getGoldCard(cardId);
        if (cornerCard == null) cornerCard = getStarterCard(cardId);

        return cornerCard;
    }

    public Card getCard(int cardId) {
        Card card = null;
        if (getResourceCard(cardId) != null) card = getResourceCard(cardId);
        else if (getGoldCard(cardId) != null) card = getGoldCard(cardId);
        else if (getStarterCard(cardId) != null) card = getStarterCard(cardId);
        else if (getObjectiveCard(cardId) != null) card = getObjectiveCard(cardId);

        return card;
    }

    public int getResourceCardsQty() {
        return this.resourceCardsMap.size();
    }

    public int getGoldCardsQty() {
        return this.goldCardsMap.size();
    }

    public int getStarterCardsQty() {
        return this.starterCardsMap.size();
    }

    public int getObjectiveCardsQty() {
        return this.objectiveCardsMap.size();
    }

    public CoverageChallenge getCoverageChallenge(int challengeId) {
        return this.coverageChallengeMap.get(challengeId);
    }

    public ElementChallenge getElementChallenge(int challengeId) {
        return this.elementChallengeMap.get(challengeId);
    }

    public StructureChallenge getStructureChallenge(int challengeId) {
        return this.structureChallengeMap.get(challengeId);
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

    public void setGoldCard(int cardId, GoldCard goldCard) {
        this.goldCardsMap.put(cardId, goldCard);
    }

    public void setResourceCard(int cardId, ResourceCard resourceCard) {
        this.resourceCardsMap.put(cardId, resourceCard);
    }

    public void setStarterCard(int cardId, StarterCard starterCard) {
        this.starterCardsMap.put(cardId, starterCard);
    }

    public void setObjectiveCard(int cardId, ObjectiveCard objectiveCard) {
        this.objectiveCardsMap.put(cardId, objectiveCard);
    }

    public void setCoverageChallenge(int challengeId, CoverageChallenge challenge) {
        this.coverageChallengeMap.put(challengeId, challenge);
    }

    public void setElementChallenge(int challengeId, ElementChallenge challenge) {
        this.elementChallengeMap.put(challengeId, challenge);
    }

    public void setStructureChallenge(int challengeId, StructureChallenge challenge) {
        this.structureChallengeMap.put(challengeId, challenge);
    }

    public void setPlayer(int index, Player player) throws NotUniquePlayerNicknameException{
        for (int j = 0; j < players.size(); j++) {
            if (player.getNickname().equals(players.get(j).getNickname())) {
                throw new NotUniquePlayerNicknameException("nickame already used");
            }
        }
        players.add(index, player);
    }

    public void setMainBoard() {
        // get all gold cards and resource cards
        ArrayList<GoldCard> goldCardDeck = new ArrayList<>(goldCardsMap.values());
        ArrayList<ResourceCard> resourceCardDeck = new ArrayList<>(resourceCardsMap.values());

        this.mainBoard.setDecks(resourceCardDeck, goldCardDeck);
    }

    // METHODS

    public void removeAllPlayers() {
        this.players.clear();
    }

    public void removePlayer(int index) {
        this.players.remove(index);
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
        this.mainBoard.shuffleCards();
        this.setSharedGoldCards();
        this.setSharedResourceCards();
        this.setStarters(); // set the starters cards for every player
        this.chooseStarterSidePhase();
    }

    //III: let players choose the side
    public void chooseStarterSidePhase(){
        if (numberAnswered == players.size()) {
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
        if (numberAnswered == players.size()) {
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
            //playTurn();
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

        mainBoard.setSharedResourceCards(cards);
    }


    public void setStarters() {
        // for every player set his starters (you have access to every player from the array players)
        ArrayList<Player> players = getPlayers();

        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < getPlayers().size()) {
            int randomNumber = Config.firstStarterCardId + ThreadLocalRandom.current().nextInt(this.getStarterCardsQty()); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }

        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < randomKeysList.size(); i++) {
            StarterCard starterCard = this.getStarterCard(randomKeysList.get(i));
            players.get(i).setStarterCard(starterCard); // we might pass it the map of all the cards
            players.get(i).initializeBoard(); // add the starter card to the board of the player
        }
    }

    public void setStarterSide(int playerIndex, Side side){
        Player player = getPlayerByIndex(playerIndex);
        player.getPlacedCardsMap().put(player.getStarterCard().getId(), side); // to fix the hash map obfuscation

        // add the initial elements of the starter card
        for (Element ele : player.getStarterCard().getUncoveredElements(side)) {
            int currentOccurencies = player.getAllElements().get(ele);
            player.getAllElements().put(ele, currentOccurencies + 1);
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

            getPlayers().get(i).getHandCardsMap().put(goldCard.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).getHandCardsMap().put(resourceCard1.getId(), Side.FRONT); // default is front side
            getPlayers().get(i).getHandCardsMap().put(resourceCard2.getId(), Side.FRONT); // default is front side
        }
    }

    //set SecretObjectiveOptions and SharedObjectiveCards
    public void setObjectives(){
        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < 2 * getPlayers().size() + 2) { // 2 for each player and 2 shared
            int randomNumber = Config.firstObjectiveCardId + ThreadLocalRandom.current().nextInt(this.getObjectiveCardsQty()); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }


        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < players.size(); i++) {
            ObjectiveCard objectiveCard0 = this.getObjectiveCard(randomKeysList.get(i));
            ObjectiveCard objectiveCard1 = this.getObjectiveCard(randomKeysList.get(i + getPlayers().size()));
            ObjectiveCard[] objectiveCards = new ObjectiveCard[2];
            objectiveCards[0] = objectiveCard0;
            objectiveCards[1] = objectiveCard1;

            players.get(i).setSecretObjectiveCardOptions(objectiveCards);
        }

        mainBoard.getSharedObjectiveCards()[0] = this.getObjectiveCard(randomKeysList.get(randomKeysList.size() - 2));
        mainBoard.getSharedObjectiveCards()[1] = this.getObjectiveCard(randomKeysList.getLast());
    }


    //called from controller
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
        for (int i = 0; i < this.getPlayers().size(); i++) {

            ObjectiveCard objCard = this.getPlayers().get(i).getObjectiveCard();
            ObjectiveCard sharedCard1 = this.mainBoard.getSharedObjectiveCards()[0];
            ObjectiveCard sharedCard2 = this.mainBoard.getSharedObjectiveCards()[1];

            int points = 0;
            // player objective
            if (this.structureChallengeMap.get(objCard.getId()) != null) {
                points = this.getPlayers().get(i).getCardPoints(objCard, this.structureChallengeMap.get(objCard.getId()));
            } else if (this.elementChallengeMap.get(objCard.getId()) != null) {
                points = this.getPlayers().get(i).getCardPoints(objCard, this.elementChallengeMap.get(objCard.getId()));
            }

            // shared 1
            if (this.structureChallengeMap.get(sharedCard1.getId()) != null) {
                points += this.getPlayers().get(i).getCardPoints(sharedCard1, this.structureChallengeMap.get(sharedCard1.getId()));
            } else if (this.elementChallengeMap.get(sharedCard1.getId()) != null) {
                points += this.getPlayers().get(i).getCardPoints(sharedCard1, this.elementChallengeMap.get(sharedCard1.getId()));
            }

            // shared 2
            if (this.structureChallengeMap.get(sharedCard2.getId()) != null) {
                points += this.getPlayers().get(i).getCardPoints(sharedCard2, this.structureChallengeMap.get(sharedCard2.getId()));
            } else if (this.elementChallengeMap.get(sharedCard2.getId()) != null) {
                points += this.getPlayers().get(i).getCardPoints(sharedCard2, this.elementChallengeMap.get(sharedCard2.getId()));
            }

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
        int maxChallengesWon = 0; // SERVE??
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

    public Player getPlayerByIndex(int index){
        return this.getPlayers().get(index);
    }

//--------------All draw cards called from controller------------------

    public void drawGoldFromDeck(Board board, Player currentPlayer) {
        // get new card
        GoldCard newGoldCard = board.getFromGoldDeck();
        // add card to player hand
        currentPlayer.getHandCardsMap().put(newGoldCard.getId(), Side.FRONT); // the front side is the default
    }

    public void drawGoldFromShared(Board board, Player currentPlayer, int index) {
        // get new gold card
        GoldCard newGoldCard = board.drawSharedGoldCard(index);
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
        ResourceCard newResourceCard = board.drawSharedResourceCard(index);
        currentPlayer.getHandCardsMap().put(newResourceCard.getId(), Side.FRONT);
        // fill the gap
        board.fillSharedCardsGap();
    }

//----------------------------place Card--------------------------------

    //QUESTO METODO VA SPLITTATO
    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos tableCornerPos, CornerPos placingCornerPos, Side placingCardSide) throws PlacingOnHiddenCornerException {
        // check if the indirect corners are hidden
        CornerCard placingCard = this.getCornerCard(placingCardId);
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
    }

    private void placeCardHelper(Player player, int matrixCardId, CornerCard placingCard, Side placingCardSide, CornerPos matrixCardCornerPos, CornerPos indirectPlacingCornerPos) throws PlacingOnHiddenCornerException {
        CornerCard matrixCard = this.getCornerCard(matrixCardId);
        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[matrixCardCornerPos.getCornerPosValue()];
        Corner indirectPlacingCorner = placingCard.getCorners(placingCardSide)[indirectPlacingCornerPos.getCornerPosValue()];

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
        if (player.getAllElements().get(cornerEle) != null) {
            int currentOccurencies = player.getAllElements().get(cornerEle);
            if (currentOccurencies > 0)
                player.getAllElements().put(cornerEle, currentOccurencies - 1);
        }
    }


    public void updateElements(Player player, CornerCard placingCard, Side placingCardSide){
        for (Element ele : Element.values()) {
            if (player.getAllElements().get(ele) != null) {
                int currentOccurencies = player.getAllElements().get(ele);
                int newOccurencies = Collections.frequency(placingCard.getUncoveredElements(placingCardSide), ele);
                player.getAllElements().put(ele, currentOccurencies + newOccurencies);
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
