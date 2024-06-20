package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;

import java.io.*;
import java.util.*;

public class ClientBot {
    static Client client;
    private static boolean sideChosen = false;
    private static boolean secretChosen = false;
    private static final int SPEED = 10; // millisecond for command
    private static final int ACTIVITY = 20; // try to place strong cards if higher than 1 (default 10)
    public static void main(String[] args) throws IOException {
        String input = args[0];
        String host = args[1];
        String port = args[2];

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        System.setIn(pis);

        Thread starter = new Thread(() -> {
            try {
                client = new Client(input, host, port);
                client.startClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread inputInserter = new Thread(() -> insertInputs(pos));
        starter.start();
        inputInserter.start();
    }

    private static void insertInputs(PipedOutputStream pos) {
        try {
            Thread.sleep(2000); // Give the client some time to start
            pos.write(("n\n").getBytes());
            System.out.println("n");
            pos.flush();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Thread.sleep(SPEED);

                String command = generateCommand();
                if (command != null) {
                    sendCommand(pos, command + "\n");
                }

            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void sendCommand(PipedOutputStream pos, String command) throws IOException {
        System.out.print(command);
        pos.write(command.getBytes());
        pos.flush();
    }

    private static String generateCommand() {
        String command = null;
        GamePhase gamePhase = client.getViewModel().getGamePhase();
        switch (gamePhase) {
            case NICKNAMEPHASE:
                if (client.getViewModel().getPlayerIndex() == -1){
                    command = String.format("ADDUSER %s", generateName());
                }
                break;
            case CHOOSESTARTERSIDEPHASE:
                if (!sideChosen){
                    command = String.format("CHOOSESTARTER %s", generateSide());
                    sideChosen = true;
                }
                break;
            case CHOOSECOLORPHASE:
                if (client.getViewModel().getColorsMap(client.getViewModel().getPlayerIndex()) == null){
                    command = String.format("CHOOSECOLOR %s", generateColor());
                }
                break;
            case CHOOSEOBJECTIVEPHASE:
                if (!secretChosen){
                    command = String.format("CHOOSEOBJECTIVE %d", generateObjective());
                    secretChosen = true;
                }
                break;
            case MAINPHASE:
            case ENDPHASE:
            case FINALPHASE:
                command = handleMainPhases();
                break;
            default:
                // No command to send for other phases
                break;
        }
        return command;
    }

    private static String handleMainPhases() {
        ViewModel viewModel = client.getViewModel();
        if (viewModel.getCurrentPlayer() == viewModel.getPlayerIndex()) {
            TurnPhase turnPhase = viewModel.getTurnPhase();
            return switch (turnPhase) {
                case PLACINGPHASE -> placeRandomCardCommand();
                case DRAWPHASE -> drawRandomCardCommand();
                default -> null;
            };
        }
        return null;
    }

    private static String generateName(){
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

    private static String generateSide(){
        Side[] sides = Side.values();

        Random random = new Random();
        int index = random.nextInt(sides.length);
        return sides[index].toString();
    }

    private static String generateColor(){
        Color[] colors = Color.values();

        Random random = new Random();
        int index = random.nextInt(colors.length);
        return colors[index].toString();
    }

    private static int generateObjective() {
        Random random = new Random();
        return random.nextInt(2);
    }

    private static String generateRandomPosition(){
        CornerPos[] positions = CornerPos.values();

        Random random = new Random();
        int index = random.nextInt(positions.length);
        return positions[index].toString();
    }

    private static String placeRandomCardCommand(){
        ViewModel viewModel = client.getViewModel();

        int placingCard = 0;
        for (int i = 0; i<ACTIVITY/2; i++){
            placingCard = getRandomHandCard(viewModel);
            if (placingCard > 40){
                break;
            }
        }

        int tableCard = getRandomBoardCard(viewModel);
        String position = generateRandomPosition();

        String side;
        if (new Random().nextInt(ACTIVITY)!=0){
            side = Side.FRONT.toString();
        } else{
            side = generateSide();
        }

        return String.format("PLACECARD %d %d %s %s", placingCard, tableCard, position, side);
    }

    private static String drawRandomCardCommand(){
        DrawType[] draws = DrawType.values();

        Random random = new Random();
        int index = random.nextInt(draws.length);

        return String.format("DRAWCARD %s", draws[index].toString());
    }

    private static int getRandomHandCard(ViewModel viewModel){
        ArrayList<Integer> handCards = viewModel.getHand(viewModel.getPlayerIndex());

        Random random = new Random();
        int index = random.nextInt(handCards.size());
        return handCards.get(index);
    }

    private static int getRandomBoardCard(ViewModel viewModel){
        ArrayList<Integer> boardCards = viewModel.getPlacingCardOrderMap(viewModel.getPlayerIndex());

        Random random = new Random();
        int index = random.nextInt(boardCards.size());
        return boardCards.get(index);
    }

    private static String generateRandomPlaceCardCommand(){
        Random random = new Random();

        // Generate random placing card id and table card id, ensuring they are different
        int placingCardId = random.nextInt(80) + 1;
        int tableCardId;
        do {
            tableCardId = random.nextInt(86) + 1;
        } while (tableCardId == placingCardId);

        // Possible orientations and positions
        String[] orientations = {"Upright", "Upleft", "Downright", "Downleft"};
        String[] positions = {"Front", "Back"};

        // Select random orientation and position
        String orientation = orientations[random.nextInt(orientations.length)];
        String position = positions[random.nextInt(positions.length)];

        // Construct the command
        return String.format("PLACECARD %d %d %s %s", placingCardId, tableCardId, orientation, position);
    }

    private static String generateRandomPlaceStarterCommand(){
        Random random = new Random();

        // Generate random placing card id and table card id, ensuring they are different
        int placingCardId = random.nextInt(80) + 1;
        int tableCardId;
        do {
            tableCardId = random.nextInt(6) + 81;
        } while (tableCardId == placingCardId);

        // Possible orientations and positions
        String[] orientations = {"Upright", "Upleft", "Downright", "Downleft"};
        String[] positions = {"Front", "Back"};

        // Select random orientation and position
        String orientation = orientations[random.nextInt(orientations.length)];
        String position = positions[random.nextInt(positions.length)];

        // Construct the command
        return String.format("PLACECARD %d %d %s %s", placingCardId, tableCardId, orientation, position);
    }

    private static List<String> fileParsing(String filePath) {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(filePath));
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return lines;
    }
}
