package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.challenge;
import static org.junit.jupiter.api.Assertions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exception.WrongStructureConfigurationSizeException;
import org.junit.jupiter.api.Test;
public class StructureChallengeTest {

    @Test
    void structureChallengeTestConfigurationSize() {
        int[][] mat33 = new int[3][3];
        int[][] mat34 = new int[3][4];
        int[][] mat43 = new int[4][3];
        int[][] mat24 = new int[2][4];
        int[][] mat42 = new int[4][2];
        int[][] mat44 = new int[4][4];

        assertAll(() -> new StructureChallenge(mat33));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(mat34));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(mat43));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(mat24));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(mat42));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(mat44));
    }
}
