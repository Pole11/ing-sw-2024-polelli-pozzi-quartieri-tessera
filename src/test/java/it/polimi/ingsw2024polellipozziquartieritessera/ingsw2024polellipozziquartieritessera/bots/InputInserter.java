package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.Client;

import java.io.IOException;
import java.io.PipedOutputStream;

public class InputInserter implements Runnable {
    public static boolean sideChosen = false;
    public static boolean secretChosen = false;
    private final PipedOutputStream pos;
    private final Client client;

    public InputInserter(PipedOutputStream pos, Client client) {
        this.pos = pos;
        this.client = client;
    }

    @Override
    public void run() {
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
                Thread.sleep(BotConfig.SPEED);

                String command = CommandGenerator.generateCommand(client);
                if (command != null) {
                    sendCommand(command + "\n");
                }

            } catch (InterruptedException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendCommand(String command) throws IOException {
        System.out.print(command);
        pos.write(command.getBytes());
        pos.flush();
    }
}

