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
    void getCardPointsTest1() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardIsNotInHandException, CardAlreadPlacedException {
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

        player.getHandCardsMap().put(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        player.getHandCardsMap().put(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, resourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(0, player.getPoints());

        player.getHandCardsMap().put(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, resourceCardId1, CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        player.getHandCardsMap().put(resourceCardId4, Side.FRONT);
        c.placeCard(0, resourceCardId4, resourceCardId3, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(1, player.getPoints());

        player.getHandCardsMap().put(goldCardId1, Side.FRONT);
        c.placeCard(0, goldCardId1, resourceCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(4 + 1, player.getPoints());

    }

    // check points and elements
    @Test
    void getCardPointsTest2() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadPlacedException, CardIsNotInHandException {
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
        int resourceCardId7 = 71;
        int resourceCardId8 = 75;
        int goldCardId3 = 53;
        int goldCardId4 = 54;

        // create game state
        GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        Controller c = new Controller(gs);
        gs.getMainBoard().shuffleCards();
        gs.setSharedGoldCards();
        gs.setSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCardsMap().get(starterCardId));
        player.initializeBoard();
        gs.chooseStarterSidePhase();
        c.chooseInitialStarterSide(0, Side.FRONT);


        player.getHandCardsMap().put(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));


        assertThrows(CardAlreadyPresentOnTheCornerException.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK));
        assertThrows(GoldCardCannotBePlacedException.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT));
        assertThrows(GoldCardCannotBePlacedException.class, () -> c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.FRONT));

        player.getHandCardsMap().put(goldCardId1, Side.FRONT);
        c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(goldCardId2, Side.FRONT);
        c.placeCard(0, goldCardId2, player.getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(1, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, player.getStarterCard().getId(), CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(2, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        assertEquals(3, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(resourceCardId4, Side.FRONT);
        c.placeCard(0, resourceCardId4, goldCardId2, CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(4, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(resourceCardId5, Side.FRONT);
        c.placeCard(0, resourceCardId5, goldCardId1, CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(4, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(4, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(resourceCardId6, Side.FRONT);
        c.placeCard(0, resourceCardId6, resourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(5, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(resourceCardId7, Side.FRONT);
        c.placeCard(0, resourceCardId7, goldCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(6, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        assertThrows(CardAlreadPlacedException.class, ()-> c.placeCard(0, resourceCardId7, resourceCardId4, CornerPos.UPRIGHT, Side.FRONT));
        assertThrows(CardAlreadPlacedException.class, ()-> c.placeCard(0, resourceCardId1, resourceCardId4, CornerPos.UPRIGHT, Side.FRONT));
        
        //2 corner covered
        player.getHandCardsMap().put(resourceCardId8, Side.FRONT);
        c.placeCard(0, resourceCardId8, goldCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(10, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        assertThrows(WrongInstanceTypeException.class, ()->c.placeCard(0, 84, resourceCardId6, CornerPos.DOWNLEFT, Side.FRONT));

        player.getHandCardsMap().put(goldCardId3, Side.FRONT);
        c.placeCard(0, goldCardId3, resourceCardId6, CornerPos.DOWNLEFT, Side.BACK);
        assertEquals(10, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        assertThrows(PlacingOnHiddenCornerException.class, () -> c.placeCard(0, goldCardId4, goldCardId3, CornerPos.UPLEFT, Side.FRONT));

        //1 corner covered
        player.getHandCardsMap().put(goldCardId4, Side.FRONT);
        c.placeCard(0, goldCardId4, goldCardId3, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(12, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

    }


}

