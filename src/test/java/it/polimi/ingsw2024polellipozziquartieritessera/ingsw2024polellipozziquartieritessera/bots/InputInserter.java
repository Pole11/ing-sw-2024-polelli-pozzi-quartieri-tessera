package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import java.io.IOException;
import java.io.PipedOutputStream;

import static it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.ClientBot.client;

public class InputInserter implements Runnable {
    public static boolean sideChosen = false;
    public static boolean secretChosen = false;
    private PipedOutputStream pos;

    public InputInserter(PipedOutputStream pos) {
        this.pos = pos;
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

