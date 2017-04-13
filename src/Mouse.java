import java.awt.*;
import java.util.ArrayList;

class Mouse {

    //these are the starting positions. Update thread updates with real-time positions
    static int X = -Mouse.mouseWidth;
    static int Y = -Mouse.mouseHeight;

    static byte mouseWidth = 15;
    static byte mouseHeight = 15;

    static int mouseDiameter = mouseWidth + mouseHeight / 2;

    static Color Fill = new Color(0, 125, 238, 200);
    static Color Outline = new Color(0, 77, 158, 200);
    static Color collisionFill = new Color(238, 0, 0, 200);
    static Color collisionOutline = new Color(158, 0, 0, 200);

    //static boolean Clicked = false;
    static boolean previousDown;
    static boolean Down = false;
    static boolean Up = false;
    static boolean OnScreen = true;

    static ArrayList<Point> mouseTailPoints = new ArrayList<>();

    static byte numberOfTails = 10;
}
