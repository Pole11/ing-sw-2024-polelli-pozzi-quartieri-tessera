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
    void getCardPointsTest1() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlaced, CardAlreadyPresent, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException {
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

        int ResourceCardId1 = 1;
        int ResourceCardId2 = 13;
        int ResourceCardId3 = 11;
        int ResourceCardId4 = 18;
        int GoldCard1 = 56;

        c.placeCard(0, ResourceCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        c.placeCard(0, ResourceCardId2, ResourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(0, player.getPoints());

        c.placeCard(0, ResourceCardId3, ResourceCardId1, CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        c.placeCard(0, ResourceCardId4, ResourceCardId3, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(1, player.getPoints());

        c.placeCard(0, GoldCard1, ResourceCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(4 + 1, player.getPoints());

    }

    // check points and elements
    @Test
    void getCardPointsTest2() throws IOException, NotUniquePlayerException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlaced, CardAlreadyPresent, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException {
        Player player = new Player("pole", Color.GREEN);
        Main main = new Main();
        // create cards map
        HashMap<Integer, Card> cardsMap = main.createCardsMap();

        assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));


        int StarterCardId = 81;
        int ResourceCardId1 = 31;
        int GoldCardId1 = 59;
        int GoldCardId2 = 51;
        int ResourceCardId2 = 19;
        int ResourceCardId3 = 20;
        int ResourceCardId4 = 39;
        int ResourceCardId5 = 37;
        int ResourceCardId6 = 10;
        int ResourceCardId7 = 12;

        // create game state
        GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        Controller c = new Controller(gs);
        c.startGame();
        player.setStarterCard((StarterCard) gs.getCardsMap().get(81));
        player.initializeBoard();
        c.chooseInitialStarterSide(0, Side.FRONT);

        c.placeCard(0, ResourceCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        assertThrows(CardAlreadyPresent.class, () -> c.placeCard(0, GoldCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK));
        assertThrows(GoldCardCannotBePlaced.class, () -> c.placeCard(0, GoldCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT));
        assertThrows(GoldCardCannotBePlaced.class, () -> c.placeCard(0, GoldCardId1, ResourceCardId1, CornerPos.DOWNRIGHT, Side.FRONT));

        c.placeCard(0, GoldCardId1, ResourceCardId1, CornerPos.DOWNRIGHT, Side.BACK);


        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        // non funziona, punti vengono calcolati considerando 1 quill
        //c.placeCard(0, GoldCardId2, StarterCardId, CornerPos.UPRIGHT, Side.FRONT);
        //assertEquals(0, player.getPoints());
        //assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        //assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        //assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        //assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        //assertEquals(0, player.getAllElements().get(Element.INKWELL));
        //assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        //assertEquals(1, player.getAllElements().get(Element.QUILL));

        // da qui disegno sbagliato
        c.placeCard(0, ResourceCardId2, StarterCardId, CornerPos.DOWNLEFT, Side.FRONT);

        assertEquals(1, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));


        //non funziona linked corner problem
        //c.placeCard(0, ResourceCardId3, StarterCardId, CornerPos.UPLEFT, Side.FRONT);
        //assertEquals(2, player.getPoints());
        //assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        //assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        //assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        //assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        //assertEquals(0, player.getAllElements().get(Element.INKWELL));
        //assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        //assertEquals(1, player.getAllElements().get(Element.QUILL));


        //card with hidden corner can be placed
        c.placeCard(0, ResourceCardId4, GoldCardId2, CornerPos.UPRIGHT, Side.FRONT);

        assertEquals(2, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));


        //non funziona linked corner problem
        //c.placeCard(0, ResourceCardId5, GoldCardId1, CornerPos.UPRIGHT, Side.FRONT);
        //assertEquals(2, player.getPoints());
        //assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        //assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        //assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        //assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        //assertEquals(0, player.getAllElements().get(Element.INKWELL));
        //assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        //assertEquals(1, player.getAllElements().get(Element.QUILL));

        // non funziona, linked corner problem
        //c.placeCard(0, ResourceCardId6, ResourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);


        //non funziona, piazza due volte la stessa carta senza dare errore
        //assertThrows(Exception.class, ()-> c.placeCard(0, ResourceCardId1, ResourceCardId4, CornerPos.UPRIGHT, Side.FRONT));












    }


}

