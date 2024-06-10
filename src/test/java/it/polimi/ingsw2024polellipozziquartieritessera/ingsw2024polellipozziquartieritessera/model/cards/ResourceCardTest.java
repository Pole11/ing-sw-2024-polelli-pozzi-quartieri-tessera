package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerColorException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.NotUniquePlayerNicknameException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.WrongStructureConfigurationSizeException;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceCardTest {
    @Test
    void testGetterSetter(){
        // setup
        try {
            Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
                @Override
                public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                    return null;
                }

                @Override
                public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

                }

                @Override
                public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

                }

                @Override
                public void rebind(String name, Remote obj) throws RemoteException, AccessException {

                }

                @Override
                public String[] list() throws RemoteException, AccessException {
                    return new String[0];
                }
            });
            GameState g = new GameState(s);
            Populate.populate(g);
            ResourceCard card = (ResourceCard) g.getCard(32);

            assertEquals(card.getResourceType(), Element.INSECT);
        } catch (WrongStructureConfigurationSizeException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    void testGetUncoveredElements() {
        // setup
        try {
            Server s = new Server(new ServerSocket(), new Controller(), new Registry() {
                @Override
                public Remote lookup(String name) throws RemoteException, NotBoundException, AccessException {
                    return null;
                }

                @Override
                public void bind(String name, Remote obj) throws RemoteException, AlreadyBoundException, AccessException {

                }

                @Override
                public void unbind(String name) throws RemoteException, NotBoundException, AccessException {

                }

                @Override
                public void rebind(String name, Remote obj) throws RemoteException, AccessException {

                }

                @Override
                public String[] list() throws RemoteException, AccessException {
                    return new String[0];
                }
            });
            GameState g = new GameState(s);
            Populate.populate(g);
            ResourceCard card = (ResourceCard) g.getCard(32);

            // verify if the return elements are the right ones
            ArrayList<Element> elements;
            elements = card.getUncoveredElements(Side.FRONT);
            assertEquals(elements.getFirst(), Element.INSECT);
            assertEquals(elements.toArray().length, 2);


            // try cover the corner and see if there are no uncovered elements
            for (Corner corner : card.getCorners(Side.FRONT)){
                corner.setCovered(true);
            }

            assertTrue(card.getUncoveredElements(Side.FRONT).isEmpty());

            // try with the back
            assertEquals(card.getUncoveredElements(Side.BACK).getFirst(), Element.INSECT);
            assertEquals(card.getUncoveredElements(Side.BACK).toArray().length, 1);
        } catch (WrongStructureConfigurationSizeException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
