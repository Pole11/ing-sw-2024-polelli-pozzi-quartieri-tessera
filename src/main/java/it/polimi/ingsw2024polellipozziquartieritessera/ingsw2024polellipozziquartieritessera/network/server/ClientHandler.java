package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

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
        // Read message type
        while ((line = input.readLine()) != null) {
            String[] message = line.split(", ");
            // Read message and perform action
            switch (message[0]) {
                case "ADDUSER":
                    System.out.println("nicname received: " + message[1]);
                    server.addConnectedPlayer(this, message[1]);
                    break;
                case "STARTGAME":
                    server.startGame();
                case "CHOOSESTARTERSIDE":
                    server.chooseInitialStarterSide(this, message[1]);
                case "CHOOSEINITIALCOLOR":
                    server.chooseInitialColor(this, message[1]);
                    break;
                case "CHOOSEINITIALOBJECTIVE":
                    server.chooseInitialObjective(this, message[1]);
                    break;
                case "PLACECARD":
                    server.placeCard(this, message[1], message[2], message[3], message[4]);
                    break;
                case "DRAWCARD":
                    server.drawCard(this, message[1]);
                    break;
                case "FLIPCARD":
                    server.flipCard(this, message[1]);
                    break;
                case "OPENCHAT":
                    server.openChat();
                    break;
                default:
                    System.err.println("[INVALID MESSAGE]");
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
}
