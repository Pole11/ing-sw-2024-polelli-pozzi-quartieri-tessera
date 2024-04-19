package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Main;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceCardTest {
    @Test
    void testGetterSetter(){
        // setup
        GameState g;
        try {
            g = Main.populate();
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }
        ResourceCard card = (ResourceCard) g.getCornerCard(32);

        assertEquals(card.getResourceType(), Element.INSECT);
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
        ResourceCard card = (ResourceCard) g.getCornerCard(32);

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
    }
}
