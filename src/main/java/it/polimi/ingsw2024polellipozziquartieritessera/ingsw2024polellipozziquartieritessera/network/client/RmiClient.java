package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

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
    public void changePhase(GamePhase nextGamePhase) {
        Client.changePhase(nextGamePhase);
    }

    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {

    }

    @Override
    public void updateAddEnd(int playerIndex, int cardIndex, Side side) throws RemoteException {

    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex, Side side) throws RemoteException {

    }

    @Override
    public void updateBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos) throws RemoteException {

    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {

    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {

    }

    @Override
    public void updateHandSide(int playerIndex, Side side) throws RemoteException {

    }

    @Override
    public void updatePoints(int playerIndex, int points) throws RemoteException {

    }

    @Override
    public void updateSecretObjective(int objectiveCardId1, int objectiveCardId2) throws RemoteException {

    }

    @Override
    public void updateSharedObjective(int sharedObjectiveCardId1, int sharedObjectiveCardId2) throws RemoteException {

    }

    @Override
    public void updateStarteCard(int cardId1) throws RemoteException {

    }

}
