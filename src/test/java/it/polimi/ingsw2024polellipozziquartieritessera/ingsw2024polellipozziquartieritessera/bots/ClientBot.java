package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.*;

public class ClientBot {
    static String lastCommand = null;
    public static void main(String[] args) throws IOException {
        String input = args[0];
        String host = args[1];
        String port = args[2];

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        System.setIn(pis);

        Thread starter = new Thread(() -> {
            try {
                (new Client()).startClient(input, host, port);
            } catch (IOException e) {
                System.out.println(lastCommand);
                throw new RuntimeException(e);
            }
        });

        Thread inputInserter = new Thread(() -> insertInputs(pos));
        starter.start();
        inputInserter.start();
    }

    private static void insertInputs(PipedOutputStream pos) {
        String path = new File("").getAbsolutePath();
        String filePath=  path + "/src/test/java/it/polimi/ingsw2024polellipozziquartieritessera/ingsw2024polellipozziquartieritessera/bots/bot_commands.txt";
        List<String> commands = fileParsing(filePath);
        int index = 0;
        Collections.shuffle(commands);

        try {
            Thread.sleep(2000); // Give the client some time to start
        } catch (InterruptedException e) {
            System.out.println(lastCommand);
            throw new RuntimeException(e);
        }

        try {
            pos.write(("n\n").getBytes());
            pos.flush();
        } catch (IOException e) {
            System.out.println(lastCommand);
            throw new RuntimeException(e);
        }

        while (true) {
            try {
                Thread.sleep(10); // Wait 1 second between inputs

                if (index == commands.size()) {
                    index = 0;
                }

                String chooseCommand = commands.get(index);

                if (Objects.equals(chooseCommand, "PLACECARD")){
                    chooseCommand = generateRandomPlaceCardCommand();
                }

                if (Objects.equals(chooseCommand, "PLACESTARTER")){
                    chooseCommand = generateRandomPlaceStarterCommand();
                }

                lastCommand = chooseCommand;

                pos.write((chooseCommand +"\n").getBytes());
                pos.flush();

                index++;
            } catch (InterruptedException | IOException e) {
                System.out.println(lastCommand);
                throw new RuntimeException(e);
            }
        }
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
