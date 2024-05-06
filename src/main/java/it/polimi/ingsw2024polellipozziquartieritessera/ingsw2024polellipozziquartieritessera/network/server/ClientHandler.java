package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Command;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.rmi.RemoteException;


public class ClientHandler implements VirtualView {
    final Server server;
    final BufferedReader input;
    final VirtualView view;

    public ClientHandler(Server server, BufferedReader input, BufferedWriter output) {
        this.server = server;
        this.input = input;
        this.view = new ClientProxy(output);
    }

    public void runVirtualView() throws IOException {
        String line;
        System.out.println("New Socket client connected");
        while ((line = input.readLine()) != null) {
            System.out.println(line);
            String[] message = line.split("; ");
            // TODO: check if message[0] exists
            // Read message and perform action
            switch (Command.valueOf(message[0].toUpperCase())) {
                case Command.ADDUSER:
                    // TODO: check if message[1] exists
                    server.addConnectedPlayer(this, message[1]);
                    break;
                case Command.SHOWNICKNAME:
                    // TODO: check if message[1] exists
                    server.showNickname(this);
                    break;
                case Command.START:
                    // TODO: check if message[1] exists
                    server.startGame(this);
                    break;
                case Command.CHOOSESTARTER:
                    // TODO: check if message[1] exists
                    server.chooseInitialStarterSide(this, message[1]);
                    break;
                case Command.CHOOSECOLOR:
                    // TODO: check if message[1] exists
                    server.chooseInitialColor(this, message[1]);
                    break;
                case Command.CHOOSEOBJECTIVE:
                    // TODO: check if message[1] exists
                    server.chooseInitialObjective(this, message[1]);
                    break;
                case Command.SHOWHAND:
                    // TODO: check if message[1] exists
                    server.showHand(this);
                    break;
                case Command.PLACECARD:
                    // TODO: check if message[1] exists
                    server.placeCard(this, message[1], message[2], message[3], message[4]);
                    break;
                case Command.DRAWCARD:
                    // TODO: check if message[1] exists
                    server.drawCard(this, message[1]);
                    break;
                case Command.FLIPCARD:
                    // TODO: check if message[1] exists
                    server.flipCard(this, message[1]);
                    break;
                case Command.OPENCHAT:
                    // TODO: check if message[1] exists
                    server.openChat();
                    break;
                default:
                    System.err.println("[INVALID MESSAGE FROM CLIENT]");
                    break;
            }
        }
    }

    @Override
    public void printMessage(String message) throws RemoteException {
        view.printMessage(message);
    }

    @Override
    public void printError(String error) throws RemoteException {
        view.printError(error);
    }

    @Override
    public void ping(String ping) throws RemoteException {
        view.ping(ping);
    }
}
