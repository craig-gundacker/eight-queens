package game;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
Class to represent a square on the chessboard.  Encapsulates the x-y coordinate,
its occupied state, and whether or not square has been previously visited
*/
public class Square extends Rectangle
{
    private boolean occupied;
    private boolean visited;
    private final int x;
    private final int y;
    
    //Constructor
    public Square(double squareDim, int x, int y, double xPosPane, double yPosPane)
    {
        super(squareDim, squareDim);
        this.x = x;
        this.y = y;
        setFill(Color.GREY);
        setStroke(Color.BLACK);
        setX(xPosPane);
        setY(yPosPane);
        occupied = false;
        visited = false;
    }
    
    public void setOccupied(boolean status)
    {
        occupied = status;
    }
    
    public boolean isOccupied()
    {
        return occupied;
    }
    
    public void setVisited(boolean visited)
    {
        this.visited = visited;
    }
    
    public boolean wasVisited()
    {
        return visited;
    }

    public int getPosX() {
        return x;
    }

    public int getPosY() {
        return y;
    }
}
