package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;

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

    public Element[][] getConfiguration() { return configuration; }

    @Override
    public int getTimesWon(Player player, GoldCard card) {
        throw new RuntimeException();
    }

    @Override
    public int getTimesWon(Player player, ObjectiveCard card) {
        int rows = player.getPlayerBoard().size();
        int cols = player.getPlayerBoard().getFirst().size();

        // instantiate the element board and fill it with the cards elements on playerBoard
        Element[][] elementBoard = new Element[rows][cols];
        for (int i=0; i<rows; i++){
            for (int j=0; j<cols; j++){
                int cardId = player.getPlayerBoard().get(i).get(j);
                if (cardId != -1){
                    elementBoard[i][j] = player.getCenterResource(cardId);
                } else{
                    elementBoard[i][j] = Element.EMPTY;
                }
            }
        }

        // instantiate the checkBoard
        int[][] checkBoard = new int[rows][cols];

        // verify the recurrences of the configuration (from up-left to down-right)
        int recurrences = 0;

        for (int i=0; i<rows; i++){
            for (int j=0; j<cols; j++){
                boolean isValidOccurrence = true;
                for (int k = 0; k < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION && isValidOccurrence; k++){
                    for(int w = 0; w < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION && isValidOccurrence; w++){
                        if (this.getConfiguration()[k][w] != Element.EMPTY){
                            // if we are outside bounds
                            if (i+k >= rows || j+w >= cols) {
                                isValidOccurrence = false;
                            }
                            // if the configuration is not matched
                            else if(elementBoard[i+k][j+w] != this.getConfiguration()[k][w]){
                                isValidOccurrence = false;
                            }
                            // if the elements are used more than one time
                            else if(checkBoard[i+k][j+w] == 1){
                                isValidOccurrence = false;
                            }
                        }
                    }
                }
                if (isValidOccurrence){
                    // set in check board the used cards from 0 to 1
                    for (int k = 0; k < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; k++){
                        for(int w = 0; w < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; w++){
                            if (i+k<rows && j+w<cols){
                                if (elementBoard[i+k][j+w] != Element.EMPTY) {
                                    checkBoard[i + k][j + w] = 1;
                                }
                            }
                        }
                    }
                    recurrences++;
                }
            }
        }
        return recurrences;
    }
}
