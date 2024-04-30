package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums;


public enum GamePhase {
    // TODO: check if the order is wrong
    NICKNAMEPHASE,
    COLORPHASE,
    STARTPHASE,// choose one of the two objective cards from the two options and the side of the starter card
    CHOOSESTARTERSIDEPHASE,
    CHOOSEOBJECTIVEPHASE,
    MAINPHASE, // does it really need a description?
    ENDPHASE,  // last round(s)
    FINALPHASE // calculate points and declare winner
}