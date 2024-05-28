package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CornerTest {
    // ALL METHODS TESTED

    @Test
    void testGetterSetter() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = Populate.populate();

        CornerCard card = (CornerCard) g.getCard(40);
        Corner corner = card.getCorners().getFirst();

        corner.setLinkedCorner(corner.getLinkedCorner());
        corner.setCovered(corner.getCovered());
        assertNotNull(corner.getHidden());

        assertNotNull(corner.getElement());
        assertEquals(corner.getCard(), card.getId());

    }
}
