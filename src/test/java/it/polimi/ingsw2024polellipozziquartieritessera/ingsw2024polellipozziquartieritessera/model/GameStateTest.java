package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import org.junit.jupiter.api.Test;
public class GameStateTest {
    @Test
    void gameStateTestConstructor() {
        // same colors
        HashMap hmap1 = new HashMap();
        Player[] players1 = {new Player("nick1", Color.RED), new Player("nick2", Color.RED)};
        assertThrowsExactly(NotUniquePlayerColorException.class, () -> new GameState(hmap1, players1));

        // same nickname
        HashMap hmap2 = new HashMap();
        Player[] players2 = {new Player("nick1", Color.RED), new Player("nick1", Color.BLUE)};
        assertThrowsExactly(NotUniquePlayerNicknameException.class, () -> new GameState(hmap2, players2));

        // same colors and nicknames
        HashMap hmap3 = new HashMap();
        Player[] players3 = {new Player("nick1", Color.RED), new Player("nick1", Color.RED)};
        assertThrowsExactly(NotUniquePlayerException.class, () -> new GameState(hmap3, players3));
    }
}
