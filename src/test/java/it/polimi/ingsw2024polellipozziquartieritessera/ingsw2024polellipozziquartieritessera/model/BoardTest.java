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

public class BoardTest {
    @Test
    void testGetFromResourceDeck() {
        // setup
        GameState g;
        try {
            g = Main.populate();
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
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
    }

    @Test
    void testGetFromGoldDeck() {
        // setup
        GameState g;
        try {
            g = Main.populate();
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
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
    }

    @Test
    void testFillSharedCardsGap() {
        // Setup
        GameState g;
        try {
            g = Main.populate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    }

    @Test
    void testShuffleCards(){
        // Setup
        GameState g;
        try {
            g = Main.populate();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
    }
}
