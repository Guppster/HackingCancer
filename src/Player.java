import java.awt.*;

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
        this.setFacingRight(true);
    }
}//End of player class
