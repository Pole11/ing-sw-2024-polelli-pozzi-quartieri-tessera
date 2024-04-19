package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

public class PlayerTest {
    @Test
    void playerTestConstructor() {
        for (Color c : Color.values()){
            // TODO: test with different nickname lengths
            assertAll(() -> new Player("Nickname", c));
        }
    }

    @Test
    void getCardPointsTest() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlaced, CardAlreadyPresent, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException {
        Player player = new Player("pole", Color.GREEN);
        Main main = new Main();
        // create cards map
        HashMap<Integer, Card> cardsMap = main.createCardsMap();

        assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));

        // create game state
        GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        Controller c = new Controller(gs);
        c.startGame();
        c.chooseInitialStarterSide(0, Side.BACK);

        int ResourceCardId1 = 40;
        int ResourceCardId2 = 32;
        int ResourceCardId3 = 53;
        int ResourceCardId4 = 18;
        int GoldCard1 = 75;

        c.placeCard(0, ResourceCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        c.placeCard(0, ResourceCardId2, ResourceCardId1, CornerPos.DOWNLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        c.placeCard(0, ResourceCardId3, ResourceCardId1, CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        c.placeCard(0, ResourceCardId4, ResourceCardId3, CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(0, player.getPoints());

        c.placeCard(0, GoldCard1, ResourceCardId2, CornerPos.UPLEFT, Side.BACK);
        assertEquals(4, player.getPoints());

    }
}

