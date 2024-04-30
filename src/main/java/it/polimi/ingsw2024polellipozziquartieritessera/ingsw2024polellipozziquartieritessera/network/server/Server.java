package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.Config;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;


public class Server implements VirtualServer {
    final Controller controller;
    final List<VirtualView> clients = new ArrayList();

    public Server(Controller controller) {
        this.controller = controller;
    }

    public static void main(String argv[]) throws IOException, WrongStructureConfigurationSizeException, NotUniquePlayerNicknameException, NotUniquePlayerColorException, NotUniquePlayerException {
        GameState gameState = null;
        boolean store = Populate.existStore(); //FA persistance
        if (store){
            // takes data from store
        } else {
            gameState = Populate.populate();
        }
        // listen to rmi
        VirtualServer engine = new Server(new Controller(gameState));
        System.setProperty("java.rmi.server.hostname", "127.0.0.1");
        String name = "VirtualServer";
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(engine, 0);
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(name, stub);

        // listen to socket
    }

    @Override
    public void connectRmi(VirtualView vv) throws RemoteException {
        if (clients.size() < Config.MAX_PLAYERS) {
            System.out.println("new client connected");
        } else {
            vv.printMsg("The game is full");
            System.out.println("A new client tried to connect but the game is full");
        }
    }

    @Override
    public void addConnectedPlayer(VirtualView client, String nickname) {
        try {
            this.controller.addPlayer(nickname);
            clients.add(client);
            for (VirtualView clientIterator : this.clients) {
                clientIterator.printMsg("User " + nickname + " has ben added to game!");
            }
        } catch (NotUniquePlayerNicknameException e) {
            try {
                client.printMsg("The nickname already exists :/");
            } catch (RemoteException ex) {
                throw new RuntimeException(ex);
            }
        } catch (RemoteException e) {

        }
    }

    @Override
    public void startGame() {

    }

    @Override
    public void chooseInitialStarterSide(int playerIndex, Side side) throws RemoteException {

    }

    @Override
    public void chooseInitialColor(int playerIndex, Color color) throws RemoteException {

    }

    @Override
    public void chooseInitialObjective(int playerIndex, int cardId) throws RemoteException {

    }

    @Override
    public void placeCard(int playerIndex, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException {

    }

    @Override
    public void drawCard(DrawType drawType) throws RemoteException {

    }

    @Override
    public void flipCard(int playerIndex, int cardId) throws RemoteException {

    }

    @Override
    public void openChat() {

    }

    @Override
    public void addMessage(Player player, String content) {

    }

}