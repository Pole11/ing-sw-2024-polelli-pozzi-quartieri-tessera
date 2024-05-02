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
            String[] message = line.split(", ");
            // Read message and perform action
            switch (Command.valueOf(message[0].toUpperCase())) {
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
                    System.err.println("[INVALID MESSAGE FROM CLIENT]");
                    break;
            }
        }
        //System.out.println("player disconnected");
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
