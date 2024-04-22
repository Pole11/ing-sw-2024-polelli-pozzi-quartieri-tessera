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
    void getCardPointsTest1() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlaced, CardAlreadyPresent, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardIsNotInHandException {
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

        int resourceCardId1 = 1;
        int resourceCardId2 = 13;
        int resourceCardId3 = 11;
        int resourceCardId4 = 18;
        int goldCardId1 = 56;

        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        c.placeCard(0, resourceCardId2, resourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(0, player.getPoints());

        c.placeCard(0, resourceCardId3, resourceCardId1, CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        c.placeCard(0, resourceCardId4, resourceCardId3, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(1, player.getPoints());

        c.placeCard(0, goldCardId1, resourceCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(4 + 1, player.getPoints());

    }

    // check points and elements
    @Test
    void getCardPointsTest2() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlaced, CardAlreadyPresent, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardIsNotInHandException {
        Player player = new Player("pole", Color.GREEN);
        Main main = new Main();
        // create cards map
        HashMap<Integer, Card> cardsMap = main.createCardsMap();

        assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));


        int starterCardId = 81;
        int resourceCardId1 = 31;
        int goldCardId1 = 59;
        int goldCardId2 = 51;
        int resourceCardId2 = 19;
        int resourceCardId3 = 20;
        int resourceCardId4 = 39;
        int resourceCardId5 = 37;
        int resourceCardId6 = 10;
        int resourceCardId7 = 12;

        // create game state
        GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        Controller c = new Controller(gs);
        c.startGame();
        player.setStarterCard((StarterCard) gs.getCardsMap().get(starterCardId));
        player.initializeBoard();
        c.chooseInitialStarterSide(0, Side.FRONT);

        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        // TODO: move them into another method
        //assertThrows(CardAlreadyPresent.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK));
        //assertThrows(GoldCardCannotBePlaced.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT));
        //assertThrows(GoldCardCannotBePlaced.class, () -> c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.FRONT));

        c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.BACK);

        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        c.placeCard(0, goldCardId2, player.getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        //assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        c.placeCard(0, resourceCardId2, player.getStarterCard().getId(), CornerPos.DOWNLEFT, Side.FRONT);
        //assertEquals(1, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        c.placeCard(0, resourceCardId3, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        //assertEquals(2, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));


        //card with hidden corner can be placed
        c.placeCard(0, resourceCardId4, goldCardId2, CornerPos.UPRIGHT, Side.FRONT);

        //assertEquals(2, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));


        //non funziona linked corner problem
        c.placeCard(0, resourceCardId5, goldCardId1, CornerPos.UPRIGHT, Side.FRONT);
        //assertEquals(2, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(4, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        c.placeCard(0, resourceCardId6, resourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        //assertEquals(???, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        //non funziona, piazza due volte la stessa carta senza dare errore
        assertThrows(CardAlreadyPresent.class, ()-> c.placeCard(0, resourceCardId1, resourceCardId4, CornerPos.UPRIGHT, Side.FRONT));

    }


}

