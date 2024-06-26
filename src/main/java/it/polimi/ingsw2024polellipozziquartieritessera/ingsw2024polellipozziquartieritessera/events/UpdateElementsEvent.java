package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.events;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class UpdateElementsEvent extends Event {
    private final int playerIndex;
    private final Element element;
    private final int numberOfElements;

    public UpdateElementsEvent(GameState gameState, ArrayList<VirtualView> clients, int playerIndex, Element element, int numberOfElements) {
        super(gameState, clients);
        this.playerIndex = playerIndex;
        this.element = element;
        this.numberOfElements = numberOfElements;
    }

    @Override
    public void execute() {
        for (VirtualView client : clients) {
            try {
                client.updateElement(playerIndex, element, numberOfElements);
            } catch (RemoteException e) {
                this.playerDisconnected(client);
            }
        }
    }
}
