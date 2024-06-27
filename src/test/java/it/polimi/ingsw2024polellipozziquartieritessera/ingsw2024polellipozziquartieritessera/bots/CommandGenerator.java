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

        if (BotConfig.CHAT_ENABLED && new Random().nextFloat(1f) < BotConfig.CHAT_FLUENCY) {
            command = String.format("ADDMESSAGE %s", generateMessage());
            return command;
        }

        switch (gamePhase) {
            case NICKNAMEPHASE:
                if (client.getViewModel().getPlayerIndex() == -1) {
                    String nick = client.getViewModel().getNickname(client.getViewModel().getPlayerIndex());
                    if (nick != null){
                        command = "ADDUSER , " + nick;
                    } else {
                        command = String.format("ADDUSER %s", generateName());
                    }
                    secretChosen = false;
                    sideChosen = false;
                } else if (BotConfig.CAN_START_IF_FULL && client.getViewModel().getPlayersSize() == BotConfig.PLAYER_NUM){
                    command = "START";
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
                break;
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

    public static String generateMessage() {
        String[] comments = {
                // comments about codex
                "May the Codex guide you!",
                "A worthy opponent! Prepare to be outwitted!",
                "Let the strategies unfold!",
                "A cunning move! But I have foreseen it!",
                "The game is afoot!",
                "The pressure is mounting... can you withstand it?",
                "Only one Codex Master can emerge victorious!",
                "A well-played turn! But the game is far from over.",
                "I see your strategy. But do you see mine?",
                "May the best Codex player win!"
        };

        String[] tutorial = {
                "If you forget some rules, check the Rules button to refresh your memory.",
                "Remember, you can't place a gold card if you don't have enough resources.",
                "Try to keep as many linkages available as possible to avoid being forced into playing cards where you don't want to.",
                "Remember, you have a secret objective that no one else can see!",
                "Keep in mind that you can see the colors of your opponents' hand cards.",
                "Sometimes it's better to draw from the deck instead of choosing a shared card.",
                "When someone reaches 20 points, there is an additional round before determining the winner.",
                "Don't forget to draw after placing a card... or the other players might become frustrated.",
                "Resource cards can be very useful for providing more options.",
                "Remember, there are shared objectives that you can complete to earn more points at the end of the game.",
                "Please take your time with your turns, but not too much.",
                "You can always view your opponents' boards, so keep an eye on them.",
                "Think carefully before placing a card; every placement could potentially be a costly mistake.",
                "Make the most of your gold cards; they are very powerful.",
                "You can always rotate a card to place it wherever you want without issues using right-click.",
                "Remember, placing a card face down reveals the resource type in the center of the card.",
                "Don't rush to end the game too quickly; build a formation that can generate points easily first.",
                "The game ends when there are no more cards left in both decks.",
                "Place, draw, place, draw... but always think it through.",
                "Other players can see your board; be mindful of this!",
                "No one can see the cards in your hand, but they can deduce them if you draw from the shared cards."
        };

        String[] curiosities = {
                "Some fungi can form mutually beneficial relationships with plants!",
                "Insects communicate through a variety of ways, including pheromones and vibrations.",
                "Plants not only convert sunlight into energy but also release oxygen as a byproduct.",
                "Certain animals exhibit complex social structures, similar to human societies.",
                "Fungi play a crucial role in nutrient cycling in ecosystems.",
                "Insects like ants are known for their remarkable teamwork and division of labor.",
                "Plants have developed various defense mechanisms against herbivores.",
                "Some animals have the ability to regenerate limbs or parts of their bodies.",
                "Certain fungi species can produce antibiotics that are beneficial to humans.",
                "Insects like bees are essential pollinators for many flowering plants.",
                "Did you know? Octopuses have three hearts and blue blood!",
                "Koalas sleep up to 18 hours a day due to their low-energy diet.",
                "Bamboo is one of the fastest-growing plants in the world, capable of growing up to 35 inches in a single day.",
                "The bombardier beetle can shoot a boiling chemical spray from its abdomen as a defense mechanism.",
                "Ants can lift and carry objects up to 50 times their body weight.",
                "Certain fungi create networks called mycelium that can cover vast areas underground.",
                "Male seahorses are the ones who give birth and care for their young.",
                "The Titan arum is the largest flower in the world and emits a foul odor resembling rotting flesh to attract pollinators.",
                "Honey never spoils. Archaeologists have found pots of honey in ancient Egyptian tombs that are over 3,000 years old and still perfectly edible.",
                "Female mosquitoes are the ones that bite because they need the protein in blood to lay eggs.",
                "Chameleons can move their eyes in two different directions at the same time, allowing them to look in two directions at once.",
                "Certain species of ants cultivate fungus as their primary food source.",
                "Carnivorous plants like the Venus flytrap and pitcher plants capture insects to supplement their nutrient intake from the soil.",
                "Dragonflies have incredible aerial acrobatic skills, enabling them to catch prey mid-air.",
                "Some fungi glow in the dark, a phenomenon known as bioluminescence.",
                "Crows are highly intelligent birds known for problem-solving and tool use.",
                "The jumping spider can leap up to 50 times its body length.",
                "The world's oldest known spider lived to be 43 years old.",
                "Polar bears have black skin under their white fur to absorb and retain heat from the sun.",
                "In certain species of fish, like the clownfish and anemonefish, individuals change sex depending on social status and environmental conditions.",
                "The world's largest organism is a fungus in Oregon's Malheur National Forest, covering over 2,385 acres.",
                "Carnivorous plants like the Venus flytrap and pitcher plants capture insects to supplement their nutrient intake from the soil.",
                "Dragonflies have incredible aerial acrobatic skills, enabling them to catch prey mid-air.",
                "Some fungi glow in the dark, a phenomenon known as bioluminescence.",
                "Crows are highly intelligent birds known for problem-solving and tool use.",
                "The jumping spider can leap up to 50 times its body length.",
                "The world's oldest known spider lived to be 43 years old.",
                "Polar bears have black skin under their white fur to absorb and retain heat from the sun.",
                "In certain species of fish, like the clownfish and anemonefish, individuals change sex depending on social status and environmental conditions.",
                "Butterflies taste with their feet.",
                "Elephants are the only animals that can't jump.",
                "A group of flamingos is called a flamboyance.",
                "The only mammals that can truly fly are bats.",
                "Cows have best friends and get stressed when they are separated.",
                "Hummingbirds are the only birds that can fly backwards.",
                "Starfish don’t have brains.",
                "Sharks have been around longer than trees.",
                "Kangaroos can't walk backwards.",
                "Seahorses mate for life and when they travel they hold onto each other’s tails.",
                "A snail can sleep for three years.",
                "Fish communicate with each other by using squeaks, growls, and other low-frequency sounds.",
                "A cockroach can live for a week without its head.",
                "A group of owls is called a parliament.",
                "A giraffe's tongue is black to prevent sunburn.",
                "Penguins propose to their mate with a pebble.",
                "A newborn kangaroo is about 1 inch in length.",
                "The fingerprints of koalas are so indistinguishable from humans that they have on occasion been confused at a crime scene.",
                "A shrimp's heart is in its head.",
                "The tongue of a blue whale weighs more than most elephants.",
                "There are more living organisms on the skin of a single human than there are humans on Earth.",
                "Dolphins sleep with one eye open.",
                "Rats laugh when they are tickled.",
                "Humpback whales create the loudest sound of any living creature.",
                "Oysters can change gender depending on which is best for mating.",
                "Sloths take two weeks to digest their food.",
                "Did you know? Some fungi can form mutually beneficial relationships with plants!",
                "Insects communicate through a variety of ways, including pheromones and vibrations.",
                "Plants not only convert sunlight into energy but also release oxygen as a byproduct.",
                "Certain animals exhibit complex social structures, similar to human societies.",
                "Fungi play a crucial role in nutrient cycling in ecosystems.",
                "Insects like ants are known for their remarkable teamwork and division of labor.",
                "Plants have developed various defense mechanisms against herbivores.",
                "Some animals have the ability to regenerate limbs or parts of their bodies.",
                "Certain fungi species can produce antibiotics that are beneficial to humans.",
                "Insects like bees are essential pollinators for many flowering plants.",
        };

        String[] didYouKnow = {
                "Did you know? Octopuses have three hearts and blue blood!",
                "Did you know? Koalas sleep up to 18 hours a day due to their low-energy diet.",
                "Did you know? Bamboo is one of the fastest-growing plants in the world, capable of growing up to 35 inches in a single day.",
                "Did you know? The bombardier beetle can shoot a boiling chemical spray from its abdomen as a defense mechanism.",
                "Did you know? Ants can lift and carry objects up to 50 times their body weight.",
                "Did you know? Certain fungi create networks called mycelium that can cover vast areas underground.",
                "Did you know? Male seahorses are the ones who give birth and care for their young.",
                "Did you know? The Titan arum is the largest flower in the world and emits a foul odor resembling rotting flesh to attract pollinators.",
                "Did you know? Honey never spoils. Archaeologists have found pots of honey in ancient Egyptian tombs that are over 3,000 years old and still perfectly edible.",
                "Did you know? Female mosquitoes are the ones that bite because they need the protein in blood to lay eggs.",
                "Did you know? Chameleons can move their eyes in two different directions at the same time, allowing them to look in two directions at once.",
                "Did you know? Certain species of ants cultivate fungus as their primary food source.",
                "Did you know? Carnivorous plants like the Venus flytrap and pitcher plants capture insects to supplement their nutrient intake from the soil.",
                "Did you know? Dragonflies have incredible aerial acrobatic skills, enabling them to catch prey mid-air.",
                "Did you know? Some fungi glow in the dark, a phenomenon known as bioluminescence.",
                "Did you know? Crows are highly intelligent birds known for problem-solving and tool use.",
                "Did you know? The jumping spider can leap up to 50 times its body length.",
                "Did you know? The world's oldest known spider lived to be 43 years old.",
                "Did you know? Polar bears have black skin under their white fur to absorb and retain heat from the sun.",
                "Did you know? In certain species of fish, like the clownfish and anemonefish, individuals change sex depending on social status and environmental conditions.",
                "Did you know? Butterflies taste with their feet.",
                "Did you know? Elephants are the only animals that can't jump.",
                "Did you know? A group of flamingos is called a flamboyance.",
                "Did you know? The only mammals that can truly fly are bats.",
                "Did you know? Cows have best friends and get stressed when they are separated.",
                "Did you know? Hummingbirds are the only birds that can fly backwards.",
                "Did you know? Starfish don’t have brains.",
                "Did you know? Sharks have been around longer than trees.",
                "Did you know? Kangaroos can't walk backwards.",
                "Did you know? Seahorses mate for life and when they travel they hold onto each other’s tails.",
                "Did you know? A snail can sleep for three years.",
                "Did you know? Fish communicate with each other by using squeaks, growls, and other low-frequency sounds.",
                "Did you know? A cockroach can live for a week without its head.",
                "Did you know? A group of owls is called a parliament.",
                "Did you know? A giraffe's tongue is black to prevent sunburn.",
                "Did you know? Penguins propose to their mate with a pebble.",
                "Did you know? A newborn kangaroo is about 1 inch in length.",
                "Did you know? The fingerprints of koalas are so indistinguishable from humans that they have on occasion been confused at a crime scene.",
                "Did you know? A shrimp's heart is in its head.",
                "Did you know? The tongue of a blue whale weighs more than most elephants.",
                "Did you know? There are more living organisms on the skin of a single human than there are humans on Earth.",
                "Did you know? Dolphins sleep with one eye open.",
                "Did you know? Rats laugh when they are tickled.",
                "Did you know? Humpback whales create the loudest sound of any living creature.",
                "Did you know? Oysters can change gender depending on which is best for mating.",
                "Did you know? Sloths take two weeks to digest their food."
        };

        Random random = new Random();

        int type = random.nextInt(4);
        int index;
        return switch (type) {
            case 0 -> {
                index = random.nextInt(comments.length);
                yield comments[index];
            }
            case 1 -> {
                index = random.nextInt(tutorial.length);
                yield tutorial[index];
            }
            case 2 -> {
                index = random.nextInt(curiosities.length);
                yield curiosities[index];
            }
            case 3 -> {
                index = random.nextInt(didYouKnow.length);
                yield didYouKnow[index];
            }
            default -> {
                index = random.nextInt(tutorial.length);
                yield tutorial[index];
            }
        };
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
        if (new Random().nextFloat(1f) < BotConfig.PASSIVITY) {
            List<CornerPos> cornerPos = new ArrayList<>(List.of(CornerPos.values()));
            Collections.shuffle(cornerPos);
            return cornerPos.getFirst();
        }

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

        if (new Random().nextFloat(1f) < BotConfig.PASSIVITY) {
            return tableCards.getFirst();
        }

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
            return (String.format("DRAWCARD %s", DrawType.values()[new Random().nextInt(DrawType.values().length)].toString()));
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

