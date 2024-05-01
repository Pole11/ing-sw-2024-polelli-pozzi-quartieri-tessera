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

    protected RmiClient(VirtualServer server) throws RemoteException {
        this.server = server;
    }

    private void run() {
        try {
            this.server.connectRmi(this);
            // CHIEDI SE VUOLE GUI O TUI
            Scanner scan = new Scanner(System.in);
            System.out.print("Do you GUI? [Y/n] ");
            String input = scan.nextLine();
            if (input != null && (input.equals("") || input.equalsIgnoreCase("y"))) {
                runGui();
            } else if (input.equalsIgnoreCase("n")) {
                runCli();
            } else {
                System.out.println("Please enter a valid input!");
            }
        } catch (RemoteException e) {

        }
    }

    private void runCli() throws RemoteException {
        System.out.println("Welcome to CODEX!");

        boolean running = true;
        Scanner scan = new Scanner(System.in);
        while (running) {
            System.out.print("> ");
            String line = scan.nextLine();
            String[] message = line.split(" ");
            if (line != null && !line.isEmpty() && !line.isBlank() && !line.equals("")) {
                this.manageInput(message);
            }
        }
    }

    private void manageInput(String[] message) throws RemoteException {
        switch(message[0].toLowerCase()) {
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
        System.out.print("]\n");

    }

    public void printError(String msg) {
        System.err.print("\nERROR FROM SERVER: " + msg + "\n");
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
