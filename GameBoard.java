package game;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/*
Class to represent the chessboard.  Contains the necessary methods to solve
problem
*/
public class GameBoard extends Pane 
{
    private final int squaresPerSide;
    private final double squareDim;
    
    private final boolean randomSim;
    List<Integer> randomizer;//Collection to hold randomized move sequence
    
    private final Square[][] grid; //Grid to represent board
    private final List<Integer[]> movesList; //Available move directions
    private final List<Integer[]> movesDiagonalList; //Available diagonal move directions
    private final SecureRandom rnd = new SecureRandom();
    
    /*
    Three argument constructor
    @param sideLength Represents number of squares per side of board
    @param boardDim Represents the dimension of board
    @param randomSim  Boolean value to represent simulation type 
    */
    public GameBoard(int sideLength, double boardDim, boolean randomSim)
    {
        this.squaresPerSide = sideLength;
        squareDim = boardDim / sideLength;
        this.randomSim = randomSim;
        grid = new Square[this.squaresPerSide][this.squaresPerSide];
        
        movesList = new ArrayList<>();
        Integer[] upLeft = {-1, -1};
        Integer[] right = {1, 0};
        Integer[] downLeft = {-1, 1};
        Integer[] downRight = {1, 1};
        Integer[] upRight = {1, -1};
        Integer[] up = {0, -1};
        Integer[] left = {-1, 0};
        Integer[] down = {0, 1};
        
        if (randomSim)
        {
            randomizer = randomizeMoves();
        }
        else
        {
            randomizer = null;
        }
        
        movesList.add(upLeft);
        movesList.add(right);
        movesList.add(downLeft);
        movesList.add(downRight);
        movesList.add(upRight);
        movesList.add(left);
        movesList.add(up);
        movesList.add(down);
        
        movesDiagonalList = new ArrayList<>();
        movesDiagonalList.add(downLeft);
        movesDiagonalList.add(upRight);
        movesDiagonalList.add(downRight);
        movesDiagonalList.add(upLeft);
    }
    
    /*Creates Square objects to fill board.  Assigns object reference
    to array of Squares, adds object to ObservableList
    */
    public void setUpBoard()
    {
        for (int y = 0; y < squaresPerSide; y++)
        {
            for (int x = 0; x < squaresPerSide; x++)
            {
                double xPosPane = x * squareDim;
                double yPosPane = y * squareDim;
                Square square = new Square(squareDim, x, y, xPosPane, yPosPane);
                grid[x][y] = square;
                this.getChildren().add(square);
            }
        }
    }
    
    /*
    Recursive function to solve EightQueens problem
    @param x The x coordinate of the current search position
    @param y The y coordinate of the current search position
    */
    public void checkSquareSafety(int x, int y, int numQueensToAdd)
    {     
        Square square = grid[x][y]; //Accesses square object at specified coordinate
        if (numQueensToAdd > 0) //base case
        {
            if (!square.wasVisited() && !square.isOccupied())
            {
                square.setVisited(true);
                boolean rowClear = checkRow(y);
                boolean colClear = checkCol(x);
                boolean diagonalsClear = checkDiagonals(x, y);
                if (rowClear && colClear && diagonalsClear) //queen not threatened
                {
                    square.setOccupied(true);
                    addQueen(x, y);
                    numQueensToAdd = numQueensToAdd - 1; //variable decremented
                    checkSquareSafety(x, y, numQueensToAdd); //simplified problem
                }
                else //current position is not safe
                {
                    probe(x, y, numQueensToAdd); //search for next move  
                }                  
            }
            else //square previously visited or is occupied by Queen
            {
                probe(x, y, numQueensToAdd); //search for next move
            }
        }
    }
    
    /*
    Searches for adjacent square that is eligible to host a queen.  If no adjacent
    squares are eligible, the queen is "boxed in".  Method  then throws 
    IndexOutOfBoundsException. In the catch block, the findEligibleSquare method
    returns a non-adjacent square on board that is eligible, otherwise returns
    null if no squares on board are eligible.  This results in a NullPointerException
    being thrown.  If the probe is successful, the recursive function is called
    with new x-y coordinates passed as parameters
    @param x Current x coordinate
    @param y Current y coordinate
    @param numQueensToAdd Number of queens remaining until solution complete
    */
    public void probe(int x, int y, int numQueensToAdd)
    {
        boolean probeCompleted = false;
        int index = 0;
        int newXPos = 0;
        int newYPos = 0;
        while(!probeCompleted)
        {
            try
            {
                Integer[] move;
                if (randomSim)
                {
                    move = movesList.get(randomizer.get(index)); //throws IndexOutOfBoundsException (Queen is "boxed in")                    
                }
                else
                {
                    move = movesList.get(index);
                }
                newXPos = move[0] + x;
                newYPos = move[1] + y;
                
                if (newXPos >= 0 && newXPos < squaresPerSide && newYPos >=0 && newYPos <= squaresPerSide) //checks boundaries
                {
                    Square s = grid[newXPos][newYPos]; 
                    if (!s.wasVisited() && !s.isOccupied()) //safe square found
                    {
                        probeCompleted = true;
                    }                    
                }
                index++;
            }
            catch(IndexOutOfBoundsException ex)
            {
                probeCompleted = true;
                Square eligibleSquare = findEligibleSquare(); 
                newXPos = eligibleSquare.getPosX(); //throws NullPointerException caught by client (App)
                newYPos = eligibleSquare.getPosY();
            }
        }
        checkSquareSafety(newXPos, newYPos, numQueensToAdd);        
    }
    
    /*
    Returns null if no squares are eligible for move.  Simulation has failed.
    Otherwise returns reference to potentially safe square
    */
    public Square findEligibleSquare()
    {
        for (int y = 0; y < squaresPerSide; y++)
        {
            for (int x = 0; x < squaresPerSide; x++)
            {
                Square square = grid[x][y];
                if (!square.isOccupied() && !square.wasVisited())
                {
                    return square;
                }
            }
        }
        return null;
    }
    
    /*
    Generates random integers to be used as indexes
    */
    public final List<Integer> randomizeMoves()
    {
        List<Integer> list = new ArrayList();
        int counter = 0;
        while (counter < squaresPerSide)
        {
            int random = rnd.nextInt(squaresPerSide);
            if (!list.contains(random))
            {
                list.add(random);
            }
            counter++;
        }
        return list;
    }
    
    /*
    Returns true if all squares in current row are unoccupied, otherwise returns
    false
    @param rowNum The row number in the grid to check
    */
    public boolean checkRow(int rowNum)
    {
        boolean allClear = true;
        int colNum = 0;
        while (allClear && colNum < squaresPerSide)
        {
            Square square = grid[colNum][rowNum];
            if (square.isOccupied())
            {
                allClear = false;
            }
            colNum++;
        }
        return allClear;
    }
    
    /*
    Returns true if all squares in current column are unoccupied, otherwise returns
    false
    @param colNum The column number to check
    */
    public boolean checkCol(int colNum)
    {
        boolean allClear = true;
        int rowNum = 0;
        while (allClear && rowNum < squaresPerSide)
        {
            Square square = grid[colNum][rowNum];
            if(square.isOccupied())
            {
                allClear = false;
            }
            rowNum++;
        }
        return allClear;        
    }
    
    /*
    Returns true if all squares in current diagonal are unoccupied, otherwise returns
    false
    @param x The current column number
    @param y The current row number
    */
    public boolean checkDiagonals(int x, int y)
    {
        boolean allClear = true;
        int index = 0;
        
        while (allClear && index < movesDiagonalList.size())
        {
            boolean onBoard = true;
            Integer[] direction = movesDiagonalList.get(index);
            int squareToCheckX = x + direction[0];
            int squareToCheckY = y + direction[1];
            while (onBoard && allClear)
            {
                try
                {
                    Square s = grid[squareToCheckX][squareToCheckY]; //throws exception when "off board"
                    if (s.isOccupied())
                    {
                        allClear = false;
                    }
                    squareToCheckX = squareToCheckX + direction[0];
                    squareToCheckY = squareToCheckY + direction[1];
                }
                catch(ArrayIndexOutOfBoundsException ex)
                {
                    onBoard = false;
                }
            }
            index++;
        }   
        return allClear;
    }
    
    /*
    Adds Circle object to safe square
    @param x, y The grid coordinate of the safe square
    */
    public void addQueen(int x, int y)
    {
        double centerX = x * squareDim + (squareDim / 2);
        double centerY = y * squareDim + (squareDim / 2);
        double radius = (squareDim / 2) - 5;
        Circle queen = new Circle(centerX, centerY, radius, Color.BLUE);
        queen.setStroke(Color.BLACK);
        this.getChildren().add(queen);
    }
    
}
