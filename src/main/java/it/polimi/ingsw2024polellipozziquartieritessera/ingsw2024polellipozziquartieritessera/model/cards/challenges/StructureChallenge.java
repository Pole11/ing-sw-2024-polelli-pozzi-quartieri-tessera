package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;

public class StructureChallenge extends Challenge{
    private final Element[][] configuration;

    public StructureChallenge(Element[][] configuration) throws WrongStructureConfigurationSizeException {
        this.configuration = configuration;

        if (configuration.length != Config.N_STRUCTURE_CHALLENGE_CONFIGURATION) {
            throw new WrongStructureConfigurationSizeException("The number of rows in the matrix is too big");
        }

        if (configuration[0].length != Config.N_STRUCTURE_CHALLENGE_CONFIGURATION ||
                    configuration[1].length != Config.N_STRUCTURE_CHALLENGE_CONFIGURATION ||
                    configuration[2].length != Config.N_STRUCTURE_CHALLENGE_CONFIGURATION) {
            throw new WrongStructureConfigurationSizeException("The number of cols in the matrix is too big");
        }
    }
}
