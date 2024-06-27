package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;

import java.io.*;

public class ClientBot {
    public static Client client;
    public static boolean sideChosen = false;
    public static boolean secretChosen = false;

    public static void main(String[] args) throws IOException {

        String input = args[0];
        String host = args[1];
        String port = args[2];
        String myIp = args.length > 3 ? args[3] : "";

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        System.setIn(pis);

        Thread starter = new Thread(() -> {
            try {
                client = new Client(input, host, port, myIp);
                client.startClient();
            } catch (IOException e) {
                throw new RuntimeException("Failed to start client", e);
            }
        });

        Thread inputInserter = new Thread(() -> {
            try {
                Thread.sleep(2000); // Give the client some time to start
                pos.write(("n\n").getBytes());
                System.out.println("n");
                pos.flush();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }

            while (true) {
                try {
                    Thread.sleep(BotConfig.SPEED);

                    String command = CommandGenerator.generateCommand();
                    if (command != null) {
                        sendCommand(command + "\n", pos);
                    }

                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        starter.start();
        inputInserter.start();
    }

    private static void sendCommand(String command, PipedOutputStream pos) throws IOException {
        System.out.print(command);
        pos.write(command.getBytes());
        pos.flush();
    }
}