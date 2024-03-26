package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.model.cards.challenges;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ElementChallenge extends Challenge {
    private final ArrayList<Element> elements;

    public ElementChallenge(ArrayList<Element> elements){
        this.elements = elements;
    }
}