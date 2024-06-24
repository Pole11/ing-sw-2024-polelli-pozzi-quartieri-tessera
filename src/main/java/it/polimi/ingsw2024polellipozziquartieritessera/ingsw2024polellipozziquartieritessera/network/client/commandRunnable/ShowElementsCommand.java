package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowElementsCommand extends CommandRunnable{
    @Override
    public void executeCLI() {
        try {
            if (messageFromCli.length < 2) {
                clientContainer.getCliController().showElements();
            } else {
                Integer playerIndex = Integer.parseInt(messageFromCli[1]);
                clientContainer.getCliController().showElements(playerIndex);
            }
        } catch (Exception r){
            System.err.print(r + "\nplease insert a valid player index\n >");
        }

    }

    @Override
    public void executeGUI() {

    }

    @Override
    public void executeHelp() {
        System.out.print("SHOWELEMENTS\nthe command shows the resources and items currently visible on the player's board\n>");
    }
}
