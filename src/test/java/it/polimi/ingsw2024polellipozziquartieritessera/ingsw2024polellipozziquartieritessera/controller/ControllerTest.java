package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.*;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ControllerTest {
    @Test
    void placeCardTest() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, WrongPlacingPositionException, WrongInstanceTypeException, CardNotPlacedException, GoldCardCannotBePlacedException, CardAlreadyPresentOnTheCornerException, PlacingOnHiddenCornerException, CardIsNotInHandException, CardAlreadPlacedException, EmptyDeckException, CardNotOnBoardException {

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
        Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
            @Override
            public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                return null;
            }

            @Override
            public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

            }

            @Override
            public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

            }

            @Override
            public void rebind(String name, Remote obj) throws RemoteException, AccessException {

            }

            @Override
            public String[] list() throws RemoteException, AccessException {
                return new String[0];
            }
        });
        GameState gs = new GameState(s);
        Populate.populate(gs);

        Populate.populate(gs);
        gs.addPlayer("paolo", new Client(null, null, null));
        gs.addPlayer("piergiorgio", new Client(null, null, null));
        gs.addPlayer("fungiforme", new Client(null, null, null));
        gs.addPlayer("paola", new Client(null, null, null));
        Player player = gs.getPlayer(0);
        Controller c = new Controller();
        c.setGameState(gs);
        gs.getMainBoard().shuffleCards();
        gs.getMainBoard().initSharedGoldCards();
        gs.getMainBoard().initSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCard(starterCardId));
        player.initializeBoard();
        c.chooseInitialStarterSide(0, starterCardSide);

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(1, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(1, player.getAllElements().get(Element.FUNGI)); // fungi

        player.addToHandCardsMap(resourceCardId1, Side.FRONT);
        c.placeCard(0, resourceCardId1, starterCardId, resourceCard1ToTableCornerPos, resourceCard1Side);
        ArrayList<Corner> starterCardCorners = ((StarterCard) gs.getCard(starterCardId)).getCorners(starterCardSide);
        ArrayList<Corner> resourceCard1Corners = ((CornerCard) gs.getCard(resourceCardId1)).getCorners(resourceCard1Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard1ToTableCornerPos)) {
                assertEquals(true, starterCardCorners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard1Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertEquals(starterCardCorners.get(cpos.ordinal()).getLinkedCorner(),
                        resourceCard1Corners.get((cpos.ordinal() + 2) % 4));
                assertTrue(starterCardCorners.get(cpos.ordinal()).getLinkedCorner() == resourceCard1Corners.get((cpos.ordinal() + 2) % 4));
                assertEquals(resourceCard1Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner(),
                        starterCardCorners.get(cpos.ordinal()));
                assertTrue(resourceCard1Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner() == starterCardCorners.get(cpos.ordinal()));
            } else {
                assertEquals(false, starterCardCorners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard1Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertEquals(null,
                        resourceCard1Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
                assertTrue(null == resourceCard1Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
                assertEquals(null,
                        starterCardCorners.get(cpos.ordinal()).getLinkedCorner());
                assertTrue(null == starterCardCorners.get(cpos.ordinal()).getLinkedCorner());
            }
        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(1, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.addToHandCardsMap(resourceCardId2, Side.FRONT);
        c.placeCard(0, resourceCardId2, resourceCardId1, resourceCard2ToTableCornerPos, resourceCard2Side);
        resourceCard1Corners = ((CornerCard) gs.getCard(resourceCardId1)).getCorners(resourceCard1Side);
        ArrayList<Corner> resourceCard2Corners = ((CornerCard) gs.getCard(resourceCardId2)).getCorners(resourceCard2Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard2ToTableCornerPos)) {
                assertEquals(true, resourceCard1Corners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard2Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertEquals(resourceCard1Corners.get(cpos.ordinal()).getLinkedCorner(),
                        resourceCard2Corners.get((cpos.ordinal() + 2) % 4));
                assertTrue(resourceCard1Corners.get(cpos.ordinal()).getLinkedCorner() == resourceCard2Corners.get((cpos.ordinal() + 2) % 4));
                assertEquals(resourceCard2Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner(),
                        resourceCard1Corners.get(cpos.ordinal()));
                assertTrue(resourceCard2Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner() == resourceCard1Corners.get(cpos.ordinal()));
            } else {
                assertEquals(false, resourceCard1Corners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard2Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertEquals(null,
                        resourceCard2Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
                assertTrue(null == resourceCard2Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
            }

        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(3, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.addToHandCardsMap(resourceCardId3, Side.FRONT);
        c.placeCard(0, resourceCardId3, resourceCardId1, resourceCard3ToTableCornerPos, resourceCard3Side);
        resourceCard1Corners = ((CornerCard) gs.getCard(resourceCardId1)).getCorners(resourceCard1Side);
        ArrayList<Corner> resourceCard3Corners = ((CornerCard) gs.getCard(resourceCardId3)).getCorners(resourceCard3Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard3ToTableCornerPos)) {
                assertEquals(true, resourceCard1Corners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard3Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertEquals(resourceCard1Corners.get(cpos.ordinal()).getLinkedCorner(),
                        resourceCard3Corners.get((cpos.ordinal() + 2) % 4));
                assertSame(resourceCard1Corners.get(cpos.ordinal()).getLinkedCorner(), resourceCard3Corners.get((cpos.ordinal() + 2) % 4));
                assertEquals(resourceCard3Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner(),
                        resourceCard1Corners.get(cpos.ordinal()));
                assertSame(resourceCard3Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner(), resourceCard1Corners.get(cpos.ordinal()));
            } else if (cpos.equals(resourceCard2ToTableCornerPos)) {
                assertEquals(true, resourceCard1Corners.get(cpos.ordinal()).getCovered());
            } else {
                assertEquals(false, resourceCard1Corners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard3Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertNull(resourceCard3Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
                assertNull(resourceCard3Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
            }
        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(4, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.addToHandCardsMap(resourceCardId4, Side.FRONT);
        c.placeCard(0, resourceCardId4, resourceCardId3, resourceCard4ToTableCornerPos, resourceCard4Side);
        resourceCard3Corners = ((CornerCard) gs.getCard(resourceCardId3)).getCorners(resourceCard3Side);
        ArrayList<Corner> resourceCard4Corners = ((CornerCard) gs.getCard(resourceCardId4)).getCorners(resourceCard4Side);

        for (CornerPos cpos : CornerPos.values()) {
            if (cpos.equals(resourceCard4ToTableCornerPos)) {
                assertEquals(true, resourceCard3Corners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard4Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertEquals(resourceCard3Corners.get(cpos.ordinal()).getLinkedCorner(),
                        resourceCard4Corners.get((cpos.ordinal() + 2) % 4));
                assertSame(resourceCard3Corners.get(cpos.ordinal()).getLinkedCorner(), resourceCard4Corners.get((cpos.ordinal() + 2) % 4));
                assertEquals(resourceCard4Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner(),
                        resourceCard3Corners.get(cpos.ordinal()));
                assertSame(resourceCard4Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner(), resourceCard3Corners.get(cpos.ordinal()));
            } else {
                assertEquals(false, resourceCard3Corners.get(cpos.ordinal()).getCovered());
                assertEquals(false, resourceCard4Corners.get((cpos.ordinal() + 2) % 4).getCovered());
                assertNull(resourceCard4Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
                assertNull(resourceCard4Corners.get((cpos.ordinal() + 2) % 4).getLinkedCorner());
            }

        }

        assertEquals(1, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(0, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(2, player.getAllElements().get(Element.FUNGI)); // fungi

        player.addToHandCardsMap(goldCardId, Side.FRONT);
        c.placeCard(0, goldCardId, resourceCardId2, goldCardToTableCornerPos, goldCardSide);
        resourceCard2Corners = ((CornerCard)  gs.getCard(resourceCardId2)).getCorners(resourceCard2Side);
        ArrayList<Corner> goldCardCorners = ((CornerCard)  gs.getCard(goldCardId)).getCorners(goldCardSide);

        assertEquals(false, goldCardCorners.get(CornerPos.UPLEFT.ordinal()).getCovered());
        assertEquals(null,
                goldCardCorners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner());
        assertTrue(null == goldCardCorners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner());
        assertEquals(false, goldCardCorners.get(CornerPos.UPRIGHT.ordinal()).getCovered());
        assertEquals(resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()),
                goldCardCorners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner());
        assertTrue(resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()) == goldCardCorners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner());
        assertTrue(resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()) == goldCardCorners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner());
        assertEquals(resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner(),
                goldCardCorners.get(CornerPos.UPRIGHT.ordinal()));
        assertTrue(resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner() == goldCardCorners.get(CornerPos.UPRIGHT.ordinal()));
        assertEquals(false, goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()).getCovered());
        assertEquals(resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()),
                goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner());
        assertTrue(resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()) == goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner());
        assertEquals(resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner(),
                goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()));
        assertTrue(resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner() == goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()));
        assertEquals(false, goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()).getCovered());
        assertEquals(null,
                goldCardCorners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner());
        assertTrue(null == goldCardCorners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner());

        assertEquals(true, resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()).getCovered());
        assertEquals(goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()),
                resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner());
        assertTrue(goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()) == resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner());
        assertEquals(goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner(),
                resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()));
        assertTrue(goldCardCorners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner() == resourceCard2Corners.get(CornerPos.UPLEFT.ordinal()));
        assertEquals(false, resourceCard2Corners.get(CornerPos.UPRIGHT.ordinal()).getCovered());
        assertEquals(resourceCard1Corners.get(CornerPos.DOWNLEFT.ordinal()),
                resourceCard2Corners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner());
        assertTrue(resourceCard1Corners.get(CornerPos.DOWNLEFT.ordinal()) == resourceCard2Corners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner());
        assertEquals(resourceCard1Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner(),
                resourceCard2Corners.get(CornerPos.UPRIGHT.ordinal()));
        assertTrue(resourceCard1Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner() == resourceCard2Corners.get(CornerPos.UPRIGHT.ordinal()));
        assertEquals(false, resourceCard2Corners.get(CornerPos.DOWNRIGHT.ordinal()).getCovered());
        assertEquals(null,
                resourceCard2Corners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner());
        assertTrue(null == resourceCard2Corners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner());
        assertEquals(false, resourceCard2Corners.get(CornerPos.DOWNLEFT.ordinal()).getCovered());
        assertEquals(null,
                resourceCard2Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner());
        assertTrue(null == resourceCard2Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner());

        assertEquals(true, resourceCard3Corners.get(CornerPos.UPLEFT.ordinal()).getCovered());
        assertEquals(resourceCard4Corners.get(CornerPos.DOWNRIGHT.ordinal()),
                resourceCard3Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner());
        assertTrue(resourceCard4Corners.get(CornerPos.DOWNRIGHT.ordinal()) == resourceCard3Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner());
        assertEquals(resourceCard4Corners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner(),
                resourceCard3Corners.get(CornerPos.UPLEFT.ordinal()));
        assertTrue(resourceCard4Corners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner() == resourceCard3Corners.get(CornerPos.UPLEFT.ordinal()));
        assertEquals(false, resourceCard3Corners.get(CornerPos.UPRIGHT.ordinal()).getCovered());
        assertEquals(null,
                resourceCard3Corners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner());
        assertTrue(null == resourceCard3Corners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner());
        assertEquals(false, resourceCard3Corners.get(CornerPos.DOWNRIGHT.ordinal()).getCovered());
        assertEquals(resourceCard1Corners.get(CornerPos.UPLEFT.ordinal()),
                resourceCard3Corners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner());
        assertTrue(resourceCard1Corners.get(CornerPos.UPLEFT.ordinal()) == resourceCard3Corners.get(CornerPos.DOWNRIGHT.ordinal()).getLinkedCorner());
        assertEquals(resourceCard1Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner(),
                resourceCard3Corners.get(CornerPos.DOWNRIGHT.ordinal()));
        assertTrue(resourceCard1Corners.get(CornerPos.UPLEFT.ordinal()).getLinkedCorner() == resourceCard3Corners.get(CornerPos.DOWNRIGHT.ordinal()));
        assertEquals(true, resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()).getCovered());
        assertEquals(goldCardCorners.get(CornerPos.UPRIGHT.ordinal()),
                resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner());
        assertTrue(goldCardCorners.get(CornerPos.UPRIGHT.ordinal()) == resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()).getLinkedCorner());
        assertEquals(goldCardCorners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner(),
                resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()));
        assertTrue(goldCardCorners.get(CornerPos.UPRIGHT.ordinal()).getLinkedCorner() == resourceCard3Corners.get(CornerPos.DOWNLEFT.ordinal()));
    }


    @Test
    void drawCard() throws WrongStructureConfigurationSizeException, IOException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, InvalidHandException, EmptyDeckException, EmptyMainBoardException {

        int starterCardId = 81;

        // create game state
        Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
            @Override
            public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                return null;
            }

            @Override
            public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

            }

            @Override
            public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

            }

            @Override
            public void rebind(String name, Remote obj) throws RemoteException, AccessException {

            }

            @Override
            public String[] list() throws RemoteException, AccessException {
                return new String[0];
            }
        });
        GameState gs = new GameState(s);
        Populate.populate(gs);
        gs.addPlayer("paolo", new Client(null, null, null));
        gs.addPlayer("piergiorgio", new Client(null, null, null));
        gs.addPlayer("fungiforme", new Client(null, null, null));
        gs.addPlayer("paola", new Client(null, null, null));

        gs.setPlayersConnected(0, true);
        gs.setPlayersConnected(1, true);
        gs.setPlayersConnected(2, true);
        gs.setPlayersConnected(3, true);

        Player player = gs.getPlayer(0);
        Controller c = new Controller();
        c.setGameState(gs);
        gs.getMainBoard().shuffleCards();
        gs.getMainBoard().initSharedGoldCards();
        gs.getMainBoard().initSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCard(starterCardId));
        player.initializeBoard();
        //gs.setHands();
        c.chooseInitialStarterSide(0, Side.FRONT);
        gs.setCurrentPlayerIndex(0);


        CornerCard card1 = gs.getMainBoard().getSharedGoldCard(0);
        CornerCard card2 = gs.getMainBoard().getSharedGoldCard(1);
        /*Map.Entry<Integer,Side> entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());*/
        c.drawCard(DrawType.SHAREDGOLD1);
        assertEquals(1,gs.getCurrentPlayerIndex());
        gs.setCurrentPlayerIndex(0);
        assertNotEquals(gs.getMainBoard().getSharedGoldCard(0), card1);
        assertEquals(gs.getMainBoard().getSharedGoldCard(1), card2);
        assertTrue(player.handCardContains(card1.getId()));

        card1 = gs.getMainBoard().getSharedGoldCard(0);
        card2 = gs.getMainBoard().getSharedGoldCard(1);
        //entry = player.getHandCardsMap().entrySet().iterator().next();
        //player.getHandCardsMap().remove(entry.getKey());
        c.drawCard(DrawType.SHAREDGOLD2);
        assertEquals(1,gs.getCurrentPlayerIndex());
        gs.setCurrentPlayerIndex(0);
        assertEquals(gs.getMainBoard().getSharedGoldCard(0), card1);
        assertNotEquals(gs.getMainBoard().getSharedGoldCard(1), card2);
        assertTrue(player.handCardContains(card2.getId()));

        card1 = gs.getMainBoard().getSharedResourceCard(0);
        card2 = gs.getMainBoard().getSharedResourceCard(1);
        /*entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());*/
        c.drawCard(DrawType.SHAREDRESOURCE1);
        assertEquals(1,gs.getCurrentPlayerIndex());
        gs.setCurrentPlayerIndex(0);
        assertNotEquals(gs.getMainBoard().getSharedResourceCard(0), card1);
        assertEquals(gs.getMainBoard().getSharedResourceCard(1), card2);
        assertTrue(player.handCardContains(card1.getId()));

        assertThrows(InvalidHandException.class, () -> c.drawCard(DrawType.SHAREDGOLD2));
        player.removeFromHandCardsMap(card1.getId());
        card1 = gs.getMainBoard().getSharedResourceCard(0);
        card2 = gs.getMainBoard().getSharedResourceCard(1);
        /*entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());*/
        c.drawCard(DrawType.SHAREDRESOURCE2);
        assertEquals(1,gs.getCurrentPlayerIndex());
        gs.setCurrentPlayerIndex(0);
        assertEquals(gs.getMainBoard().getSharedResourceCard(0), card1);
        assertNotEquals(gs.getMainBoard().getSharedResourceCard(1), card2);
        assertTrue(player.handCardContains(card2.getId()));

        player.removeFromHandCardsMap(card2.getId());
        //card1 = gs.getMainBoard().getResourceDeck().getLast();
        int size = gs.getMainBoard().getResourceDeckSize();
        /*entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());*/
        c.drawCard(DrawType.DECKRESOURCE);
        //card1 = gs.getMainBoard().drawFromResourceDeck();
        assertEquals(1,gs.getCurrentPlayerIndex());
        gs.setCurrentPlayerIndex(0);
        //assertTrue(player.handCardContains(card1.getId()));
        assertEquals(gs.getMainBoard().getResourceDeckSize(), size -1);
        //assertNotEquals(gs.getMainBoard().getResourceDeck().getLast(), card1);


        //TODO: this test cannot be done because we dont have the card to remove
        /*
        player.removeFromHandCardsMap(player.getHan);
        //place instead of manually remove
        c.placeCard(, player);
        //card1 = gs.getMainBoard().getGoldDeck().getLast();
        size = gs.getMainBoard().getGoldDeckSize();
        entry = player.getHandCardsMap().entrySet().iterator().next();
        player.getHandCardsMap().remove(entry.getKey());
        c.drawCard(DrawType.DECKGOLD);
        card1 = gs.getMainBoard().drawFromGoldDeck();
        assertEquals(1,gs.getCurrentPlayerIndex());
        gs.setCurrentPlayerIndex(0);
        //assertTrue(player.handCardContains(card1.getId()));
        assertEquals(gs.getMainBoard().getGoldDeckSize(), size -1);
        //assertNotEquals(gs.getMainBoard().getGoldDeckSize().getLast(), card1);
        */
    }

    @Test
    void flipCardTest() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException, CardIsNotInHandException {
        Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
            @Override
            public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                return null;
            }

            @Override
            public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

            }

            @Override
            public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

            }

            @Override
            public void rebind(String name, Remote obj) throws RemoteException, AccessException {

            }

            @Override
            public String[] list() throws RemoteException, AccessException {
                return new String[0];
            }
        });
        GameState gs = new GameState(s);
        Populate.populate(gs);
        gs.addPlayer("paolo", new Client(null, null, null));
        gs.addPlayer("piergiorgio", new Client(null, null, null));
        gs.addPlayer("fungiforme", new Client(null, null, null));
        gs.addPlayer("paola", new Client(null, null, null));
        Controller c = new Controller();
        c.setGameState(gs);

        gs.getPlayer(0).addToHandCardsMap(1, Side.FRONT);
        assertEquals(Side.FRONT, gs.getPlayer(0).getHandCardSide(1));
        c.flipCard(0, 1);
        assertEquals(Side.BACK, gs.getPlayer(0).getHandCardSide(1));
        c.flipCard(0, 1);
        assertEquals(Side.FRONT, gs.getPlayer(0).getHandCardSide(1));


    }

}