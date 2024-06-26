package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ElementChallengeTest {
    @Test
    void testGetElements() throws IOException {
        Server s = new Server(null, null, null);
        GameState g = new GameState(s);
        Populate.populate(g);

        GoldCard card1 = (GoldCard) g.getCard(53);

        assertEquals( 1, ((ElementChallenge) card1.getChallenge()).getElements().size());
        assertEquals( Element.INKWELL, ((ElementChallenge) card1.getChallenge()).getElements().getFirst());
    }
}
