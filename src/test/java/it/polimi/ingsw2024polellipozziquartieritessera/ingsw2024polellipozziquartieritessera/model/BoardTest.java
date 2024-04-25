package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Main;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ResourceCard;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BoardTest {
    // ALL METHODS TESTED

    @Test
    void testGetterSetter(){
        // setup
        try {
            Main.populate();
            GameState g = Main.gameState;

            Board b = g.getMainBoard();

            b.setSharedObjectiveCards(b.getSharedObjectiveCards());
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testDrawSharedCards(){
        // setup
        try {
            Main.populate();
            GameState g = Main.gameState;

            Board b = g.getMainBoard();
            b.fillSharedCardsGap();

            // get initial state
            ResourceCard[] initialResourceCards = {b.getSharedResourceCards()[0], b.getSharedResourceCards()[1]};
            GoldCard[] initialGoldCards = {b.getSharedGoldCards()[0], b.getSharedGoldCards()[1]};

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

            // verify drawn cards removed from the shared
            assertNull(b.getSharedResourceCards()[0]);
            assertNull(b.getSharedResourceCards()[1]);
            assertNull(b.getSharedGoldCards()[0]);
            assertNull(b.getSharedGoldCards()[1]);

            // verify drawn cards in initial shared
            assertArrayEquals(new ResourceCard[]{drawnResourceCard1, drawnResourceCard2}, initialResourceCards);
            assertArrayEquals(new GoldCard[]{drawnGoldCard1, drawnGoldCard2}, initialGoldCards);

            // test if launch exception
            assertThrows(IllegalArgumentException.class, () -> b.drawSharedGoldCard(4));
            assertThrows(IllegalArgumentException.class, () -> b.drawSharedResourceCard(4));
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void testGetFromResourceDeck() {
        // setup
        try {
            Main.populate();
            GameState g = Main.gameState;

            Board b = g.getMainBoard();


            // function to test
            ResourceCard drawnCard = b.getFromResourceDeck();
            // Verify not null
            assertNotNull(drawnCard);
            // Verify card has been removed
            assertFalse(b.getResourceDeck().contains(drawnCard));

            // Draw every card from the deck
            while (!b.getResourceDeck().isEmpty()){
                drawnCard = b.getFromResourceDeck();
            }

            // If deck is ended
            assertNull(drawnCard = b.getFromResourceDeck());
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetFromGoldDeck() {
        // setup
        try {
            Main.populate();
            GameState g = Main.gameState;

            Board b = g.getMainBoard();


            // function to test
            GoldCard drawnCard = b.getFromGoldDeck();
            // Verify not null
            assertNotNull(drawnCard);
            // Verify card has been removed
            assertFalse(b.getGoldDeck().contains(drawnCard));

            // Draw every card from the deck
            while (!b.getGoldDeck().isEmpty()){
                drawnCard = b.getFromGoldDeck();
            }

            // If deck is ended
            assertNull(drawnCard = b.getFromGoldDeck());
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testFillSharedCardsGap() {
        // Setup
        try {
            Main.populate();
            GameState g = Main.gameState;

            Board b = g.getMainBoard();

            // Function to test
            b.fillSharedCardsGap();

            // Verify that sharedResourceCards have no gaps
            for (ResourceCard card : b.getSharedResourceCards()) {
                assertNotNull(card);
            }

            // Verify that sharedGoldCards have no gaps
            for (GoldCard card : b.getSharedGoldCards()) {
                assertNotNull(card);
            }

            // remove the cards from each deck
            b.setSharedResourceCards(new ResourceCard[2]);
            b.setSharedGoldCards(new GoldCard[2]);
            // verify that there are no gaps and all the cards has been replaced
            b.fillSharedCardsGap();
            for (ResourceCard card : b.getSharedResourceCards()) {
                assertNotNull(card);
            }
            for (GoldCard card : b.getSharedGoldCards()) {
                assertNotNull(card);
            }
            assertEquals(b.getSharedGoldCards().length, 2);
            assertEquals(b.getSharedResourceCards().length, 2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testShuffleCards(){
        // Setup
        try {
            Main.populate();
            GameState g = Main.gameState;

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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
