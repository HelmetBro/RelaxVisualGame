import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class Game extends JFrame{

    static byte state;
    static StartScreen startScreen;
    static GameScreen gameScreen;

    final UpdateThread update;
    final DrawThread draw;

    private static int screenWidth;
    private static int screenHeight;

    static ArrayList<ClickEffect> clickEffects;
    private static byte clickEffectFillPulse;
    private static byte clickEffectOutlinePulse;
    private static double clickEffectLock;
    private static float clickEffectSpeed = 0.005f;

    private static double mouseTailLock;
    private static float mouseTailSpeed = 0.01f;

    public static PauseOverlay pauseOverlay = null;

    static Color currentMouseFill = Mouse.Fill;
    static Color currentMouseOutline = Mouse.Outline;

    private static double mouseFillLock;
    private static float mouseFillSpeed = 0.002f;

    private static double mouseOutlineLock;
    private static float mouseOutlineSpeed= 0.002f;

    static final Object lock = new Object();

    Game(){
        super("Relax");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        screenWidth = (int)screenSize.getWidth();
        screenHeight = (int)screenSize.getHeight();

        LoadContent();
        this.add(startScreen);

        //creating and running base threads
        update = new UpdateThread("Update");
        draw = new DrawThread("Draw");

        update.start();
        draw.start();
    }

    public static int getScreenWidth(){
        return screenWidth;
    }
    public static int getScreenHeight(){
        return screenHeight;
    }

    static void updateMousePulse(double elapsedTime){
        mouseFillLock += elapsedTime;
        mouseOutlineLock += elapsedTime;

        if (mouseFillLock % 1 >= mouseFillSpeed){

            int alphaFill = currentMouseFill.getAlpha();

            if (alphaFill <= 10)
                clickEffectFillPulse = 1;
            if (alphaFill >= 100)
                clickEffectFillPulse = 0;

            if (clickEffectFillPulse == 1)
                currentMouseFill = new Color(currentMouseFill.getRed(), currentMouseFill.getGreen(),
                        currentMouseFill.getBlue(), currentMouseFill.getAlpha() + 1);
            if (clickEffectFillPulse == 0)
                currentMouseFill = new Color(currentMouseFill.getRed(), currentMouseFill.getGreen(),
                        currentMouseFill.getBlue(), currentMouseFill.getAlpha() - 1);

            mouseFillLock = 0;
        }


        if(mouseOutlineLock % 1 >= mouseOutlineSpeed){
            int alphaOutline = currentMouseOutline.getAlpha();

            if (alphaOutline <= 20)
                clickEffectOutlinePulse = 1;
            if (alphaOutline >= 200)
                clickEffectOutlinePulse = 0;

            if (clickEffectOutlinePulse == 1)
                currentMouseOutline = new Color(currentMouseOutline.getRed(), currentMouseOutline.getGreen(),
                        currentMouseOutline.getBlue(), currentMouseOutline.getAlpha() + 1);
            if (clickEffectOutlinePulse == 0)
                currentMouseOutline = new Color(currentMouseOutline.getRed(), currentMouseOutline.getGreen(),
                        currentMouseOutline.getBlue(), currentMouseOutline.getAlpha() - 1);

            mouseOutlineLock = 0;
        }

    }

    static void drawMouse(Graphics2D g2d){
        g2d.setColor(currentMouseFill);
        g2d.fillOval(Mouse.X - Mouse.mouseWidth/2 - 2, Mouse.Y - Mouse.mouseHeight/2 - 2,
                Mouse.mouseHeight + 4, Mouse.mouseWidth + 4);

        g2d.setColor(currentMouseOutline);
        g2d.fillOval(Mouse.X - Mouse.mouseWidth/2, Mouse.Y - Mouse.mouseHeight/2 ,
                Mouse.mouseWidth, Mouse.mouseHeight);
    }
    public static void drawTail(Graphics2D g2d){
        try{
            for (byte i = 1; i < Mouse.mouseTailPoints.size(); i++){
                g2d.setColor(new Color(currentMouseFill.getRed(), currentMouseFill.getGreen(), currentMouseFill.getBlue(),
                        currentMouseFill.getAlpha() - i*(int)Math.floor(currentMouseFill.getAlpha()/Mouse.numberOfTails)));

                g2d.setStroke(new BasicStroke(Mouse.mouseDiameter/Mouse.numberOfTails*i));

                g2d.draw(new Line2D.Float(Mouse.mouseTailPoints.get(i-1).x, Mouse.mouseTailPoints.get(i-1).y,
                        Mouse.mouseTailPoints.get(i).x, Mouse.mouseTailPoints.get(i).y));
            }
        }catch (IndexOutOfBoundsException ignore){}
    }
    static void updateMouseTail(double elapsedTime){

        mouseTailLock += elapsedTime;

        if (mouseTailLock % 1 >= mouseTailSpeed){

            if (Mouse.Down && Mouse.mouseTailPoints.size() < Mouse.numberOfTails)
                Mouse.mouseTailPoints.add(new Point(Mouse.X, Mouse.Y));
            else if ((Mouse.Down && Mouse.mouseTailPoints.size() >= Mouse.numberOfTails) ||
                    (Mouse.Up && Mouse.mouseTailPoints.size() > 0))
                Mouse.mouseTailPoints.remove(0);

            mouseTailLock = 0;
        }
    }
    static void drawClickEffects(Graphics2D g2d){
        try{
            for (ClickEffect ce : clickEffects){
                g2d.setStroke(new BasicStroke(ce.thickness));
                g2d.setColor(ce.color);
                g2d.drawOval(ce.x - ce.currentRadius/2, ce.y - ce.currentRadius/2, ce.currentRadius, ce.currentRadius);
            }
        }catch (ConcurrentModificationException ignore){}
    }
    public static void drawBubbles(Graphics2D g2d, ArrayList<Bubble> bubbles){

        for(Bubble b : new ArrayList<>(bubbles)) {
            //drawing outline
            g2d.setColor(b.outlineColor);
            g2d.fillOval(b.getX(), b.getY(), b.getRadius() * 2, b.getRadius() * 2);

            //drawing middle
            g2d.setColor(b.fillColor);
            g2d.fillOval(b.getX() + b.getOutlineWidth() / 2, b.getY() + b.getOutlineWidth() / 2,
                    (b.getRadius() * 2) - b.getOutlineWidth(), (b.getRadius() * 2) - b.getOutlineWidth());
        }

    }
    static void updateClickEffects(double elapsedTime){
        clickEffectLock += elapsedTime;
        if(clickEffectLock % 1 >= clickEffectSpeed){
            for (ClickEffect ce : clickEffects){
                ce.currentRadius += 1;

                if (ce.color.getAlpha() - 1 > 0)
                    ce.color = new Color(ce.color.getRed(), ce.color.getGreen(), ce.color.getBlue(), ce.color.getAlpha() - 1);
                else
                    ce.color = new Color(ce.color.getRed(), ce.color.getGreen(), ce.color.getBlue(), 0);
            }
            clickEffectLock = 0;
        }
        for (Iterator<ClickEffect> iterator = clickEffects.iterator(); iterator.hasNext();) {
            ClickEffect value = iterator.next();
            if (value.currentRadius >= value.startRadius * 2) {
                iterator.remove();
            }
        }
    }

    public static boolean polyCollide(int nvert, int[] vertx, int[] verty, int testx, int testy) {
        int i, j;
        boolean result = false;
        for (i = 0, j = nvert - 1; i < nvert; j = i++)
            if ((verty[i] > testy != verty[j] > testy) &&
                    (testx < (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]))
                result = !result;
        return result;
    }

    public static boolean polyCollideDouble(int nvert, double[] vertx, double[] verty, int testx, int testy) {
        int i, j;
        boolean result = false;
        for (i = 0, j = nvert - 1; i < nvert; j = i++)
            if ((verty[i] > testy != verty[j] > testy) &&
                    (testx < (vertx[j] - vertx[i]) * (testy - verty[i]) / (verty[j] - verty[i]) + vertx[i]))
                result = !result;
        return result;
    }

    public static float calcDistance(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
    private void LoadContent(){
        //state 0 starts with startScreen
        state = 0;

        this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Mouse.X = e.getX();
                Mouse.Y = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Mouse.X = e.getX();
                Mouse.Y = e.getY();
            }
        });
        this.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //only detects clicking when mouse is not moving
            }

            @Override
            public void mousePressed(MouseEvent e) {
                Mouse.Up = false;
                Mouse.Down = true;
                Mouse.previousDown = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                Mouse.Up = true;
                Mouse.Down = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                Mouse.OnScreen = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {

                if (pauseOverlay == null){
                    pauseOverlay = new PauseOverlay();
                    Program.gameWindow.add(pauseOverlay);
                    if (!pauseOverlay.Stop){
                        pauseOverlay = null;
                    }
                }

                Mouse.OnScreen = false;

            }
        });

        // Transparent 16 x 16 pixel cursor image.
        BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        // Create a new blank cursor.
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "Blank Cursor");
        // Set the blank cursor to the JFrame.
        getContentPane().setCursor(blankCursor);

        clickEffects = new ArrayList<>();
        clickEffectFillPulse = 0;
        clickEffectOutlinePulse = 0;

        startScreen = new StartScreen();
        gameScreen = new GameScreen();
        startScreen.LoadContent();
        gameScreen.LoadContent();
    }

}
