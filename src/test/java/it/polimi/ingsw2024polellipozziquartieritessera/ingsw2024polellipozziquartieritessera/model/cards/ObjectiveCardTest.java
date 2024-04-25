package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Main;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectiveCardTest {
    // ALL METHODS TESTED
    @Test
    void testGetterSetter(){
        // setup
        try {
            Main.populate();
            GameState g = Main.gameState;

            ObjectiveCard card = g.getObjectiveCard(88);

            assertNotNull(card.getChallenge());
            assertTrue(card.getPoints()>0);
        } catch (WrongStructureConfigurationSizeException | IOException | NotUniquePlayerNicknameException |
                 NotUniquePlayerColorException | NotUniquePlayerException e) {
            throw new RuntimeException(e);
        }

    }
}
