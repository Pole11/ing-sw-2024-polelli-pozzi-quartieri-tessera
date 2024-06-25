package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;
import java.util.Arrays;

public class AdduserCommandRunnable extends CommandRunnable{
    private String nickname;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void executeCLI() {
        try {
            try {
                String username = String.join(" ", Arrays.copyOfRange(messageFromCli, 1, messageFromCli.length));
                if (checkNickname(username)){
                    server.addConnectedPlayer(client, username);
                } else {
                    System.err.print("To reconnect use your previous nickname\n> ");
                }
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
            if (checkNickname(nickname)){
                server.addConnectedPlayer(client, this.nickname);
                if (clientContainer != null){
                    clientContainer.resetViewModel();
                }
            } else {
                guiController.setServerError("To reconnect use your previous nickname");
            }
        } catch (RemoteException e) {
            this.serverDisconnectedGUI();
        }
    }

    @Override
    public void executeHelp() {
        System.out.print("ADDUSER [nickname]\nthe command allows the player to join a new game and set his new nickname or, if he previously disconnected, to reconnect to the game\n> ");
    }

    private boolean checkNickname(String nicknameToCheck){
        //this is runned only if I am in the client

        if (clientContainer != null){
            System.out.println(nicknameToCheck);
            System.out.println(clientContainer.getViewModel().getNickname(clientContainer.getViewModel().getPlayerIndex()));
            //if the user already connected
            int index = clientContainer.getViewModel().getPlayerIndex();
            if (index!= -1){
                String nick = clientContainer.getViewModel().getNickname(index);
                if (nick != null){
                    if (!nick.equals(nicknameToCheck)){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
