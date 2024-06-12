package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectiveCardTest {
    // ALL METHODS TESTED
    /*
    @Test
    void testGetterSetter() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = new GameState()
        Populate.populate(g);

        GoldCard card = (GoldCard) g.getCard(45);

        assertEquals(card.getPoints(), 3);
        assertNotNull(card.getChallenge());
    }

    @Test
    void testCalculatePoints() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = Populate.populate();
        Controller c = new Controller(g);

        ObjectiveCard card = (ObjectiveCard) g.getCard(95);

        Player player = new Player("Bob", null, g);

        player.addToPlacedCardsMap(41, Side.BACK);
        player.addToPlacedCardsMap(42, Side.BACK);
        player.addToPlacedCardsMap(43, Side.BACK);

        assertEquals(card.calculatePoints(player), 0);
    }

     */
}
