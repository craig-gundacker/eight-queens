package game;

import java.security.SecureRandom;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
Driver class for GameBoard
*/
public class App extends Application 
{
    
    private final static double BOARD_DIM = 400;
    private final static int SQUARES_PER_SIDE = 8;
    private final static int NUM_QUEENS = 8;
    private final static int START_X = 3;
    private final static int START_Y = 4;
    private final static SecureRandom RND = new SecureRandom();
    private final static TextArea TA_SIM_RESULTS = new TextArea();        
    private final static VBox VB_GAME_BOARD = new VBox(10); //Pane for chessboards
    private static int simulationNum = 0;
    
    @Override
    public void start(Stage primaryStage) 
    {
        BorderPane root = new BorderPane();
        root.setCenter(createSelectionForm());
        root.setRight(createSimPane());

        Scene scene = new Scene(root, BOARD_DIM * 1.80, BOARD_DIM * 1.2);
        
        primaryStage.setTitle("Eight Queens");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private Node createSelectionForm()
    {
        ToggleGroup toggleGroup = new ToggleGroup();
        RadioButton rbFixed = new RadioButton("Fixed start position, Fixed move sequence");
        rbFixed.setToggleGroup(toggleGroup);
        rbFixed.setSelected(true);
        RadioButton rbRandom = new RadioButton("Random start position, Random move sequence");
        rbRandom.setToggleGroup(toggleGroup);
        
        Button btnGo = new Button();
        btnGo.setText("Go");
        btnGo.setOnAction((ActionEvent event) -> {
            
            if (rbFixed.isSelected()) 
            {
                runSimulation(false);
            }
            else
            {
                runSimulation(true);
            }
        });
                 
        TA_SIM_RESULTS.setEditable(false);
        TA_SIM_RESULTS.setMinHeight(BOARD_DIM * .75);
        
        VBox vbForm = new VBox(10);
        vbForm.setAlignment(Pos.TOP_CENTER);
        vbForm.setPadding(new Insets(10, 10, 10, 10));
        vbForm.getChildren().addAll(new Label("Select Mode"), rbFixed, rbRandom, 
                                    btnGo, TA_SIM_RESULTS);
        return vbForm;
    }
    
    private Node createSimPane()
    {
        VB_GAME_BOARD.setAlignment(Pos.CENTER);
        VB_GAME_BOARD.setPadding(new Insets(10, 10, 10, 0));
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(VB_GAME_BOARD);
        scrollPane.setMinSize(BOARD_DIM, BOARD_DIM); 
        
        return scrollPane;
    }
    
    /*
    Assigns start coordinates and creates a GameBoard object. The while 
    loop continues until a solution to the problem is found.  The recursive method
    checkSquareSafety() throws NullPointerException if no solution can be found.
    This results in a new simulation being run
    @param randomSim Boolean value representing simulation type(random or fixed)
    */
    private static void runSimulation(boolean randomSim) 
    {   
        int startX;
        int startY;
        if (randomSim)
        {
            startX = RND.nextInt(NUM_QUEENS);
            startY = RND.nextInt(NUM_QUEENS);
        }
        else
        {
            startX = START_X;
            startY = START_Y;
        }
        
        int numAttempts = 1;
        boolean solutionFound = false;
        simulationNum++;
        TA_SIM_RESULTS.appendText("Simulation " + simulationNum+"\n");
        TA_SIM_RESULTS.setFont(Font.getDefault());
        TA_SIM_RESULTS.appendText("Searching for solution...\n\n");
        while (!solutionFound)
        {
            TA_SIM_RESULTS.appendText("Attempt: " + numAttempts+"\n");
            try
            {
                GameBoard gameBoard = new GameBoard(SQUARES_PER_SIDE, BOARD_DIM, randomSim);
                gameBoard.setUpBoard();
                gameBoard.checkSquareSafety(startX, startY, NUM_QUEENS);
                solutionFound = true; //base case reached
                TA_SIM_RESULTS.appendText("Attempt " + numAttempts + " succeeded\n\n");
                String simNum = "Simulation" + String.valueOf(simulationNum);
                VB_GAME_BOARD.getChildren().addAll(new Label(simNum),gameBoard);
            }
            catch (NullPointerException ex)
            {
                TA_SIM_RESULTS.appendText("Attempt " + numAttempts + " failed\n\n");
                numAttempts++;
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
    
}
