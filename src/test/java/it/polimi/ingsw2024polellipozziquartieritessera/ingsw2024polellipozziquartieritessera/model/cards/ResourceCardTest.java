package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceCardTest {
    @Test
    void testGetterSetter() {
        try {
            Server server = new Server(null, null, null);
            GameState g = new GameState(server);
            Populate.populate(g);
            ResourceCard card = (ResourceCard) g.getCard(32);

            assertEquals(card.getResourceType(), Element.INSECT);
            assertNull(card.getChallenge());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testGetUncoveredElements() {
        // setup
        try {
            Server server = new Server(null, null, null);
            GameState g = new GameState(server);
            Populate.populate(g);
            ResourceCard card = (ResourceCard) g.getCard(32);

            // verify if the return elements are the right ones
            ArrayList<Element> elements;
            elements = card.getUncoveredElements(Side.FRONT);
            assertEquals(elements.getFirst(), Element.INSECT);
            assertEquals(elements.toArray().length, 2);


            // try cover the corner and see if there are no uncovered elements
            for (Corner corner : card.getCorners(Side.FRONT)){
                corner.setCovered(true);
            }

            assertTrue(card.getUncoveredElements(Side.FRONT).isEmpty());

            // try with the back
            assertEquals(card.getUncoveredElements(Side.BACK).getFirst(), Element.INSECT);
            assertEquals(card.getUncoveredElements(Side.BACK).toArray().length, 1);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
