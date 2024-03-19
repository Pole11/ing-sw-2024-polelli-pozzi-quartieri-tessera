package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.card.challenge;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exception.WrongStructureConfigurationSizeException;

public class StructureChallenge {
    int[][] configuration;

    public StructureChallenge(int[][] configuration) throws WrongStructureConfigurationSizeException {
        this.configuration = configuration;

        if (configuration.length != 3) {
            throw new WrongStructureConfigurationSizeException("The number of rows in the matrix is too big");
        }

        if (configuration[0].length != 3 ||
                    configuration[1].length != 3 ||
                    configuration[2].length != 3) {
            throw new WrongStructureConfigurationSizeException("The number of cols in the matrix is too big");
        }
    }
}
