package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Main;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.Card;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.StarterCard;
import org.junit.jupiter.api.Test;
public class GameStateTest {

    @Test
    void gameStateTestConstructor() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException {
        // same colors
        HashMap hmap1 = new HashMap();

        ArrayList<Player> players1 = new ArrayList<>();
        players1.add(new Player("nick1"));
        players1.add(new Player("nick2"));

        GameState gs1 = new GameState(hmap1, players1);

        gs1.getPlayers().get(0).setColor(Color.YELLOW);
        gs1.getPlayers().get(1).setColor(Color.YELLOW);
        assertThrowsExactly(NotUniquePlayerColorException.class, () -> gs1.ColorsAreValid() );


        // same nickname
        HashMap hmap2 = new HashMap();

        ArrayList players2 = new ArrayList<>();
        players2.add(new Player("nick1"));
        players2.add(new Player("nick1"));

        assertThrowsExactly(NotUniquePlayerNicknameException.class, () -> new GameState(hmap2, players2));

        // same colors and nicknames
        HashMap hmap3 = new HashMap();
        ArrayList<Player> players3 = new ArrayList<>();
        GameState gs3 = new GameState(hmap3, players3);
        players3.add(new Player("nick1"));
        players3.add(new Player("nick1"));
        gs3.getPlayers().get(0).setColor(Color.YELLOW);
        gs3.getPlayers().get(1).setColor(Color.YELLOW);
        assertThrowsExactly(NotUniquePlayerException.class, () -> gs3.NicknameAndColorsAreValid());
    }

    @Test
    void calculateFinalPointsTest() throws CardNotPlacedException, CardIsNotInHandException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadyPresentOnTheCornerException, GoldCardCannotBePlacedException, CardAlreadPlacedException, WrongInstanceTypeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException, WrongStructureConfigurationSizeException, IOException {

//-----------------------RECREATE SITUATION IN getCardPointsTest2-------------------------

        Player player = new Player("pole");
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
        int resourceCardId9 = 12;
        int goldCardId5 = 58;

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

        player.getHandCardsMap().put(goldCardId1, Side.FRONT);
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

        player.getHandCardsMap().put(resourceCardId7, Side.FRONT);
        player.getHandCardsMap().put(resourceCardId1, Side.FRONT);
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

        player.getHandCardsMap().put(goldCardId4, Side.FRONT);
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

        player.getHandCardsMap().put(resourceCardId9, Side.FRONT);
        c.placeCard(0, resourceCardId9, goldCardId4, CornerPos.DOWNLEFT, Side.FRONT);
        assertEquals(12, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(0, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));

        player.getHandCardsMap().put(goldCardId5, Side.FRONT);
        c.placeCard(0, goldCardId5, resourceCardId2, CornerPos.UPLEFT, Side.FRONT);
        assertEquals(15, player.getPoints());
        assertEquals(0, player.getAllElements().get(Element.ANIMAL)); // animal
        assertEquals(5, player.getAllElements().get(Element.PLANT)); // plant
        assertEquals(4, player.getAllElements().get(Element.INSECT)); // insect
        assertEquals(0, player.getAllElements().get(Element.FUNGI)); // fungi
        assertEquals(1, player.getAllElements().get(Element.INKWELL));
        assertEquals(1, player.getAllElements().get(Element.MANUSCRIPT));
        assertEquals(1, player.getAllElements().get(Element.QUILL));



// -------------------------ADD OBJECTIVES-----------------------------------------------------------



        int sharedObj1 = 87;
        int sharedObj2 = 88;
        int secretObj = 89;
        gs.getMainBoard().getSharedObjectiveCards()[0] = (ObjectiveCard) gs.getCard(sharedObj1);
        gs.getMainBoard().getSharedObjectiveCards()[1] = (ObjectiveCard) gs.getCard(sharedObj2);
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(15, player.getPoints());

        sharedObj1 = 90;
        sharedObj2 = 91;
        secretObj = 92;
        gs.getMainBoard().getSharedObjectiveCards()[0] = (ObjectiveCard) gs.getCard(sharedObj1);
        gs.getMainBoard().getSharedObjectiveCards()[1] = (ObjectiveCard) gs.getCard(sharedObj2);
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(15, player.getPoints());

        sharedObj1 = 93;
        sharedObj2 = 94;
        secretObj = 95;
        gs.getMainBoard().getSharedObjectiveCards()[0] = (ObjectiveCard) gs.getCard(sharedObj1);
        gs.getMainBoard().getSharedObjectiveCards()[1] = (ObjectiveCard) gs.getCard(sharedObj2);
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(15, player.getPoints());

        sharedObj1 = 96; //gives 2 points
        sharedObj2 = 97; //gives 0 points
        secretObj = 98; //gives 2 points
        gs.getMainBoard().getSharedObjectiveCards()[0] = (ObjectiveCard) gs.getCard(sharedObj1);
        gs.getMainBoard().getSharedObjectiveCards()[1] = (ObjectiveCard) gs.getCard(sharedObj2);
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(19, player.getPoints());

        sharedObj1 = 99; //gives 3 points
        sharedObj2 = 100; //gives 0 points
        secretObj = 101; //gives 0 points
        gs.getMainBoard().getSharedObjectiveCards()[0] = (ObjectiveCard) gs.getCard(sharedObj1);
        gs.getMainBoard().getSharedObjectiveCards()[1] = (ObjectiveCard) gs.getCard(sharedObj2);
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(22, player.getPoints());

        sharedObj1 = 102; //gives 0 points
        gs.getMainBoard().getSharedObjectiveCards()[0] = (ObjectiveCard) gs.getCard(sharedObj1);
        gs.getMainBoard().getSharedObjectiveCards()[1] = (ObjectiveCard) gs.getCard(sharedObj2);
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        assertEquals(22, player.getPoints());


    }


    @Test
    void calculateFinalPointsTest2() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException, WrongStructureConfigurationSizeException, IOException, CardNotPlacedException, CardIsNotInHandException, WrongPlacingPositionException, PlacingOnHiddenCornerException, CardAlreadyPresentOnTheCornerException, GoldCardCannotBePlacedException, CardAlreadPlacedException, WrongInstanceTypeException {

        Player player = new Player("pole");
        Main main = new Main();
        // create cards map
        HashMap<Integer, Card> cardsMap = main.createCardsMap();

        assertDoesNotThrow(() -> new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player}))));

        int starterCardId = 85;

        // create game state
        GameState gs = new GameState(cardsMap, new ArrayList<>(Arrays.asList(new Player[]{player})));
        Controller c = new Controller(gs);
        gs.getMainBoard().shuffleCards();
        gs.setSharedGoldCards();
        gs.setSharedResourceCards();
        player.setStarterCard((StarterCard) gs.getCardsMap().get(starterCardId));
        player.initializeBoard();
        gs.chooseStarterSidePhase();
        c.chooseInitialStarterSide(0, Side.BACK);

        player.getHandCardsMap().put(41, Side.FRONT);
        c.placeCard(0, 41, starterCardId, CornerPos.UPLEFT, Side.BACK);

        player.getHandCardsMap().put(42, Side.FRONT);
        c.placeCard(0, 42, 41, CornerPos.UPRIGHT, Side.BACK);

        player.getHandCardsMap().put(43, Side.FRONT);
        c.placeCard(0, 43, 42, CornerPos.UPRIGHT, Side.BACK);

        player.getHandCardsMap().put(44, Side.FRONT);
        c.placeCard(0, 44, 42, CornerPos.DOWNRIGHT, Side.BACK);

        player.getHandCardsMap().put(45, Side.FRONT);
        c.placeCard(0, 45, starterCardId, CornerPos.DOWNRIGHT, Side.BACK);

        player.getHandCardsMap().put(51, Side.FRONT);
        c.placeCard(0, 51, 45, CornerPos.UPRIGHT, Side.BACK);

        player.getHandCardsMap().put(52, Side.FRONT);
        c.placeCard(0, 52, 45, CornerPos.DOWNRIGHT, Side.BACK);

        int sharedObj1 = 87; //gives 2 points
        int sharedObj2 = 91; //gives 3 points
        int secretObj = 90; //gives 0 points
        gs.getMainBoard().getSharedObjectiveCards()[0] = (ObjectiveCard) gs.getCard(sharedObj1);
        gs.getMainBoard().getSharedObjectiveCards()[1] = (ObjectiveCard) gs.getCard(sharedObj2);
        player.setObjectiveCard( (ObjectiveCard) gs.getCard(secretObj));
        gs.calculateFinalPoints();
        //assertEquals(5, player.getPoints());

    }

    @Test
    void isGameEnded(){

    }

    @Test
    void getWinnerPlayerIndex(){

    }

    @Test

    void setColorTest(){
        Player player = new Player("pole");
    }

}
