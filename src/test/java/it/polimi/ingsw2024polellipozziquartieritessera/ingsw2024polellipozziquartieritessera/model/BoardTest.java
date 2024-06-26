package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ResourceCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.*;

public class BoardTest {
    private Server server;
    private GameState gameState;
    private Board board;

    @BeforeEach
    void setUp() {
        server = new Server(null, null, null);
        gameState = new GameState(server);
        try {
            Populate.populate(gameState);
        } catch (IOException e) {
            throw new RuntimeException("Error during setup: " + e.getMessage(), e);
        }
        board = gameState.getMainBoard();
    }

    @Test
    void testGetterSetter() {
        board.setSharedObjectiveCard(0, board.getSharedObjectiveCard(0));
        board.setSharedObjectiveCard(1, board.getSharedObjectiveCard(1));
        board.setSharedResourceCard(0, board.getSharedResourceCard(0));
        board.setSharedGoldCard(0, board.getSharedGoldCard(0));
        board.setSharedResourceCard(1, board.getSharedResourceCard(1));
        board.setSharedGoldCard(1, board.getSharedGoldCard(1));

        assertEquals(board.getSharedObjectiveCards().length, 2);
        assertEquals(board.getSharedResourceCards().length, 2);
        assertEquals(board.getSharedGoldCards().length, 2);

        board.setGoldDeck(board.getGoldDeck());
        board.setResourceDeck(board.getResourceDeck());

        assertEquals(40, board.getGoldDeckSize(), "Gold deck size should be 40.");
        assertEquals(40, board.getResourceDeckSize(), "Resource deck size should be 40.");

        assertNotNull(board.getFirstGoldDeckCard(), "First gold deck card should not be null.");
        assertNotNull(board.getFirstResourceDeckCard(), "First resource deck card should not be null.");

        assertFalse(board.isResourceDeckEmpty(), "Resource deck should not be empty.");
        assertFalse(board.isGoldDeckEmpty(), "Gold deck should not be empty.");
    }

    @Test
    void testDrawSharedCards() throws EmptyDeckException, EmptyMainBoardException {
        board.initSharedResourceCards();
        board.initSharedGoldCards();

        ResourceCard[] initialResourceCards = {board.getSharedResourceCard(0), board.getSharedResourceCard(1)};
        GoldCard[] initialGoldCards = {board.getSharedGoldCard(0), board.getSharedGoldCard(1)};

        ResourceCard drawnResourceCard1 = board.drawSharedResourceCard(1);
        ResourceCard drawnResourceCard2 = board.drawSharedResourceCard(2);
        GoldCard drawnGoldCard1 = board.drawSharedGoldCard(1);
        GoldCard drawnGoldCard2 = board.drawSharedGoldCard(2);

        assertNotNull(drawnResourceCard1, "Drawn resource card 1 should not be null.");
        assertNotNull(drawnResourceCard2, "Drawn resource card 2 should not be null.");
        assertNotNull(drawnGoldCard1, "Drawn gold card 1 should not be null.");
        assertNotNull(drawnGoldCard2, "Drawn gold card 2 should not be null.");

        assertNotNull(board.getSharedResourceCard(0), "Shared resource card 0 should not be null.");
        assertNotNull(board.getSharedResourceCard(1), "Shared resource card 1 should not be null.");
        assertNotNull(board.getSharedGoldCard(0), "Shared gold card 0 should not be null.");
        assertNotNull(board.getSharedGoldCard(1), "Shared gold card 1 should not be null.");

        assertArrayEquals(new ResourceCard[]{drawnResourceCard1, drawnResourceCard2}, initialResourceCards, "Drawn resource cards should match initial shared cards.");
        assertArrayEquals(new GoldCard[]{drawnGoldCard1, drawnGoldCard2}, initialGoldCards, "Drawn gold cards should match initial shared cards.");

        assertThrows(IllegalArgumentException.class, () -> board.drawSharedGoldCard(4), "Expected IllegalArgumentException when drawing invalid gold card index.");
        assertThrows(IllegalArgumentException.class, () -> board.drawSharedResourceCard(4), "Expected IllegalArgumentException when drawing invalid resource card index.");
    }

    @Test
    void testGetFromResourceDeck() throws EmptyDeckException {
        ResourceCard drawnCard = board.drawFromResourceDeck();
        assertNotNull(drawnCard, "Drawn resource card should not be null.");

        while (!board.isResourceDeckEmpty()) {
            assertNotSame(drawnCard, board.drawFromResourceDeck(), "Drawn card should not be same as previous card.");
        }

        assertThrows(EmptyDeckException.class, board::drawFromResourceDeck, "Expected EmptyDeckException when drawing from an empty resource deck.");
    }

    @Test
    void testDrawFromGoldDeck() throws EmptyDeckException {
        GoldCard firstCard = board.getFirstGoldDeckCard();
        GoldCard drawnCard = board.drawFromGoldDeck();
        assertNotNull(drawnCard, "Drawn gold card should not be null.");
        assertNotSame(firstCard, board.getFirstGoldDeckCard(), "First card should not be same after drawing.");

        while (!board.isGoldDeckEmpty()) {
            assertNotSame(drawnCard, board.drawFromGoldDeck(), "Drawn card should not be same as previous card.");
        }

        assertThrows(EmptyDeckException.class, board::drawFromGoldDeck, "Expected EmptyDeckException when drawing from an empty gold deck.");
    }

    @Test
    void testShuffleCards() {
        ArrayList<GoldCard> initialGoldDeck = new ArrayList<>(board.getGoldDeck());
        ArrayList<ResourceCard> initialResourceDeck = new ArrayList<>(board.getResourceDeck());

        board.shuffleCards();

        assertNotEquals(initialGoldDeck, board.getGoldDeck(), "Gold deck should be shuffled.");
        assertNotEquals(initialResourceDeck, board.getResourceDeck(), "Resource deck should be shuffled.");

        assertTrue(initialGoldDeck.containsAll(board.getGoldDeck()) && board.getGoldDeck().containsAll(initialGoldDeck), "Gold deck should contain the same elements after shuffle.");
        assertTrue(initialResourceDeck.containsAll(board.getResourceDeck()) && board.getResourceDeck().containsAll(initialResourceDeck), "Resource deck should contain the same elements after shuffle.");
    }

    @Test
    void testShuffleCards2() {
        int goldSize = board.getGoldDeckSize();

        int resourceSize = board.getResourceDeckSize();
        board.shuffleCards();

        assertEquals(goldSize, board.getGoldDeckSize(), "Gold deck size should remain the same after shuffle.");
        assertEquals(resourceSize, board.getResourceDeckSize(), "Resource deck size should remain the same after shuffle.");
    }

    @Test
    void drawAllCards() {
        try {
            while (board.drawSharedGoldCard(1) != null) {
                assertNotNull(board);
            }
            assertNull(board.drawSharedGoldCard(1));
            board.drawSharedGoldCard(2);
            assertNull(board.drawSharedGoldCard(2));

        } catch (EmptyMainBoardException e) {
            System.out.println("Gold ended");
        }

        try {
            while (board.drawSharedResourceCard(1) != null) {
                assertNotNull(board);
            }
            assertNull(board.drawSharedResourceCard(1));
            board.drawSharedResourceCard(2);
            assertNull(board.drawSharedResourceCard(2));

        } catch (EmptyMainBoardException e) {
            System.out.println("Resources ended");
        }
    }
}