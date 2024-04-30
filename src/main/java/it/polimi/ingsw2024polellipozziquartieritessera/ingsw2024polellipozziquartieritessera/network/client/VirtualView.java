package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import java.rmi.*;

//clint che vede il server
public interface VirtualView extends Remote {
    public void printMsg(String msg) throws RemoteException;
    // qua metto le stampe da fare sul client, ci sarà un input handler
    // ask username
    // ask color
    // ask starter side
    // ask objective card
    // ask to place
    // ask if wants to flip
    // ask to draw
    // ask to wait
}
