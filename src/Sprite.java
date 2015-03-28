import java.awt.*;

/**
 * Created by Gurpreet Singh on 3/28/2015.
 */
public class Sprite
{

    private Image image;                        //The image of the object
    private Rectangle rectangle;                //Create a rectangle
    private String name;                        //Name of the sprite
    private int jumpTimer;                      //Used with jump power
    private int jumpPower;                      //How high you jump
    private boolean facingRight;                //Indicates if you are facing right
    private double velocityX;                    //Positive velocity takes you to the right side, negative velocity takes you to the left side
    private double velocityY;                    //Positive velocity takes you UP, negative velocity takes you DOWN

    public Sprite(Image image, Rectangle rectangle, String name, int jumptimer, int jumpPower)
    {
        this.image = image;
        this.rectangle = rectangle;
        this.name = name;
        this.jumpTimer = jumptimer;
        this.jumpPower = jumpPower;
    }
    public Sprite() {
    }
    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getJumpTimer() {
        return jumpTimer;
    }

    public void setJumpTimer(int jumpTimer) {
        this.jumpTimer = jumpTimer;
    }

    public int getJumpPower() {
        return jumpPower;
    }

    public void setJumpPower(int jumpPower) {
        this.jumpPower = jumpPower;
    }

    public boolean isFacingRight() {
        return facingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }
    public void update() {

        rectangle.translate((int) velocityX, (int) velocityY * (-1));
    }
}//End of sprite class
