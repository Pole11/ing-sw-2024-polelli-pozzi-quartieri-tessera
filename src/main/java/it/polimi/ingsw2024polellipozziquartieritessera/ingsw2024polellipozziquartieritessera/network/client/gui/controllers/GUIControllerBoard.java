package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.ViewModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class GUIControllerBoard extends GUIController {
    @FXML
    private VBox mainContainerBoard;
    @FXML private GridPane gridPaneContainerBoard;

    public GUIControllerBoard() {
        setFontSize(mainContainerBoard);
        initGridPaneContainerBoard();
        this.update();
    }

    public void printBoard() {
        Platform.runLater(() -> {
            ViewModel vm = getViewModel();
            ArrayList<ArrayList<Integer>> playerBoard = vm.getPlayerBoard(getViewModel().getPlayerIndex()); // the first arg is the index of the player to print the board of
            playerBoard = rotateBoard(playerBoard);
            resizeI(playerBoard);

            if (gridPaneContainerBoard != null) { gridPaneContainerBoard.getChildren().clear(); }

            for (int k = 0; k < getViewModel().getPlacingCardOrderMap(getViewModel().getPlayerIndex()).size(); k++) {
                for (int i = 0; i < playerBoard.size(); i++) {
                    for (int j = 0; j < playerBoard.getFirst().size(); j++) {
                        if (playerBoard.get(i).get(j) != -1 && getViewModel().getPlacingCardOrderMap(getViewModel().getPlayerIndex()).get(k).equals(playerBoard.get(i).get(j))) {
                            int ele = playerBoard.get(i).get(j);
                            ImageView tempImageView;
                            String imageUrl = null;
                            if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                                imageUrl = "/img/carte_fronte/" + ele + ".jpg";
                            } else {
                                imageUrl = "/img/carte_retro/" + ele + ".jpg";
                            }
                            tempImageView = createCardImageView(imageUrl, (int) (getWindowHeight() * 0.108));

                            gridPaneContainerBoard.setHalignment(tempImageView, HPos.CENTER);
                            gridPaneContainerBoard.setValignment(tempImageView, VPos.CENTER);
                            gridPaneContainerBoard.add(tempImageView, j, i);
                        }
                    }
                }
            }
        });
    }

    public void handleBackToGame(ActionEvent event) {
        goToScene("/fxml/game.fxml");
    }

    @FXML
    public void handleRestoreView(ActionEvent event) {
        Platform.runLater(() -> {
            GridPane boardGridPane = (GridPane) mainContainerBoard.lookup("#boardGridPane");
            boardGridPane.setTranslateX(0);
            boardGridPane.setTranslateY(0);
        });
    }

    private void initGridPaneContainerBoard() {
        int gridPaneVPadding = 50;
        int gridPaneHgap = 0;
        int gridPaneVgap = 0;


        Platform.runLater(() -> {
            int cornerWidth = (int) (getWindowHeight() * 0.0304);
            int cornerHeight = (int) (getWindowHeight() * 0.0310);

            ViewModel vm = getViewModel();
            ArrayList<ArrayList<Integer>> playerBoard = vm.getPlayerBoard(getViewModel().getPlayerIndex()); // the first arg is the index of the player to print the board of
            playerBoard = rotateBoard(playerBoard);
            resizeI(playerBoard);

            gridPaneContainerBoard.setHgap(gridPaneHgap); // Spacing orizzontale
            gridPaneContainerBoard.setVgap(gridPaneVgap); // Spacing verticale
            gridPaneContainerBoard.setPadding(new Insets(gridPaneVPadding)); // Margine di 20 pixel su tutti i lati

            addPanning(gridPaneContainerBoard);

            for (int i = 0; i < playerBoard.size(); i++) {
                gridPaneContainerBoard.getRowConstraints().add(new RowConstraints(getWindowHeight() * 0.108 - cornerHeight));
            }
            for (int i = 0; i < playerBoard.getFirst().size(); i++) {
                gridPaneContainerBoard.getColumnConstraints().add(new ColumnConstraints(((int) getWindowHeight() * 0.108*3/2) - cornerWidth));
            }
        });
    }

    @Override
    public void update() {
        System.gc();
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                if (getViewModel() != null) {
                    printBoard();
                }
            }
        });
    }
}

