package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;
import java.util.Scanner;

public class Client{

    public static void main(String[] args) throws IOException {
        String input = args[0];
        String host = args[1];
        String port = args[2];

        if (input.equalsIgnoreCase("socket")) {
            SocketClient.execute(host, port);
        } else {
            RmiClient.execute(host, port);
        }
    }

    public static void runCli(VirtualServer server, VirtualView client) {
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter a nickname to start, with the command ADDUSER <nickname>");
        while (running) {
            System.out.print("> ");
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                try {
                    manageInput(server, message, client);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void runGui(){

    }


    private static void manageInput(VirtualServer server, String[] message, VirtualView client) throws RemoteException {
        try {
            Command.valueOf(message[0].toUpperCase());
        } catch(IllegalArgumentException e) {
            System.err.println("INVALID COMMAND\n> ");
            return;
        }

        switch (Command.valueOf(message[0].toUpperCase())) {
            case Command.HELP:
                printCommands();
                break;
            case Command.ADDUSER:
                try {
                    server.addConnectedPlayer(client, message[1]);
                } catch(IllegalArgumentException e) {
                    System.err.println("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.START:
                server.startGame();
                break;
            case Command.CHOOSESTARTER:
                try {
                    server.chooseInitialStarterSide(client, message[1]);
                } catch(IllegalArgumentException e) {
                    System.err.println("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.CHOOSECOLOR:
                try {
                    server.chooseInitialColor(client, message[1]);
                } catch(IllegalArgumentException e) {
                    System.err.println("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.CHOOSEOBJECTIVE:
                try {
                    server.chooseInitialObjective(client, message[1]);
                } catch(IllegalArgumentException e) {
                    System.err.println("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.PLACECARD:
                try {
                    server.placeCard(client, message[1], message[2], message[3], message[4]);
                } catch(IllegalArgumentException e) {
                    System.err.println("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.DRAWCARD:
                try {
                    server.drawCard(client, message[1]);
                } catch(IllegalArgumentException e) {
                    System.err.println("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.FLIPCARD:
                try {
                    server.flipCard(client, message[1]);
                } catch(IllegalArgumentException e) {
                    System.err.println("INVALID COMMAND\n> ");
                    return;
                }
                break;
            case Command.OPENCHAT:
                server.openChat();
                break;
            default:
                System.err.println("[INVALID MESSAGE]\n> ");
                break;
        }
    }


    // dopo questa esecuzione, in RMI torna subito a runCli, in Socket no, quindi in
    //Socket non viene stampato subito >, mantre in RMI si
    public static void printCommands() {
        System.out.print("The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + ", ");
        });
        System.out.print("]\n");
    }
}
