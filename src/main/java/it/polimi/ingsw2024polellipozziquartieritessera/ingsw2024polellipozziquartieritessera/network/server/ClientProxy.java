package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

public class ClientProxy implements VirtualView {
    final PrintWriter output;

    public ClientProxy(BufferedWriter output) {
        this.output = new PrintWriter(output);
    }

    @Override
    public void printMessage(String message) throws RemoteException {
        output.println("MESSAGE, " + message);
        output.flush();
    }

    public void printError(String error) throws RemoteException {
        output.println("ERROR, " + error);
        output.flush();
    }

    @Override
    public void ping(String ping) throws RemoteException {
        output.println("PING, " + ping);
        output.flush();
    }
}