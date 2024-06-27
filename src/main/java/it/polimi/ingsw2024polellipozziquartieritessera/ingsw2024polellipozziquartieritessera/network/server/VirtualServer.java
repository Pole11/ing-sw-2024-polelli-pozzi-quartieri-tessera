package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.Player;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.*;

/**
 * Interface representing the virtual server for client communication.
 * This interface defines methods for managing client connections and game interactions.
 */
public interface VirtualServer extends Remote {
    /**
     * Connects a client to the server via RMI.
     *
     * @param vv the VirtualView instance representing the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void connectRmi(VirtualView vv) throws RemoteException;

    /**
     * Adds a connected player to the server.
     *
     * @param client the VirtualView instance representing the client.
     * @param nickname the nickname of the player.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void addConnectedPlayer(VirtualView client, String nickname) throws RemoteException;

    /**
     * Starts the game for the specified client.
     *
     * @param client the VirtualView instance representing the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void startGame(VirtualView client) throws RemoteException;

    /**
     * Allows the client to choose the initial starter side.
     *
     * @param client the VirtualView instance representing the client.
     * @param side the initial starter Side chosen by the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void chooseInitialStarterSide(VirtualView client, Side side) throws RemoteException;

    /**
     * Allows the client to choose the initial color.
     *
     * @param client the VirtualView instance representing the client.
     * @param color the initial Color chosen by the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void chooseInitialColor(VirtualView client, Color color) throws RemoteException;

    /**
     * Allows the client to choose an initial objective card.
     *
     * @param client the VirtualView instance representing the client.
     * @param cardId the ID of the objective card chosen by the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void chooseInitialObjective(VirtualView client, int cardId) throws RemoteException;

    /**
     * Allows the client to place a card on the table.
     *
     * @param client the VirtualView instance representing the client.
     * @param placingCardId the ID of the card to be placed.
     * @param tableCardId the ID of the card already on the table.
     * @param tableCornerPos the CornerPos where the card should be placed.
     * @param placingCardSide the Side of the card being placed.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void placeCard(VirtualView client, int placingCardId, int tableCardId, CornerPos tableCornerPos, Side placingCardSide) throws RemoteException;

    /**
     * Allows the client to draw a card.
     *
     * @param client the VirtualView instance representing the client.
     * @param drawType the DrawType specifying the type of draw action.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void drawCard(VirtualView client, DrawType drawType) throws RemoteException;

    /**
     * Allows the client to flip a card.
     *
     * @param client the VirtualView instance representing the client.
     * @param cardId the ID of the card to be flipped.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void flipCard(VirtualView client, int cardId) throws RemoteException;

    /**
     * Adds a message from the client to the server.
     *
     * @param client the VirtualView instance representing the client.
     * @param content the content of the message.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void addMessage(VirtualView client, String content) throws RemoteException;

    /**
     * Sends a ping message from the client to the server.
     *
     * @param client the VirtualView instance representing the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void ping(VirtualView client) throws RemoteException;

    /**
     * Sends a pong message from the client to the server in response to a ping.
     *
     * @param client the VirtualView instance representing the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void pong(VirtualView client) throws RemoteException;

    /**
     * Indicates that the game has ended for the specified client.
     *
     * @param client the VirtualView instance representing the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public void gameEnded(VirtualView client) throws RemoteException;
}