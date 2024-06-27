package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class InternetCheck {

    public static boolean isConnectedToInternet() {
        try (Socket socket = new Socket()) {
            // Try to connect to a well-known host (Google DNS) with a timeout
            socket.connect(new InetSocketAddress("8.8.8.8", 53), 2000);
            System.out.println("Connected to Internet");
            return true;
        } catch (IOException e) {
            System.out.println("Not connected to Internet");
            return false;
        }
    }
}