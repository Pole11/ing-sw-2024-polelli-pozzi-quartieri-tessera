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
            Command command = null;
            try {
                command = Command.valueOf(message[0].toUpperCase());
            } catch(IllegalArgumentException e) {
                System.out.println("Invalid command: " + message[0]);
            }
            switch (command) {
                case Command.ADDUSER:
                    server.addConnectedPlayer(this, message[1]);
                    break;
                case Command.START:
                    server.startGame(this);
                    break;
                case Command.CHOOSESTARTER:
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
                case Command.PLACECARD:
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
                    server.openChat();
                    break;
                default:
                    System.err.println("[INVALID MESSAGE FROM CLIENT]");
                    break;
            }
        }
    }

    @Override
    public void ping(String ping) throws RemoteException {
        view.ping(ping);
    }

    @Override
    public void sendMessage(String message) throws RemoteException {
        view.sendMessage(message);
    }

    @Override
    public void sendError(String error) throws RemoteException {
        view.sendError(error);
    }

    @Override
    public void nicknameUpdate(int index, String nickname) throws RemoteException {
        view.nicknameUpdate(index, nickname);
    }

    @Override
    public void sendIndex(int index) throws RemoteException {
        view.sendIndex(index);
    }

    @Override
    public void changePhase(GamePhase nextGamePhase) throws RemoteException {
        view.changePhase(nextGamePhase);
    }

    @Override
    public void start() throws RemoteException {

    }

    @Override
    public void connectionInfo(int playerIndex, boolean connected) throws RemoteException {

    }

    @Override
    public void updateAddHand(int playerIndex, int cardIndex) throws RemoteException {

    }

    @Override
    public void updateRemoveHand(int playerIndex, int cardIndex) throws RemoteException {

    }

    @Override
    public void updatePlayerBoard(int playerIndex, int placingCardId, int tableCardId, CornerPos existingCornerPos, Side side) throws RemoteException {

    }

    @Override
    public void updateColor(int playerIndex, Color color) throws RemoteException {

    }

    @Override
    public void updateCurrentPlayer(int currentPlayerIndex) throws RemoteException {

    }

    @Override
    public void updateHandSide(int cardIndex, Side side) throws RemoteException {

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
    public void updateStarterCard(int playerIndex, int cardId1, Side side) throws RemoteException {

    }
}
