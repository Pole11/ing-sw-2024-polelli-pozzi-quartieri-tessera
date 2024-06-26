package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.ClientBot;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.RmiClient;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.SocketClient;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.rmi.RemoteException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FullGameTest {
    @Test
    public void fullTest() {
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
        c.chooseInitialStarterSide(1,Side.BACK);
        c.chooseInitialStarterSide(2,Side.BACK);
        c.chooseInitialStarterSide(3,Side.BACK);

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
        c.chooseInitialColor(1, Color.GREEN);
        c.chooseInitialColor(2, Color.RED);
        c.chooseInitialColor(3, Color.YELLOW);

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
        c.chooseInitialObjective(1, 1);
        c.chooseInitialObjective(2, 0);
        c.chooseInitialObjective(3, 1);

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

        g.setPlayersConnected(0, false);
        g.setPlayersConnected(1, false);
        g.setPlayersConnected(2, false);
        g.setPlayersConnected(3, false);
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


        // end game for disconnection
        g.setPlayersConnected(0, false);
        g.setPlayersConnected(1, false);
        g.setPlayersConnected(2, false);
        g.setPlayersConnected(3, false);
        g.playerDisconnected(0);
        g.playerDisconnected(1);
        g.playerDisconnected(2);
        g.playerDisconnected(3);

        try {
            Thread.sleep(1000*Config.TIMEOUT_TIME + 1);
        } catch (InterruptedException e) {
            System.out.println("The game ended, but there is no server to restart, success");
        }
    }
}
