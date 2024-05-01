package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.VirtualServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Client{

    public static void main(String[] args) throws IOException {
        String input = args[0];
        String host = args[1];
        String port = args[2];

        if (input.equalsIgnoreCase("socket")) {
            SocketClient.execute(host, port);
        } else {
            RmiClient.execute(host, port);
        }
    }
}
