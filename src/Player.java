import java.awt.*;

/**
 * Created by Gurpreet Singh on 3/28/2015.
 */
public class Player extends Sprite
{
    public Player(Image image, Rectangle rectangle, String name, int jumptimer, int jumpPower)
    {
        super(image, rectangle, name, jumptimer, jumpPower);
    }//End of constructor
    public Player(Rectangle rect)
    {
        this.setJumpPower(5);
        this.setX(rect.getX());
        this.setY(rect.getX());
        this.setRectangle(rect);
    }
}//End of player class
