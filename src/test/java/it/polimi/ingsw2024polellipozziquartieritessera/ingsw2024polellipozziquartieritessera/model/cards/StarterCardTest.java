package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongInstanceTypeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StarterCardTest {
    @Test
    void testGetterSetter() {
        // setup
        try {
            Server s = new Server(null, null, null);
            GameState g = new GameState(s);
            Populate.populate(g);
            StarterCard card = (StarterCard) g.getCard(84);

            assertEquals(card.getCenterResources().getFirst(), Element.ANIMAL);
            assertEquals(card.getCenterResources().size(), 2);
            assertNull(card.getChallenge());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetResourceType() throws IOException {
        // setup
        Server s = new Server(null, null, null);
        GameState g = new GameState(s);
        Populate.populate(g);
        StarterCard card = (StarterCard) g.getCard(84);

        assertThrows(WrongInstanceTypeException.class, card::getResourceType);
    }

    @Test
    void testGetUncoveredElements() {
        // setup
        try {
            Server s = new Server(null, null, null);
            GameState g = new GameState(s);
            Populate.populate(g);

            StarterCard card = (StarterCard) g.getCard(84);

            // verify if the front elements are right
            ArrayList<Element> elements;
            elements = card.getUncoveredElements(Side.FRONT);
            assertEquals(elements.getFirst(), Element.ANIMAL);
            assertEquals(elements.toArray().length, 2);

            // verify if the back elements are right
            elements = card.getUncoveredElements(Side.BACK);
            assertEquals(elements.getFirst(), Element.PLANT);
            assertEquals(elements.toArray().length, 4);

            // try cover the corner and see if there are no uncovered elements
            for (Corner corner : card.getCorners(Side.BACK)){
                corner.setCovered(true);
            }

            assertTrue(card.getUncoveredElements(Side.BACK).isEmpty());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testCalculatePoints() throws IOException {
        Server s = new Server(null, null, null);
        GameState g = new GameState(s);
        Populate.populate(g);

        StarterCard card = (StarterCard) g.getCard(84);

        Player player = new Player("Bob", null, g);

        assertEquals(card.calculatePoints(player), 0);
    }
}
