package it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.controllers;

import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.enums.Side;
import it.polimi.ingsw2024polellipozziquartieritessera.ingsw2024polellipozziquartieritessera.network.client.gui.GUIApplication;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.spreadsheet.Grid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class GUIControllerBoard extends GUIController {
    @FXML private VBox mainContainerBoard;

    public GUIControllerBoard() {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                if (getArgs() != null) {
                    printBoard(Integer.parseInt(getArgs()[0]));
                }
            }
        });
    }

    public void printBoard(int playerId) {
        Platform.runLater(new Runnable() { // da quello che ho capito qui ci metto quello che voglio far fare al thread della UI
            @Override
            public void run() {
                int boardImageWidth = 100;
                int gridPaneVPadding = 50;
                int gridPaneHgap = -30;
                int gridPaneVgap = -35;
                int rowOffset = 1;
                int colOffset = 1;

                ArrayList<ArrayList<Integer>> playerBoard = new ArrayList<>();
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 2, 6, null)));
                playerBoard.add(new ArrayList<>(Arrays.asList(3, 1, 4, 5)));
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 7, 76, null)));
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 71, 73, null)));
                playerBoard.add(new ArrayList<>(Arrays.asList(null, 23, 56, null)));

                HashMap<Integer, Side> placedSideMap = new HashMap<>();
                placedSideMap.put(2, Side.FRONT);
                placedSideMap.put(6, Side.BACK);
                placedSideMap.put(3, Side.FRONT);
                placedSideMap.put(1, Side.FRONT);
                placedSideMap.put(4, Side.BACK);
                placedSideMap.put(5, Side.FRONT);
                placedSideMap.put(7, Side.FRONT);
                placedSideMap.put(76, Side.FRONT);
                placedSideMap.put(71, Side.FRONT);
                placedSideMap.put(73, Side.FRONT);
                placedSideMap.put(23, Side.FRONT);
                placedSideMap.put(56, Side.BACK);

                GridPane gridPane = new GridPane();
                mainContainerBoard.getChildren().add(gridPane);
                gridPane.setHgap(gridPaneHgap); // Spacing orizzontale
                gridPane.setVgap(gridPaneVgap); // Spacing verticale
                gridPane.setPadding(new Insets(gridPaneVPadding)); // Margine di 20 pixel su tutti i lati
                for(ArrayList<Integer> row : playerBoard) {
                    for (Integer ele : row) {
                        if (ele != null) {
                            ImageView tempImageView;
                            if (placedSideMap.get(ele) == Side.FRONT) {
                                tempImageView = createCardImageView("/img/carte_fronte/" + ele + ".jpg", boardImageWidth);
                            } else {
                                tempImageView = createCardImageView("/img/carte_retro/" + ele + ".jpg", boardImageWidth);
                            }
                            tempImageView.getStyleClass().add("clickable");
                            tempImageView.setOnMousePressed(mouseEvent -> {
                                // handle place card
                            });
                            gridPane.add(tempImageView, rowOffset + row.indexOf(ele), colOffset + playerBoard.indexOf(row));
                        }
                    }
                }
            }
        });
    }

    @FXML
    public void handleBackToGame(ActionEvent event) {
        goToScene("/fxml/game.fxml");
        System.out.println("ciao");
    }

}
