package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
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
        System.out.println("New Socket client connected");
        while ((line = input.readLine()) != null) {
            System.out.println(line);
            String[] message = line.split("; ");
            // TODO: check if message[0] exists
            // Read message and perform action
            Command command = null;
            try {
                command = Command.valueOf(message[0].toUpperCase());
            } catch(IllegalArgumentException e) {
                System.out.println("Invalid command: " + message[0]);
            }
            switch (command) {
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
                    Side side = null;
                    try {
                        side = Side.valueOf(message[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid side, please enter a valid side (Front / Back)");
                        return;
                    }
                    server.chooseInitialStarterSide(this, side);
                    break;
                case Command.CHOOSECOLOR:
                    // TODO: check if message[1] exists
                    Color color = null;
                    try {
                        color = Color.valueOf(message[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid color, please enter a valid color (Blue / Green / Yellow / Red)");
                        return;
                    }
                    server.chooseInitialColor(this, color);
                    break;
                case Command.CHOOSEOBJECTIVE: {
                    // TODO: check if message[1] exists
                    int cardId;
                    try {
                        cardId = Integer.parseInt(message[1]);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid card id, please enter a valid one");
                        return;
                    }
                    server.chooseInitialObjective(this, cardId);
                    break;
                }
                case Command.SHOWHAND:
                    // TODO: check if message[1] exists
                    server.showHand(this);
                    break;
                case Command.PLACECARD:
                    // TODO: check if message[1] exists
                    int placingCardId;
                    int tableCardId;
                    CornerPos tableCornerPos;
                    Side placingCardSide;
                    try {
                        placingCardId = Integer.parseInt(message[1]);
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid id of the placing card, please insert a valid id"));
                        return;
                    }
                    try {
                        tableCardId = Integer.parseInt(message[2]);
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid id of the table card, please insert a valid id"));
                        return;
                    }
                    try {
                        tableCornerPos = CornerPos.valueOf(message[3].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid corner, please insert a valid corner position (Upleft / Upright / Downleft / Downright)"));
                        return;
                    }
                    try {
                        placingCardSide = Side.valueOf(message[4].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println(("Invalid side, please insert a valid side (Front / Back)"));
                        return;
                    }
                    server.placeCard(this, placingCardId, tableCardId, tableCornerPos, placingCardSide);
                    break;
                case Command.DRAWCARD:
                    // TODO: check if message[1] exists
                    DrawType drawType;
                    try {
                        drawType = DrawType.valueOf(message[1]);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid draw option, please choose a valid option (SharedGold1 / SharedGold2 / DeckGold / SharedResource1 / SharedResource2 / DeckResource)");
                        return;
                    }
                    server.drawCard(this, drawType);
                    break;
                case Command.FLIPCARD: {
                    // TODO: check if message[1] exists
                    int cardId;
                    try {
                        cardId = Integer.parseInt(message[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("Please enter a valid card id");
                        return;
                    }
                    server.flipCard(this, cardId);
                    break;
                }
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

    @Override
    public void printCard(int id1, Side side1, int id2, Side side2, int id3, Side side3) throws RemoteException {
        view.printCard(id1, side1, id2, side2, id3, side3);
    }
    @Override
    public void printCard(int id1, Side side1, int id2, Side side2) throws RemoteException {
        view.printCard(id1, side1, id2, side2);
    }
    @Override
    public void printCard(int id, Side side) throws RemoteException {
        view.printCard(id, side);
    }

    @Override
    public void changePhase(String nextGamePhaseString) throws RemoteException {
        view.changePhase(nextGamePhaseString);
    }
}
