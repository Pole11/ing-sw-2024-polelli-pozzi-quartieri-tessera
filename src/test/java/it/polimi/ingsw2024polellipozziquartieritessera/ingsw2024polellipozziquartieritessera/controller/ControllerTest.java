package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    @Test
    void placeCard() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, NotUniquePlayerException, IOException, WrongPlacingPositionException, WrongInstanceTypeException, CardNotPlacedException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, PlacingOnHiddenCornerException, CardIsNotInHandException, CardAlreadPlacedException {
        Player player = new Player("pole");
        Main main = new Main();
        // create cards map
        HashMap<Integer, Card> cardsMap = main.createCardsMap();

        assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));

        int starterCardId = 85;
        Side starterCardSide = Side.BACK;
        int resourceCardId1 = 1;
        Side resourceCard1Side = Side.BACK;
        CornerPos resourceCard1ToTableCornerPos = CornerPos.UPLEFT;
        int resourceCardId2 = 13;
        Side resourceCard2Side = Side.FRONT;
        CornerPos resourceCard2ToTableCornerPos = CornerPos.DOWNLEFT;
        int resourceCardId3 = 11;
        Side resourceCard3Side = Side.BACK;
        CornerPos resourceCard3ToTableCornerPos = CornerPos.UPLEFT;
        int resourceCardId4 = 18;
        Side resourceCard4Side = Side.FRONT;
        CornerPos resourceCard4ToTableCornerPos = CornerPos.UPLEFT;
        int goldCardId = 56;
        Side goldCardSide = Side.FRONT;
        CornerPos goldCardToTableCornerPos = CornerPos.UPLEFT;


        // create game state
        GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        Controller c = new Controller(gs);
        gs.getMainBoard().shuffleCards();
        gs.setSharedGoldCards();
        gs.setSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCardsMap().get(starterCardId));
        player.initializeBoard();
        gs.chooseStarterSidePhase();
        c.chooseInitialStarterSide(0, starterCardSide);

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(1, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi

        player.getHandCardsMap().put(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, starterCardId, resourceCard1ToTableCornerPos, resourceCard1Side);
        Corner[] starterCardCorners = ((CornerCard) gs.getCardsMap().get(starterCardId)).getCorners(starterCardSide);
        Corner[] resourceCard1Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId1)).getCorners(resourceCard1Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard1ToTableCornerPos)) {
                assertEquals(true, starterCardCorners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(starterCardCorners[cpos.getCornerPosValue()].getLinkedCorner(),
                        resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertTrue(starterCardCorners[cpos.getCornerPosValue()].getLinkedCorner() == resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertEquals(resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner(),
                        starterCardCorners[cpos.getCornerPosValue()]);
                assertTrue(resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner() == starterCardCorners[cpos.getCornerPosValue()]);
            } else {
                assertEquals(false, starterCardCorners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(null,
                        resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
                assertTrue(null == resourceCard1Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
                assertEquals(null,
                        starterCardCorners[cpos.getCornerPosValue()].getLinkedCorner());
                assertTrue(null == starterCardCorners[cpos.getCornerPosValue()].getLinkedCorner());
            }
        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.getHandCardsMap().put(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, resourceCardId1, resourceCard2ToTableCornerPos, resourceCard2Side);
        resourceCard1Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId1)).getCorners(resourceCard1Side);
        Corner[] resourceCard2Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId2)).getCorners(resourceCard2Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard2ToTableCornerPos)) {
                assertEquals(true, resourceCard1Corners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(resourceCard1Corners[cpos.getCornerPosValue()].getLinkedCorner(),
                        resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertTrue(resourceCard1Corners[cpos.getCornerPosValue()].getLinkedCorner() == resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertEquals(resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner(),
                        resourceCard1Corners[cpos.getCornerPosValue()]);
                assertTrue(resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner() == resourceCard1Corners[cpos.getCornerPosValue()]);
            } else {
                assertEquals(false, resourceCard1Corners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(null,
                        resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
                assertTrue(null == resourceCard2Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
            }

        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.getHandCardsMap().put(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, resourceCardId1, resourceCard3ToTableCornerPos, resourceCard3Side);
        resourceCard1Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId1)).getCorners(resourceCard1Side);
        Corner[] resourceCard3Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId3)).getCorners(resourceCard3Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard3ToTableCornerPos)) {
                assertEquals(true, resourceCard1Corners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(resourceCard1Corners[cpos.getCornerPosValue()].getLinkedCorner(),
                        resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertTrue(resourceCard1Corners[cpos.getCornerPosValue()].getLinkedCorner() == resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertEquals(resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner(),
                        resourceCard1Corners[cpos.getCornerPosValue()]);
                assertTrue(resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner() == resourceCard1Corners[cpos.getCornerPosValue()]);
            } else if (cpos.equals(resourceCard2ToTableCornerPos)) {
                assertEquals(true, resourceCard1Corners[cpos.getCornerPosValue()].getCovered());
            }else {
                assertEquals(false, resourceCard1Corners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(null,
                        resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
                assertTrue(null == resourceCard3Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
            }

        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(4, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.getHandCardsMap().put(resourceCardId4, Side.FRONT);
        c.placeCard(0, resourceCardId4, resourceCardId3, resourceCard4ToTableCornerPos, resourceCard4Side);
        resourceCard3Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId3)).getCorners(resourceCard3Side);
        Corner[] resourceCard4Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId4)).getCorners(resourceCard4Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard4ToTableCornerPos)) {
                assertEquals(true, resourceCard3Corners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(resourceCard3Corners[cpos.getCornerPosValue()].getLinkedCorner(),
                        resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertTrue(resourceCard3Corners[cpos.getCornerPosValue()].getLinkedCorner() == resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4]);
                assertEquals(resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner(),
                        resourceCard3Corners[cpos.getCornerPosValue()]);
                assertTrue(resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner() == resourceCard3Corners[cpos.getCornerPosValue()]);
            } else {
                assertEquals(false, resourceCard3Corners[cpos.getCornerPosValue()].getCovered());
                assertEquals(false, resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4].getCovered());
                assertEquals(null,
                        resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
                assertTrue(null == resourceCard4Corners[(cpos.getCornerPosValue() + 2) % 4].getLinkedCorner());
            }

        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.getHandCardsMap().put(goldCardId, Side.FRONT);
        c.placeCard(0, goldCardId, resourceCardId2, goldCardToTableCornerPos, goldCardSide);
        resourceCard2Corners = ((CornerCard) gs.getCardsMap().get(resourceCardId2)).getCorners(resourceCard2Side);
        Corner[] goldCardCorners = ((CornerCard) gs.getCardsMap().get(goldCardId)).getCorners(goldCardSide);

        assertEquals(false, goldCardCorners[CornerPos.UPLEFT.getCornerPosValue()].getCovered());
        assertEquals(null,
                goldCardCorners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner());
        assertTrue(null == goldCardCorners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner());
        assertEquals(false, goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()].getCovered());
        assertEquals(resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()],
                goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner());
        assertTrue(resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()] == goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner());
        assertTrue(resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()] == goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner());
        assertEquals(resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner(),
                goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()]);
        assertTrue(resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner() == goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()]);
        assertEquals(false, goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()].getCovered());
        assertEquals(resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()],
                goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner());
        assertTrue(resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()] == goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner());
        assertEquals(resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner(),
                goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()]);
        assertTrue(resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner() == goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()]);
        assertEquals(false, goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()].getCovered());
        assertEquals(null,
                goldCardCorners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner());
        assertTrue(null == goldCardCorners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner());

        assertEquals(true, resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()].getCovered());
        assertEquals(goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()],
                resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner());
        assertTrue(goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()] == resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner());
        assertEquals(goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner(),
                resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()]);
        assertTrue(goldCardCorners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner() == resourceCard2Corners[CornerPos.UPLEFT.getCornerPosValue()]);
        assertEquals(false, resourceCard2Corners[CornerPos.UPRIGHT.getCornerPosValue()].getCovered());
        assertEquals(resourceCard1Corners[CornerPos.DOWNLEFT.getCornerPosValue()],
                resourceCard2Corners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner());
        assertTrue(resourceCard1Corners[CornerPos.DOWNLEFT.getCornerPosValue()] == resourceCard2Corners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner());
        assertEquals(resourceCard1Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner(),
                resourceCard2Corners[CornerPos.UPRIGHT.getCornerPosValue()]);
        assertTrue(resourceCard1Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner() == resourceCard2Corners[CornerPos.UPRIGHT.getCornerPosValue()]);
        assertEquals(false, resourceCard2Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getCovered());
        assertEquals(null,
                resourceCard2Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner());
        assertTrue(null == resourceCard2Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner());
        assertEquals(false, resourceCard2Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getCovered());
        assertEquals(null,
                resourceCard2Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner());
        assertTrue(null == resourceCard2Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner());

        assertEquals(true, resourceCard3Corners[CornerPos.UPLEFT.getCornerPosValue()].getCovered());
        assertEquals(resourceCard4Corners[CornerPos.DOWNRIGHT.getCornerPosValue()],
                resourceCard3Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner());
        assertTrue(resourceCard4Corners[CornerPos.DOWNRIGHT.getCornerPosValue()] == resourceCard3Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner());
        assertEquals(resourceCard4Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner(),
                resourceCard3Corners[CornerPos.UPLEFT.getCornerPosValue()]);
        assertTrue(resourceCard4Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner() == resourceCard3Corners[CornerPos.UPLEFT.getCornerPosValue()]);
        assertEquals(false, resourceCard3Corners[CornerPos.UPRIGHT.getCornerPosValue()].getCovered());
        assertEquals(null,
                resourceCard3Corners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner());
        assertTrue(null == resourceCard3Corners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner());
        assertEquals(false, resourceCard3Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getCovered());
        assertEquals(resourceCard1Corners[CornerPos.UPLEFT.getCornerPosValue()],
                resourceCard3Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner());
        assertTrue(resourceCard1Corners[CornerPos.UPLEFT.getCornerPosValue()] == resourceCard3Corners[CornerPos.DOWNRIGHT.getCornerPosValue()].getLinkedCorner());
        assertEquals(resourceCard1Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner(),
                resourceCard3Corners[CornerPos.DOWNRIGHT.getCornerPosValue()]);
        assertTrue(resourceCard1Corners[CornerPos.UPLEFT.getCornerPosValue()].getLinkedCorner() == resourceCard3Corners[CornerPos.DOWNRIGHT.getCornerPosValue()]);
        assertEquals(true, resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getCovered());
        assertEquals(goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()],
                resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner());
        assertTrue(goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()] == resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()].getLinkedCorner());
        assertEquals(goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner(),
                resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()]);
        assertTrue(goldCardCorners[CornerPos.UPRIGHT.getCornerPosValue()].getLinkedCorner() == resourceCard3Corners[CornerPos.DOWNLEFT.getCornerPosValue()]);
    }


    @Test
    void drawCard() throws WrongStructureConfigurationSizeException, IOException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException, InvalidHandException {
        Player player = new Player("pole");
        Main main = new Main();
        // create cards map
        HashMap<Integer, Card> cardsMap = main.createCardsMap();

        assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));

        int starterCardId = 81;

        // create game state
        GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        Controller c = new Controller(gs);
        gs.getMainBoard().shuffleCards();
        gs.setSharedGoldCards();
        gs.setSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCardsMap().get(starterCardId));
        player.initializeBoard();
        gs.chooseStarterSidePhase();
        gs.setHands();
        c.chooseInitialStarterSide(0, Side.FRONT);
        gs.setCurrentPlayerIndex(0);



        CornerCard card1 = gs.getMainBoard().getSharedGoldCards()[0];
        CornerCard card2 = gs.getMainBoard().getSharedGoldCards()[1];
        Map.Entry<Integer,Side> entry = player.getHandCardsMap().entrySet().iterator().next();
        c.drawCard(DrawType.SHAREDGOLD1);
        assertNotEquals(gs.getMainBoard().getSharedGoldCards()[0], card1);
        assertEquals(gs.getMainBoard().getSharedGoldCards()[1], card2);
        assertTrue(player.getHandCardsMap().containsKey(card1.getId()));

        card1 = gs.getMainBoard().getSharedGoldCards()[0];
        card2 = gs.getMainBoard().getSharedGoldCards()[1];
        entry = player.getHandCardsMap().entrySet().iterator().next();

        assertThrows(InvalidHandException.class, () -> c.drawCard(DrawType.SHAREDGOLD2));
        player.getHandCardsMap().remove(entry.getKey());
        c.drawCard(DrawType.SHAREDGOLD2);


        assertEquals(gs.getMainBoard().getSharedGoldCards()[0], card1);
        assertNotEquals(gs.getMainBoard().getSharedGoldCards()[1], card2);
        assertTrue(player.getHandCardsMap().containsKey(card2.getId()));

        card1 = gs.getMainBoard().getSharedResourceCards()[0];
        card2 = gs.getMainBoard().getSharedResourceCards()[1];
        entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());
        c.drawCard(DrawType.SHAREDRESOURCE1);
        assertNotEquals(gs.getMainBoard().getSharedResourceCards()[0], card1);
        assertEquals(gs.getMainBoard().getSharedResourceCards()[1], card2);
        assertTrue(player.getHandCardsMap().containsKey(card1.getId()));

        card1 = gs.getMainBoard().getSharedResourceCards()[0];
        card2 = gs.getMainBoard().getSharedResourceCards()[1];
        entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());
        c.drawCard(DrawType.SHAREDRESOURCE2);
        assertEquals(gs.getMainBoard().getSharedResourceCards()[0], card1);
        assertNotEquals(gs.getMainBoard().getSharedResourceCards()[1], card2);
        assertTrue(player.getHandCardsMap().containsKey(card2.getId()));

        card1 = gs.getMainBoard().getResourceDeck().getLast();
        int size = gs.getMainBoard().getResourceDeck().size();
        entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());
        c.drawCard(DrawType.DECKRESOURCE);
        assertTrue(player.getHandCardsMap().containsKey(card1.getId()));
        assertEquals(gs.getMainBoard().getResourceDeck().size(), size -1);
        assertNotEquals(gs.getMainBoard().getResourceDeck().getLast(), card1);

        card1 = gs.getMainBoard().getGoldDeck().getLast();
        size = gs.getMainBoard().getGoldDeck().size();
        entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());
        c.drawCard(DrawType.DECKGOLD);
        assertTrue(player.getHandCardsMap().containsKey(card1.getId()));
        assertEquals(gs.getMainBoard().getGoldDeck().size(), size -1);
        assertNotEquals(gs.getMainBoard().getGoldDeck().getLast(), card1);
    }

    void flipCard(){

    }

}
