import javax.swing.*;
import java.awt.*;

public class PauseOverlay extends JPanel {

    public boolean Stop;

    private byte countDown = 3;
    //private byte currentCount = countDown;
    private long start;
    long currentTime;

    //number visual
    private short numStartAlpha = 255;
    private Color numColor = new Color(43, 43, 43, numStartAlpha);
    private Font numFont = new Font("Monospaced", Font.BOLD, 96);

    //pause word visual
    private Color pauseColor = new Color(43, 43, 43);
    private Font pauseFont = new Font("Monospaced", Font.BOLD, 96);
    private String pauseText = "Paused";

    private float backClrIncrement = 0;
    private float backClrLock = 0;
    private float backClrChangeSpeed = 100f;

    public PauseOverlay(){
        super();
        setBounds(0, 0, Game.getScreenWidth(), Game.getScreenHeight());
        Stop = true;
        start = System.currentTimeMillis();

        currentTime = System.currentTimeMillis();
    }

    public void Update(double elapsedTime){
        backClrLock += elapsedTime;

        if (backClrLock - 1 >= backClrChangeSpeed){

            // Pass .5 (= 180 degrees) as HUE
            if(backClrIncrement > 360)
                backClrIncrement = 0;

            backClrIncrement += 1;

            backClrLock = 0;
        }


        if(!Mouse.OnScreen){
            start = System.currentTimeMillis();
        }else{
            currentTime = System.currentTimeMillis();

            if ((currentTime - start) >= countDown)
                Stop = true;
        }
    }//update

    public void Draw(){
        repaint();

        /*
        if(!Mouse.OnScreen){
            numColor = new Color(numColor.getRed(), numColor.getGreen(), numColor.getBlue(), numStartAlpha);
        }else{
            //for number decay
            long newAlpha = numStartAlpha - (((currentTime - start)/countDown) * numStartAlpha);
            if (!(newAlpha < 0))
                numColor = new Color(numColor.getRed(), numColor.getGreen(), numColor.getBlue(), newAlpha);
        }
        */
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //formatting paint component
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        float hue = backClrIncrement/360;
        setBackground(Color.getHSBColor(hue, 0.15f, 1));

        //draws pause label
        g2d.setColor(pauseColor);
        FontMetrics pauseMetrics = g.getFontMetrics(pauseFont);
        g2d.setFont(pauseFont);
        g2d.drawString(pauseText,
                (Game.getScreenWidth() - pauseMetrics.stringWidth(pauseText))/2,
                ((Game.getScreenHeight() - pauseMetrics.getHeight()) / 2) + pauseMetrics.getAscent());

        /*
        if(!Mouse.OnScreen){
            //draws pause label
            g2d.setColor(pauseColor);
            FontMetrics pauseMetrics = g.getFontMetrics(pauseFont);
            g2d.setFont(pauseFont);
            g2d.drawString(pauseText,
                    (Game.getScreenWidth() - pauseMetrics.stringWidth(pauseText))/2,
                    ((Game.getScreenHeight() - pauseMetrics.getHeight()) / 2) + pauseMetrics.getAscent());
        }
        else{
            //number countdown
            g2d.setColor(numColor);

            FontMetrics numMetrics = g.getFontMetrics(numFont);
            g2d.setFont(numFont);
            g2d.drawString(Byte.toString(currentCount),
                    (Game.getScreenWidth() - numMetrics.stringWidth(Byte.toString(currentCount)))/2,
                    ((Game.getScreenHeight() - numMetrics.getHeight()) / 2) + numMetrics.getAscent());
        }
        */

        ///Order is important, this should go last. Mouse-
        //drawing mouse tail when appropriate
        if (Mouse.Down || Mouse.mouseTailPoints.size() > 0)
            Game.drawTail(g2d);

        //drawing the mouse
        Game.drawMouse(g2d);

        //drawing effectCircle
        Game.drawClickEffects(g2d);
        ///


    }
}
