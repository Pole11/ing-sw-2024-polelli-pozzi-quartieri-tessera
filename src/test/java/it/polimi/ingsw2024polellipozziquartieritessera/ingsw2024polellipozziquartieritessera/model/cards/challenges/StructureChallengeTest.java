package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;
import static org.junit.jupiter.api.Assertions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import org.junit.jupiter.api.Test;
public class StructureChallengeTest {

    @Test
    void structureChallengeTestConfigurationSize() {
        Element[][] mat33 = new Element[3][3];
        Element[][] mat34 = new Element[3][4];
        Element[][] mat43 = new Element[4][3];
        Element[][] mat24 = new Element[2][4];
        Element[][] mat42 = new Element[4][2];
        Element[][] mat44 = new Element[4][4];

        int id = -1;

        assertAll(() -> new StructureChallenge(id, mat33));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(id, mat34));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(id, mat43));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(id, mat24));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(id, mat42));
        assertThrows(WrongStructureConfigurationSizeException.class, () -> new StructureChallenge(id, mat44));
    }
}
