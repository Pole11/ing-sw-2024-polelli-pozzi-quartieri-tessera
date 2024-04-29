package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.rmi;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.util.Scanner;

public class RmiClient extends UnicastRemoteObject implements VirtualView {
    final VirtualServer server;

    protected RmiClient(VirtualServer server, int id) throws RemoteException {
        this.server = server;
    }

    private void inputHandler() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("> ");
            String command = scan.nextLine();

            //if (command == setta username) {
            // server.setUsername();
            //}
        }
    }

    // main
}
