package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
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
            e.printStackTrace();
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
                Client.runGui(server, this);
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
    public void sendMessage(String msg) {
        // check if there is gui and if so show the message on the gui
        Client.printMessage(msg);
        //System.out.print("\nINFO FROM SERVER: " + msg + "\n> ");

    }

    @Override
    public void sendError(String msg) throws RemoteException {
        Client.printError(msg);
        //System.err.print("\nERROR FROM SERVER: " + msg + "\n> ");

    }

    @Override
    public void ping(String ping) throws RemoteException {
    }

    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        Client.nicknameUpdate(index, nickname);
    }

    @Override
    public void sendIndex(int index) throws RemoteException {

    }

    @Override
    public void printCard(int id1, Side side1, int id2, Side side2, int id3, Side side3) throws RemoteException {
        Client.printCard(id1, side1, id2, side2, id3, side3);
    }

    @Override
    public void printCard(int id1, Side side1, int id2, Side side2) throws RemoteException {
        Client.printCard(id1, side1, id2, side2);
    }

    @Override
    public void printCard(int id, Side side) throws RemoteException {
    }

    @Override
    public void changePhase(String nextGamePhaseString) {
        Client.changePhase(nextGamePhaseString);
        /*try {
            GamePhase nextGamePhase = GamePhase.valueOf(nextGamePhaseString);
            Client.changePhase(nextGamePhase);
        } catch (Exception e) {
            System.err.println("Invalid game phase");
        }*/
    }

}
