package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
import org.junit.jupiter.api.Test;
public class PlayerTest {
    @Test
    void playerTestConstructor() {
        // TODO: iterate all the colors
        // TODO: test with different nickname lengths
        Color color = Color.GREEN;
        //assertDoesNotThrow(() -> new Player("Pole", color));
        assertAll(() -> new Player("Pole", color));
    }
}
