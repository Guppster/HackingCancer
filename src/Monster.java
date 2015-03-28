import java.awt.*;

/**
 * Created by Gurpreet Singh on 3/28/2015.
 */
public class Monster extends Sprite
{
    public Monster(Image image, Rectangle rectangle, String name, int jumptimer, int jumpPower)
    {
        super(image, rectangle, name, jumptimer, jumpPower);
    }//End of constructor

    public String nextMove(Player player)
    {
        //Use players direction and velocity to determine the next move of the AI monster
        return "R";
    }
}//End of monster class
