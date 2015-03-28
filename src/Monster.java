import java.awt.*;

/**
 * Created by Gurpreet Singh on 3/28/2015.
 */
public class Monster extends Sprite
{
    //The monster's height should be a little bit smaller than the player's height.

    public Monster(Image image, Rectangle rectangle, String name, int jumptimer, int jumpPower)
    {
        super(image, rectangle, name, jumptimer, jumpPower);
    }//End of constructor
    public Monster(Rectangle rect)
    {
        this.setJumpPower(5);
        this.setX(rect.getX());
        this.setY(rect.getX());
        this.setRectangle(rect);
    }
    public String nextMove(Player player)
    {
        double playerX = player.getRectangle().getX();
        double playerY = player.getRectangle().getY();
        double playerVelocity = player.getVelocityX();

        double monsterX = this.getRectangle().getX();
        double monsterY = this.getRectangle().getY();

        double playerJumpAttackableRight = playerX + 20;
        double playerJumpAttackableLeft = playerX - 20;


        double differenceBetweenSprites = monsterX - playerX;

        //Use players direction and velocity to determine the next move of the AI monster
        if(playerX < monsterX)
        {
            if(player.isFacingRight() && playerVelocity > 0)
            {
                //Fake Run Away

                //Wait till he gets close (20 pixel approx. range)
                if(monsterX - playerX < 20)
                {
                    //Turn Around
                    //Jump at him!
                }

            }
            else
            {
                //Chase (Max Negative Velocity)
            }
        }
        else if(playerX > monsterX)
        {
            if(!player.isFacingRight())
            {
                //Fake Run Away

                //Wait till he gets close (20 pixel approx. range)
                if(playerX - monsterX < 20)
                {
                    //Turn Around
                    //Jump at him!
                }
            }
            else
            {
                //Chase (Max Positive Velocity)
            }
        }
        else if(playerY > monsterY && (((playerX > monsterX) && playerX - monsterX < 20) || (playerX < monsterX) && monsterX - playerX < 20))
        {
            //JUMP AND REKT HIM BRUH
        }
        else if(player.getRectangle().intersects(this.getRectangle()))
        {
            //KILL HIM
        }

        return "MOVE";
    }
}//End of monster class
