package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.controller.Controller;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Populate;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Element;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.exceptions.*;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.GameState;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.server.Server;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.rmi.*;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class StarterCardTest {

    @Test
    void testGetterSetter() {
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
            StarterCard card = (StarterCard) g.getCard(84);

            assertEquals(card.getCenterResources().getFirst(), Element.ANIMAL);
            assertEquals(card.getCenterResources().size(), 2);
        } catch (WrongStructureConfigurationSizeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetResourceType() throws NotUniquePlayerNicknameException, NotUniquePlayerColorException, WrongStructureConfigurationSizeException, IOException {
        // setup
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
        StarterCard card = (StarterCard) g.getCard(84);
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

            StarterCard card = (StarterCard) g.getCard(84);

            // verify if the front elements are right
            ArrayList<Element> elements;
            elements = card.getUncoveredElements(Side.FRONT);
            assertEquals(elements.getFirst(), Element.ANIMAL);
            assertEquals(elements.toArray().length, 2);

            // verify if the back elements are right
            elements = card.getUncoveredElements(Side.BACK);
            assertEquals(elements.getFirst(), Element.PLANT);
            assertEquals(elements.toArray().length, 4);

            // try cover the corner and see if there are no uncovered elements
            for (Corner corner : card.getCorners(Side.BACK)){
                corner.setCovered(true);
            }

            assertTrue(card.getUncoveredElements(Side.BACK).isEmpty());
        } catch (WrongStructureConfigurationSizeException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
