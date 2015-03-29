import java.awt.*;

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
    public void nextMove(Player player)
    {
        double playerX = player.getRectangle().getX();
        double playerY = player.getRectangle().getY();
        double playerVelocity = player.getVelocityX();

        double monsterX = this.getRectangle().getX();
        double monsterY = this.getRectangle().getY();

        //Use players direction and velocity to determine the next move of the AI monster

        //If Monster is on the right side of Player
        if(monsterX - playerX > 100){}
        else if(playerX < monsterX)
        {
            if(player.isFacingRight() && playerVelocity > 0)
            {
               this.setVelocityX(1.5);

                //Wait till he gets close (20 pixel approx. range)
                if(monsterX - playerX < 20)
                {
                    //Stop and turn around
                    this.setVelocityX(-1);

                    //Jump at him!
                    if(this.isGrounded())
                    {
                        this.setGrounded(false);
                        this.setJumpTimer(10);
                        this.setVelocityY(this.getJumpPower());
                    }

                }

            }
            else
            {
                //Chance (Max Positive Velocity)
                this.setVelocityX(-1.5);
            }
        }
        //If Player is on the right side of Monster
        else if(playerX > monsterX)
        {
            if(!player.isFacingRight())
            {
                this.setVelocityX(-1.5);

                //Wait till he gets close (20 pixel approx. range)
                if(playerX - monsterX < 20)
                {
                    //Stop and turn around
                    this.setVelocityX(1);

                    //Jump at him!
                    if(this.isGrounded())
                    {
                        this.setGrounded(false);
                        this.setJumpTimer(10);
                        this.setVelocityY(this.getJumpPower());
                    }
                }
            }
            else
            {
                //Chase (Max Positive Velocity)
                this.setVelocityX(-1.5);
            }
        }
        //If the player is jumping within range of monster, monster jumps to hit from underneath before player can jump over
        else if(playerY > monsterY && (((playerX > monsterX) && playerX - monsterX < 20) || (playerX < monsterX) && monsterX - playerX < 20))
        {
            //Jump from underneath and kill player
            if(this.isGrounded())
            {
                this.setGrounded(false);
                this.setJumpTimer(10);
                this.setVelocityY(this.getJumpPower());
            }
        }
    }//End of nextMove method
}//End of monster class
