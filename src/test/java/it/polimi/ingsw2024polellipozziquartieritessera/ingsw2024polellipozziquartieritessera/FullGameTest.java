package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.ClientBot;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.RmiClient;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.SocketClient;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.*;

public class FullGameTest {
    @Test
    public void gameWithDisconnections() {
        Server server = new Server(null, null, null);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        VirtualView client1 = null;
        VirtualView client2 = null;
        try {
            client2 = new RmiClient(null, null);
            client1 = new RmiClient(null, null);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        VirtualView client3 = new SocketClient(null, null, null);
        VirtualView client4 = new SocketClient(null, null, null);
        VirtualView client5 = new SocketClient(null, null, null);

        GameState g = new GameState(server);
        try {
            Populate.populate(g);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Controller c = new Controller();
        c.setGameState(g);

        c.addPlayer(client1, "A");
        c.addPlayer(client2, "A");
        c.addPlayer(client2, "B");
        c.addPlayer(client3, "C");
        c.addPlayer(client4, "D");
        c.addPlayer(client5, "E");

        // testing
        assertEquals(2, c.getObjectiveCardOptions(0).length);
        assertEquals("A", c.getPlayerNickname(0));
        assertEquals(TurnPhase.PLACINGPHASE, c.getTurnPhase());
        assertEquals(0, c.getPlayerIndex(client1));
        assertEquals(0, c.getCurrentPlayerIndex());
        assertEquals(GamePhase.NICKNAMEPHASE, c.getGamePhase());

        c.startGame(client1);

        // testing
        assertEquals(2, c.getObjectiveCardOptions(0).length);
        assertEquals("A", c.getPlayerNickname(0));
        assertEquals(TurnPhase.PLACINGPHASE, c.getTurnPhase());
        assertEquals(0, c.getPlayerIndex(client1));
        assertEquals(0, c.getCurrentPlayerIndex());
        assertEquals(GamePhase.CHOOSESTARTERSIDEPHASE, c.getGamePhase());

        // wrong commands
        try {
            c.placeCard(0, 10, 10, CornerPos.UPLEFT, Side.FRONT);
            c.placeCard(0, 10, 80, CornerPos.UPLEFT, Side.FRONT);
            c.drawCard(DrawType.DECKRESOURCE);
            c.chooseInitialObjective(0,100);
            c.chooseInitialColor(0, Color.BLUE);
            c.startGame(client2);
        } catch (WrongPlacingPositionException | CardNotPlacedException | GoldCardCannotBePlacedException |
                 CardAlreadyPresentOnTheCornerException | CardIsNotInHandException | PlacingOnHiddenCornerException |
                 CardAlreadPlacedException | CardNotOnBoardException | WrongInstanceTypeException |
                 EmptyMainBoardException | EmptyDeckException | InvalidHandException e) {
            System.out.println("Exception called (wrong command");
        }

        c.ping(client1);
        c.ping(client2);
        c.ping(client3);
        c.ping(client4);

        c.chooseInitialStarterSide(0,Side.BACK);

        g.playerDisconnected(1);

        c.chooseInitialStarterSide(2,Side.BACK);

        g.playerDisconnected(3);

        g.manageReconnection(g.getPlayer(1));
        g.manageReconnection(g.getPlayer(3));

        // testing
        assertEquals(TurnPhase.PLACINGPHASE, c.getTurnPhase());
        assertEquals(0, c.getCurrentPlayerIndex());
        assertEquals(GamePhase.CHOOSECOLORPHASE, c.getGamePhase());

        // wrong commands
        try {
            c.placeCard(0, 10, 10, CornerPos.UPLEFT, Side.FRONT);
            c.placeCard(0, 10, 80, CornerPos.UPLEFT, Side.FRONT);
            c.drawCard(DrawType.DECKRESOURCE);
            c.chooseInitialObjective(0,100);
            c.chooseInitialColor(0, Color.BLUE);
            c.startGame(client2);
        } catch (WrongPlacingPositionException | CardNotPlacedException | GoldCardCannotBePlacedException |
                 CardAlreadyPresentOnTheCornerException | CardIsNotInHandException | PlacingOnHiddenCornerException |
                 CardAlreadPlacedException | CardNotOnBoardException | WrongInstanceTypeException |
                 EmptyMainBoardException | EmptyDeckException | InvalidHandException e) {
            System.out.println("Exception called (wrong command");
        }

        c.chooseInitialColor(0, Color.BLUE);
        c.chooseInitialColor(1, Color.BLUE);
        g.playerDisconnected(1);

        c.chooseInitialColor(2, Color.YELLOW);

        g.playerDisconnected(3);

        g.manageReconnection(g.getPlayer(1));
        g.manageReconnection(g.getPlayer(3));

        // testing
        assertEquals(TurnPhase.PLACINGPHASE, c.getTurnPhase());
        assertEquals(0, c.getCurrentPlayerIndex());
        assertEquals(GamePhase.CHOOSEOBJECTIVEPHASE, c.getGamePhase());

        // wrong commands
        try {
            c.placeCard(0, 10, 10, CornerPos.UPLEFT, Side.FRONT);
            c.placeCard(0, 10, 80, CornerPos.UPLEFT, Side.FRONT);
            c.drawCard(DrawType.DECKRESOURCE);
            c.chooseInitialObjective(0,100);
            c.chooseInitialColor(0, Color.BLUE);
            c.startGame(client2);
        } catch (WrongPlacingPositionException | CardNotPlacedException | GoldCardCannotBePlacedException |
                 CardAlreadyPresentOnTheCornerException | CardIsNotInHandException | PlacingOnHiddenCornerException |
                 CardAlreadPlacedException | CardNotOnBoardException | WrongInstanceTypeException |
                 EmptyMainBoardException | EmptyDeckException | InvalidHandException e) {
            System.out.println("Exception called (wrong command");
        }

        c.chooseInitialObjective(0, 0);
        g.playerDisconnected(1);
        c.chooseInitialObjective(2, 0);
        g.playerDisconnected(3);

        g.manageReconnection(g.getPlayer(1));
        g.manageReconnection(g.getPlayer(3));

        try {
            Thread.sleep(Config.WAIT_FOR_PING_TIME-1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        c.ping(client1);
        c.ping(client2);
        c.ping(client3);

        try {
            Thread.sleep(Config.WAIT_DISCONNECTED_SERVER-1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        c.ping(client1);
        c.ping(client2);
        c.ping(client3);
        c.ping(client4);

        g.setPlayersConnected(0, false);
        g.playerDisconnected(0);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        c.addPlayer(client1, "A");

        g.playerDisconnected(0);
        g.playerDisconnected(1);
        g.playerDisconnected(2);
        g.playerDisconnected(3);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        c.addPlayer(client1, "A");
        c.addPlayer(client2, "B");
        c.addPlayer(client3, "C");
        c.addPlayer(client4, "D");
        c.addPlayer(client5, "E");

        // game

        // testing
        assertEquals(TurnPhase.PLACINGPHASE, c.getTurnPhase());
        assertEquals(0, c.getCurrentPlayerIndex());
        assertEquals(GamePhase.MAINPHASE, c.getGamePhase());

        // wrong commands
        try {
            c.placeCard(0, 10, 10, CornerPos.UPLEFT, Side.FRONT);
            c.placeCard(0, 10, 80, CornerPos.UPLEFT, Side.FRONT);
            c.drawCard(DrawType.DECKRESOURCE);
            c.chooseInitialObjective(0,100);
            c.chooseInitialColor(0, Color.BLUE);
            c.startGame(client2);
        } catch (WrongPlacingPositionException | CardNotPlacedException | GoldCardCannotBePlacedException |
                 CardAlreadyPresentOnTheCornerException | CardIsNotInHandException | PlacingOnHiddenCornerException |
                 CardAlreadPlacedException | CardNotOnBoardException | WrongInstanceTypeException |
                 EmptyMainBoardException | EmptyDeckException | InvalidHandException e) {
            System.out.println("Exception called (wrong command");
        }

        int handCardId = 0;

        // normal draw and placing round
        for (int i = 0; i<102; i++){
            if(g.getPlayer(0).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        try {
            c.placeCard(0, handCardId, g.getPlayer(0).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        } catch (WrongPlacingPositionException | CardNotOnBoardException | WrongInstanceTypeException |
                 CardIsNotInHandException | CardAlreadPlacedException | PlacingOnHiddenCornerException |
                 CardNotPlacedException | GoldCardCannotBePlacedException | CardAlreadyPresentOnTheCornerException e) {
            throw new RuntimeException(e);
        }

        // end game for disconnection
        try {
            g.playerDisconnected(0);
            g.setPlayersConnected(0, false);
            Thread.sleep(10);
            g.playerDisconnected(1);
            g.setPlayersConnected(1, false);
            Thread.sleep(10);
            g.playerDisconnected(2);
            g.setPlayersConnected(2, false);
            Thread.sleep(10);
            g.playerDisconnected(3);
            g.setPlayersConnected(3, false);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Thread.sleep(1000*Config.TIMEOUT_TIME + 1);
        } catch (InterruptedException e) {
            System.out.println("Game ended");
        }
    }

    @Test
    void testGameForcingEnd() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, CardNotPlacedException, CardIsNotInHandException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadyPresentOnTheCornerException, GoldCardCannotBePlacedException, CardAlreadPlacedException, WrongInstanceTypeException, EmptyDeckException, InvalidHandException, CardNotOnBoardException, EmptyMainBoardException {
        VirtualView client1 = new SocketClient(null, null, null);
        VirtualView client2 = new SocketClient(null, null, null);
        VirtualView client3 = new SocketClient(null, null, null);
        VirtualView client4 = new SocketClient(null, null, null);

        // client not in the game
        VirtualView client5 = null;

        Server server = new Server(null, null, null);
        GameState gs = new GameState(server);
        try {
            Populate.populate(gs);
        } catch (IOException e) {
            throw new RuntimeException("Error during setup: " + e.getMessage(), e);
        }
        Controller c = new Controller();
        c.setGameState(gs);

        // random commands
        c.getGamePhase();
        c.getCurrentPlayerIndex();
        c.getPlayerIndex(client1);
        c.getTurnPhase();


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

        assertEquals(gs.getAnswered().get(0), true);
        gs.setAnswered(0, true);
        gs.setAnswered(3,false);

        // random commands
        c.getGamePhase();
        c.getCurrentPlayerIndex();
        c.getPlayerIndex(client1);
        c.getTurnPhase();
        c.addMessage(0,"message1");
        c.addMessage(1,"message2");

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
        c.placeCard(0, handCardId, gs.getPlayer(0).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        // verification of the turn
        assertEquals(gs.getCurrentGamePhase(), GamePhase.MAINPHASE);
        gs.setCurrentGamePhase(GamePhase.MAINPHASE);
        assertEquals(TurnPhase.DRAWPHASE, gs.getCurrentGameTurn());
        gs.setCurrentGameTurn(TurnPhase.DRAWPHASE);

        c.drawCard(DrawType.DECKRESOURCE);

        // verification of the turn
        assertEquals(gs.getCurrentGamePhase(), GamePhase.MAINPHASE);
        gs.setCurrentGamePhase(GamePhase.MAINPHASE);
        assertEquals(TurnPhase.PLACINGPHASE, gs.getCurrentGameTurn());
        gs.setCurrentGameTurn(TurnPhase.PLACINGPHASE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(1).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(1, handCardId, gs.getPlayer(1).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        c.drawCard(DrawType.DECKGOLD);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(2).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(2, handCardId, gs.getPlayer(2).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        c.drawCard(DrawType.SHAREDGOLD1);

        // random commands
        c.getGamePhase();
        c.getCurrentPlayerIndex();
        c.getPlayerIndex(client1);
        c.getTurnPhase();
        c.getObjectiveCardOptions(0);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(3).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(3, handCardId, gs.getPlayer(3).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        c.drawCard(DrawType.SHAREDRESOURCE1);

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
        c.placeCard(0, handCardId, gs.getPlayer(0).getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        c.drawCard(DrawType.SHAREDGOLD2);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(1).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(1, handCardId, gs.getPlayer(1).getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        c.drawCard(DrawType.SHAREDRESOURCE2);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(2).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(2, handCardId, gs.getPlayer(2).getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        c.drawCard(DrawType.DECKRESOURCE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(3).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(3, handCardId, gs.getPlayer(3).getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        c.drawCard(DrawType.DECKRESOURCE);

        assertEquals(gs.getTurnToPlay(), 0);
        assertNull(gs.getPrevGamePhase());
        gs.setTurnToPlay(gs.getTurnToPlay());

        gs.playerDisconnected(0);
        gs.clientEnded(client1);
        gs.pingAnswer(client2);
        gs.allConnectedClients();
        gs.allClients();
    }

    @Test
    void testGameEndDecks() throws CardNotPlacedException, CardIsNotInHandException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadyPresentOnTheCornerException, GoldCardCannotBePlacedException, CardAlreadPlacedException, WrongInstanceTypeException, EmptyDeckException, InvalidHandException, CardNotOnBoardException, EmptyMainBoardException {
        VirtualView client1 = new SocketClient(null, null, null);
        VirtualView client2 = new SocketClient(null, null, null);
        VirtualView client3 = new SocketClient(null, null, null);
        VirtualView client4 = new SocketClient(null, null, null);

        Server server = new Server(null, null, null);
        GameState gs = new GameState(server);
        try {
            Populate.populate(gs);
        } catch (IOException e) {
            throw new RuntimeException("Error during setup: " + e.getMessage(), e);
        }
        Controller c = new Controller();
        c.setGameState(gs);

        c.addPlayer(client1, "Pippo");
        c.addPlayer(client2, "Jhonny");
        c.addPlayer(client3, "Rezzonico");
        c.addPlayer(client4, "Pollo");

        // initial phase
        assertEquals(c.getGamePhase(), GamePhase.NICKNAMEPHASE);
        c.startGame(client1);

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
        c.chooseInitialColor(2, Color.YELLOW);
        c.chooseInitialColor(3, Color.BLUE);

        // objectives choice
        assertEquals(c.getGamePhase(), GamePhase.CHOOSEOBJECTIVEPHASE);
        c.chooseInitialObjective(0,0);
        c.chooseInitialObjective(1,1);
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
        c.placeCard(0, handCardId, gs.getPlayer(0).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);

        // verification of the turn
        assertEquals(gs.getCurrentGamePhase(), GamePhase.MAINPHASE);
        gs.setCurrentGamePhase(GamePhase.MAINPHASE);
        assertEquals(TurnPhase.DRAWPHASE, gs.getCurrentGameTurn());
        gs.setCurrentGameTurn(TurnPhase.DRAWPHASE);

        c.drawCard(DrawType.DECKRESOURCE);

        // verification of the turn
        assertEquals(gs.getCurrentGamePhase(), GamePhase.MAINPHASE);
        gs.setCurrentGamePhase(GamePhase.MAINPHASE);
        assertEquals(TurnPhase.PLACINGPHASE, gs.getCurrentGameTurn());
        gs.setCurrentGameTurn(TurnPhase.PLACINGPHASE);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(1).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(1, handCardId, gs.getPlayer(1).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        c.drawCard(DrawType.DECKGOLD);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(2).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(2, handCardId, gs.getPlayer(2).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        c.drawCard(DrawType.SHAREDGOLD1);

        // random commands
        c.getGamePhase();
        c.getCurrentPlayerIndex();
        c.getPlayerIndex(client1);
        c.getTurnPhase();
        c.getObjectiveCardOptions(0);

        for (int i = 0; i<102; i++){
            if(gs.getPlayer(3).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(3, handCardId, gs.getPlayer(3).getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        c.drawCard(DrawType.SHAREDRESOURCE1);

        // end game
        while (!gs.getMainBoard().isGoldDeckEmpty()){
            gs.getMainBoard().drawFromGoldDeck();
        }

        while (!gs.getMainBoard().isResourceDeckEmpty()){
            gs.getMainBoard().drawFromResourceDeck();
        }

        gs.getMainBoard().drawSharedResourceCard(1);
        gs.getMainBoard().drawSharedResourceCard(2);
        gs.getMainBoard().drawSharedGoldCard(1);
        gs.getMainBoard().drawSharedGoldCard(2);

        gs.checkGameEnded();

        handCardId = 0;

        // normal draw and placing round
        for (int i = 0; i<102; i++){
            if(gs.getPlayer(0).handCardContains(i)){
                handCardId = i;
                break;
            }
        }
        c.placeCard(0, handCardId, gs.getPlayer(0).getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
    }
}
