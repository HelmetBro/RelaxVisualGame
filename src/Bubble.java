import java.awt.*;
import java.util.Random;

public class Bubble {

    private float x;
    private float baseX;
    private float y;
    private int radius;
    private int speed;

    boolean shouldRemove;

    public Color outlineColor;
    public Color fillColor;

    private short outlineWidth = 10;//divisible by 2 to look even

    public int standardFillAlpha;
    public int standardOutlineAlpha;

    public Bubble(int screenWidth, int screenHeight, int radiusUpper, int radiusLower, int upperSpeed, int lowerSpeed,
                  Color outlineColor) {
        this.outlineColor = outlineColor;
        this.standardOutlineAlpha = outlineColor.getAlpha();

        Random gen = new Random();
        this.radius = gen.nextInt(radiusUpper - radiusLower) + radiusLower;
        this.y = screenHeight + radius;
        this.x = gen.nextInt(screenWidth + this.radius) - this.radius;
        this.baseX = this.x;
        this.speed = gen.nextInt(upperSpeed - lowerSpeed) + lowerSpeed;

        int shade = gen.nextInt(255);
        standardFillAlpha = gen.nextInt(20);

        fillColor = new Color(255-shade, 255-shade, 255-shade, standardFillAlpha);
    }

    public void updateBubble(double elapsedTime) {

        if (this.y + radius < 0){
            shouldRemove = true;
            return;
        }

        this.y += -1 * (speed * elapsedTime);

        float yCalc = (float)Math.sin(this.y /50) * 20;
        float xCalc = baseX + yCalc;
        this.x = (int)xCalc;
    }

    public int getOutlineWidth(){
        return this.outlineWidth;
    }

    public int getRadius(){
        return this.radius;
    }

    //return ints for drawing
    public int getX(){
        return (int)x;
    }
    public int getY(){
        return (int)y;
    }

}