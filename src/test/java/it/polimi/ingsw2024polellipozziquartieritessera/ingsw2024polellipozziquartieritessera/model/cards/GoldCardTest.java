package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GoldCardTest {
    // ALL METHODS TESTED
    @Test
    void testGetterSetter() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = Populate.populate();

        GoldCard card = (GoldCard) g.getCard(45);

        assertNotNull(card.getResourceNeeded());
        assertNotNull(card.getResourceType());
        card.getChallenge();
        card.getPoints();
    }

    @Test
    void testGetUncoveredElements() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        // setup
        GameState g = Populate.populate();

        GoldCard card = (GoldCard) g.getCard(58);

        // verify if the return elements are the right ones
        ArrayList<Element> elements;
        elements = card.getUncoveredElements(Side.FRONT);
        assertEquals(elements.getFirst(), Element.MANUSCRIPT);
        assertEquals(elements.toArray().length, 1);


        // try cover the corner and see if there are no uncovered elements
        for (Corner corner : card.getCorners(Side.FRONT)){
            corner.setCovered(true);
        }

        assertTrue(card.getUncoveredElements(Side.FRONT).isEmpty());

        // try with the back
        assertEquals(card.getUncoveredElements(Side.BACK).getFirst(), Element.PLANT);
        assertEquals(card.getUncoveredElements(Side.BACK).toArray().length, 1);


    }
}
