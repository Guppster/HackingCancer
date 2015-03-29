import java.awt.*;

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
    private double x, y;
    private boolean grounded = false;

    public Sprite(Image image, Rectangle rectangle, String name, int jumptimer, int jumpPower)
    {
        this.image = image;
        this.rectangle = rectangle;
        this.name = name;
        this.jumpTimer = jumptimer;
        this.jumpPower = jumpPower;
        this.facingRight = true;
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
        return new Rectangle((int)x, (int)y, (int)rectangle.getWidth(), (int)rectangle.getHeight());
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
        x = rectangle.getX();
        y = rectangle.getY();
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


    public boolean isGrounded() {
        return grounded;
    }

    public void setGrounded(boolean g) {
        this.grounded = g;
    }
    public double getVelocityX() {
        return velocityX;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
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
    public void update()
    {
        if(velocityX > 0)
        {
            facingRight = true;
        }
        else if(velocityX<0)
        {
            facingRight = false;
        }
        rectangle.translate((int) velocityX, (int) velocityY * (-1));
        x+=velocityX;
        y-=velocityY;
    }
}//End of sprite class
