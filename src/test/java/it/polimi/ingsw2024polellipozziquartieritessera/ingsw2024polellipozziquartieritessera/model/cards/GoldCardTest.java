package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.CardNotPlacedException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class GoldCardTest {
    // ALL METHODS TESTED

    @Test
    void testGetterSetter() throws IOException {

        Server server = new Server(null, null, null);
        GameState g = new GameState(server);
        Populate.populate(g);

        GoldCard card = (GoldCard) g.getCard(45);

        assertEquals(card.getPoints(), 2);
        assertNotNull(card.getChallenge());
    }

    @Test
    void testCalculatePoints() throws IOException, CardNotPlacedException {
        Server server = new Server(null, null, null);
        GameState g = new GameState(server);
        Populate.populate(g);
        Controller c = new Controller();
        c.setGameState(g);

        ObjectiveCard card = (ObjectiveCard) g.getCard(95);

        Player player = new Player("Bob", null, g);

        player.addToPlacedCardsMap(41, Side.BACK);
        player.addToPlacedCardsMap(42, Side.BACK);
        player.addToPlacedCardsMap(43, Side.BACK);

        assertEquals(card.calculatePoints(player), 0);

        GoldCard card2 = (GoldCard) g.getCard(53);
    }
}
