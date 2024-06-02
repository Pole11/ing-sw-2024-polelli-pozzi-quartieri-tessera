package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.commandRunnable;

public class showHandCommandRunnable extends  CommandRunnable{


    @Override
    public void executeCLI() {
        try {
            clientContainer.getCliController().showHand();
        } catch (Exception r){
            //A SCHIUMA: Ricordati di aggiungere \n> \
            System.err.println("Error: could not get hand\n> ");
        }
    }

    @Override
    public void executeGUI() {
        ;
    }
}
