package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Color;
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
import javafx.scene.shape.Circle;

import java.util.ArrayList;
import java.util.Stack;

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
            int playerId = (Integer) getParamsMap().get("playerId");
            ArrayList<ArrayList<Integer>> playerBoard = vm.getPlayerBoard(playerId); // the first arg is the index of the player to print the board of

            playerBoard = rotateBoard(playerBoard);
            resizeI(playerBoard);

            if (gridPaneContainerBoard != null) { gridPaneContainerBoard.getChildren().clear(); }

            //boolean first = true;
            for (int k = 0; k < getViewModel().getPlacingCardOrderMap(playerId).size(); k++) {
                for (int i = 0; i < playerBoard.size(); i++) {
                    for (int j = 0; j < playerBoard.getFirst().size(); j++) {
                        if (playerBoard.get(i).get(j) != -1 && getViewModel().getPlacingCardOrderMap(playerId).get(k).equals(playerBoard.get(i).get(j))) {
                            int ele = playerBoard.get(i).get(j);

                            ImageView tempImageView;
                            String imageUrl = null;
                            if (getViewModel().getPlacedCardSide(ele) == Side.FRONT) {
                                imageUrl = "/img/carte_fronte/" + ele + ".jpg";
                            } else {
                                imageUrl = "/img/carte_retro/" + ele + ".jpg";
                            }
                            tempImageView = createCardImageView(imageUrl, (int) (getWindowHeight() * 0.108));

                            /*if (first) {
                                Circle coloredCircle = new Circle(0,0, getWindowHeight()*0.015);
                                if (getViewModel().getColorsMap(playerId).equals(Color.BLUE)) { coloredCircle.setFill(javafx.scene.paint.Color.BLUE); }
                                else if (getViewModel().getColorsMap(playerId).equals(Color.GREEN)) { coloredCircle.setFill(javafx.scene.paint.Color.GREEN); }
                                else if (getViewModel().getColorsMap(playerId).equals(Color.RED)) { coloredCircle.setFill(javafx.scene.paint.Color.RED); }
                                else if (getViewModel().getColorsMap(playerId).equals(Color.YELLOW)) { coloredCircle.setFill(javafx.scene.paint.Color.YELLOW); }

                                coloredCircle.setTranslateX(getWindowHeight() * 0.025);
                                coloredCircle.setTranslateY(getWindowHeight() * 0.020);

                                StackPane tempStackPane = new StackPane(tempImageView, coloredCircle);
                                if (playerId == 0) {
                                    Circle blackCircle = new Circle(0,0, getWindowHeight()*0.01);
                                    tempStackPane.getChildren().add(blackCircle);
                                } else {

                                }

                                gridPaneContainerBoard.add(tempStackPane, j, i);
                                first = false;
                            } else {
                                gridPaneContainerBoard.add(tempImageView, j, i);
                            }*/

                            gridPaneContainerBoard.add(tempImageView, j, i);
                            gridPaneContainerBoard.setHalignment(tempImageView, HPos.CENTER);
                            gridPaneContainerBoard.setValignment(tempImageView, VPos.CENTER);
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
            int playerId = (Integer) getParamsMap().get("playerId");

            ViewModel vm = getViewModel();
            ArrayList<ArrayList<Integer>> playerBoard = vm.getPlayerBoard(playerId); // the first arg is the index of the player to print the board of
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

