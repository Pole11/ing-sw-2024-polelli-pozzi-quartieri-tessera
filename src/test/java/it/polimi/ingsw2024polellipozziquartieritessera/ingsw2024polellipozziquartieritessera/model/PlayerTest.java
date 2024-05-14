package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges.StructureChallenge;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

public class PlayerTest {
    @Test
    void playerTestConstructor() {
        for (Color c : Color.values()){
            // TODO: test with different nickname lengths
            assertAll(() -> new Player("Nickname"));
        }
    }

    @Test
    void getCardPointsTest1() throws IOException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardIsNotInHandException, CardAlreadPlacedException, EmptyDeckException {
        int resourceCardId1 = 1;
        int resourceCardId2 = 13;
        int resourceCardId3 = 11;
        int resourceCardId4 = 18;
        int goldCardId1 = 56;


        // create game state
        GameState gs = Populate.createCardsMap();
        gs.setPlayer(0, new Player("Nickname"));
        Player player = gs.getPlayer(0);
        Controller c = new Controller(gs);
        c.startGame();
        c.chooseInitialStarterSide(0, Side.BACK);


        player.addToHandCardsMap(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        player.addToHandCardsMap(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, resourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(0, player.getPoints());

        player.addToHandCardsMap(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, resourceCardId1, CornerPos.UPLEFT, Side.BACK);
        assertEquals(0, player.getPoints());

        player.addToHandCardsMap(resourceCardId4, Side.FRONT);
        c.placeCard(0, resourceCardId4, resourceCardId3, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(1, player.getPoints());

        player.addToHandCardsMap(goldCardId1, Side.FRONT);
        c.placeCard(0, goldCardId1, resourceCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(4 + 1, player.getPoints());

    }

    // check points and elements
    @Test
    void getCardPointsTest2() throws IOException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadPlacedException, CardIsNotInHandException, EmptyDeckException {
        // create cards map

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
        int resourceCardId9 = 12;
        int goldCardId5 = 58;

        // create game state
        GameState gs = Populate.createCardsMap();
        gs.setPlayer(0, new Player("jhonny"));
        Player player = gs.getPlayer(0);
        Controller c = new Controller(gs);
        gs.getMainBoard().shuffleCards();
        gs.getMainBoard().initSharedGoldCards();
        gs.getMainBoard().initSharedResourceCards();
        player.setStarterCard((StarterCard)  gs.getCard(starterCardId));
        player.initializeBoard();
        c.chooseInitialStarterSide(0, Side.FRONT);

        assertThrows(CardNotPlacedException.class ,() ->  gs.getCard(3).calculatePoints(player));

        player.addToHandCardsMap(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId1, Side.FRONT);
        assertThrows(CardAlreadyPresentOnTheCornerException.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.DOWNRIGHT, Side.BACK));
        assertThrows(GoldCardCannotBePlacedException.class, () -> c.placeCard(0, goldCardId1, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT));
        assertThrows(GoldCardCannotBePlacedException.class, () -> c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.FRONT));

        c.placeCard(0, goldCardId1, resourceCardId1, CornerPos.DOWNRIGHT, Side.BACK);
        assertEquals(0, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(0, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId2, Side.FRONT);
        c.placeCard(0, goldCardId2, player.getStarterCard().getId(), CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(1, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, player.getStarterCard().getId(), CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(2, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, player.getStarterCard().getId(), CornerPos.UPLEFT, Side.FRONT);
        assertEquals(3, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(2, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId4, Side.FRONT);
        c.placeCard(0, resourceCardId4, goldCardId2, CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(4, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(3, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(0, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId5, Side.FRONT);
        c.placeCard(0, resourceCardId5, goldCardId1, CornerPos.UPRIGHT, Side.FRONT);
        assertEquals(4, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(4, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId6, Side.FRONT);
        c.placeCard(0, resourceCardId6, resourceCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(5, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId7, Side.FRONT);
        c.placeCard(0, resourceCardId7, goldCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(6, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(resourceCardId7, Side.FRONT);
        player.addToHandCardsMap(resourceCardId1, Side.FRONT);
        assertThrows(CardAlreadPlacedException.class, ()-> c.placeCard(0, resourceCardId7, resourceCardId4, CornerPos.UPRIGHT, Side.FRONT));
        assertThrows(CardAlreadPlacedException.class, ()-> c.placeCard(0, resourceCardId1, resourceCardId4, CornerPos.UPRIGHT, Side.FRONT));
        
        //2 corner covered
        player.addToHandCardsMap(resourceCardId8, Side.FRONT);
        c.placeCard(0, resourceCardId8, goldCardId1, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(10, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(2, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        assertThrows(WrongInstanceTypeException.class, ()-> c.placeCard(0, 84, resourceCardId6, CornerPos.DOWNLEFT, Side.FRONT));

        player.addToHandCardsMap(goldCardId3, Side.FRONT);
        c.placeCard(0, goldCardId3, resourceCardId6, CornerPos.DOWNLEFT, Side.BACK);
        assertEquals(10, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId4, Side.FRONT);
        assertThrows(PlacingOnHiddenCornerException.class, () -> c.placeCard(0, goldCardId4, goldCardId3, CornerPos.UPLEFT, Side.FRONT));

        assertThrows(CardIsNotInHandException.class ,() -> c.placeCard(0,32, starterCardId, CornerPos.DOWNRIGHT, Side.FRONT));

        player.addToHandCardsMap(32, Side.FRONT);
        assertThrows(CardAlreadyPresentOnTheCornerException.class ,() -> c.placeCard(0,32, starterCardId, CornerPos.DOWNRIGHT, Side.FRONT));

        //1 corner covered
        player.addToHandCardsMap(goldCardId4, Side.FRONT);
        c.placeCard(0, goldCardId4, goldCardId3, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(12, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));


        player.addToHandCardsMap(resourceCardId9, Side.FRONT);
        c.placeCard(0, resourceCardId9, goldCardId4, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(12, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.addToHandCardsMap(goldCardId5, Side.FRONT);
        c.placeCard(0, goldCardId5, resourceCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(15, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(1, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));


    }

    @Test
    void getStructurePointsTest() throws IOException, NotUniquePlayerColorException, NotUniquePlayerNicknameException, WrongStructureConfigurationSizeException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, WrongInstanceTypeException, CardNotPlacedException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardIsNotInHandException, CardAlreadPlacedException, EmptyDeckException {
        // create cards map

        int starterCardId = 81;
        int resourceCardId1 = 3;
        int resourceCardId2 = 4;
        int resourceCardId3 = 2;
        int objectiveCardId1 = 87;
        int objectiveCardId2 = 90;

        // create game state
        GameState gs = Populate.createCardsMap();
        gs.setPlayer(0, new Player("test"));
        Player player = gs.getPlayer(0);
        Controller c = new Controller(gs);
        gs.getMainBoard().shuffleCards();
        gs.getMainBoard().initSharedGoldCards();
        gs.getMainBoard().initSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCard(starterCardId));
        player.initializeBoard();
        c.chooseInitialStarterSide(0, Side.FRONT);


        // testing one single structure challenge situation
        ObjectiveCard objective1 = (ObjectiveCard) gs.getCard(objectiveCardId1);
        ObjectiveCard objective2 = (ObjectiveCard) gs.getCard(objectiveCardId2);
        Element[][] configuration1  = ((StructureChallenge) objective1.getChallenge()).getConfiguration();
        Element[][] configuration2  = ((StructureChallenge) objective2.getChallenge()).getConfiguration();

        player.addToHandCardsMap(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, player.getStarterCard().getId(), CornerPos.UPRIGHT, Side.BACK);
        assertEquals(0, objective1.getChallenge().getTimesWon(player, objective1));

        player.addToHandCardsMap(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, resourceCardId1, CornerPos.UPRIGHT, Side.BACK);
        assertEquals(0, objective1.getChallenge().getTimesWon(player, objective1));

        player.addToHandCardsMap(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, resourceCardId2, CornerPos.UPRIGHT, Side.BACK);
        assertEquals(1, objective1.getChallenge().getTimesWon(player, objective1));

    }

/*
    @Test
    public void getAllElementsTest() {
        Player player = new Player("test");
        assertEquals(player.getAllElements(), player.allElements);
        assertFalse(player.getAllElements()==player.allElements);
    }

    @Test
    public void getPlayerBorardTest() {
        Player player = new Player("test");
        assertEquals(player.getPlayerBoard(), player.playerBoard);
        assertFalse(player.getPlayerBoard()==player.playerBoard);
    }
*/

}

