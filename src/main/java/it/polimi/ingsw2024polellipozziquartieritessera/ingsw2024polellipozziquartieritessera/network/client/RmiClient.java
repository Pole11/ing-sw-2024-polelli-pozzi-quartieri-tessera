package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;

import java.util.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.rmi.*;

public class RmiClient extends UnicastRemoteObject implements VirtualView {
    final VirtualServer server;

    private RmiClient(VirtualServer server) throws RemoteException {
        this.server = server;
    }

    private void run() {
        try {
            System.out.println("Welcome to CODEX!");

            // CHIEDI SE VUOLE GUI O TUI
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

        } catch (RemoteException e) {

        }
    }

    private void runCli() {
        boolean running = true;
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter a nickname to start, with the command ADDUSER <nickname>");
        while (running) {
            System.out.print("> ");
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                try {
                    this.manageInput(message);
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void manageInput(String[] message) throws RemoteException {
        try {
            Command.valueOf(message[0].toUpperCase());
        } catch(IllegalArgumentException e) {
            System.err.println("INVALID COMMAND");
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
            default:
                System.err.println("[INVALID MESSAGE] \n >");
                break;
        }
    }

    private void runGui() throws RemoteException {

    }

    private void printCommands() {
        System.out.print("The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + ", ");
        });
        System.out.print("]\n");
    }

    public void printMessage(String msg) {
        System.out.print("\nINFO FROM SERVER: " + msg + "\n The possible commands are: [");
        Arrays.stream(Command.values()).forEach(e->{
            System.out.print(e + ", ");
        });
        System.out.print("]\n>");
    }

    public void printError(String msg) {
        System.err.print("\nERROR FROM SERVER: " + msg + "\n>");
    }

    @Override
    public void ping(String ping) throws RemoteException {

    }

    public static void execute(String host, String port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, Integer.parseInt(port));
            VirtualServer server = (VirtualServer) registry.lookup("VirtualServer");
            (new RmiClient(server)).run(); // crea la mia copia sul server
        } catch (RemoteException | NotBoundException e) {
            System.out.println("An error occurred while executing RmiClient!");
        }
    }
}
