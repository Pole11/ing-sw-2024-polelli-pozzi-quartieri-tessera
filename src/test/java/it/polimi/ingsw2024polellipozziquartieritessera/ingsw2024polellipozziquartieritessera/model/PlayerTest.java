package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
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
    void getTimesWonCoverageTest() {
        Player player = new Player("pole", Color.GREEN);
        Main main = new Main();

        try { // create cards map
            HashMap<Integer, Card> cardsMap = main.createCardsMap();

            assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));

            try { // create game state
                GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
                Controller c = new Controller(gs);
                c.startGame();
                c.chooseInitialStarterSide(0, Side.FRONT);

                try {
                    int goldCoverageCardId = 55;
                    c.placeCard(0, goldCoverageCardId, player.getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
                    assertEquals(1, player.getCardPoints((GoldCard) gs.getCardsMap().get(goldCoverageCardId)));
                } catch(WrongInstanceTypeException e) {

                }

            } catch (NotUniquePlayerException e) {

            } catch (NotUniquePlayerColorException e) {

            } catch (NotUniquePlayerNicknameException e) {

            }
        } catch (WrongStructureConfigurationSizeException e) {

        } catch (IOException e) {

        }



    }
}
