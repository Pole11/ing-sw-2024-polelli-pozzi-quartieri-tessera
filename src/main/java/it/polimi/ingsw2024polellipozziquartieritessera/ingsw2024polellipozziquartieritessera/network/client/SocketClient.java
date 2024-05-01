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
        runCli();
    }

    private void runVirtualServer() throws IOException {
        String line;
        // Read message type
        while ((line = input.readLine()) != null) {
            String[] message = line.split(", ");
            switch (message[0]) {
                case "MESSAGE" -> this.printMessage(message[1]);
                case "ERROR" -> this.printError(message[1]);
                default -> System.err.println("[INVALID MESSAGE]");
            }
        }
    }

    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                this.manageInput(message);
            }
        }
    }

    private void manageInput(String[] message) throws RemoteException {
        switch (message[0].toLowerCase()) {
            case Config.HELP_STRING:
                this.printCommands();
                break;
            case Config.ADDUSER_STRING:
                server.addConnectedPlayer(this, message[1]);
                break;
            case Config.START_STRING:
                server.startGame();
                break;
            case Config.CHOOSESTARTER_STRING:
                server.chooseInitialStarterSide(this, message[1]);
                break;
            case Config.CHOOSECOLOR_STRING:
                server.chooseInitialColor(this, message[1]);
                break;
            case Config.CHOOSEOBJECTIVE_STRING:
                server.chooseInitialObjective(this, message[1]);
                break;
            case Config.PLACECARD_STRING:
                server.placeCard(this, message[1], message[2], message[3], message[4]);
                break;
            case Config.DRAWCARD_STRING:
                server.drawCard(this, message[1]);
                break;
            case Config.FLIPCARD_STRING:
                server.flipCard(this, message[1]);
                break;
            case Config.OPENCHAT_STRING:
                server.openChat();
                break;
            case Config.ADDMESSAGE_STRING:
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
        System.out.print("]\n");
    }

    @Override
    public void printMessage(String msg) {
        System.out.print("\nINFO FROM SERVER: " + msg + "\n The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + ", ");
        });
        System.out.print("]\n");
    }


    @Override
    public void printError(String msg) throws RemoteException {
        System.err.print("\nERROR FROM SERVER: " + msg + "\n");

    }

    public static void execute(String host, String portString) throws IOException {

        int port = Integer.parseInt(portString);
        Socket serverSocket = new Socket(host, port);

        InputStreamReader socketRx = new InputStreamReader(serverSocket.getInputStream());
        OutputStreamWriter socketTx = new OutputStreamWriter(serverSocket.getOutputStream());

        new SocketClient(new BufferedReader(socketRx), new BufferedWriter(socketTx)).run();
    }

}