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
    void getCardPoints() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlaced, CardAlreadyPresent, CardNotPlacedException, WrongPlacingPositionException, WrongInstanceTypeException {
        Player player = new Player("pole", Color.GREEN);
        Main main = new Main();
        try { // create cards map
            HashMap<Integer, Card> cardsMap = main.createCardsMap();

            assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));

            try { // create game state
                GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
                Controller c = new Controller(gs);
                gs.getMainBoard().shuffleCards();
                gs.setSharedGoldCards();
                gs.setSharedResourceCards();

                player.setStarterCard( (StarterCard) gs.getCardsMap().get(83));
                player.initializeBoard();

                gs.chooseStarterSidePhase();

                c.chooseInitialStarterSide(0, Side.BACK);


                int ResourceCardId1 = 40;
                int ResourceCardId2 = 32;
                int GoldCard1 = 79;

                c.placeCard(0, ResourceCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
                assertEquals(1, player.getCardPoints((ResourceCard) gs.getCardsMap().get(ResourceCardId1)));

                c.placeCard(0, ResourceCardId2, ResourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
                assertEquals(0, player.getCardPoints((ResourceCard) gs.getCardsMap().get(ResourceCardId2)));

                c.placeCard(0, GoldCard1, ResourceCardId2, CornerPos.DOWNLEFT, Side.FRONT);
                System.out.println(player.getCardPoints((GoldCard) gs.getCardsMap().get(GoldCard1)) );
                assertEquals(1, player.getCardPoints((GoldCard) gs.getCardsMap().get(GoldCard1)));



            } catch (NotUniquePlayerException e) {
                throw new NotUniquePlayerException("Testing");
            } catch (NotUniquePlayerColorException e) {
                throw new NotUniquePlayerColorException("Testing");
            } catch (NotUniquePlayerNicknameException e) {
                throw new NotUniquePlayerNicknameException("Testing");
            }
        } catch (WrongStructureConfigurationSizeException e) {
            throw new WrongStructureConfigurationSizeException("Testing");
        } catch (IOException e) {
            throw new IOException();
        }
    }
}

