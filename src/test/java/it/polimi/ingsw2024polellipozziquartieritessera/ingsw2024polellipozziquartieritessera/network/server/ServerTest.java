package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.RmiClient;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.SocketClient;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    @Test
    public void GameIsFullTest() throws IOException, NotBoundException {
        String host = "127.0.0.1";
        String portRmi = "1234";
        String portSocket = "1235";

        Registry registry = LocateRegistry.getRegistry(host, Integer.parseInt(portRmi));
        VirtualServer server = (VirtualServer) registry.lookup("VirtualServer");

        RmiClient rmiClient1 = new RmiClient(server);
        RmiClient rmiClient2 = new RmiClient(server);
        RmiClient rmiClient3 = new RmiClient(server);
        RmiClient rmiClient4 = new RmiClient(server);
        RmiClient rmiClient5 = new RmiClient(server);

        Socket socketToServer = new Socket(host, Integer.parseInt(portSocket));

        InputStreamReader socketRx1 = new InputStreamReader(socketToServer.getInputStream());
        OutputStreamWriter socketTx1 = new OutputStreamWriter(socketToServer.getOutputStream());
        SocketClient socketClient1 = new SocketClient(new BufferedReader(socketRx1), new BufferedWriter(socketTx1));

        InputStreamReader socketRx2 = new InputStreamReader(socketToServer.getInputStream());
        OutputStreamWriter socketTx2 = new OutputStreamWriter(socketToServer.getOutputStream());
        SocketClient socketClient2 = new SocketClient(new BufferedReader(socketRx2), new BufferedWriter(socketTx2));

        InputStreamReader socketRx3 = new InputStreamReader(socketToServer.getInputStream());
        OutputStreamWriter socketTx3 = new OutputStreamWriter(socketToServer.getOutputStream());
        SocketClient socketClient3 = new SocketClient(new BufferedReader(socketRx3), new BufferedWriter(socketTx3));

        InputStreamReader socketRx4 = new InputStreamReader(socketToServer.getInputStream());
        OutputStreamWriter socketTx4 = new OutputStreamWriter(socketToServer.getOutputStream());
        SocketClient socketClient4 = new SocketClient(new BufferedReader(socketRx4), new BufferedWriter(socketTx4));

        InputStreamReader socketRx5 = new InputStreamReader(socketToServer.getInputStream());
        OutputStreamWriter socketTx5 = new OutputStreamWriter(socketToServer.getOutputStream());
        SocketClient socketClient5 = new SocketClient(new BufferedReader(socketRx5), new BufferedWriter(socketTx5));


        // check with 4+ RMI

        // check with 4+ socket

        // check with 3 RMI 2 socket

        // check with 2 RMI 3 socket
    }
}
