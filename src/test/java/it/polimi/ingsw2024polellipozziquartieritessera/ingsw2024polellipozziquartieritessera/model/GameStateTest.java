package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.util.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.StarterCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.SocketClient;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;
import org.junit.jupiter.api.Test;

public class GameStateTest {

    @Test
    void SetObjectiveTest() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, InvalidObjectiveCardException {
        VirtualView client1 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client2 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client3 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client4 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());

        GameState gs = Populate.populate();
        gs.addPlayer("paolo", client1);
        gs.addPlayer("piergiorgio", client2);
        gs.addPlayer("fungiforme", client3);
        gs.addPlayer("paola", client4);
        gs.setObjectives();
        assertNotEquals(gs.getMainBoard().getSharedObjectiveCard(0), gs.getMainBoard().getSharedObjectiveCard(1));

        gs.setSecretObjective(0, 0);
        gs.setSecretObjective(1,1);
        gs.setSecretObjective(2,0);
        gs.setSecretObjective(3,0);

        assertNotEquals(gs.getMainBoard().getSharedObjectiveCard(1), gs.getPlayer(0).getObjectiveCard());
        for (int i = 0; i < gs.getPlayersSize()-1; i++) {
            assertNotEquals(gs.getPlayer(i).getObjectiveCard(), gs.getPlayer(i+1).getObjectiveCard());
        }
    }


    @Test
    void NicknameAndColorTest() throws WrongStructureConfigurationSizeException, IOException, NotUniquePlayerNicknameException, NotUniquePlayerColorException {
        // same colors and nicknames
        VirtualView client1 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client2 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());

        GameState gs = Populate.createCardsMap();
        gs.addPlayer("paolo", client1);
        gs.addPlayer("piergiorgio", client2);
        gs.setColor(0, Color.BLUE);
        gs.setColor(0, Color.YELLOW);
    }

    @Test
    void calculateFinalPointsTest() throws CardNotPlacedException, CardIsNotInHandException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadyPresentOnTheCornerException, GoldCardCannotBePlacedException, CardAlreadPlacedException, WrongInstanceTypeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, EmptyDeckException {

//-----------------------RECREATE SITUATION IN getCardPointsTest2-------------------------

        VirtualView client = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());

        GameState gs = Populate.createCardsMap();
        gs.addPlayer("paolo", client);
        Controller c = new Controller(gs);
        Player player = gs.getPlayer(0);

        int starterCardId = 81;
        int resourceCardId1 = 31;
        int goldCardId1 = 59;
        int goldCardId2 = 51;
        int resourceCardId2 = 19;
        int resourceCardId3 = 20;
        int resourceCardId4 = 39;
        int resourceCardId5 = 37;
        int resourceCardId6 = 10;
        int resourceCardId7 = 71;
        int resourceCardId8 = 75;
        int goldCardId3 = 53;
        int goldCardId4 = 54;
        int resourceCardId9 = 12;
        int goldCardId5 = 58;

        // create game state
        //GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        //Controller c = new Controller(gs);
        gs.getMainBoard().shuffleCards();
        gs.getMainBoard().initSharedGoldCards();
        gs.getMainBoard().initSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCard(starterCardId));
        player.initializeBoard();
        c.chooseInitialStarterSide(0, Side.FRONT);


        player.addToHandCardsMap(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId1, Side.FRONT);
        assertThrows(CardAlreadyPresentOnTheCornerException.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK));
        assertThrows(GoldCardCannotBePlacedException.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT));
        assertThrows(GoldCardCannotBePlacedException.class, () -> c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.FRONT));


        c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId2, Side.FRONT);
        c.placeCard(0, goldCardId2, player.getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(1, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, player.getStarterCard().getId(), CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(2, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        assertEquals(3, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId4, Side.FRONT);
        c.placeCard(0, resourceCardId4, goldCardId2, CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(4, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId5, Side.FRONT);
        c.placeCard(0, resourceCardId5, goldCardId1, CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(4, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(4, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId6, Side.FRONT);
        c.placeCard(0, resourceCardId6, resourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(5, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId7, Side.FRONT);
        c.placeCard(0, resourceCardId7, goldCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(6, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId7, Side.FRONT);
        player.addToHandCardsMap(resourceCardId1, Side.FRONT);
        assertThrows(CardAlreadPlacedException.class, ()-> c.placeCard(0, resourceCardId7, resourceCardId4, CornerPos.UPRIGHT, Side.FRONT));
        assertThrows(CardAlreadPlacedException.class, ()-> c.placeCard(0, resourceCardId1, resourceCardId4, CornerPos.UPRIGHT, Side.FRONT));

        //2 corner covered
        player.addToHandCardsMap(resourceCardId8, Side.FRONT);
        c.placeCard(0, resourceCardId8, goldCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(10, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        assertThrows(WrongInstanceTypeException.class, ()->c.placeCard(0, 84, resourceCardId6, CornerPos.DOWNLEFT, Side.FRONT));

        player.addToHandCardsMap(goldCardId3, Side.FRONT);
        c.placeCard(0, goldCardId3, resourceCardId6, CornerPos.DOWNLEFT, Side.BACK);
        assertEquals(10, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId4, Side.FRONT);
        assertThrows(PlacingOnHiddenCornerException.class, () -> c.placeCard(0, goldCardId4, goldCardId3, CornerPos.UPLEFT, Side.FRONT));

        //1 corner covered
        player.addToHandCardsMap(goldCardId4, Side.FRONT);
        c.placeCard(0, goldCardId4, goldCardId3, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(12, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId9, Side.FRONT);
        c.placeCard(0, resourceCardId9, goldCardId4, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(12, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId5, Side.FRONT);
        c.placeCard(0, goldCardId5, resourceCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(15, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(1, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));



// -------------------------ADD OBJECTIVES-----------------------------------------------------------



        int sharedObj1 = 87;
        int sharedObj2 = 88;
        int secretObj = 89;
        gs.getMainBoard().setSharedObjectiveCard(0, (ObjectiveCard) gs.getCard(sharedObj1));
        gs.getMainBoard().setSharedObjectiveCard(1, (ObjectiveCard) gs.getCard(sharedObj2));
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(15, player.getPoints());

        sharedObj1 = 90;
        sharedObj2 = 91;
        secretObj = 92;
        gs.getMainBoard().setSharedObjectiveCard(0, (ObjectiveCard) gs.getCard(sharedObj1));
        gs.getMainBoard().setSharedObjectiveCard(1, (ObjectiveCard) gs.getCard(sharedObj2));
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(15, player.getPoints());

        sharedObj1 = 93;
        sharedObj2 = 94;
        secretObj = 95;
        gs.getMainBoard().setSharedObjectiveCard(0, (ObjectiveCard) gs.getCard(sharedObj1));
        gs.getMainBoard().setSharedObjectiveCard(1, (ObjectiveCard) gs.getCard(sharedObj2));
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(15, player.getPoints());

        sharedObj1 = 96; //gives 2 points
        sharedObj2 = 97; //gives 0 points
        secretObj = 98; //gives 2 points
        gs.getMainBoard().setSharedObjectiveCard(0, (ObjectiveCard) gs.getCard(sharedObj1));
        gs.getMainBoard().setSharedObjectiveCard(1, (ObjectiveCard) gs.getCard(sharedObj2));
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(19, player.getPoints());

        sharedObj1 = 99; //gives 3 points
        sharedObj2 = 100; //gives 0 points
        secretObj = 101; //gives 0 points
        gs.getMainBoard().setSharedObjectiveCard(0, (ObjectiveCard) gs.getCard(sharedObj1));
        gs.getMainBoard().setSharedObjectiveCard(1, (ObjectiveCard) gs.getCard(sharedObj2));
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(22, player.getPoints());

        sharedObj1 = 102; //gives 0 points
        gs.getMainBoard().setSharedObjectiveCard(0, (ObjectiveCard) gs.getCard(sharedObj1));
        gs.getMainBoard().setSharedObjectiveCard(1, (ObjectiveCard) gs.getCard(sharedObj2));
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(22, player.getPoints());
    }


    @Test
    void calculateFinalPointsTest2() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, CardNotPlacedException, CardIsNotInHandException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadyPresentOnTheCornerException, GoldCardCannotBePlacedException, CardAlreadPlacedException, WrongInstanceTypeException, EmptyDeckException {
        VirtualView client = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());

        GameState gs = Populate.createCardsMap();
        gs.addPlayer("paolo", client);
        Controller c = new Controller(gs);
        Player player = gs.getPlayer(0);

        int starterCardId = 85;

        // create game state
        gs.getMainBoard().shuffleCards();
        gs.getMainBoard().initSharedGoldCards();
        gs.getMainBoard().initSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCard(starterCardId));
        player.initializeBoard();
        c.chooseInitialStarterSide(0, Side.BACK);

        player.addToHandCardsMap(41, Side.FRONT);
        c.placeCard(0, 41, starterCardId, CornerPos.UPLEFT, Side.BACK);

        player.addToHandCardsMap(42, Side.FRONT);
        c.placeCard(0, 42, 41, CornerPos.UPRIGHT, Side.BACK);

        player.addToHandCardsMap(43, Side.FRONT);
        c.placeCard(0, 43, 42, CornerPos.UPRIGHT, Side.BACK);

        player.addToHandCardsMap(44, Side.FRONT);
        c.placeCard(0, 44, 42, CornerPos.DOWNRIGHT, Side.BACK);

        player.addToHandCardsMap(45, Side.FRONT);
        c.placeCard(0, 45, starterCardId, CornerPos.DOWNRIGHT, Side.BACK);

        player.addToHandCardsMap(51, Side.FRONT);
        c.placeCard(0, 51, 45, CornerPos.UPRIGHT, Side.BACK);

        player.addToHandCardsMap(52, Side.FRONT);
        c.placeCard(0, 52, 45, CornerPos.DOWNRIGHT, Side.BACK);

        int sharedObj1 = 87; //gives 2 points
        int sharedObj2 = 91; //gives 3 points
        int secretObj = 90; //gives 0 points
        gs.getMainBoard().setSharedObjectiveCard(0, (ObjectiveCard) gs.getCard(sharedObj1));
        gs.getMainBoard().setSharedObjectiveCard(1, (ObjectiveCard) gs.getCard(sharedObj2));
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(5, player.getPoints());
    }

    @Test
    void getWinnerPlayerIndexTest() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, GameIsNotEndedException, CardNotPlacedException, WrongInstanceTypeException {
        GameState gs = Populate.populate();
        VirtualView client1 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client2 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());

        gs.addPlayer("paolo", client1);
        gs.addPlayer("piergiorgio", client2);

        ArrayList<Integer> first_wins = new ArrayList<>();
        ArrayList<Integer> second_wins = new ArrayList<>();
        ArrayList<Integer> first_second_win = new ArrayList<>();
        first_wins.add(0);
        second_wins.add(1);
        first_second_win.add(0);
        first_second_win.add(1);

        assertThrows(GameIsNotEndedException.class, gs::getWinnerPlayerIndex);

        gs.getPlayer(0).addPoints(24);
        assertEquals(first_wins, gs.getWinnerPlayerIndex());

        gs.getPlayer(1).addToAllElements(Element.INSECT, 4);

        //increments ObjectivesWon
        gs.getCard(98).calculatePoints(gs.getPlayer(1));
        assertEquals(first_wins, gs.getWinnerPlayerIndex());

        gs.getPlayer(1).addPoints(24);
        assertEquals(second_wins, gs.getWinnerPlayerIndex());


        gs.getPlayer(0).addToAllElements(Element.INSECT, 6);
        gs.getCard(98).calculatePoints(gs.getPlayer(0));
        assertEquals(first_second_win, gs.getWinnerPlayerIndex());

        gs.getPlayer(0).addToAllElements(Element.FUNGI, 6);
        gs.getCard(95).calculatePoints(gs.getPlayer(0));
        assertEquals(first_wins, gs.getWinnerPlayerIndex());

        gs.getPlayer(1).addPoints(1);
        assertEquals(second_wins, gs.getWinnerPlayerIndex());
    }

    @Test
    void testWholeGame() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, CardNotPlacedException, CardIsNotInHandException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadyPresentOnTheCornerException, GoldCardCannotBePlacedException, CardAlreadPlacedException, WrongInstanceTypeException, EmptyDeckException, InvalidHandException {
        VirtualView client1 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client2 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client3 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());
        VirtualView client4 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());

        // client not in the game
        VirtualView client5 = new SocketClient(new BufferedReader(new InputStreamReader(InputStream.nullInputStream())), new BufferedWriter(new OutputStreamWriter(OutputStream.nullOutputStream())), new Client());

        GameState gs = Populate.populate();
        Controller c = new Controller(gs);

        c.addPlayer(client1, "Pippo");

        // player 1 tries to start the game alone
        c.startGame(client1);

        // player 1 tries to enter a second time
        c.addPlayer(client1, "Bongo");

        c.addPlayer(client2, "Pippo"); // intentional error
        c.addPlayer(client2, "Jhonny");
        c.addPlayer(client3, "Rezzonico");
        c.addPlayer(client4, "Pollo");
        c.addPlayer(client5, "Gianpiero"); // player 5 should not enter the game

        // initial phase
        assertEquals(c.getGamePhase(), GamePhase.NICKNAMEPHASE);
        c.startGame(client5); // should not work
        c.startGame(client1);
        assertEquals(gs.getPlayerIndex(client2), 1);
        assertEquals(gs.getPlayerIndex(client5), -1);

        // verification of set player index
        gs.setCurrentPlayerIndex(gs.getCurrentPlayerIndex());

        // verification of catching not existing players
        Player fake = new Player("fake", null, gs);
        assertEquals(gs.getPlayerIndex(fake), -1);

        // side choice
        assertEquals(c.getGamePhase(), GamePhase.CHOOSESTARTERSIDEPHASE);
        c.chooseInitialStarterSide(0,Side.BACK);
        c.chooseInitialStarterSide(1,Side.BACK);
        c.chooseInitialStarterSide(2,Side.BACK);
        c.chooseInitialStarterSide(3,Side.BACK);

        // color choice
        assertEquals(c.getGamePhase(), GamePhase.CHOOSECOLORPHASE);
        c.chooseInitialColor(0, Color.RED);
        c.chooseInitialColor(1, Color.GREEN);
        c.chooseInitialColor(2, Color.GREEN); // intentional error
        c.chooseInitialColor(2, Color.YELLOW);
        c.chooseInitialColor(3, Color.BLUE);

        // objectives choice
        assertEquals(c.getGamePhase(), GamePhase.CHOOSEOBJECTIVEPHASE);
        c.chooseInitialObjective(0,0);
        c.chooseInitialObjective(1,1);
        c.chooseInitialObjective(2,2); // intentional error
        c.chooseInitialObjective(2,0);
        c.chooseInitialObjective(3,0);

        // main phase
        assertEquals(c.getGamePhase(), GamePhase.MAINPHASE);

        int handCardId = 0;

        // normal draw and placing round
        for (int i = 0; i<102; i++){
            if(gs.getPlayer(0).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(0, handCardId, gs.getPlayer(0).getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);

        // verification of the turn
        assertEquals(gs.getCurrentGamePhase(), GamePhase.MAINPHASE);
        gs.setCurrentGamePhase(GamePhase.MAINPHASE);
        assertEquals(TurnPhase.DRAWPHASE, gs.getCurrentGameTurn());
        gs.setCurrentGameTurn(TurnPhase.DRAWPHASE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(1).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(1, handCardId, gs.getPlayer(1).getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(2).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(2, handCardId, gs.getPlayer(2).getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(3).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(3, handCardId, gs.getPlayer(3).getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);

        // end game
        gs.getPlayer(0).addPoints(100);
        gs.checkGameEnded();
        assertEquals(c.getGamePhase(), GamePhase.ENDPHASE);

        // play the last turns

        // normal draw and placing round
        for (int i = 0; i<102; i++){
            if(gs.getPlayer(0).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(0, handCardId, gs.getPlayer(0).getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(1).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(1, handCardId, gs.getPlayer(1).getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(2).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(2, handCardId, gs.getPlayer(2).getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(3).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(3, handCardId, gs.getPlayer(3).getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        c.drawCard(DrawType.DECKRESOURCE);
    }
}
