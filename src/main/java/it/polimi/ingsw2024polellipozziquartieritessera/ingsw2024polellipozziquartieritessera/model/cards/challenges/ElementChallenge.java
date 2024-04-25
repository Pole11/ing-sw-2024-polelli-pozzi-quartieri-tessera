package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;

public class ElementChallenge extends Challenge {
    private final ArrayList<Element> elements;

    public ElementChallenge(ArrayList<Element> elements){
        // should we put a copy in here???
        this.elements = elements;
    }

    public ArrayList<Element> getElements() {
        return this.elements;
    }
}