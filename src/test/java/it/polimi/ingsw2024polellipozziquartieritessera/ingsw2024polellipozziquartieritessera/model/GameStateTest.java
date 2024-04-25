package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import org.junit.jupiter.api.Test;
public class GameStateTest {
    @Test
    void gameStateTestConstructor() {
        // same colors
        HashMap hmap1 = new HashMap();

        ArrayList<Player> players1 = new ArrayList<>();
        players1.add(new Player("nick1", Color.RED));
        players1.add(new Player("nick2", Color.RED));

        assertThrowsExactly(NotUniquePlayerColorException.class, () -> new GameState(hmap1, hmap1, hmap1, hmap1, players1));

        // same nickname
        HashMap hmap2 = new HashMap();

        ArrayList players2 = new ArrayList<>();
        players2.add(new Player("nick1", Color.RED));
        players2.add(new Player("nick1", Color.BLUE));

        assertThrowsExactly(NotUniquePlayerNicknameException.class, () -> new GameState(hmap2, hmap2, hmap2, hmap2, players2));

        // same colors and nicknames
        HashMap hmap3 = new HashMap();

        ArrayList<Player> players3 = new ArrayList<>();
        players3.add(new Player("nick1", Color.RED));
        players3.add(new Player("nick1", Color.RED));

        assertThrowsExactly(NotUniquePlayerException.class, () -> new GameState(hmap3, hmap3, hmap3, hmap3, players3));
    }

    @Test
    void calculateFinalPoints(){

    }

    @Test
    void isGameEnded(){

    }

    @Test
    void getWinnerPlayerIndex(){

    }

}
