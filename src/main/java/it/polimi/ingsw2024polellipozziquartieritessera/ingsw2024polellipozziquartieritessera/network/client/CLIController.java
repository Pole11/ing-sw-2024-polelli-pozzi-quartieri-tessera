package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Chat;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Message;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.CommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.GameEndedCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PingCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PongCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;


import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Thread.sleep;

/**
 * Class that manage cli printings and cli actions
 */
public class CLIController {
    private ViewModel viewModel;
    private final ArrayDeque<CommandRunnable> commandQueue;
    private Thread executeCommands;
    private Thread pongThread;
    private boolean executeCommandRunning;
    private boolean pongRunning;

    private Thread serverThread;

    /**
     * Initializes a new CLIController with the specified ViewModel.
     *
     * @param viewModel the ViewModel to be associated with this controller
     */
    public CLIController(ViewModel viewModel){
        this.commandQueue = new ArrayDeque();
        this.viewModel = viewModel;

        executeCommandRunning = true;
        restartExecuteCommand();

        pongRunning = true;
    }

    /**
     * Interrupts the server thread to respond to a pong.
     */
    public void pongAnswer(){
        serverThread.interrupt();
    }

    /**
     * Restarts the pong mechanism with the given server, client, and client container.
     *
     * @param server         the VirtualServer instance
     * @param client         the VirtualView instance
     * @param clientContainer the Client instance which contains the common implementations between VirtualViews
     */
    public void restartPong(VirtualServer server, VirtualView client, Client clientContainer){
        if (pongThread != null && pongThread.isAlive()){
            pongRunning = false;
            pongThread.interrupt();
            try {
                pongThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        pongRunning = true;


        if (serverThread != null){
            if (serverThread.isAlive()){
                serverThread.interrupt();
            }
            try {
                serverThread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        serverThread = new Thread(()->{
            try {
                Thread.sleep(1000*Config.WAIT_FOR_PONG_TIME);
                System.out.println("disconnection from gui controller");
                pongRunning = false;
                clientContainer.serverDisconnected();
            } catch (InterruptedException e) {
                System.out.println("serverhread interrupted");
            }
        });


        pongThread = new Thread(()->{
            while (pongRunning) {
                if (server == null) continue;
                if (serverThread != null){
                    if (serverThread.isAlive()){
                        serverThread.interrupt();
                    }
                    try {
                        serverThread.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                serverThread = new Thread(()->{
                    try {
                        Thread.sleep(1000*Config.WAIT_FOR_PONG_TIME);
                        System.out.println("disconnection from gui controller");
                        pongRunning = false;
                        clientContainer.serverDisconnected();
                    } catch (InterruptedException e) {

                    }
                });
                serverThread.start();
                synchronized (commandQueue){
                    PongCommandRunnable commandRunnable = new PongCommandRunnable();
                    commandRunnable.setClient(client);
                    commandRunnable.setServer(server);
                    commandRunnable.setClientContainer(clientContainer);
                    commandQueue.add(commandRunnable);
                    commandQueue.notifyAll();
                }
                try {
                    sleep(1000*Config.NEXT_PONG);
                    //sleep(5000);
                } catch (InterruptedException e) {
                    System.out.println("pong thread interrupted");
                }
            }
        });
        pongThread.start();
    }

    /**
     * Sets the ViewModel associated with this controller.
     *
     * @param viewModel the ViewModel to be set
     */
    public void setViewModel(ViewModel viewModel){
        this.viewModel = viewModel;
    }

//new
    /**
     * Restarts the execution of commands.
     */
    public void restartExecuteCommand(){
        if (executeCommands != null && executeCommands.isAlive()){
            //executeCommandRunning = false;
            executeCommands.interrupt();
            try {
                executeCommands.join();
            } catch (InterruptedException ignored) {}

        }
        commandQueue.clear();
        executeCommandRunning = true;
        this.executeCommands = new Thread(this::executeCommandsRunnable);
        this.executeCommands.start();
    }

    /**
     * Restarts the controller with the given client and server.
     *
     * @param client the VirtualView instance
     * @param server the VirtualServer instance
     */
    public void restart(VirtualView client, VirtualServer server) {
        synchronized (commandQueue) {
            commandQueue.clear();
            GameEndedCommandRunnable commandRunnable = new GameEndedCommandRunnable();
            commandRunnable.setClient(client);
            commandRunnable.setServer(server);
            commandQueue.add(commandRunnable);
            commandQueue.notifyAll();
        }
        restartExecuteCommand();
    }

    private void executeCommandsRunnable() {
        while (executeCommandRunning) {
            CommandRunnable command = null;
            synchronized (commandQueue) {
                while (commandQueue.isEmpty()) {
                    try {
                        commandQueue.wait();
                    } catch (InterruptedException e) {
                        System.out.println("interrupted");
                        return;
                    }
                }

                command = commandQueue.remove();
            }
            command.executeCLI();

        }
    }

    /**
     * Manages the input from the client.
     *
     * @param server          the VirtualServer instance
     * @param client          the VirtualView instance
     * @param clientContainer the Client instance
     * @param message         the input message
     * @throws RemoteException if a remote error occurs
     */
    public void manageInput(VirtualServer server, VirtualView client, Client clientContainer, String[] message) throws RemoteException {
        //
        if (message[0].equalsIgnoreCase(Command.PING.toString()) || message[0].equalsIgnoreCase(Command.GAMEENDED.toString())){
            System.err.print("INVALID COMMAND\n> ");
            return;
        }
        try {
            if (message[1].equals("-h")){
                Command.valueOf(message[0].toUpperCase()).getCommandRunnable(message, server, clientContainer, client).executeHelp();
                return;
            }
        } catch (Exception ignored) {}

        try {
            if (Command.valueOf(message[0].toUpperCase()).getType().equals("Local")){
                Command.valueOf(message[0].toUpperCase()).getCommandRunnable(message, server, clientContainer, client).executeCLI();
            } else {
                synchronized (commandQueue){
                    commandQueue.add(Command.valueOf(message[0].toUpperCase()).getCommandRunnable(message, server, clientContainer, client));
                    commandQueue.notifyAll();
                }
            }
        } catch (IllegalArgumentException e) {

            System.err.print("INVALID COMMAND\n> ");
        }
    }

    /**
     * Sends a ping to the server.
     *
     * @param client the VirtualView instance that sends the ping
     * @param server the VirtualServer instance that should receive the ping
     */
    public void ping(VirtualView client, VirtualServer server){
        synchronized (commandQueue) {
            PingCommandRunnable commandRunnable = new PingCommandRunnable();
            commandRunnable.setClient(client);
            commandRunnable.setServer(server);
            commandQueue.add(commandRunnable);
            commandQueue.notifyAll();
        }
    }

    /**
     * Displays the elements visible to the current player.
     */
    public void showElements(){
        int index = viewModel.getPlayerIndex();
        showElements(index);
    }

    /**
     * Displays the elements of a specified player.
     *
     * @param index the index of the player
     */
    public void showElements(int index){
        if(index >= viewModel.getPlayersSize() || index < 0){
            System.err.print("the index doesn't match an existing player");
        } else {
            HashMap<Integer, HashMap> map = viewModel.getElementsMap();
            if(index == viewModel.getPlayerIndex()) {
                System.out.println("this are the elements visible on your board");
            } else{
                System.out.println("this are the elements visible on " + viewModel.getNickname(index) +"'s board");
            }
            for (Object e : map.get(index).keySet()) {
                if ((int) map.get(index).get(e) != 0) {
                    System.out.println(e + ": " + map.get(index).get(e));
                }
            }
        }
        System.out.print("> ");
    }

    /**
     * Prints all available commands.
     */
    public void printAllCommands() {
        AtomicInteger i = new AtomicInteger();
        System.out.print("The possible commands are: \n[");
        Arrays.stream(Command.values()).forEach(e->{
            if (!e.equals(Command.PING) && !e.equals(Command.GAMEENDED)){
                System.out.print(e + " ");
                if(i.incrementAndGet() % 5 == 0)
                    System.out.print("]\n[");
            } else {
                i.decrementAndGet();
            }
        });
        System.out.print("]\n");
        System.out.print("To see in more detail: <COMMAND> -h\n> ");
    }

    /**
     * Prints the specified board and card order.
     *
     * @param board      the board to be printed
     * @param cardsOrder the order of the cards on the board
     */
    public void printBoard(ArrayList<ArrayList<Integer>> board, ArrayList<Integer> cardsOrder){
        int i = 0, j = 0, u = 0 ,v = 0, colorIndex = 0;
        int irel = 0, jrel = 0;
        boolean found = false;
        String openString = "", closeString = "";
        ArrayList<ArrayList<Integer>> rotatedBoard = new ArrayList<>();
        ArrayList<ArrayList<String>> printedBoard = new ArrayList<>();
        ArrayList<String> cardList  = new ArrayList<>();
        ArrayList<String> cardToPrint = new ArrayList<>();
        ArrayList<Card> realCardOrder = new ArrayList<>();
        ArrayList<Side> sides = new ArrayList<>();

        for(i = 0; i < 7; i++){
            cardList.add("");
        }
        //initialize rotatedBoard
        int a = board.size();
        int b = board.get(0).size();
        int c = a+b;

        for(i = 0; i < c; i++){
            rotatedBoard.add(new ArrayList<>());
            for(j  = 0; j < c; j++){
                rotatedBoard.get(i).add(0);
            }
        }
        //rotate board of 45°
        int centeri = (int)Math.floor((double) board.size()/2);
        int centerj = (int)Math.floor((double) board.get(0).size()/2);
        int rcenteri = (int)Math.floor((double) rotatedBoard.size()/2);
        int rcenterj = (int)Math.floor((double) rotatedBoard.get(0).size()/2);

        for(i = 0; i < board.size(); i++){
            for(j = 0; j < board.get(i).size(); j++){
                irel = i - centeri;
                jrel = j - centerj;
                rotatedBoard.get(rcenteri - centeri + i - jrel).set(rcenterj -centerj + j + irel, board.get(i).get(j));
            }
        }
        //delete useless rows and columns
        resizeI(rotatedBoard);
        //initialize printedBoard
        for(i = 0; i < rotatedBoard.size()*2 +1; i++){
            printedBoard.add(new ArrayList<>());
            printedBoard.add(new ArrayList<>());
            for(j = 0; j < rotatedBoard.get(0).size() * 2 + 1; j++){
                printedBoard.get(i*2).add(PrintableCardParts.EMPTY.firstRow());
                printedBoard.get(i*2 + 1).add(PrintableCardParts.EMPTY.secondRow());
            }
        }
        //calculate printBoard
        for(int k = 0; k < cardsOrder.size(); k++){
            found = false;
            //search the id indexes in the rotatedBoard
            for(i = 0; !found && i < rotatedBoard.size(); i++){
                for(j = 0; !found && j < rotatedBoard.get(i).size(); j++){
                    if(rotatedBoard.get(i).get(j) == cardsOrder.get(k)){
                        found = true;
                        i--;
                        j--;
                    }
                }
            }
            u = 2*i;
            v = 2*j;
            colorIndex = ColorPrint.getColorIndex(cardsOrder.get(k));
            openString = ColorPrint.BLACK.toString() + ColorPrint.byIndex(colorIndex).toString();
            closeString = ColorPrint.RESET.toString();
            for(int n = 0; n < 3; n++){
                for(int m = 0; m < 3; m++){
                    if(n == 1 && m == 1){
                        printedBoard.get(u*2+n*2).set(v+m,openString + PrintableCardParts.byIndex(n*3+m).firstRow() + closeString);
                        printedBoard.get(u*2+n*2+1).set(v+m,openString + String.format(PrintableCardParts.byIndex(n*3+m).secondRow(), cardsOrder.get(k)) + closeString);

                    } else {
                        printedBoard.get(u*2 + n*2).set(v + m, openString + PrintableCardParts.byIndex(n * 3 + m).firstRow() + closeString);
                        printedBoard.get(u*2 + n*2 + 1).set(v + m, openString + PrintableCardParts.byIndex(n * 3 + m).secondRow() + closeString);
                    }
                }
            }
            sides.add(viewModel.getPlacedCardSide(cardsOrder.get(k)));

        }
        for(i = 0; i < printedBoard.size(); i++){
            for (j = 0; j < printedBoard.get(i).size(); j++){
                System.out.print(printedBoard.get(i).get(j));
            }
            System.out.println();
        }
        System.out.println("these are the cards placed in the board");
        printNCards(cardsOrder, sides);

    }

    /**
     * Displays the cards in the current player's hand.
     */
    public void showHand(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.CHOOSEOBJECTIVEPHASE.ordinal() || viewModel.getHand(viewModel.getPlayerIndex())==null){
            System.out.println("your hand is not initialized yet");
        } else {
            ArrayList<Integer> hand = viewModel.getHand(viewModel.getPlayerIndex());
            ArrayList<Side> sides = new ArrayList<>();
            for (int i = 0; i < hand.size(); i++) {
                sides.add(viewModel.getHandCardsSide(hand.get(i)));
            }
            System.out.println("this is your hand");
            printNCards(hand, sides);
        }
    }

    /**
     * Displays the common objectives of the game.
     */
    public void ShowCommonObjective(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.CHOOSEOBJECTIVEPHASE.ordinal()){
            System.err.print("common objectives are not initialized yet\n> ");
        }else {
            ArrayList<Integer> cards = new ArrayList<>();
            ArrayList<Side> sides = new ArrayList<>();

            cards.add(viewModel.getCommonObjectiveCards()[0]);
            cards.add(viewModel.getCommonObjectiveCards()[1]);
            sides.add(Side.FRONT);
            sides.add(Side.FRONT);

            System.out.println("these are the common objectives for this game");
            printNCards(cards, sides);
        }
    }

    /**
     * Displays the secret objectives of the current player.
     */
    public void ShowSecretObjective(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.CHOOSEOBJECTIVEPHASE.ordinal()){
            System.err.print("Secret objectives not initialized yet\n>   ");
        } else {
            int objectives[] = viewModel.getObjectives();
            ArrayList<Integer> cards = new ArrayList<>();
            ArrayList<Side> sides = new ArrayList<>();

            cards.add(objectives[2]);
            sides.add(viewModel.getPlacedCardSide(objectives[2]));
            if (objectives[3] != -1) {
                cards.add(objectives[3]);
                sides.add(viewModel.getPlacedCardSide(objectives[3]));
            }
            System.out.println("these are your secret objectives");
            printNCards(cards, sides);
        }
    }

    /**
     * Displays the board of the player at the specified index.
     *
     * @param player_index the index of the player whose board is to be displayed
     */
    public void showBoard(int player_index){
        if(viewModel.getGamePhase().ordinal() < GamePhase.MAINPHASE.ordinal() || viewModel.getPlayerBoard(player_index)==null){
            System.out.print("this board does not exists yet\n> ");
        } else {
            String playerName = viewModel.getNickname(player_index) + " 's";
            ArrayList<ArrayList<Integer>> board = viewModel.getPlayerBoard(player_index);
            ArrayList<Integer> cardsOrder = viewModel.getPlacingCardOrderMap(player_index);
            if(player_index == viewModel.getPlayerIndex())
                playerName = "your";
            System.out.println("this is " + playerName + " board");
            printBoard(board, cardsOrder);
        }
    }

    /**
     * Displays the board of the current player.
     */
    public void showBoard(){
        showBoard(viewModel.getPlayerIndex());
    }

    /**
     * Displays the starter card of the current player.
     */
    public void showStarterCard(){
        ArrayList<Integer> cards = new ArrayList<>();
        ArrayList<Side> sides = new ArrayList<>();

        cards.add(viewModel.getStarterCard());
        cards.add(viewModel.getStarterCard());
        sides.add(Side.FRONT);
        sides.add(Side.BACK);
        System.out.println("\n" + getFormattedString(19,Side.FRONT.toString()) +" " +  getFormattedString(19, Side.BACK.toString()));
        printNCards(cards, sides);

    }

    /**
     * Displays the nicknames and connection statuses of all players.
     */
    public void showPlayers(){
        int n_players = viewModel.getPlayersSize();
        String colorPlayer = "";

        if(n_players < 1){
            System.out.print("you are not registered\n> ");
        } else {
            System.out.println("these are the players nicknames ");
            //System.out.println("You are the player with index: " + viewModel.getPlayerIndex() + ", your nickname is " +viewModel.getNickname(viewModel.getPlayerIndex()));
            for(int i = 0; i < n_players; i++){
                colorPlayer = "";
                if(viewModel.getColorsMap(i) != null) {
                    colorPlayer = ColorPrint.getColorPrint(viewModel.getColorsMap(i)).toString();
                }
                System.out.println("index: " + i + " nickname: " + colorPlayer + viewModel.getNickname(i) + ColorPrint.RESET + " is connected: " + viewModel.getConnession(i));

            }
            System.out.print("> ");
        }
    }

    /**
     * Displays the points of all players.
     */
    public void showPoints(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.MAINPHASE.ordinal()) {
            System.err.print("you cant ask players points before the main phase has started\n> ");
        }else {
            int n_players = viewModel.getPlayersSize();
            System.out.println("these are the players points ");
            for (int i = 0; i < n_players; i++) {
                System.out.println(" nickname: " + viewModel.getNickname(i) + "\tpoints: " + viewModel.getPointsMap(i));
            }
            System.out.print("> ");

        }
    }

    /**
     * Displays the current state of all decks.
     */
    public void showDecks(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.MAINPHASE.ordinal()) {
            System.out.print("you cant ask to see the decks before the main phase has starter\n> ");
        } else {
            ArrayList<Integer> cards = new ArrayList<>();
            ArrayList<Side> sides = new ArrayList<>();
            int[] decks = viewModel.getSharedCards();
            String[] titles = {"SHAREDRESOURCE 1","SHAREDRESOURCE 2","SHAREDGOLD 1","SHAREDGOLD 2","DECKRESOURCE","DECKGOLD"};

            for (int i = 0; i < decks.length; i++) {
                cards.add(decks[i]);
                if(i < decks.length - 2) {
                    sides.add(Side.FRONT);
                }
                else {
                    sides.add(Side.BACK);
                }
            }
            System.out.println("this is the current main board");
            for(int i = 0; i < titles.length; i++){
                System.out.print(getFormattedString(20,titles[i]));
            }
            System.out.println();
            printNCards(cards, sides);
        }
    }

    /**
     * Displays the colors assigned to the current player and other players.
     */
    public void showColors(){
        System.out.println("your color is: " + viewModel.getColorsMap(viewModel.getPlayerIndex()));
        for (int i = 0; i < viewModel.getPlayersSize(); i++){
            if (i != viewModel.getPlayerIndex()){
                System.out.println("the color of " + viewModel.getNickname(i) + " is: " + viewModel.getColorsMap(i));

            }
        }
    }

    /**
     * Prints the specified card with the given side.
     *
     * @param card the card to be printed
     * @param side the side of the card to be printed
     * @return a list of strings representing the printed card
     */
    private ArrayList<String> printCard(Card card, Side side){
        if(card == null){
           ArrayList<String> emptyCard = new ArrayList<>();
           emptyCard.add("+-----------------+");
           emptyCard.add("|      \\   /      |");
           emptyCard.add("|       \\ /       |");
           emptyCard.add("|        X        |");
           emptyCard.add("|       / \\       |");
           emptyCard.add("|      /   \\      |");
           emptyCard.add("+-----------------+");
           return emptyCard;
        } else if(card instanceof ObjectiveCard){
            return printCard((ObjectiveCard) card, side);
        } else if(card instanceof ResourceCard){
            return printCard((ResourceCard) card, side);
        } else if(card instanceof GoldCard){
            return printCard((GoldCard) card, side);
        } else if(card instanceof StarterCard){
            return printCard((StarterCard) card, side );
        } else{ //todo: gestire meglio caso d'errore
            return new ArrayList<>();
        }

    }

    /**
     * Prints an ObjectiveCard.
     *
     * @param card The ObjectiveCard to be printed.
     * @param side The side of the card to be printed.
     * @return A formatted representation of the card as an ArrayList of strings.
     */
    private ArrayList<String> printCard(ObjectiveCard card, Side side) {
        // attributes
        int points;
        ArrayList<Element> elementChallenge = null;
        Element[][] structureChallenge = null;
        String elementString = "";
        ArrayList<ArrayList<String>> challengeMatrix = null;
        String structureString = null;
        ArrayList<String> output = new ArrayList<>();

        int irel = 0, jrel = 0;

        // set points
        points = card.getPoints();
        // set challenge
        if (card.getChallenge() instanceof StructureChallenge) {
            Element[][] configuration = new Element[Config.N_STRUCTURE_CHALLENGE_CONFIGURATION][Config.N_STRUCTURE_CHALLENGE_CONFIGURATION];
            for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++) {
                for (int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++) {
                    configuration[i][j] = ((StructureChallenge) card.getChallenge()).getConfiguration()[i][j];
                }
            }
            structureChallenge = configuration;
        } else if (card.getChallenge() instanceof ElementChallenge) {
            ArrayList<Element> elements = new ArrayList<>();
            for (Object e : ((ElementChallenge) card.getChallenge()).getElements()) {
                elements.add(Element.valueOf(e.toString().toUpperCase()));
            }
            elementChallenge = elements;
        }
        //printing objects settings

        if(card.getChallenge() instanceof ElementChallenge) {
            for (Element e : elementChallenge) {
                elementString =  elementString + " " + e.toString().substring(0, 3).toUpperCase();
            }
            if (elementString.startsWith(" ")) {
                elementString = elementString.substring(1);
            }
        } else if (card.getChallenge() instanceof StructureChallenge) {
            challengeMatrix = new ArrayList<ArrayList<String>>();
            for(int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION + 2; i++){
                challengeMatrix.add(new ArrayList<String>());
                for(int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION + 2; j++){
                    challengeMatrix.get(i).add("");
                }
            }

            //rotation of 45° of the structureChallenge 3x3 -> 5x5
            int centeri = (int)Math.floor((double) Config.N_STRUCTURE_CHALLENGE_CONFIGURATION/2);
            int centerj = (int)Math.floor((double) Config.N_STRUCTURE_CHALLENGE_CONFIGURATION/2);
            for(int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++){
                for(int j = 0; j < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; j++){
                    irel = i - centeri;
                    jrel = j - centerj;
                    challengeMatrix.get(centeri + i - jrel).set(centerj + j + irel, structureChallenge[i][j].toString().substring(0,3));
                }
            }
            //remove empty rows and columns
            resizeS(challengeMatrix);

        }
        if (card.getChallenge() instanceof ElementChallenge) {
            output.add("+-----------------+");
            output.add("|" + getFormattedString(17, "ELEMENT") + "|");
            output.add("|" + getFormattedString(17, "points: " + points) + "|");
            output.add("|" + getFormattedString(17, "<"+ elementString +">") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("+-----------------+");

        } else if (card.getChallenge() instanceof StructureChallenge) {
            output.add("+-----------------+");
            output.add("|" + getFormattedString(17, "STRUCTURE") + "|");
            output.add("|" + getFormattedString(17, "points: " + points) + "|");
            for(int i = 0; i < challengeMatrix.size(); i++){
                structureString = "";
                for(int j = 0; j < challengeMatrix.get(i).size(); j++){
                    if(challengeMatrix.get(i).get(j).isEmpty() || challengeMatrix.get(i).get(j).equals("EMP"))
                        structureString = structureString + "   ";
                    else
                        structureString = structureString + challengeMatrix.get(i).get(j);
                }
                output.add("|" + getFormattedString(17, structureString) + "|");
            }
            output.add("+-----------------+");
        }
        return output;
    }

    /**
     * Prints a ResourceCard.
     *
     * @param card The ResourceCard to be printed.
     * @param side The side of the card to be printed.
     * @return A formatted representation of the card as an ArrayList of strings.
     */
    private ArrayList<String> printCard(ResourceCard card, Side side){
        // attributes
        int points;
        Element resource;
        String[] frontCornerStrings = new String[Config.N_CORNERS];
        String[] backCornerStrings = new String[Config.N_CORNERS];
        ArrayList<String> output = new ArrayList<>();

        // set points
        points = card.getPoints();

        // set resource
        resource = card.getResourceType();

        // set corners
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            frontCornerStrings[i] = card.getFrontCorners()[i].getElement().toString().substring(0,3).toUpperCase();
            if(card.getFrontCorners()[i].getHidden() || card.getFrontCorners()[i].getCovered()){
                frontCornerStrings[i] = "   ";
            }
        }
        //for in backCorners
        for (int i = 0; i < Config.N_CORNERS; i++) {
            backCornerStrings[i] = card.getBackCorners()[i].getElement().toString().substring(0,3).toUpperCase();
            if(card.getBackCorners()[i].getHidden() || card.getBackCorners()[i].getCovered()){
                backCornerStrings[i] = "   ";
            }
        }

        if (side == Side.BACK){
            output.add("+-----------------+");
            output.add("|"+backCornerStrings[0] + getFormattedString(11, "") + backCornerStrings[1]+"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") +"|");
            output.add("|" + getFormattedString(17, resource.toString().substring(0,3).toUpperCase()) +"|");
            output.add("|"+backCornerStrings[3] + getFormattedString(11, "id: "+card.getId()) + backCornerStrings[2]+"|");
            output.add("+-----------------+");
        } else{
            output.add("+-----------------+");
            output.add("|"+frontCornerStrings[0] + getFormattedString(11, String.valueOf(points)) + frontCornerStrings[1]+"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") +"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|"+frontCornerStrings[3] + getFormattedString(11, "id: "+card.getId()) + frontCornerStrings[2]+"|");
            output.add("+-----------------+");
        }
        return output;
    }

    /**
     * Prints multiple cards.
     *
     * @param cards A list of card IDs to be printed.
     * @param sides A list of sides (FRONT or BACK) for each card to be printed.
     */
    private void printNCards(ArrayList<Integer> cards, ArrayList<Side> sides){
        ArrayList<String> output = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            output.add("");
        }
        for(int i = 0; i < cards.size(); i++) {
            temp = printCard(viewModel.cardById(cards.get(i)),sides.get(i));
            for (int j = 0; j < 7; j++) {
                output.set(j, output.get(j) + " " + temp.get(j));
            }
        }
        for (int i = 0; i < output.size(); i++){
            System.out.println(output.get(i));
        }
        System.out.print("\n> ");

    }

    /**
     * Prints a GoldCard.
     *
     * @param card The GoldCard to be printed.
     * @param side The side of the card to be printed.
     * @return A formatted representation of the card as an ArrayList of strings.
     */
    private ArrayList<String> printCard(GoldCard card, Side side){
        //todo remove id from backs

        // attributes
        int points;
        Element resource;
        String[] frontCornerStrings = new String[Config.N_CORNERS];
        String[] backCornerStrings = new String[Config.N_CORNERS];
        boolean coverageChallenge = false;
        boolean noChallenge = false;
        ArrayList<Element> elementChallenge = new ArrayList<>();
        ArrayList<Element> resourceNeeded = new ArrayList<>();
        ArrayList<String> output = new ArrayList<>();

        // set points
        points = card.getPoints();

        // set resource
        resource = card.getResourceType();

        // set corners
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            frontCornerStrings[i] = card.getFrontCorners()[i].getElement().toString().substring(0,3).toUpperCase();
            if(card.getFrontCorners()[i].getHidden() || card.getFrontCorners()[i].getCovered()){
                frontCornerStrings[i] = "   ";
            }
        }
        //for in backCorners
        for (int i = 0; i < Config.N_CORNERS; i++) {
            backCornerStrings[i] = card.getBackCorners()[i].getElement().toString().substring(0,3).toUpperCase();
            if(card.getBackCorners()[i].getHidden() || card.getBackCorners()[i].getCovered()){
                backCornerStrings[i] = "   ";
            }
        }

        // set challenge
        if (card.getChallenge() instanceof CoverageChallenge){
            coverageChallenge = true;
        } else if (card.getChallenge() instanceof ElementChallenge){
            ArrayList<Element> elements = new ArrayList<>();
            for (Element e : ((ElementChallenge) card.getChallenge()).getElements()) {
                elements.add(Element.valueOf(e.toString().toUpperCase()));
            }
            elementChallenge = elements;
        } else{
            noChallenge = true;
        }

        // set needed resources
        for (Element e :  card.getResourceNeeded()) {
            resourceNeeded.add(Element.valueOf(e.toString().toUpperCase()));
        }

        // define cost and challenge strings
        HashMap<Element, Integer> cost = new HashMap<>();
        for (Element e : resourceNeeded){
            if(!cost.containsKey(e)){
                cost.put(e,1);
            } else{
                cost.put(e, cost.get(e)+1);
            }
        }
        String costString = "";
        for (Element key : cost.keySet()){
            costString = costString + " " +  cost.get(key) + key.toString().substring(0,3).toUpperCase();
        }

        String challengeString = "";
        if (coverageChallenge){
            challengeString = "cha: COV";
        } else if (!elementChallenge.isEmpty()){
            challengeString = "cha: " + elementChallenge.getFirst().toString().substring(0,3).toUpperCase();
        }

        if (side == Side.BACK){
            output.add(ColorPrint.GOLD + "+-----------------+" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + backCornerStrings[0] + getFormattedString(11, "") +backCornerStrings[1] + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, "") + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, resource.toString().substring(0,3).toUpperCase()) + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + backCornerStrings[3] + getFormattedString(11, "id: "+card.getId()) + backCornerStrings[2] + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "+-----------------+" + ColorPrint.RESET);
        } else{
            output.add(ColorPrint.GOLD + "+-----------------+" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + frontCornerStrings[0] + getFormattedString(11, ""+ points) + frontCornerStrings[1] + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, costString)+"|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, challengeString)+"|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + frontCornerStrings[3] + getFormattedString(11, "id: "+card.getId()) + frontCornerStrings[2] + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "+-----------------+" + ColorPrint.RESET);
        }
        return output;
    }

    /**
     * Prints a StarterCard.
     *
     * @param card The StarterCard to be printed.
     * @param side The side of the card to be printed.
     * @return A formatted representation of the card as an ArrayList of strings.
     */
    private ArrayList<String> printCard(StarterCard card, Side side){
        // attributes
        String[] frontCornerStrings = new String[Config.N_CORNERS];
        String[] backCornerStrings = new String[Config.N_CORNERS];
        ArrayList<Element> centerResources = new ArrayList<>();
        ArrayList<String> output = new ArrayList<>();
        // set corners
        //for in frontCorners
        for (int i = 0; i < Config.N_CORNERS; i++){
            frontCornerStrings[i] = card.getFrontCorners()[i].getElement().toString().substring(0,3).toUpperCase();
            if(card.getFrontCorners()[i].getHidden() || card.getFrontCorners()[i].getCovered()){
                frontCornerStrings[i] = "   ";
            }
        }
        //for in backCorners
        for (int i = 0; i < Config.N_CORNERS; i++) {
            backCornerStrings[i] = card.getBackCorners()[i].getElement().toString().substring(0,3).toUpperCase();
            if(card.getBackCorners()[i].getHidden() || card.getBackCorners()[i].getCovered()){
                backCornerStrings[i] = "   ";
            }
        }

        // set center resources
        for (Element e : card.getCenterResources()) {
            centerResources.add(Element.valueOf(e.toString().toUpperCase()));
        }

        // create centerString
        String centerString = "";
        for (Element e : centerResources){
            centerString = centerString + " " + e.toString().substring(0, 3).toUpperCase();
        }
        if (centerString.startsWith(" ")) {
            centerString = centerString.substring(1);
        }

        if (side == Side.BACK){
            output.add("+-----------------+");
            output.add("|" + backCornerStrings[0] + getFormattedString(11, "") + backCornerStrings[1] + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + backCornerStrings[3] + getFormattedString(11, "id: " + card.getId()) + backCornerStrings[2] + "|");
            output.add("+-----------------+");
        } else{
            output.add("+-----------------+");
            output.add("|" + frontCornerStrings[0] + getFormattedString(11, "") + frontCornerStrings[1] + "|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + getFormattedString(17, centerString) +"|");
            output.add("|" + getFormattedString(17, "") + "|");
            output.add("|" + frontCornerStrings[3] + getFormattedString(11, "id: "+card.getId()) + frontCornerStrings[2] + "|");
            output.add("+-----------------+");
        }
        return output;
    }

    /**
     * Formats a string to be centered within a specified length.
     *
     * @param length The total length of the formatted string.
     * @param content The content to be centered.
     * @return The formatted string with the content centered.
     */
    private String getFormattedString(int length, String content){
        // ------
        if (length%2 == 0){
            while (content.length() < length){
                if (content.length()%2 == 0){
                    content = " " + content + " ";
                } else{
                    content = content + " ";
                }
            }
        } else{
            while (content.length() < length){
                if (content.length()%2 == 0){
                    content = content + " ";
                } else{
                    content = " " + content + " ";
                }
            }
        }
        return content;
    }



    /**
     * Resizes a matrix of strings by removing empty rows and columns.
     *
     * @param matrix The matrix to be resized.
     */
    private void resizeS(ArrayList<ArrayList<String>> matrix){
        //remove empty rows
        for(int j = 0; j < matrix.size(); j++){
            Boolean isEmpty = true;
            for(int i = 0; i < matrix.get(j).size() && isEmpty; i++){
                if(!matrix.get(j).get(i).equals("") && !matrix.get(j).get(i).equals("EMP")){
                    isEmpty = false;
                }
            }
            if(isEmpty){
                matrix.remove(j);
                j--;
            }
        }
        //remove empty columns
        ArrayList<Boolean> areEmpty = new ArrayList<>();
        for(int i = 0; i < matrix.get(0).size(); i++){
            areEmpty.add(true);
        }

        for(int i = 0; i < matrix.size(); i++){
            for (int j = 0; j < matrix.get(i).size(); j++){
                if(!matrix.get(i).get(j).equals("") && !matrix.get(i).get(j).equals("EMP")){
                    areEmpty.set(j, false);
                }
            }
        }
        for(int j = 0; j < areEmpty.size(); j++){
            if(areEmpty.get(j).equals(true)){
                for(int i = 0; i < matrix.size(); i++){
                    matrix.get(i).remove(j);
                }
                areEmpty.remove(j--);
            }
        }
    }

    /**
     * Resizes a matrix of integers by removing empty rows and columns.
     *
     * @param matrix The matrix to be resized.
     */
    private void resizeI(ArrayList<ArrayList<Integer>> matrix){
        //remove empty rows
        for(int j = 0; j < matrix.size(); j++){
            Boolean isEmpty = true;
            for(int i = 0; i < matrix.get(j).size() && isEmpty; i++){
                if(!matrix.get(j).get(i).equals(0)){
                    isEmpty = false;
                }
            }
            if(isEmpty){
                matrix.remove(j);
                j--;
            }
        }
        //remove empty columns
        ArrayList<Boolean> areEmpty = new ArrayList<>();
        for(int i = 0; i < matrix.get(0).size(); i++){
            areEmpty.add(true);
        }

        for(int i = 0; i < matrix.size(); i++){
            for (int j = 0; j < matrix.get(i).size(); j++){
                if(!matrix.get(i).get(j).equals(0)){
                    areEmpty.set(j, false);
                }
            }
        }
        for(int j = 0; j < areEmpty.size(); j++){
            if(areEmpty.get(j).equals(true)){
                for(int i = 0; i < matrix.size(); i++){
                    matrix.get(i).remove(j);
                }
                areEmpty.remove(j--);
            }
        }
    }

    /**
     * Displays the game chat.
     */
    public void showChat() {
        Chat chat = viewModel.getChat();

        System.out.println("-------- GAME CHAT --------");
        for (Message message : chat.getMessages()){
            String playerName = viewModel.getNickname(message.getAuthor());
            System.out.println("[" + playerName + "] " + message.getContent());
        }
        viewModel.resetNewMessages();

        System.out.print("> ");
    }

    /**
     * Displays the current game status.
     */
    public void status(){
        System.out.println("-----GAME-STATUS-----");
        GamePhase currentGamePhase = viewModel.getGamePhase();
        System.out.println("current game phase: " + currentGamePhase.toString());
        if(currentGamePhase.equals(GamePhase.MAINPHASE) || currentGamePhase.equals(GamePhase.ENDPHASE)){
            System.out.println("current turn phase: " + viewModel.getTurnPhase().toString());
        }
        if(viewModel.getNickname(viewModel.getCurrentPlayer()) != null) {
            if (viewModel.getPlayerIndex() == viewModel.getCurrentPlayer()) {
                System.out.print("it's your turn to play" + "\n >");
            } else {
                System.out.print("the current player is: " + viewModel.getNickname(viewModel.getCurrentPlayer()) + "\n> ");
            }
        }
    }
}
