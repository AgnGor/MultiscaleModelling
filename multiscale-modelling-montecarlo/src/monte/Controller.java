package monte;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

public class Controller implements Initializable{

    private int width, height, seeds;
    private boolean drawBool;
    private boolean runned;
    private Functions functions;
    private GraphicsContext graphicsContext;
    private List<Color> selectedGrains;

    @FXML
    private ChoiceBox<String> structTab;

    @FXML
    private ChoiceBox<String> energyTab;

    @FXML
    private TextField MCSTextField;

    @FXML
    private TextField widthTextField;

    @FXML
    private TextField heightTextField;

    @FXML
    private TextField seedsTextField;

    @FXML
    private Canvas canvas;

    @FXML
    private TextField hMinTextField;

    @FXML
    private TextField hMaxTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        graphicsContext = canvas.getGraphicsContext2D();
        structTab.getItems().addAll("Disable", "Substructure", "Dual-Phase");
        energyTab.getItems().addAll("Homogeneous", "Heterogeneus");
        selectedGrains = new ArrayList();
        structTab.setValue("Dual-Phase");
        energyTab.setValue("Heterogeneus");
    }

    @FXML
    public void startCanvas() {
        width = Integer.parseInt(widthTextField.getText());
        height = Integer.parseInt(heightTextField.getText());
        seeds = Integer.parseInt(seedsTextField.getText());
        canvas.setHeight(height);
        canvas.setWidth(width);
        graphicsContext.clearRect(0, 0, width, height); //filling with zeros
        functions = new Functions(width, height);
        functions.setSeeds(seeds); //picks places for seeds
        runned=false; //flag to reset selected grains
        drawBool =true; //doesnt show energy right away
        for (int i = 0; i < width; i++) { //paintingggg
            for (int j = 0; j < height; j++) {
                graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor);
                graphicsContext.fillRect(i, j, 1, 1);
            }
        }
    }

    @FXML
    public void cellularAutomataRunner() {
        int iter = 0;
        if(runned) {
            seeds = Integer.parseInt(seedsTextField.getText());
            functions.setSeeds(seeds);
            selectedGrains.clear();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor);
                    graphicsContext.fillRect(i, j, 1, 1);
                }
            }
            functions.clearBorders();
        }

        while (!functions.isBoardFull()) { //checking if there is any white point on board
            iter++;
            if (iter > 1000)
                break; //for mc->ca
            functions.iterationCA();
        }

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor);
                graphicsContext.fillRect(i, j, 1, 1);
            }
        }
        runned=true;
    }

    @FXML
    private void selectGrain(MouseEvent mouseEvent) { //click + point getting
        if (!selectedGrains.contains(functions.pointsTab[(int) mouseEvent.getX() + 1][(int) mouseEvent.getY() + 1].pointColor))
            selectedGrains.add(functions.pointsTab[(int) mouseEvent.getX() + 1][(int) mouseEvent.getY() + 1].pointColor);
    }

    @FXML
    private void removingGrains() { //gets arr of selected grains
        functions.removeGrains(selectedGrains, structTab.getValue());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor); //same as below
                graphicsContext.fillRect(i, j, 1, 1);
            }
        }
    }

    @FXML
    private void showBorders() { //checks if neighbour has different colour and paints borders
        functions.checkNeighbours();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor); //if not +1 then 1px up and from left
                graphicsContext.fillRect(i, j, 1, 1);
            }
        }
    }

    @FXML
    private void initializeMonteCarlo() {

        seeds = Integer.parseInt(seedsTextField.getText());
        functions.setSeeds(seeds);
        selectedGrains.clear();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor);
                graphicsContext.fillRect(i, j, 1, 1);
            }
        }
        functions.clearBorders();

        functions.initMC(seeds);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor);
                graphicsContext.fillRect(i, j, 1, 1);
            }
        }
        runned=true;
    }

    @FXML
    private void runMonteCarlo() {
        int iteration = Integer.parseInt(MCSTextField.getText());
        for (int i = 0; i < iteration; i++) {
            functions.iterationMC();
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor);
                graphicsContext.fillRect(i, j, 1, 1);
            }
        }
    }


    @FXML
    private void showEnergy() {
        if (!drawBool) {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    graphicsContext.setFill(functions.pointsTab[i + 1][j + 1].pointColor);
                    graphicsContext.fillRect(i, j, 1, 1);
                }
            }
        } else {
            int hmax;
            int hmin = Integer.parseInt(hMinTextField.getText());

            if( energyTab.getValue().equalsIgnoreCase("Homogeneous"))
                hmax=hmin;
            else
                hmax=Integer.parseInt(hMaxTextField.getText());

                functions.countEnergy(hmin, hmax);


            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    int h = functions.pointsTab[i + 1][j + 1].h;
                    Color c;

                    if (h == hmin)
                        c = Color.rgb(0,86,199);//rgb(135,213,0)   rgb(0,86,199) rgb(0,124,255)
                    else if (h == 0)
                        c = Color.rgb(0,124,255);
                    else if(h==hmax)
                        c = Color.rgb(135,213,0);
                    else {
                        c=Color.rgb(255,0,0);
                    }

                    graphicsContext.setFill(c);
                    graphicsContext.fillRect(i, j, 1, 1);
                }
            }
        }
        drawBool = !drawBool;
    }
}
