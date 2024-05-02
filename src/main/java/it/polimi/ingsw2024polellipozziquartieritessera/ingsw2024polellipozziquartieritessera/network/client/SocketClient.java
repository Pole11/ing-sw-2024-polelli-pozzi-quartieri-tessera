package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;



import java.io.*;
import java.net.*;
import java.rmi.*;
import java.util.*;



public class SocketClient implements VirtualView {
    final BufferedReader input;
    final ServerProxy server;

    protected SocketClient(BufferedReader input, BufferedWriter output) {
        this.input = input;
        this.server = new ServerProxy(output);
    }

    private void run() throws RemoteException {
        new Thread(() -> {
            try {
                runVirtualServer();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        Scanner scan = new Scanner(System.in);
        System.out.print("Do you GUI? [Y/n] ");
        String input = scan.nextLine();
        if (input != null && (input.equals("") || input.equalsIgnoreCase("y"))) {
            runGui();
        } else if (input.equalsIgnoreCase("n")) {
            this.server.connectRmi(this);
            runCli();
        } else {
            System.out.println("Please enter a valid input!");
        }
    }

    private void runVirtualServer() throws IOException {
        System.out.println("Welcome to CODEX!");

        String line;
        // Read message type
        while ((line = input.readLine()) != null) {
            String[] message = line.split(", ");
            switch (message[0]) {
                case "MESSAGE":
                    this.printMessage(message[1]);
                    break;
                case "ERROR":
                    this.printError(message[1]);
                    break;
                case "PING":
                    this.ping(message[1]);
                    break;
                default:
                    System.err.println("[5xx INVALID MESSAGE FROM SERVER]");
                    break;
            }
        }
    }

    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter a nickname to start, with the command ADDUSER <nickname>");
        while (true) {
            System.out.print("> ");
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                this.manageInput(message);
            }
        }
    }

    private void runGui() throws RemoteException {

    }

    private void manageInput(String[] message) throws RemoteException {
        try {
            Command.valueOf(message[0].toUpperCase());
        } catch(IllegalArgumentException e) {
            System.err.println("[4xx INVALID COMMAND]");
            return;
        }

        switch (Command.valueOf(message[0].toUpperCase())) {
            case Command.HELP:
                this.printCommands();
                break;
            case Command.ADDUSER:
                server.addConnectedPlayer(this, message[1]);
                break;
            case Command.START:
                server.startGame();
                break;
            case Command.CHOOSESTARTER:
                server.chooseInitialStarterSide(this, message[1]);
                break;
            case Command.CHOOSECOLOR:
                server.chooseInitialColor(this, message[1]);
                break;
            case Command.CHOOSEOBJECTIVE:
                server.chooseInitialObjective(this, message[1]);
                break;
            case Command.PLACECARD:
                server.placeCard(this, message[1], message[2], message[3], message[4]);
                break;
            case Command.DRAWCARD:
                server.drawCard(this, message[1]);
                break;
            case Command.FLIPCARD:
                server.flipCard(this, message[1]);
                break;
            case Command.OPENCHAT:
                server.openChat();
                break;
            case Command.ADDMESSAGE:
                //sarà sbagliato perchè splitta sugli spazi
                server.addMessage(this, message[1]);
                break;
        }
    }


    private void printCommands() {
        System.out.print("The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + ", ");
        });
        System.out.print("]\n>");
    }

    @Override
    public void printMessage(String msg) {
        System.out.print("\nINFO FROM SERVER: " + msg + "\n The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + ", ");
        });
        System.out.print("]\n>");
    }


    @Override
    public void printError(String msg) throws RemoteException {
        System.err.print("\nERROR FROM SERVER: " + msg + "\n");

    }

    @Override
    public void ping(String ping) throws RemoteException {

    }

    public static void execute(String host, String portString) throws IOException {

        int port = Integer.parseInt(portString);
        Socket serverSocket = new Socket(host, port);

        InputStreamReader socketRx = new InputStreamReader(serverSocket.getInputStream());
        OutputStreamWriter socketTx = new OutputStreamWriter(serverSocket.getOutputStream());

        new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx)).run();
    }

}