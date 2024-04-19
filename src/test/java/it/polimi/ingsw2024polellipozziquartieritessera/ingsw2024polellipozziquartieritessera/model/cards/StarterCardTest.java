package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Main;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StarterCardTest {

    @Test
    void testGetterSetter() {
        // setup
        GameState g;
        try {
            g = Main.populate();
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
        StarterCard card = (StarterCard) g.getCornerCard(84);

        assertEquals(card.getCenterResource().getFirst(), Element.ANIMAL);
        assertEquals(card.getCenterResource().size(), 2);
    }

    @Test
    void testGetResourceType(){
        // setup
        GameState g;
        try {
            g = Main.populate();
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
        StarterCard card = (StarterCard) g.getCornerCard(84);

        assertThrows(WrongInstanceTypeException.class, card::getResourceType);
        }

    @Test
    void testGetUncoveredElements() {
        // setup
        GameState g;
        try {
            g = Main.populate();
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
        StarterCard card = (StarterCard) g.getCornerCard(84);

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
    }
}
