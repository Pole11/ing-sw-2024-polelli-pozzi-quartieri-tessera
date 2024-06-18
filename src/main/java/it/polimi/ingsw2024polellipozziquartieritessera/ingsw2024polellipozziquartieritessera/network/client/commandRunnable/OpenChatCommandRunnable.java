package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class OpenChatCommandRunnable extends CommandRunnable {
    @Override
    public void executeCLI() {
        clientContainer.getCliController().showChat();
    }

    @Override
    public void executeGUI() {

    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWCHAT\nthe command shows the chat from the beginning of the game\n>");
    }
}
