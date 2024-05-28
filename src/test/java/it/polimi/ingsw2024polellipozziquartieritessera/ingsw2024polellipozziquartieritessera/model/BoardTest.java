package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.EmptyDeckException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ResourceCard;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.*;

public class BoardTest {
    // ALL METHODS TESTED

    @Test
    void testGetterSetter() {
        // setup
        try {
            GameState g = Populate.populate();

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
    }

    @Test
    void testDrawSharedCards() {
        // setup
        try {
            GameState g = Populate.populate();

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
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException  e) {
            throw new RuntimeException(e);
        } catch (EmptyDeckException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetFromResourceDeck() throws EmptyDeckException {
        // setup
        try {
            GameState g = Populate.populate();
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
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testdrawFromGoldDeck() throws EmptyDeckException {
        // setup
        try {
            GameState g = Populate.populate();

            Board b = g.getMainBoard();


            // function to test
            GoldCard drawnCard = b.drawFromGoldDeck();
            // Verify not null
            assertNotNull(drawnCard);

            // Draw every card from the deck
            while (!b.isGoldDeckEmpty()) {
                //drawnCard = b.drawFromGoldDeck();
                assertNotSame(b.drawFromGoldDeck(), drawnCard);
            }

            // If deck is ended
            assertThrows(EmptyDeckException.class, b::drawFromGoldDeck);
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException  e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShuffleCards() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = Populate.populate();
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