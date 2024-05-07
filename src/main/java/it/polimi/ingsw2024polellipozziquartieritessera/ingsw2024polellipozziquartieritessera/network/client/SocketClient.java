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

    public SocketClient(BufferedReader input, BufferedWriter output) {
        this.input = input;
        this.server = new ServerProxy(output);
    }

    public static void execute(String host, String portString) throws IOException {

        int port = Integer.parseInt(portString);
        Socket socketToServer = new Socket(host, port);

        InputStreamReader socketRx = new InputStreamReader(socketToServer.getInputStream());
        OutputStreamWriter socketTx = new OutputStreamWriter(socketToServer.getOutputStream());

        new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx)).run();
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
            Client.runGui();
        } else if (input.equalsIgnoreCase("n")) {
            this.server.connectRmi(this);
            Client.runCli(server, this);
        } else {
            System.out.println("Please enter a valid input!");
        }
    }

    private void runVirtualServer() throws IOException {
        System.out.println("Welcome to CODEX!");

        String line;
        // Read message type
        while ((line = input.readLine()) != null) {
            String[] message = line.split("; ");
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

    @Override
    public void printMessage(String msg) {
        System.out.print("\nINFO FROM SERVER: " + msg + "\n> ");
    }

    @Override
    public void printError(String msg) throws RemoteException {
        System.err.print("\nERROR FROM SERVER: " + msg + "\n> ");
    }

    @Override
    public void ping(String ping) throws RemoteException {

    }

    @Override
    public void printCard(int id1, Side side1, int id2, Side side2, int id3, Side side3) throws RemoteException {

    }

    @Override
    public void printCard(int id1, Side side1, int id2, Side side2) throws RemoteException {

    }

    @Override
    public void printCard(int id, Side side) throws RemoteException {

    }

}