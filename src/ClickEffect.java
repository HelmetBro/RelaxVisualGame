import java.awt.*;

public class ClickEffect {

    public int x;
    public int y;

    //starter color. Can later pass this through
    //constructor for cool stuff
    public Color color = new Color(0, 0, 0, 100);

    public int startRadius = Mouse.mouseWidth + Mouse.mouseHeight;
    public int currentRadius = startRadius;

    public byte thickness = 3;

    public ClickEffect(int x, int y){
        this.x = x;
        this.y = y;
    }
}
