package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;

import java.io.*;
import java.util.*;
import static it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.ClientBot.client;

public class ClientBot {
    static Client client;

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

        Thread inputInserter = new Thread(new InputInserter(pos));
        starter.start();
        inputInserter.start();
    }
}