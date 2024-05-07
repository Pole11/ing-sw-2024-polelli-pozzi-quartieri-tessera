package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CornerCardTest {
    // ALL METHODS TESTED
    @Test
    void testGetterSetter() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        // setup
        GameState g = Populate.populate();

        CornerCard card = (CornerCard) g.getCard(40);

        assertEquals(card.getFrontCorners().length, 4);
        assertEquals(card.getBackCorners().length, 4);

        for (Corner corner : card.getFrontCorners()){
            assertNotNull(corner);
        }
        for (Corner corner : card.getBackCorners()){
            assertNotNull(corner);
        }
    }

    @Test
    void testGetCorners() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = Populate.populate();

        CornerCard card = (CornerCard) g.getCard(40);

        // get all corners
        ArrayList<Corner> allCorners = card.getCorners();

        // get all front and back corners
        ArrayList<Corner> frontCorners = card.getCorners(Side.FRONT);
        ArrayList<Corner> backCorners = card.getCorners(Side.BACK);

        // verify number of corners is correct
        assertEquals(8, allCorners.size());
        assertEquals(4, frontCorners.size());
        assertEquals(4, backCorners.size());

        // verify corners are not null
        for (Corner corner : allCorners){
            assertNotNull(corner);
        }
    }

    @Test
    void testGetLinkedCards() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = Populate.populate();

        CornerCard card1 = (CornerCard) g.getCard(40);
        CornerCard card2 = (CornerCard) g.getCard(30);
        CornerCard card3 = (CornerCard) g.getCard(20);

        // set the linked corner
        card1.getCorners().getFirst().setLinkedCorner(card2.getCorners().getFirst());
        card1.getCorners().getLast().setLinkedCorner(card3.getCorners().getFirst());

        ArrayList<Integer> expectedLinkedCards = new ArrayList<>();
        expectedLinkedCards.add(card2.getId());
        expectedLinkedCards.add(card3.getId());

        // verify if the cards are linked
        assertEquals(card1.getLinkedCards(), expectedLinkedCards);
    }

    @Test
    void testGetUncoveredCorners() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        GameState g = Populate.populate();

        CornerCard card = (CornerCard) g.getCard(40);
        ArrayList<Corner> cornersFront = card.getCorners(Side.FRONT);
        ArrayList<Corner> cornersBack = card.getCorners(Side.BACK);


        // verify if none covered
        assertEquals(card.getUncoveredCorners(Side.FRONT), cornersFront);
        assertEquals(card.getUncoveredCorners(Side.BACK), cornersBack);

        // cover all the corners
        for (Corner corner : card.getCorners()){
            corner.setCovered(true);
        }

        // verify if covered
        cornersFront.clear();
        assertEquals(card.getUncoveredCorners(Side.FRONT), cornersFront);
        cornersBack.clear();
        assertEquals(card.getUncoveredCorners(Side.BACK), cornersBack);
    }
}
