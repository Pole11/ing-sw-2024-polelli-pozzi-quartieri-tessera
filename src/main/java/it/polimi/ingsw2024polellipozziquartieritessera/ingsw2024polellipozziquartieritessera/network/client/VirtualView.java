package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.GamePhase;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;

import java.rmi.*;

//clint che vede il server
public interface VirtualView extends Remote {
    void printMessage(String message) throws RemoteException;
    void printError(String error) throws RemoteException;
    void ping(String ping) throws RemoteException;
    void printCard(int id1, Side side1, int id2, Side side2, int id3, Side side3) throws RemoteException;
    void printCard(int id1, Side side1, int id2, Side side2) throws RemoteException;
    void printCard(int id, Side side) throws RemoteException;
    void changePhase(String nextGamePhaseString) throws RemoteException;
}