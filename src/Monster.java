import java.awt.*;

public class Monster extends Sprite
{
    //The monster's height should be a little bit smaller than the player's height.

    private int level; //Level 0 = Easy; Level 1 = Smart; Level 2 = Impossible

    public Monster(Image image, Rectangle rectangle, String name, int jumptimer, int jumpPower, int level)
    {
        super(image, rectangle, name, jumptimer, jumpPower);
        this.level = level;
    }//End of constructor
    public Monster(Rectangle rect, int level)
    {
        this.setJumpPower(5);
        this.setX(rect.getX());
        this.setY(rect.getX());
        this.setRectangle(rect);
        this.level = level;
    }
    public void nextMove(Player player)
    {
        double playerX = player.getRectangle().getX();
        double playerY = player.getRectangle().getY();
        double playerVelocity = player.getVelocityX();

        double monsterX = this.getRectangle().getX();
        double monsterY = this.getRectangle().getY();

        //Use players direction and velocity to determine the next move of the AI monster

        if(level == 0)
        {
            //If Monster is on the right side of Player
            if(monsterX - playerX > 500 || playerX - monsterX > 500)
            {
                return;
            }
        }
        else
        {
            //If Monster is on the right side of Player
            if(monsterX - playerX > 300 || playerX - monsterX > 300)
            {
                return;
            }
        }


        if(monsterX > 10)
        {
            this.setVelocityX(this.getVelocityX() * -1);
        }

        //when the monster is ahead of the player on the course
        if(playerX < monsterX)
        {
            if(player.isFacingRight() && playerVelocity > 0)
            {
               this.setVelocityX(1.5);

                if(level == 1 || level == 2)
                {
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
                    this.setVelocityX(-1);
                }
            }
            else {
                if (level == 0)
                {
                    //Chance (Max Negative Velocity)
                    this.setVelocityX(-1);
                }
                else
                {
                    //Chance (Max Negative Velocity)
                    this.setVelocityX(-1.5);
                }

            }
        }//End of Monster ahead of Player method

        //If Player is on the right side of Monster
        if(playerX > monsterX)
        {
            if(!player.isFacingRight())
            {
                this.setVelocityX(-1.5);

                if(level == 1 || level == 2) {
                    //Wait till he gets close (20 pixel approx. range)
                    if (playerX - monsterX < 20) {
                        //Stop and turn around
                        this.setVelocityX(1);

                        //Jump at him!
                        if (this.isGrounded()) {
                            this.setGrounded(false);
                            this.setJumpTimer(10);
                            this.setVelocityY(this.getJumpPower());
                        }
                    }
                }
                else
                {
                    this.setVelocityX(1);
                }
            }
            else
            {
                if (level == 0)
                {
                    //Chance (Max Positive Velocity)
                    this.setVelocityX(1);
                }
                else
                {
                    //Chance (Max Positive Velocity)
                    this.setVelocityX(1.5);
                }
            }
        }
        //If the player is jumping within range of monster, monster jumps to hit from underneath before player can jump over
        else if((playerY > monsterY && (((playerX > monsterX) && playerX - monsterX < 20) || (playerX < monsterX) && monsterX - playerX < 20)) && level == 2)
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

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}//End of monster class
