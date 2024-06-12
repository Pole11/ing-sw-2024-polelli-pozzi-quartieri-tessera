package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.EmptyDeckException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ResourceCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.*;

public class BoardTest {
    // ALL METHODS TESTED

    @Test
    void testGetterSetter() {
        // setup
        //TODO
        /*
        try {
            Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
                @Override
                public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                    return null;
                }

                @Override
                public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

                }

                @Override
                public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

                }

                @Override
                public void rebind(String name, Remote obj) throws RemoteException, AccessException {

                }

                @Override
                public String[] list() throws RemoteException, AccessException {
                    return new String[0];
                }
            });
            GameState g = new GameState(s);
            Populate.populate(g);

            Board b = g.getMainBoard();

            b.setSharedObjectiveCard(0,b.getSharedObjectiveCard(0));
            b.setSharedObjectiveCard(1,b.getSharedObjectiveCard(1));
            b.setSharedResourceCard(0,b.getSharedResourceCard(0));
            b.setSharedGoldCard(0,b.getSharedGoldCard(0));
            b.setSharedResourceCard(1,b.getSharedResourceCard(1));
            b.setSharedGoldCard(1,b.getSharedGoldCard(1));

            assertEquals(b.getGoldDeckSize(), 40);
            assertEquals(b.getResourceDeckSize(), 40);

            assertNotNull(b.getFirstGoldDeckCard());
            assertNotNull(b.getFirstResourceDeckCard());

            assertFalse(b.isResourceDeckEmpty());
            assertFalse(b.isGoldDeckEmpty());

        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException  e) {
            throw new RuntimeException(e);
        }

         */
    }

    @Test
    void testDrawSharedCards() {
        // setup
        try {
            Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
                @Override
                public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                    return null;
                }

                @Override
                public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

                }

                @Override
                public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

                }

                @Override
                public void rebind(String name, Remote obj) throws RemoteException, AccessException {

                }

                @Override
                public String[] list() throws RemoteException, AccessException {
                    return new String[0];
                }
            });
            GameState g = new GameState(s);
            Populate.populate(g);
            Board b = g.getMainBoard();
            b.initSharedResourceCards();
            b.initSharedGoldCards();

            // get initial state
            ResourceCard[] initialResourceCards = {b.getSharedResourceCard(0), b.getSharedResourceCard(1)};
            GoldCard[] initialGoldCards = {b.getSharedGoldCard(0), b.getSharedGoldCard(1)};

            // draw cards
            ResourceCard drawnResourceCard1 = b.drawSharedResourceCard(1);
            ResourceCard drawnResourceCard2 = b.drawSharedResourceCard(2);
            GoldCard drawnGoldCard1 = b.drawSharedGoldCard(1);
            GoldCard drawnGoldCard2 = b.drawSharedGoldCard(2);

            // verify drawn cards not null
            assertNotNull(drawnResourceCard1);
            assertNotNull(drawnResourceCard2);
            assertNotNull(drawnGoldCard1);
            assertNotNull(drawnGoldCard2);

            // verify cards has been replaced
            assertNotNull(b.getSharedResourceCard(0));
            assertNotNull(b.getSharedResourceCard(1));
            assertNotNull(b.getSharedGoldCard(0));
            assertNotNull(b.getSharedGoldCard(1));

            // verify drawn cards in initial shared
            assertArrayEquals(new ResourceCard[]{drawnResourceCard1, drawnResourceCard2}, initialResourceCards);
            assertArrayEquals(new GoldCard[]{drawnGoldCard1, drawnGoldCard2}, initialGoldCards);

            // test if launch exception
            assertThrows(IllegalArgumentException.class, () -> b.drawSharedGoldCard(4));
            assertThrows(IllegalArgumentException.class, () -> b.drawSharedResourceCard(4));
        } catch (WrongStructureConfigurationSizeException | IOException e) {
            throw new RuntimeException(e);
        } catch (EmptyDeckException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetFromResourceDeck() throws EmptyDeckException {
        // setup
        try {
            Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
                @Override
                public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                    return null;
                }

                @Override
                public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

                }

                @Override
                public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

                }

                @Override
                public void rebind(String name, Remote obj) throws RemoteException, AccessException {

                }

                @Override
                public String[] list() throws RemoteException, AccessException {
                    return new String[0];
                }
            });
            GameState g = new GameState(s);
            Populate.populate(g);
            Board b = g.getMainBoard();

            // function to test
            ResourceCard drawnCard = b.drawFromResourceDeck();
            // Verify not null
            assertNotNull(drawnCard);
            // Draw every card from the deck
            while (!b.isResourceDeckEmpty()) {
                // Verify card has been removed
                assertNotSame(b.drawFromResourceDeck(), drawnCard);
            }

            // If deck is ended
            assertThrows(EmptyDeckException.class, ()->b.drawFromResourceDeck());
        } catch (WrongStructureConfigurationSizeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDrawFromGoldDeck() throws EmptyDeckException {

        try {

            GameState g = new GameState(null);
            Populate.populate(g);
            Board b = g.getMainBoard();

            GoldCard firstCard = b.getFirstGoldDeckCard();

            // function to test
            GoldCard drawnCard = b.drawFromGoldDeck();
            // Verify not null
            assertNotNull(drawnCard);

            assertNotSame(firstCard, b.getFirstGoldDeckCard());


            // Draw every card from the deck
            while (!b.isGoldDeckEmpty()) {
                assertNotSame(b.drawFromGoldDeck(), drawnCard);
            }


            // If deck is ended
            assertThrows(EmptyDeckException.class, b::drawFromGoldDeck);
        } catch (WrongStructureConfigurationSizeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShuffleCards() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        //TODO
        /*
        GameState g = Populate.populate();
        Board b = g.getMainBoard();

        // Save initial state
        ArrayList<GoldCard> initialGoldDeck = new ArrayList<>(b.getGoldDeck());
        ArrayList<ResourceCard> initialResourceDeck = new ArrayList<>(b.getResourceDeck());

        // Shuffle decks
        b.shuffleCards();

        // verify if different position of the cards
        assertNotEquals(initialGoldDeck, b.getGoldDeck());
        assertNotEquals(initialResourceDeck, b.getResourceDeck());

        // verify that the lists contains the same elements of before
        assertTrue(initialGoldDeck.containsAll(b.getGoldDeck()) && b.getGoldDeck().containsAll(initialGoldDeck));
        assertTrue(initialResourceDeck.containsAll(b.getResourceDeck()) && b.getResourceDeck().containsAll(initialResourceDeck));

         */
    }






    @Test
    void testShuffleCards2() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
            @Override
            public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                return null;
            }

            @Override
            public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

            }

            @Override
            public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

            }

            @Override
            public void rebind(String name, Remote obj) throws RemoteException, AccessException {

            }

            @Override
            public String[] list() throws RemoteException, AccessException {
                return new String[0];
            }
        });
        GameState g = new GameState(s);
        Populate.populate(g);
        Board b = g.getMainBoard();

        // Save initial length
        int goldSize = b.getGoldDeckSize();
        int resourceSize = b.getGoldDeckSize();

        // Shuffle decks
        b.shuffleCards();

        assertEquals(goldSize, b.getGoldDeckSize());
        assertEquals(resourceSize, b.getResourceDeckSize());
    }
}