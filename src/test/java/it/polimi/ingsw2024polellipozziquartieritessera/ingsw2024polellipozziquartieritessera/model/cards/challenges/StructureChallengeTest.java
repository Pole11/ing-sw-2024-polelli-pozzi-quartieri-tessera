package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class StructureChallengeTest {
    @Test
    void getterSetter() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        Server s = new Server(null, null, null);
        GameState g = new GameState(s);
        Populate.populate(g);
        ObjectiveCard card1 = (ObjectiveCard) g.getCard(94);
        StructureChallenge c = (StructureChallenge) card1.getChallenge();

        assertNotNull(c.getConfiguration());
    }

    @Test
    void testTimesWon() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        Server s = new Server(null, null, null);
        GameState g = new GameState(s);
        Populate.populate(g);

        GoldCard card1 = (GoldCard) g.getCard(54);
        ObjectiveCard card2 = (ObjectiveCard) g.getCard(94);
        StructureChallenge c = (StructureChallenge) card2.getChallenge();

        Player player = new Player("Bob", null, g);

        assertThrows(RuntimeException.class, () -> c.getTimesWon(player, card1));
    }

    @Test
    public void testWrongDimension(){
        Element[][] configuration = {{},{}};

        assertThrows(RuntimeException.class, () -> {
            new StructureChallenge(configuration);
        });    }
}
