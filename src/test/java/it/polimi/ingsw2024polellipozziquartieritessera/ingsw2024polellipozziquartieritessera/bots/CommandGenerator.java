package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.ClientBot.client;
import static it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.InputInserter.secretChosen;
import static it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.InputInserter.sideChosen;

public class CommandGenerator {
    public static String generateCommand(Client client) {
        String command = null;
        GamePhase gamePhase = client.getViewModel().getGamePhase();
        switch (gamePhase) {
            case NICKNAMEPHASE:
                if (client.getViewModel().getPlayerIndex() == -1) {
                    command = String.format("ADDUSER %s", generateName());
                }
                break;
            case CHOOSESTARTERSIDEPHASE:
                if (!sideChosen) {
                    command = String.format("CHOOSESTARTER %s", generateSide());
                    sideChosen = true;
                }
                break;
            case CHOOSECOLORPHASE:
                if (client.getViewModel().getColorsMap(client.getViewModel().getPlayerIndex()) == null) {
                    command = String.format("CHOOSECOLOR %s", generateColor());
                }
                break;
            case CHOOSEOBJECTIVEPHASE:
                if (!secretChosen) {
                    command = String.format("CHOOSEOBJECTIVE %d", generateNumber(1));
                    secretChosen = true;
                }
                break;
            case MAINPHASE:
            case ENDPHASE:
                command = handleMainPhases(client);
                break;
            case FINALPHASE:
                System.out.println("Siamo in final phase");
            default:
                // No command to send for other phases
                break;
        }
        return command;
    }

    private static String handleMainPhases(Client client) {
        ViewModel viewModel = client.getViewModel();
        if (viewModel.getCurrentPlayer() == viewModel.getPlayerIndex()) {
            TurnPhase turnPhase = viewModel.getTurnPhase();
            return switch (turnPhase) {
                case PLACINGPHASE -> placeCardCommand();
                case DRAWPHASE -> drawCardCommand();
            };
        }
        return null;
    }

    private static String generateName() {
        String[] names = {"James", "John", "Robert", "Michael", "William", "David", "Richard", "Joseph", "Thomas", "Charles",
                "Christopher", "Daniel", "Matthew", "Anthony", "Donald", "Mark", "Paul", "Steven", "Andrew", "Kenneth",
                "Joshua", "Kevin", "Brian", "George", "Edward", "Ronald", "Timothy", "Jason", "Jeffrey", "Ryan",
                "Jacob", "Gary", "Nicholas", "Eric", "Stephen", "Jonathan", "Larry", "Scott", "Frank", "Brandon",
                "Raymond", "Gregory", "Benjamin", "Samuel", "Patrick", "Alexander", "Jack", "Dennis", "Jerry", "Tyler",
                "Aaron", "Henry", "Douglas", "Jose", "Peter", "Adam", "Zachary", "Nathan", "Walter", "Harold",
                "Kyle", "Carl", "Arthur", "Gerald", "Roger", "Keith", "Jeremy", "Terry", "Lawrence", "Sean",
                "Christian", "Albert", "Joe", "Ethan", "Austin", "Jesse", "Willie", "Billy", "Bryan", "Bruce",
                "Jordan", "Ralph", "Roy", "Noah", "Dylan", "Eugene", "Wayne", "Alan", "Juan", "Louis",
                "Russell", "Gabriel", "Randy", "Philip", "Harry", "Vincent", "Bobby", "Johnny", "Logan", "Leonard",
                "Mary", "Patricia", "Jennifer", "Linda", "Elizabeth", "Barbara", "Susan", "Jessica", "Sarah", "Karen",
                "Nancy", "Lisa", "Betty", "Margaret", "Sandra", "Ashley", "Kimberly", "Emily", "Donna", "Michelle",
                "Dorothy", "Carol", "Amanda", "Melissa", "Deborah", "Stephanie", "Rebecca", "Sharon", "Laura", "Cynthia",
                "Kathleen", "Amy", "Shirley", "Angela", "Helen", "Anna", "Brenda", "Pamela", "Nicole", "Emma",
                "Samantha", "Katherine", "Christine", "Debra", "Rachel", "Catherine", "Carolyn", "Janet", "Ruth", "Maria",
                "Heather", "Diane", "Virginia", "Julie", "Joyce", "Victoria", "Olivia", "Kelly", "Christina", "Lauren",
                "Joan", "Evelyn", "Judith", "Megan", "Cheryl", "Andrea", "Hannah", "Martha", "Jacqueline", "Frances",
                "Gloria", "Ann", "Teresa", "Sara", "Janice", "Jean", "Alice", "Madison", "Doris", "Abigail",
                "Julia", "Judy", "Grace", "Denise", "Amber", "Marilyn", "Beverly", "Danielle", "Theresa", "Sophia",
                "Marie", "Diana", "Brittany", "Natalie", "Isabella", "Charlotte", "Rose", "Alexis", "Kayla", "Ann"
        };

        Random random = new Random();
        int index = random.nextInt(names.length);
        return names[index];
    }

    private static String generateSide() {
        Side[] sides = Side.values();

        Random random = new Random();
        int index = random.nextInt(sides.length);
        return sides[index].toString();
    }

    private static String generateColor() {
        Color[] colors = Color.values();

        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index].toString();
    }

    private static int generateNumber(int max) {
        Random random = new Random();
        return random.nextInt(max + 1);
    }

    private static String placeCardCommand() {
        ViewModel viewModel = client.getViewModel();

        // CHOOSE CARD TO PLACE AND SIDE
        ArrayList<Integer> handCards = viewModel.getHand(viewModel.getPlayerIndex());

        Side side;
        int placingCard = chooseCardToPlace(handCards);
        if (placingCard == -1) {
            placingCard = handCards.getFirst();
            side = Side.BACK;
        } else {
            side = Side.FRONT;
        }

        // CHOOSE TABLE CARD
        ArrayList<Integer> tableCards = viewModel.getPlacingCardOrderMap(viewModel.getPlayerIndex());

        int tableCard = chooseTableCard(tableCards);
        CornerPos position = choosePlacingPosition(tableCard);

        return String.format("PLACECARD %d %d %s %s", placingCard, tableCard, position, side);
    }

    private static CornerPos choosePlacingPosition(int tableCard) {
        ViewModel viewModel = client.getViewModel();

        ArrayList<Corner> corners;
        if (tableCard <= 40) {
            ResourceCard card = (ResourceCard) viewModel.cardById(tableCard);
            corners = card.getCorners(viewModel.getPlacedCardSide(tableCard));
        } else if (tableCard <= 80) {
            GoldCard card = (GoldCard) viewModel.cardById(tableCard);
            corners = card.getCorners(viewModel.getPlacedCardSide(tableCard));
        } else {
            StarterCard card = (StarterCard) viewModel.cardById(tableCard);
            corners = card.getCorners(viewModel.getPlacedCardSide(tableCard));
        }

        ArrayList<CornerPos> suitablePositions = new ArrayList<>();
        int cornerIndex = 0;
        for (Corner corner : corners) {
            if (!corner.getHidden() && !corner.getCovered() && corner.getElement() == Element.EMPTY) {
                switch (cornerIndex) {
                    case 0:
                        suitablePositions.add(CornerPos.UPLEFT);
                    case 1:
                        suitablePositions.add(CornerPos.UPRIGHT);
                    case 2:
                        suitablePositions.add(CornerPos.DOWNRIGHT);
                    case 3:
                        suitablePositions.add(CornerPos.DOWNLEFT);
                }
            }
            cornerIndex++;
        }


        if (suitablePositions.isEmpty()) {
            suitablePositions.addAll(List.of(CornerPos.values()));
        }

        Collections.shuffle(suitablePositions);
        return suitablePositions.getFirst();
    }

    private static int chooseTableCard(ArrayList<Integer> tableCards) {
        ViewModel viewModel = client.getViewModel();

        Collections.shuffle(tableCards);

        // try finding a card with empty corner
        for (int cardId : tableCards) {
            Side side = viewModel.getPlacedCardSide(cardId);
            if (side == Side.BACK) {
                return cardId;
            } else {
                ArrayList<Corner> corners;
                if (cardId <= 40) {
                    ResourceCard card = (ResourceCard) viewModel.cardById(cardId);
                    corners = card.getCorners(Side.FRONT);
                } else if (cardId <= 80) {
                    GoldCard card = (GoldCard) viewModel.cardById(cardId);
                    corners = card.getCorners(Side.FRONT);
                } else {
                    StarterCard card = (StarterCard) viewModel.cardById(cardId);
                    corners = card.getCorners(Side.FRONT);
                }

                for (Corner corner : corners) {
                    if (!corner.getHidden() && !corner.getCovered() && corner.getElement() == Element.EMPTY) {
                        return cardId;
                    }
                }

            }
        }

        return tableCards.getFirst();
    }

    private static int chooseCardToPlace(ArrayList<Integer> handCards) {
        if (new Random().nextFloat(1f) < BotConfig.PASSIVITY) {
            return -1;
        }

        ArrayList<Integer> goldCards = new ArrayList<>();
        ArrayList<Integer> resourceCards = new ArrayList<>();

        // try placing gold cards
        for (int cardId : handCards) {
            if (cardId > 40) {
                goldCards.add(cardId);
            } else {
                resourceCards.add(cardId);
            }
        }

        // Initialize random number generator
        Random random = new Random();

        // Determine weighted choice
        double totalWeight = BotConfig.GOLDWEIGHT * goldCards.size() + BotConfig.RESOURCEWEIGHT * resourceCards.size();
        double choice = random.nextDouble() * totalWeight;

        if (choice < BotConfig.GOLDWEIGHT * goldCards.size()) {
            // Choose a random gold card
            return goldCards.get(random.nextInt(goldCards.size()));
        } else {
            // Choose a random resource card
            return resourceCards.get(random.nextInt(resourceCards.size()));
        }
    }

    private static String drawCardCommand() {
        if (new Random().nextFloat(1f) < BotConfig.PASSIVITY) {
            return (DrawType.values()[new Random().nextInt(DrawType.values().length)].toString());
        }

        ViewModel viewModel = client.getViewModel();

        ArrayList<Integer> handCards = viewModel.getHand(viewModel.getPlayerIndex());
        ArrayList<Integer> goldCards = new ArrayList<>();
        ArrayList<Integer> resourceCards = new ArrayList<>();

        // get cards types
        for (int cardId : handCards) {
            if (cardId > 40) {
                goldCards.add(cardId);
            } else {
                resourceCards.add(cardId);
            }
        }

        ArrayList<DrawType> choices = new ArrayList<>();
        if (resourceCards.isEmpty()) {
            Collections.addAll(choices, DrawType.SHAREDRESOURCE1, DrawType.SHAREDRESOURCE2, DrawType.DECKRESOURCE);
        } else if (goldCards.isEmpty()) {
            Collections.addAll(choices, DrawType.SHAREDGOLD1, DrawType.SHAREDGOLD2, DrawType.DECKGOLD);
        } else {
            Collections.addAll(choices, DrawType.values());
        }

        Collections.shuffle(choices);
        return String.format("DRAWCARD %s", choices.getFirst().toString());
    }
}

