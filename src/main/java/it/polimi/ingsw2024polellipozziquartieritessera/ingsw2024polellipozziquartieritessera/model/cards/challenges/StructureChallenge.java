package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.GoldCard;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.ObjectiveCard;

public class StructureChallenge extends Challenge {
    private final Element[][] configuration;

    public StructureChallenge(Element[][] configuration) {
        this.configuration = configuration;

        // Validate the number of rows in the configuration matrix
        if (configuration.length != Config.N_STRUCTURE_CHALLENGE_CONFIGURATION) {
            System.err.println("Error in JSON: Structure challenge configuration has incorrect number of rows.");
            throw new RuntimeException();
        }

        // Validate the number of columns in each row of the configuration matrix
        for (int i = 0; i < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; i++) {
            if (configuration[i].length != Config.N_STRUCTURE_CHALLENGE_CONFIGURATION) {
                System.err.println("Error in JSON: Structure challenge configuration has incorrect number of columns.");
                throw new RuntimeException();
            }
        }
    }

    public Element[][] getConfiguration() {
        return configuration;
    }

    @Override
    public int getTimesWon(Player player, GoldCard card) {
        throw new RuntimeException();
    }

    @Override
    public int getTimesWon(Player player, ObjectiveCard card) {
        int rows = player.getPlayerBoard().size();
        int cols = player.getPlayerBoard().getFirst().size();

        // Create a board to store elements from the player's board
        Element[][] elementBoard = new Element[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int cardId = player.getPlayerBoard().get(i).get(j);
                if (cardId != -1) {
                    elementBoard[i][j] = player.getCenterResource(cardId);
                } else {
                    elementBoard[i][j] = Element.EMPTY;
                }
            }
        }

        // Create a board to track used positions on the player's board
        int[][] checkBoard = new int[rows][cols];

        // Calculate the number of valid occurrences of the challenge configuration
        int recurrences = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                boolean isValidOccurrence = true;
                for (int k = 0; k < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION && isValidOccurrence; k++) {
                    for (int w = 0; w < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION && isValidOccurrence; w++) {
                        if (this.getConfiguration()[k][w] != Element.EMPTY) {
                            // Check if the current position is within bounds
                            if (i + k >= rows || j + w >= cols) {
                                isValidOccurrence = false;
                            }
                            // Check if the elements match the configuration
                            else if (elementBoard[i + k][j + w] != this.getConfiguration()[k][w]) {
                                isValidOccurrence = false;
                            }
                            // Check if the element position has already been used
                            else if (checkBoard[i + k][j + w] == 1) {
                                isValidOccurrence = false;
                            }
                        }
                    }
                }
                if (isValidOccurrence) {
                    // Mark positions on the check board as used
                    for (int k = 0; k < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; k++) {
                        for (int w = 0; w < Config.N_STRUCTURE_CHALLENGE_CONFIGURATION; w++) {
                            if (i + k < rows && j + w < cols) {
                                if (elementBoard[i + k][j + w] != Element.EMPTY) {
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
