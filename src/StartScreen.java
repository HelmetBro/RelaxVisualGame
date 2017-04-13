import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class StartScreen extends JPanel {

    //for background
    private Color backgroundColor = new Color(255, 255, 255);//5 alpha cool superForm effect

    //list of bubbles
    private ArrayList<Bubble> bubbles = new ArrayList<>();
    private short numOfBubbles = 50;
    private Color bubbleOutlineColor = new Color(35, 228, 255, 60);

    //superformula shape
    private SuperFormula formula;

    //start button font
    private Font startBFont = new Font("Monospaced", Font.BOLD, 96);
    private Color startBColor = new Color(43, 43, 43, 0);
    private String startText = "Start";

    public StartScreen(){
        super();
        setBounds(0, 0, Game.getScreenWidth(), Game.getScreenHeight());
        setBackground(backgroundColor);

        formula = new SuperFormula();
    }

    public void Update(double elapsedTime){
        formula.createSuperFormula();

        updateBubblesSS(elapsedTime);

        formula.updateTheSuperFormula(elapsedTime);

        //changes alpha of start + superformula
        float dToCenter = Game.calcDistance(Mouse.X, Mouse.Y, Game.getScreenWidth()/2, Game.getScreenHeight()/2);
        int newAlpha = (int) ((255/dToCenter) * 100);
        if (newAlpha < 255 && newAlpha > 0){
            formula.fillColor = new Color(formula.fillColor.getRed(), formula.fillColor.getGreen(), formula.fillColor.getBlue(), newAlpha);
            //formula.outlineColor = new Color(formula.fillColor.getRed(), formula.fillColor.getGreen(), formula.fillColor.getBlue(), newAlpha);
            startBColor = new Color(startBColor.getRed(), startBColor.getGreen(), startBColor.getBlue(), newAlpha);
        }

        //adds clickEffect and checks for state change
        if(Mouse.previousDown && Mouse.Up){
            Mouse.previousDown = false;
            Game.clickEffects.add(new ClickEffect(Mouse.X, Mouse.Y));
            checkToStartGame();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        //formatting paint component
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        //drawing bubbles
        Game.drawBubbles(g2d, bubbles);

        //superformula
        g2d.setStroke(new BasicStroke(formula.stroke));
        g2d.setColor(formula.outlineColor);
        g2d.drawPolygon(formula.superArrayX, formula.superArrayY, formula.getSuperArrayLength());

        g2d.setColor(formula.fillColor);
        g2d.fillPolygon(formula.superArrayX, formula.superArrayY, formula.getSuperArrayLength());

        //start button font
        g2d.setColor(startBColor);
        FontMetrics metrics = g.getFontMetrics(startBFont);

        g2d.setFont(startBFont);
        g2d.drawString(startText, (Game.getScreenWidth() - metrics.stringWidth(startText))/2,
                ((Game.getScreenHeight() - metrics.getHeight()) / 2) + metrics.getAscent());

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

    public void Draw(){
        repaint();
    }

    private void updateBubblesSS(double elapsedTime){
        for (Iterator<Bubble> iterator = bubbles.iterator(); iterator.hasNext();) {
            Bubble value = iterator.next();
            if (value.shouldRemove) {
                iterator.remove();
            }
        }

        if (bubbles.size() < numOfBubbles)
            bubbles.add(new Bubble(Game.getScreenWidth(), Game.getScreenHeight(), 20, 10, 500, 200, bubbleOutlineColor));

        for(Bubble b : bubbles)
            b.updateBubble(elapsedTime);
    }

    private void checkToStartGame(){
        //if mouse is down and within start polygon
        if (Game.polyCollide(formula.getSuperArrayLength(), formula.superArrayX, formula.superArrayY, Mouse.X, Mouse.Y)){
            Game.state = 1;//start the game (change the state)
            Program.gameWindow.remove(Game.startScreen);
            Program.gameWindow.add(Game.gameScreen);
            Game.gameScreen.revalidate();
        }
    }

    public void LoadContent(){
        //for additions, later
    }
}//class
