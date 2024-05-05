package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;

import java.util.*;
import java.rmi.server.*;
import java.rmi.registry.*;
import java.rmi.*;

public class RmiClient extends UnicastRemoteObject implements VirtualView {
    private final VirtualServer server;

    // TODO: it is public for testing purpose
    public RmiClient(VirtualServer server) throws RemoteException {
        this.server = server;
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

    private void run() {
        try {
            System.out.println("Welcome to CODEX!");

            // CHIEDI SE VUOLE GUI O TUI
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
        } catch (RemoteException e) {

        }
    }

    @Override
    public void printMessage(String msg) {
        System.out.print("\nINFO FROM SERVER: " + msg + "\n The possible commands are: [");
        Client.printCommands();
    }

    @Override
    public void printError(String msg) {
        System.err.print("\nERROR FROM SERVER: " + msg + "\n> ");
    }

    @Override
    public void ping(String ping) throws RemoteException {

    }
}
