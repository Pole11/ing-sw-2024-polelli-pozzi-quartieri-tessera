package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.registry.Registry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CornerTest {
    // ALL METHODS TESTED

    @Test
    void testGetterSetter() throws IOException {
        Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
            @Override
            public Remote lookup(String name) throws RemoteException {
                return null;
            }

            @Override
            public void bind(String name, Remote obj) throws RemoteException {

            }

            @Override
            public void unbind(String name) throws RemoteException {

            }

            @Override
            public void rebind(String name, Remote obj) throws RemoteException {

            }

            @Override
            public String[] list() throws RemoteException {
                return new String[0];
            }
        });
        GameState g = new GameState(s);
        Populate.populate(g);

        CornerCard card = (CornerCard) g.getCard(40);
        Corner corner = card.getCorners().getFirst();

        corner.setLinkedCorner(corner.getLinkedCorner());
        corner.setCovered(corner.getCovered());
        assertNotNull(corner.getHidden());

        assertNotNull(corner.getElement());
        assertEquals(corner.getCard(), card.getId());

    }
}
