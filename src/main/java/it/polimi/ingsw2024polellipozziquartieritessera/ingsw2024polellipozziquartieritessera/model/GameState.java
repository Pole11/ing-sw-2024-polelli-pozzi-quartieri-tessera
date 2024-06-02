package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.events.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * GameState Class
 */
public class GameState {
    /**
     * Map of all the cards, that matches an ID with the proper card
     */
    private final HashMap<Integer, Card> cardsMap;
    /**
     * Main board of the game, that contains all deck and shared cards
     */
    private final Board mainBoard;
    /**
     * Chat of the game, that contains all messages
     */
    private final Chat chat;
    /**
     * List of all the playing players, player[0] is black Player and the first to start
     */
    private final ArrayList<Player> players;
    /**
     * Index of the current playing player
     */
    private int currentPlayerIndex;
    /**
     * Current game phase
     */
    private GamePhase currentGamePhase;
    /**
     * Current turn phase
     */
    private TurnPhase currentGameTurn;
    /**
     * Queue o all the events that the clint could do (for synchronization)
     */
    private final ArrayDeque<Event> eventQueue;
    /**
     * Thread that reads and manages the queque
     */
    private Thread executeEvents;
    /**
     * Map that set if a player has answered to the request or not (useful for starting phases of the game)
     */
    private final HashMap<Integer, Boolean> answered;
    /**
     * Thread that manages pings
     */
    private Thread pingThread;
    /**
     * Thread list that manages all the players, one for each player
     */
    private ArrayList<Thread> playerThreads = new ArrayList<>();
    /**
     * Thread that manages timeout for curent player
     */
    private Thread timeoutThread;
    /**
     * Saving of the previous game phase
     */
    private GamePhase prevGamePhase;

    /**
     * Number of turns left to play
     */
    private int turnToPlay;


    /**
     * GameState Constructor
     */
    public GameState() {
        this.cardsMap = new HashMap<>();

        this.mainBoard = new Board();
        this.chat = new Chat();

        this.players = new ArrayList<>();
        this.currentPlayerIndex = 0;

        this.currentGamePhase = GamePhase.NICKNAMEPHASE;
        this.currentGameTurn = TurnPhase.PLACINGPHASE;
        this.prevGamePhase = null;

        this.eventQueue = new ArrayDeque<>();

        this.timeoutThread = null;

        this.executeEvents = new Thread(this::executeEventsRunnable);
        this.executeEvents.start();

        this.pingThread = new Thread(this::pingThreadRunnable);
        pingThread.start();

        this.answered = new HashMap<>();
        resetAnswered();

        //cambia in intMax
        this.turnToPlay = 1;

    }

    // GETTER
    public Board getMainBoard() {
        return mainBoard;
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

    public int getPlayersSize() {
        return this.players.size();
    }

    public ArrayDeque<Event> getEventQueue() {
        return eventQueue;
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

    //ADDER/REMOVER

    /**
     * Add a card to the gamestate cards
     * @param cardId Card Identifier
     * @param card Card object
     */
    public void addCardToCardsMap(int cardId, Card card) {
        this.cardsMap.put(cardId, card);
    }

    /**
     * Add event to the queue
     * @param event Event to add
     */
    public void addToEventQueue(Event event) {
        this.eventQueue.add(event);
    }

    //EVENTQUEUE

    /**
     * Resets the number of answers (even if there aren't always 4 players)
     */
    private void resetAnswered() {
        this.answered.put(0, false);
        this.answered.put(1, false);
        this.answered.put(2, false);
        this.answered.put(3, false);
    }

    /**
     * Get the number of player that has answered the request
     * @return Number of answers
     */
    private int numberAnswered() {
        return this.answered.values().stream().mapToInt(e -> e ? 1 : 0).sum();
    }

    /**
     * Run the event queue listener
     */
    private void executeEventsRunnable() {
        while (true) {
            synchronized (eventQueue) {
                while (eventQueue.isEmpty()) {
                    try {
                        eventQueue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                eventQueue.remove().execute();
            }
        }
    }

    /**
     * Trasform a client to a list
     * @param client Selected client
     * @return List of the single client
     */
    public ArrayList<VirtualView> singleClient(VirtualView client) {
        ArrayList<VirtualView> clients = new ArrayList<>();
        clients.add(client);
        return clients;
    }

    /**
     * Get a list of all the cient different from the selected one
     * @param client Client to exclude
     * @return List of the other clients
     */
    public ArrayList<VirtualView> otherClients(VirtualView client) {
        ArrayList<VirtualView> clients = new ArrayList<>();
        for (Player playerIterator : players) {
            if (playerIterator.isConnected() && !client.equals(playerIterator.getClient())) {
                clients.add(playerIterator.getClient());
            }
        }
        return clients;
    }

    /**
     * Get all the clients in the gamestate
     * @return List of all clients
     */
    public ArrayList<VirtualView> allClients() {
        return (ArrayList<VirtualView>) players.stream().filter(Player::isConnected).map(Player::getClient).collect(Collectors.toList());
    }


    //DISCONNECTIONS
    /**
     * Run the ping thread
     */
    public void pingThreadRunnable() {
        while (true) {
            updatePlayersConnected();
            int i = 0;
            for (Thread playerThread : playerThreads) {
                int finalI = i;
                playerThread = new Thread(() -> {
                    int j = finalI;
                    try {
                        //wait for the answare of players
                        Thread.sleep(1000 * 3);
                    } catch (InterruptedException e) {
                    }
                    synchronized (players) {
                        players.get(j).setConnected(false);
                        players.notifyAll();
                    }
                });
                playerThread.start();
                i++;
            }

            try {
                //wait to ping another time
                Thread.sleep(1000 * 5);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Get the ping response from a player
     * @param client The client who has responded
     */
    public void pingAnswer(VirtualView client) {
        synchronized (players) {
            playerThreads.get(getPlayerIndex(client)).interrupt();
            players.get(getPlayerIndex(client)).setConnected(true);
            players.notifyAll();
        }
    }

    /**
     * Set the connection of a player (null or connected)
     * @param index Player index
     * @param connected Connection status
     */
    public void setPlayersConnected(int index, boolean connected) {
        players.get(index).setConnected(connected);
        players.get(index).setClient(null);
    }

    /**
     * Manage the reconnection of a player
     * @param player Player to be reconnected
     */
    private void manageReconnection(Player player) {
        synchronized (players) {
            player.setConnected(true);
            players.notifyAll();
        }
        restoreView(player.getClient());
        if (this.currentGamePhase.equals(GamePhase.TIMEOUT)) {
            this.timeoutThread.interrupt();
            currentGamePhase = prevGamePhase;
            prevGamePhase = null;
        }
    }

    /**
     * View restoration for reconnection
     * @param client Client to be reconnected
     */
    public void restoreView(VirtualView client) {
        //send everything to client
    }

    /**
     * Set and verify the disconnection of a player, and manage the thread call
     */
    public void playerDisconnected() {
        long numberConnected;
        synchronized (players) {
            numberConnected = players.stream().filter(Player::isConnected).count();
            players.notifyAll();
        }
        if (numberConnected == 1) {
            this.prevGamePhase = currentGamePhase;
            this.currentGamePhase = GamePhase.TIMEOUT;
            timeoutThread = new Thread(() -> {
                try {
                    Thread.sleep(60 * 1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            timeoutThread.start();
        } else if (numberConnected == 0) {
            //chiedere specifica
        }
    }

    /**
     * Change the current playing player
     */
    public void changeCurrentPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        //check for disconnetions
        for (int i = 0; i < players.size(); i++) {
            if (!getPlayer(getCurrentPlayerIndex()).isConnected()) {
                currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            } else {
                break;
            }
        }
        synchronized (eventQueue) {
            eventQueue.add(new UpdateCurrentPlayer(this, allClients(), currentPlayerIndex));
            eventQueue.notifyAll();
        }
        //TODO: capire se va bene decrementare di 1 in ogni caso
        if (currentGamePhase.equals(GamePhase.ENDPHASE)) {
            turnToPlay--;
        }
        playerDisconnected();

    }

    /**
     * Send the ping to verify connection
     */
    private void updatePlayersConnected() {
        players.stream().map(Player::getClient).forEach(e -> {
            eventQueue.add(new PingEvent(this, allClients()));
            eventQueue.notifyAll();
        });
    }


    //PLAYERS MANAGING

    /**
     * Get the current playing player
     * @return Current player
     */
    public Player getCurrentPlayer() {
        return players.get(this.currentPlayerIndex);
    }

    /**
     * Get the player index of the requested client
     * @param client Virtualview of the client
     * @return Player index
     */
    public Integer getPlayerIndex(VirtualView client) {
        int i = 0;
        for (Player playerIterator : this.players) {
            if (playerIterator.getClient().equals(client)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Get the player index of the requested player
     * @param player Selected player
     * @return Player index
     */
    public Integer getPlayerIndex(Player player) {
        int i = 0;
        for (Player playerIterator : this.players) {
            if (playerIterator.equals(player)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Add a player to the game
     * @param nickname Nickname of the player
     * @param client VirtualView of the client
     */
    public void addPlayer(String nickname, VirtualView client) {
        Player player = new Player(nickname, client, this);
        //TODO: CONTROLLA se salta i null
        List<VirtualView> clients = allClients();
        System.out.println("CLIENTS:" + clients.size());
        if (clients.size() >= Config.MAX_PLAYERS) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(client), "The game is full"));
                eventQueue.notifyAll();
            }
            return;
        }
        if (clients.contains(client)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(client), "You already chose a nickname, you cannot change it"));
                eventQueue.notifyAll();
            }
            return;
        }

        for (int j = 0; j < players.size(); j++) {
            if (player.getNickname().equals(players.get(j).getNickname())) {
                //takes for granted that player connection is updated
                //if the player is not connected but the gameState doesn't know, the following code fails
                if (!players.get(j).isConnected()) {
                    player.setClient(client);
                    this.manageReconnection(player);
                    synchronized (eventQueue) {
                        //only to the client
                        eventQueue.add(new UpdateGamePhaseEvent(this, singleClient(player.getClient()), this.currentGamePhase));
                        //to all the other clients says that a client reconnected
                        eventQueue.add(new ConnectionInfoEvent(this, otherClients(player.getClient()), player, true));
                        eventQueue.notifyAll();
                    }
                } else {
                    //only to the client
                    synchronized (eventQueue) {
                        eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The nickname is already used, please choose another one"));
                        eventQueue.notifyAll();
                    }
                }
                return;
            }
        }
        players.add(player);
        synchronized (eventQueue) {
            eventQueue.add(new SendIndexEvent(this, singleClient(player.getClient()), getPlayerIndex(player)));
            eventQueue.add(new UpdateGamePhaseEvent(this, singleClient(player.getClient()), GamePhase.NICKNAMEPHASE));
            //to all the other clients gives the nickname so that they can execute their model
            eventQueue.add(new NicknameEvent(this, otherClients(player.getClient()), getPlayerIndex(player), player.getNickname()));
            for (Player playerIterator : players) {
                eventQueue.add(new NicknameEvent(this, singleClient(player.getClient()), getPlayerIndex(playerIterator), playerIterator.getNickname()));
            }
            eventQueue.notifyAll();
        }
    }


    //STARTING PHASE SETTERS


    // I: start game / set phase

    // II: initialize deck and shared cards / starter cards

    //III: let players choose the side

    // IV: let the player choose the color from the available colors
    // V: set hands and objectives for choice

    // VI: let the player choose the objective

    // VII: si potrebbe come da specifica scegliere a questo punto
    // un giocatore randomico per essere il blackPlayer (al momento sembra superflua)

    /**
     * Function to start the game
     * @param client Client that started the game
     * @throws EmptyDeckException Deck it's not initialized
     */
    public void startGame(VirtualView client) throws EmptyDeckException {
        ArrayList<VirtualView> clients = allClients();
        if (!clients.contains(client)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(client), "Please register before starting the game"));
                eventQueue.notifyAll();
            }
            return;
        }
        if (clients.size() < 2) {
            if (clients.contains(client)) {
                synchronized (eventQueue) {
                    eventQueue.add(new ErrorEvent(this, singleClient(client), "Number of player insufficient"));
                    eventQueue.notifyAll();
                }
            } /*else { //NON CAPISCO A COSA SERVA QUESTO CODICE
                client.sendError("You must choose your nickname with adduser first");
            }*/
            return;
        }

        this.mainBoard.shuffleCards();
        this.mainBoard.initSharedGoldCards();
        this.mainBoard.initSharedResourceCards();
        synchronized (eventQueue) {
            eventQueue.add(new updateMainBoardEvent(this, allClients(), mainBoard));
            eventQueue.notifyAll();
        }
        this.initStarters(); // set the starters cards for every player
        System.out.println("Starting game");
        this.currentGamePhase = GamePhase.CHOOSESTARTERSIDEPHASE;
        synchronized (eventQueue) {
            //eventQueue.add(new StartEvent(this, allClients()));
            eventQueue.add(new UpdateGamePhaseEvent(this, allClients(), GamePhase.CHOOSESTARTERSIDEPHASE));
            eventQueue.notifyAll();
        }
    }

    /**
     * Initialization of starter cards
     */
    public void initStarters() {
        // for every player set his starters (you have access to every player from the array players)

        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < players.size()) {
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

    /**
     * Set player choice for starter card side
     * @param playerIndex Player who wants to set the side
     * @param side Side chosen
     */
    public void setStarterSide(int playerIndex, Side side) {
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the starter card"));
                eventQueue.notifyAll();
            }
            return;
        }
        StarterCard playerStarterCard = player.getStarterCard();
        //doesn't call updateBoard because the boardMatrix already has the card (without the side)
        player.addToPlacedCardsMap(playerStarterCard.getId(), side);

        synchronized (eventQueue) {
            eventQueue.add(new UpdateStarterCardEvent(this, allClients(), playerIndex, playerStarterCard.getId(), side));
            eventQueue.notifyAll();
        }

        // add the initial elements of the starter card
        for (Element ele : player.getStarterCard().getUncoveredElements(side)) {
            int currentOccurrences = player.getAllElements().get(ele);
            player.addToAllElements(ele, currentOccurrences + 1);
        }


        this.answered.put(playerIndex, true);
        System.out.println("someone chose his startercard, the number of ansewred is: " + numberAnswered());
        //notify him and all the others about the change
        if (numberAnswered() == players.size()) {
            System.out.println("everyone answred");
            this.currentGamePhase = GamePhase.CHOOSECOLORPHASE;
            synchronized (eventQueue) {
                eventQueue.add(new UpdateGamePhaseEvent(this, allClients(), GamePhase.CHOOSECOLORPHASE));
                eventQueue.notifyAll();
            }
            resetAnswered();
        }
    }

    /**
     * Set player choice for the color
     * @param playerIndex Player who wants to set the color
     * @param color Color chosen
     */
    public void setColor(int playerIndex, Color color) {//method called by view when the player chooses the side
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the color"));
                eventQueue.notifyAll();
            }
            return;
        }

        for (int j = 0; j < players.size(); j++) {
            if (color.equals(players.get(j).getColor())) {
                synchronized (eventQueue) {
                    eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The color was already selected by another user, please select a new one"));
                    eventQueue.notifyAll();
                }
                return;

            }
        }
        player.setColor(color);
        this.answered.put(playerIndex, true);

        if (numberAnswered() == players.size()) {
            resetAnswered();
            try {
                //send also the cards to the view
                this.setHands();
            } catch (EmptyDeckException e) {
                // if in this moment of the game the deck is empty it's a programming mistake
                throw new RuntimeException(e);
            }
            //send also the cards to the view
            this.setObjectives();
            this.currentGamePhase = GamePhase.CHOOSEOBJECTIVEPHASE;
            synchronized (eventQueue) {
                eventQueue.add(new UpdateGamePhaseEvent(this, allClients(), GamePhase.CHOOSEOBJECTIVEPHASE));
                eventQueue.notifyAll();
            }
        }
    }

    /**
     * Initialization of players hands
     * @throws EmptyDeckException Deck is empty
     */
    public void setHands() throws EmptyDeckException {
        // popolate hands for every player
        for (int i = 0; i < players.size(); i++) {
            GoldCard goldCard = mainBoard.drawFromGoldDeck();
            ResourceCard resourceCard1 = mainBoard.drawFromResourceDeck();
            ResourceCard resourceCard2 = mainBoard.drawFromResourceDeck();

            players.get(i).addToHandCardsMap(goldCard.getId(), Side.FRONT); // default is front side
            players.get(i).addToHandCardsMap(resourceCard1.getId(), Side.FRONT); // default is front side
            players.get(i).addToHandCardsMap(resourceCard2.getId(), Side.FRONT); // default is front side
        }
    }

    /**
     * Set both secret and shared objectives for each player
     */
    public void setObjectives() {
        Set<Integer> randomKeysSet = new HashSet<>(); // a set has no duplicates
        // Generate four different random numbers
        while (randomKeysSet.size() < 2 * players.size() + 2) { // 2 for each player and 2 shared
            int randomNumber = Config.firstObjectiveCardId + ThreadLocalRandom.current().nextInt(Config.OBJECTIVEQTY); // Adjust range as needed
            randomKeysSet.add(randomNumber);
        }

        ArrayList<Integer> randomKeysList = new ArrayList<>(randomKeysSet); // convert to a list to iterate
        for (int i = 0; i < players.size(); i++) {
            ObjectiveCard objectiveCard0 = (ObjectiveCard) cardsMap.get(randomKeysList.get(i));
            ObjectiveCard objectiveCard1 = (ObjectiveCard) cardsMap.get(randomKeysList.get(i + players.size()));
            ObjectiveCard[] objectiveCards = new ObjectiveCard[2];
            objectiveCards[0] = objectiveCard0;
            objectiveCards[1] = objectiveCard1;

            players.get(i).setSecretObjectiveCardOptions(objectiveCards);
        }

        ObjectiveCard objectiveCard0 = (ObjectiveCard) cardsMap.get(randomKeysList.get(randomKeysList.size() - 2));
        ObjectiveCard objectiveCard1 = (ObjectiveCard) cardsMap.get(randomKeysList.getLast());

        mainBoard.setSharedObjectiveCard(0, objectiveCard0);
        mainBoard.setSharedObjectiveCard(1, objectiveCard1);
        ArrayList<ObjectiveCard> sharedObjectives = new ArrayList<>();
        sharedObjectives.add(objectiveCard0);
        sharedObjectives.add(objectiveCard1);

        synchronized (eventQueue) {
            eventQueue.add(new UpdateSharedObjectiveEvent(this, allClients(), sharedObjectives));
            eventQueue.notifyAll();
        }
    }

    /**
     * Set player choice of the secret objective
     * @param playerIndex Player who wants to set the objective
     * @param cardIndex Index that the player choose (0-1)
     */
    //called from controller
    public void setSecretObjective(int playerIndex, int cardIndex) {
        Player player = this.players.get(playerIndex);
        if (this.answered.get(playerIndex)) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "You already selected the objective card"));
                eventQueue.notifyAll();
            }
            return;
        }

        if (cardIndex != 0 && cardIndex != 1) {
            synchronized (eventQueue) {
                eventQueue.add(new ErrorEvent(this, singleClient(player.getClient()), "The card chosen was not in the options list"));
                eventQueue.notifyAll();
            }
            return;
        }

        player.setObjectiveCard(player.getObjectiveCardOption(cardIndex));
        this.answered.put(playerIndex, true);

        if (numberAnswered() == players.size()) {
            resetAnswered();
            currentGamePhase = GamePhase.MAINPHASE;
            synchronized (eventQueue) {
                eventQueue.add(new UpdateGamePhaseEvent(this, allClients(), GamePhase.MAINPHASE));
                eventQueue.add(new UpdateCurrentPlayer(this, allClients(), currentPlayerIndex));
                eventQueue.notifyAll();
            }
        }
    }


    //GAMEPHASE


//----------------------------place Card--------------------------------

    /**
     * Funzion for plaicing a card on the player board
     * @param player Player who is playcing the card
     * @param placingCardId Card to place
     * @param tableCardId Card on the table to be connected to
     * @param tableCornerPos Corner position of the table card for the connection
     * @param placingCornerPos Corner posizion of the hand card for the connection
     * @param placingCardSide Side of the playing card
     * @throws PlacingOnHiddenCornerException The corner is hidden
     */
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
        //NON MI PIACE MESSA QUI PERCHE NON E' L'ULTIMA AZIONE DEL CONTROLLER, CERCARE DI CAPIRE COME MODIFIFCARE
        //ANDRA MESSO NELLO STATE PATTERN
        synchronized (eventQueue) {
            eventQueue.add(new UpdateTurnPhaseEvent(this, allClients(), TurnPhase.DRAWPHASE));
            eventQueue.notifyAll();
        }
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

    /**
     * Update the elements in the player attributes
     * @param player Player to be updated
     * @param placingCard Card placed
     * @param placingCardSide Side of the placed card
     */
    public void updateElements(Player player, CornerCard placingCard, Side placingCardSide) {
        for (Element ele : Element.values()) {
            if (player.getAllElements().get(ele) != null) {
                int currentOccurencies = player.getAllElements().get(ele);
                int newOccurencies = Collections.frequency(placingCard.getUncoveredElements(placingCardSide), ele);
                player.addToAllElements(ele, currentOccurencies + newOccurencies);
            }
        }
    }

    //ENDGAME

    /**
     * Check if game is ended by one of the possible conditions
     */
    public void checkGameEnded() {
        // 20 points?
        if (!currentGamePhase.equals(GamePhase.ENDPHASE)) {
            for (Player player : this.players) {
                if (player.getPoints() >= Config.POINTSTOENDPHASE) {
                    currentGamePhase = GamePhase.ENDPHASE;
                    break;
                }
            }
            if (currentGamePhase.equals(GamePhase.ENDPHASE)) {
                //currentPlayer is already updated here, it's the one that plays next, right after the previous drew
                //currentPlayer = 0 => playerSize
                //currentPlayer = 1 => playerSize + playerSize - 1
                //currentPlayer = 2 => playerSize + playerSize - 2
                //currentPlayer = 3 => playerSize + playerSize - 3
                if (currentPlayerIndex == 0) {
                    this.turnToPlay = players.size();
                } else {
                    this.turnToPlay = 2 * players.size() - currentPlayerIndex;
                }
            }
        }

        // cards ended?
        if ((this.getMainBoard().isResourceDeckEmpty() && this.getMainBoard().isGoldDeckEmpty()) || turnToPlay == 0) {
            currentGamePhase = GamePhase.FINALPHASE;
            calculateFinalPoints();
            ArrayList<Integer> winners;
            try {
                winners = getWinnerPlayerIndex();
            } catch (GameIsNotEndedException e) {
                throw new RuntimeException(e);
            }
            synchronized (eventQueue) {
                eventQueue.add(new GameEndedEvent(this, allClients(), winners));
                eventQueue.notifyAll();
            }
        }
    }

    /**
     * Calculation of the points at the end of the game
     */
    void calculateFinalPoints() {
        ObjectiveCard sharedCard1 = this.mainBoard.getSharedObjectiveCard(0);
        ObjectiveCard sharedCard2 = this.mainBoard.getSharedObjectiveCard(1);

        for (int i = 0; i < this.players.size(); i++) {
            Player player = this.players.get(i);
            ObjectiveCard objCard = player.getObjectiveCard();

            int points = 0;

            points += objCard.calculatePoints(player);
            points += sharedCard1.calculatePoints(player);
            points += sharedCard2.calculatePoints(player);

            this.players.get(i).addPoints(points);
        }
    }

    /**
     * Get the winner player
     * @return List of indexes of winners
     * @throws GameIsNotEndedException Game not ended
     */
    public ArrayList<Integer> getWinnerPlayerIndex() throws GameIsNotEndedException {
        int maxPoints = 0;
        ArrayList<Integer> winnerPlayerIndeces = new ArrayList<>();
        boolean playerReachedPoints = false;
        for (int i = 0; i < this.players.size(); i++) {
            if (this.players.get(i).getPoints() >= Config.POINTSTOENDPHASE) {
                playerReachedPoints = true;
            }
            if (this.players.get(i).getPoints() > maxPoints) {
                winnerPlayerIndeces = new ArrayList<>();
                winnerPlayerIndeces.add(i);
                maxPoints = this.players.get(i).getPoints();
            } else if (this.players.get(i).getPoints() == maxPoints) {
                winnerPlayerIndeces.add(i);
            }
        }
        if (!playerReachedPoints) {
            throw new GameIsNotEndedException("You called getWinnerPlayerIndex even if the game is not ended, no one is at 20 points");
        }

        // now winnerPlayerIndeces contains who has the maximum number of points
        // decide the real winner if there is a draw
        ArrayList<Integer> winnerObjectives = new ArrayList<>();
        if (winnerPlayerIndeces.size() > 1) {
            int mostObjectivesWins = -1;
            for (int i = 0; i < winnerPlayerIndeces.size(); i++) {
                if (this.players.get(i).getObjectivesWon() > mostObjectivesWins) {
                    winnerObjectives.clear();
                    winnerObjectives.add(i);
                    mostObjectivesWins = this.players.get(i).getObjectivesWon();
                } else if (this.players.get(i).getObjectivesWon() == mostObjectivesWins) {
                    winnerObjectives.add(i);
                }
            }
            winnerPlayerIndeces = winnerObjectives;
        }
        return (winnerPlayerIndeces);
    }


//------------ others --------------------

    // !!! to implement !!!
    public void saveGameState() {
    }

    // !!! optional !!!
    public int getTurnTime() {
        return 0;
    }
}