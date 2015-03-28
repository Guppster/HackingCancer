import javax.swing.*;
import java.awt.*;

/**
 * Created by mlh on 3/28/2015.
 */
public class Sprite
{

    private Image image;
    private Rectangle rectangle;
    private String name;
    private int jumpTimer;
    private int jumpPower;

    public Sprite(Image image, Rectangle rectangle, String name, int jumptimer, int jumpPower)
    {
        this.image = image;
        this.rectangle = rectangle;
        this.name = name;
        this.jumpTimer = jumptimer;
        this.jumpPower = jumpPower;
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
}//End of sprite class
