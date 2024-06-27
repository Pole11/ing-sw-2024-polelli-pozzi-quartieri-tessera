package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.*;

import java.io.*;
import java.util.*;
import static it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.bots.ClientBot.client;

public class ClientBot {
    static Client client;

    public static void main(String[] args) throws IOException {
        String input = "";
        String host = "";
        String port = "";
        String myIp = "";
        try {
            input = args[0];
            host = args[1];
            port = args[2];
            try {
                myIp = args[3];
            } catch (IndexOutOfBoundsException e) {
                if (input.equals("rmi")) System.out.println("If you are encountering an error with rmi, please provide the ip address of the current machine");
            }
        } catch(Exception e) {
            System.out.println("This is the bot, please remember to use the right parameters: [rmi/socket] [server ip] [server port (depends on rmi and socket)] [optional with rmi: ip address of the current machine]");
            return;
        }

        PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream(pos);
        System.setIn(pis);

        String finalHost = host;
        String finalInput = input;
        String finalPort = port;
        String finalMyIp = myIp;
        Thread starter = new Thread(() -> {
            try {
                client = new Client(finalInput, finalHost, finalPort, finalMyIp);
                client.startClient();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Thread inputInserter = new Thread(new InputInserter(pos, client));
        starter.start();
        inputInserter.start();
    }
}