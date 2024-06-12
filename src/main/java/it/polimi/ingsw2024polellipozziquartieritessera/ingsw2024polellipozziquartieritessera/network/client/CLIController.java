package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.CoverageChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.ElementChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.CommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable.PingCommandRunnable;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;


import java.rmi.RemoteException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CLIController {
    private final ViewModel viewModel;
    private final ArrayDeque<CommandRunnable> commandQueue;
    private Thread executeCommands;
    private boolean executeCommandRunning;

    public CLIController(ViewModel viewModel){
        this.commandQueue = new ArrayDeque();
        this.viewModel = viewModel;

        executeCommandRunning = true;
        restartExecuteCommand();
    }

    public void restartExecuteCommand(){
        if (executeCommands != null && executeCommands.isAlive()){
            executeCommandRunning = false;
            executeCommands.interrupt();
            synchronized (commandQueue){
                commandQueue.notifyAll();
            }

            try {
                executeCommands.join();
            } catch (InterruptedException ignored) {}

        }
        executeCommandRunning = true;
        this.executeCommands = new Thread(this::executeCommandsRunnable);
        this.executeCommands.start();
    }

    private void executeCommandsRunnable() {
        while (executeCommandRunning) {
            CommandRunnable command = null;
            synchronized (commandQueue) {
                while (commandQueue.isEmpty()) {
                    try {
                        commandQueue.wait();
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                if (!executeCommandRunning){
                    break;
                }
                command = commandQueue.remove();
                commandQueue.notifyAll();
            }
            command.executeCLI();
        }
    }


    public void manageInput(VirtualServer server, VirtualView client, Client clientContainer, String[] message) throws RemoteException {
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

    public void ping(VirtualView client, VirtualServer server){
        synchronized (commandQueue) {
            PingCommandRunnable commandRunnable = new PingCommandRunnable();
            commandRunnable.setClient(client);
            commandRunnable.setServer(server);
            commandQueue.add(commandRunnable);
            commandQueue.notifyAll();
        }
    }


    public void printAllCommands() {
        AtomicInteger i = new AtomicInteger();
        System.out.print("The possible commands are: \n[");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + " ");
            if(i.incrementAndGet() % 5 == 0)
                System.out.print("]\n[");
        });
        System.out.print("]\n");
        System.out.println("To see in more detail: <COMMAND> -h\n> ");
    }

/*    public void printCard(int CardId, Side side){
        ArrayList<String> cardToPrint = new ArrayList<>();
        cardToPrint = getPrintedCard(CardId, side);

        for(int i = 0; i < cardToPrint.size(); i++){
            System.out.println(cardToPrint.get(i));
        }
    }*/

/*    public ArrayList<String> getPrintedCard(int cardId, Side side) {
        ArrayList<String> cardToPrint = null;
        Card card = viewModel.cardById(cardId);
        return cardToPrint = printCard(card, side);

    }*/

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
            colorIndex = Math.min((int)(Math.floor((double) cardsOrder.get(k)/20) +1), 5);
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
/*            cardToPrint = printCard(card, viewModel.getPlacedCardSide(cardsOrder.get(k)));
            for(i = 0; i < cardList.size(); i++){
                cardList.get(i).concat(openString + " " + cardToPrint.get(i) + closeString);
            }*/
        }
        for(i = 0; i < printedBoard.size(); i++){
            for (j = 0; j < printedBoard.get(i).size(); j++){
                System.out.print(printedBoard.get(i).get(j));
            }
            System.out.println();
        }
        System.out.println("this are the card placed in the board");
        printNCards(cardsOrder, sides);

    }

    public void showHand(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.CHOOSEOBJECTIVEPHASE.ordinal()){
            System.err.println("your hand is not initialized yet");
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

            System.out.println("this are the common objectives for this game");
            printNCards(cards, sides);
        }
    }
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
            System.out.println("this are your secret objectives");
            printNCards(cards, sides);
        }
    }

    public void showBoard(int player_index){
        if(viewModel.getGamePhase().ordinal() < GamePhase.MAINPHASE.ordinal()){
            System.err.print("this board does not exists yet\n> ");
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
    public void showBoard(){
        showBoard(viewModel.getPlayerIndex());
    }

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
    public void showPlayers(){
        int n_players = viewModel.getPlayersSize();
        if(n_players < 1){
            System.out.print("there are no players registered\n> ");
        } else {
            System.out.println("this are the players nicknames ");
            for(int i = 0; i < n_players; i++){
                System.out.println("index: " + i + " nickname: " + viewModel.getNickname(i));
            }
            System.out.print("> ");
        }
;    }
    public void showPoints(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.MAINPHASE.ordinal()) {
            System.err.print("you cant ask players points before the main phase has started\n> ");
        }else {
            int n_players = viewModel.getPlayersSize();
            System.out.println("this are the players points ");
            for (int i = 0; i < n_players; i++) {
                System.out.println(" nickname: " + viewModel.getNickname(i) + "\tpoints: " + viewModel.getPointsMap(i));
            }
            System.out.print("> ");

        }
    }

    public void showDecks(){
        if(viewModel.getGamePhase().ordinal() < GamePhase.MAINPHASE.ordinal()) {
            System.err.print("you cant ask to see the decks before the main phase has starter\n> ");
        } else {
            ArrayList<Integer> cards = new ArrayList<>();
            ArrayList<Side> sides = new ArrayList<>();
            int decks[] = viewModel.getSharedCards();
            String titles[] = {"SHARED RESOURCE 0","SHARED RESOURCE 1","SHARED GOLD 0","SHARED GOLD 1","RESOURCE DECK","GOLD DECK"};

            for (int i = 0; i < decks.length; i++) {
                cards.add(decks[i]);
                sides.add(Side.FRONT);
            }
            System.out.println("this is the current main board");
            for(int i = 0; i < titles.length; i++){
                System.out.print(getFormattedString(20,titles[i]));
            }
            System.out.println();
            printNCards(cards, sides);
        }
    }

    private ArrayList<String> printCard(Card card, Side side){
        if(card instanceof ObjectiveCard){
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

    private void printNCards(ArrayList<Integer> cards, ArrayList<Side> sides){
        ArrayList<String> output = new ArrayList<>();
        ArrayList<String> temp = new ArrayList<>();
        for(int i = 0; i < 7; i++){
            output.add("");
        }
        for(int i = 0; i < cards.size(); i++) {
            temp = printCard(viewModel.cardById(cards.get(i)),sides.get(i)) ;
            for (int j = 0; j < 7; j++) {
                output.set(j, output.get(j) + " " + temp.get(j));
            }
        }
        for (int i = 0; i < output.size(); i++){
            System.out.println(output.get(i));
        }
        System.out.print("\n> ");

    }

    private ArrayList<String> printCard(GoldCard card, Side side){
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
            costString = cost.get(key) + key.toString().substring(0,3).toUpperCase();
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
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, "") + "|");
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, "<"+resource.toString().substring(0,3).toUpperCase()+">") + "|" + ColorPrint.RESET);
            output.add(ColorPrint.GOLD + "|" + getFormattedString(17, resource.toString().substring(0,3).toUpperCase()) + "|");
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

    private  void resizeI(ArrayList<ArrayList<Integer>> matrix){
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

}
