package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.rmi;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.VirtualServer;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.VirtualView;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.util.Scanner;

public class RmiClient extends UnicastRemoteObject implements VirtualView {
    final VirtualServer server;

    protected RmiClient(VirtualServer server, int id) throws RemoteException {
        this.server = server;
    }
    

    // main
}
