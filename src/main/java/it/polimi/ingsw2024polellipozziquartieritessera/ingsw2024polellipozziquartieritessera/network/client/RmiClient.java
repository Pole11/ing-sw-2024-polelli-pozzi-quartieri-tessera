package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

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
                runTui();
            } else {
                System.out.println("Please enter a valid input!");
            }
        } catch (RemoteException e) {

        }
    }

    private void runTui() throws RemoteException {
        System.out.println("Welcome to CODEX!");
        System.out.print("Please enter your username > ");
        Scanner scan = new Scanner(System.in);
        String nickname = scan.nextLine();

        this.server.addConnectedPlayer(this, nickname);

    }

    private void runGui() throws RemoteException {

    }

    public void printMsg(String msg) {
        System.out.println(msg);
    }

    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", 1234);
            VirtualServer server = (VirtualServer)registry.lookup("VirtualServer");
            (new RmiClient(server)).run(); // crea la mia copia sul server
        } catch (RemoteException e) {

        } catch (NotBoundException e) {

        }
    }
    

    // main
}
