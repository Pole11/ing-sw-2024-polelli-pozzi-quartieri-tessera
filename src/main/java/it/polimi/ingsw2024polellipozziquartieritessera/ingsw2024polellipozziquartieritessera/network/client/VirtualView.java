package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;

import java.rmi.*;

//clint che vede il server
public interface VirtualView extends Remote {
    void ping(String ping) throws RemoteException;
    void sendMessage(String message) throws RemoteException;
    void sendError(String error) throws RemoteException;
    void sendIndex(int index) throws RemoteException;
    void nicknameUpdate(int index, String nickname) throws RemoteException;
    void changePhase(String nextGamePhaseString) throws RemoteException;
}