package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

import java.rmi.RemoteException;

public class AdduserCommandRunnable extends CommandRunnable{
    private String nickname;

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public void executeCLI() {
        try {
            try {
                if (checkNickname(messageFromCli[1])){
                    server.addConnectedPlayer(client, messageFromCli[1]);
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
        System.out.println("1");
        //this is runned only if I am in the client
        if (clientContainer != null){
            System.out.println("2");
            //if the user already connected
            int index = clientContainer.getViewModel().getPlayerIndex();
            if (index!= -1){
                System.out.println("3");
                String nick = clientContainer.getViewModel().getNickname(index);
                if (nick != null){
                    System.out.println("4");
                    if (!nick.equals(nicknameToCheck)){
                        System.out.println("5");
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
