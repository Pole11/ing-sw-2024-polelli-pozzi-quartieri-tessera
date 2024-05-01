package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import java.rmi.*;

//clint che vede il server
public interface VirtualView extends Remote {
    void printMessage(String message) throws RemoteException;
    void printError(String error) throws RemoteException;
}
