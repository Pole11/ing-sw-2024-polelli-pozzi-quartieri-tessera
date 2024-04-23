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

    // !!! THE REFERENCE TO CARDSMAP IS FINAL !!!
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

        this.players = players;
        this.currentPlayerIndex = 0;
        this.currentGamePhase = GamePhase.MAINPHASE;
        this.currentGameTurn = null;
        this.chat = new Chat();
        this.isLastTurn = false;


        // get all gold cards and resource cards
        ArrayList<GoldCard> goldCardDeck = new ArrayList<>();
        ArrayList<ResourceCard> resourceCardDeck = new ArrayList<>();
        for (int i = 0; i < this.getCardsMap().size(); i++) {
            if (this.getCardsMap().get(i) instanceof GoldCard) {
                goldCardDeck.add((GoldCard) this.getCardsMap().get(i));
            } else if (this.getCardsMap().get(i) instanceof ResourceCard) {
                resourceCardDeck.add((ResourceCard) this.getCardsMap().get(i));
            }
        }

        this.mainBoard = new Board(resourceCardDeck,goldCardDeck);

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
        return this.cardsMap;
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
            players.get(i).setStarterCard(starterCard); // we might pass it the map of all the cards
            players.get(i).initializeBoard(); // add the starter card to the board of the player
        }
    }

    public void setStarterSide(int playerIndex, Side side){
        Player player = getPlayerByIndex(playerIndex);
        player.getPlacedCardsMap().put(player.getStarterCard().getId(), side);

        // add the initial elements of the starter card
        for (Element ele : player.getStarterCard().getUncoveredElements(side)) {
            int currentOccurencies = player.getAllElements().get(ele);
            player.getAllElements().put(ele, currentOccurencies + 1);
        }
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

    private void calculateFinalPoints() throws CardNotPlacedException, WrongInstanceTypeException {
        for (int i = 0; i < this.getPlayers().size(); i++) {
            int points = this.getPlayers().get(i).getCardPoints(this.getPlayers().get(i).getObjectiveCard());
            this.getPlayers().get(i).addPoints(points);
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


    //QUESTO METODO VA SPLITTATO
    public void placeCard(Player player, int placingCardId, int tableCardId, CornerPos tableCornerPos, CornerPos placingCornerPos, Side placingCardSide) throws PlacingOnHiddenCornerException {
        // check if the indirect corners are hidden
        CornerCard placingCard = (CornerCard) this.getCardsMap().get(placingCardId);
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
                        CornerCard matrixCard = (CornerCard) this.getCardsMap().get(matrixCardId); // use the keys of player.placedCardsMap
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.DOWNRIGHT.getCornerPosValue()];


                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }

                        Corner indirectPlacingCorner = placingCard.getCorners(placingCardSide)[CornerPos.UPLEFT.getCornerPosValue()];

                        indirectPlacingCorner.setLinkedCorner(matrixCorner);
                        indirectPlacingCorner.setCovered(false);// is false at default anyway
                        matrixCorner.setLinkedCorner(indirectPlacingCorner);
                        matrixCorner.setCovered(true);

                        // !!! remove elements of the table card that are covered
                        Element cornerEle = matrixCorner.getElement();
                        if (player.getAllElements().get(cornerEle) != null) {
                            int currentOccurencies = player.getAllElements().get(cornerEle);
                            if (currentOccurencies > 0)
                                player.getAllElements().put(cornerEle, currentOccurencies - 1);
                        }

                    }
                    // check if there is a card in the up right corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol + 1 >= 0 && placingCol + 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol + 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol + 1);
                        CornerCard matrixCard = (CornerCard) this.getCardsMap().get(matrixCardId);
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.DOWNLEFT.getCornerPosValue()];

                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }


                        Corner indirectPlacingCorner = placingCard.getCorners(placingCardSide)[CornerPos.UPRIGHT.getCornerPosValue()];

                        indirectPlacingCorner.setLinkedCorner(matrixCorner);
                        indirectPlacingCorner.setCovered(false);// is false at default anyway
                        matrixCorner.setLinkedCorner(indirectPlacingCorner);
                        matrixCorner.setCovered(true);

                        // !!! remove elements of the table card that are covered
                        Element cornerEle = matrixCorner.getElement();
                        if (player.getAllElements().get(cornerEle) != null) {
                            int currentOccurencies = player.getAllElements().get(cornerEle);
                            if (currentOccurencies > 0)
                                player.getAllElements().put(cornerEle, currentOccurencies - 1);

                        }

                    }
                    // check if there is a card in the down right corner
                    if (placingRow + 1 >= 0 && placingRow + 1 < playerBoard.size() && placingCol >= 0 && placingCol < playerBoard.getFirst().size() && playerBoard.get(placingRow + 1).get(placingCol) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow + 1).get(placingCol);
                        CornerCard matrixCard = (CornerCard) this.getCardsMap().get(matrixCardId);
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.UPLEFT.getCornerPosValue()];


                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }


                        Corner indirectPlacingCorner = placingCard.getCorners(placingCardSide)[CornerPos.DOWNRIGHT.getCornerPosValue()];

                        indirectPlacingCorner.setLinkedCorner(matrixCorner);
                        indirectPlacingCorner.setCovered(false);// is false at default anyway
                        matrixCorner.setLinkedCorner(indirectPlacingCorner);
                        matrixCorner.setCovered(true);

                        // !!! remove elements of the table card that are covered
                        Element cornerEle = matrixCorner.getElement();
                        if (player.getAllElements().get(cornerEle) != null) {
                            int currentOccurencies = player.getAllElements().get(cornerEle);
                            if (currentOccurencies > 0)
                                player.getAllElements().put(cornerEle, currentOccurencies - 1);
                        }



                    }

                    // check if there is a card in the down left corner
                    if (placingRow >= 0 && placingRow < playerBoard.size() && placingCol - 1 >= 0 && placingCol - 1 < playerBoard.getFirst().size() && playerBoard.get(placingRow).get(placingCol - 1) != -1) { // -1 means empty
                        int matrixCardId = playerBoard.get(placingRow).get(placingCol - 1);
                        CornerCard matrixCard = (CornerCard) this.getCardsMap().get(matrixCardId);
                        Corner matrixCorner = matrixCard.getCorners(player.getBoardSide(matrixCardId))[CornerPos.UPRIGHT.getCornerPosValue()];

                        if (matrixCorner.getHidden()) {
                            throw new PlacingOnHiddenCornerException("you are trying to place on an hidden corner");
                        }

                        Corner indirectPlacingCorner = placingCard.getCorners(placingCardSide)[CornerPos.DOWNLEFT.getCornerPosValue()];

                        indirectPlacingCorner.setLinkedCorner(matrixCorner);
                        indirectPlacingCorner.setCovered(false);// is false at default anyway
                        matrixCorner.setLinkedCorner(indirectPlacingCorner);
                        matrixCorner.setCovered(true);

                        // !!! remove elements of the table card that are covered
                        Element cornerEle = matrixCorner.getElement();
                        if (player.getAllElements().get(cornerEle) != null) {
                            int currentOccurencies = player.getAllElements().get(cornerEle);
                            if (currentOccurencies > 0)
                                player.getAllElements().put(cornerEle, currentOccurencies - 1);
                        }
                    }
                    break;
                }
            }
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
