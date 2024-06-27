package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Allows clients to add a message to the chat
 */
public class AddMessageCommandRunnable extends CommandRunnable {
private String content;

    public void setContent(String content) { this.content = content; }

    @Override
    public void executeCLI() {
        try {
            try{
                server.addMessage(client, Arrays.stream(messageFromCli)
                        .skip(1)
                        .collect(Collectors.joining(" ")));
                System.out.print("Message sent\n> ");
            } catch (RemoteException e) {
                serverDisconnectedCLI();
            }
        } catch(IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            System.err.print("INVALID COMMAND\n> ");
        }
    }

    @Override
    public void executeGUI() {
        try {
            server.addMessage(client, content);
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }
    }

    @Override
    public void executeHelp() {
        System.out.print("ADDMESSAGE\n content\nThe command add a message to the global chat with all players\n> ");
    }
}
