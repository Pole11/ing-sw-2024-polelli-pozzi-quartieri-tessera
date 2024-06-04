package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class ShowBoardCommandRunnable extends CommandRunnable {
    @Override
    public void executeCLI() {
        try {
            Integer playerIndex = Integer.parseInt(messageFromCli[1]);
            clientContainer.getCliController().showBoard(playerIndex);
        } catch (Exception r){
            System.err.print(r + "\nplease insert a valid index");
        }

    }

    @Override
    public void executeGUI() {
        ;
    }
}
